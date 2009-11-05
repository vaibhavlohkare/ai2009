package crawler;

import myGoogle.Crawler.Message.UrlDistibutionMessage;
import myGoogle.Distributed.ConstantNodeIdFactory;
import rice.p2p.commonapi.Id;

public class UrlDispatcher implements Runnable
{
	private UrlDistibutionMessage[] messages;
	private Id[] crawlerNodeIds;
	private int totalCrawler;

	
	public UrlDispatcher(int totalCrawler)
	{
		this.totalCrawler = totalCrawler;

		crawlerNodeIds = new Id[totalCrawler];
		messages = new UrlDistibutionMessage[totalCrawler];
		for(int i = 0;i< totalCrawler;i++)
		{
			crawlerNodeIds[i] = ConstantNodeIdFactory.generateNodeId(totalCrawler, i);
			messages[i] = new UrlDistibutionMessage();
		}
	}
	
	public void flush()
	{
		for(int i = 0;i< totalCrawler;i++)
		{
			if(messages[i].urls.size() > 0)
			{
				Crawler.distributeChannel.sendMessage(crawlerNodeIds[i], messages[i]);
				messages[i] = new UrlDistibutionMessage();
			}
		}
	}
	
	public void process(String host,String urlString)
	{
		int i = (host.hashCode() % totalCrawler + totalCrawler)% totalCrawler;
		messages[i].addUrl(urlString);
	}

	public void run()
	{
		while(!Crawler.shutDown)
		{
			try
			{
				flush();
				Thread.sleep(100);
				
			}
			catch (Exception e)
			{
				e.printStackTrace();
				continue;
			}
			
		}
		
	}
}
