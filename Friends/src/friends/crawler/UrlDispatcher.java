package friends.crawler;

//import friends.common.UrlDistibutionMessage;
import friends.common.UrlsDistributedMessage;
//import friends.distributed.ConstantNodeIdFactory;
//import rice.p2p.commonapi.Id;

public class UrlDispatcher implements Runnable
{
	/***********************
	 * This class works as the buffer to dispatch url links
	 */
	
	
	//	private UrlDistibutionMessage[] messages;
	private UrlsDistributedMessage[] channels;
//	private Id[] crawlerNodeIds;
	private int totalCrawler;

	
	public UrlDispatcher(int totalCrawler)
	{
		this.totalCrawler = totalCrawler;

		channels = new UrlsDistributedMessage[totalCrawler];
		for (int i = 0; i < totalCrawler; i++)
		{
			channels[i] = new UrlsDistributedMessage();
		}
//		crawlerNodeIds = new Id[totalCrawler];
//		messages = new UrlDistibutionMessage[totalCrawler];
//		for(int i = 0;i< totalCrawler;i++)
//		{
//			crawlerNodeIds[i] = ConstantNodeIdFactory.generateNodeId(totalCrawler, i);
//			messages[i] = new UrlDistibutionMessage();
//		}
	}
	
	public void flush()
	{
		for(int crawlerIndex = 0;crawlerIndex< totalCrawler;crawlerIndex++)
		{
			int urlsSize = channels[crawlerIndex].urls.size();
			if(urlsSize > 0)
			{
				for(int messageIndex = 0;messageIndex<urlsSize;messageIndex++)
				{
					if (Crawler.showLog)
					{
//						System.out.printf("@@@ UrlDispatcher, dispatch url: %s\n", 
//								channels[crawlerIndex].urls.elementAt(messageIndex));
					}
					Crawler.due.process(channels[crawlerIndex].urls.elementAt(messageIndex));
					
				}
				channels[crawlerIndex].urls.clear(); 
			}
		}
	}
	
	public void process(String host,String urlString)
	{
		int i = (host.hashCode() % totalCrawler + totalCrawler)% totalCrawler;
		channels[i].addUrl(urlString);
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
