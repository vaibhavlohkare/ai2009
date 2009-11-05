package crawler;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

import myGoogle.Util.Disk;

public class UrlFrontier
{
	public  Vector<LinkedBlockingQueue<String>>  urlQueues;
	private int queueCount;
	private int totalQueueSize = 0;
	private int crawlerId;
	
	public boolean loaded = false;
	
	
	public void save()
	{
		try
		{
			for (int i = 0;i<queueCount;i ++)
			{
					Disk.save(urlQueues.get(i), "Data/Crawler"+crawlerId,"savedQueue"+i);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	
	@SuppressWarnings("unchecked")
	private void load()
	{
		int i = 0;
		try
		{
			System.out.print("Loading unprocessed links...");
			this.urlQueues = new  Vector<LinkedBlockingQueue<String>>(queueCount);
			for (;i<queueCount;i ++)
			{
				urlQueues.add((LinkedBlockingQueue<String>)Disk.load( "Data/Crawler"+crawlerId+"/savedQueue"+i));
			}
			System.out.println("success");
			loaded = true;
		}
		catch (Exception e)
		{
			System.out.println("failed, creating new...");
			for(;i< queueCount;i++)
			{
				urlQueues.add(new LinkedBlockingQueue<String>());
			}
		}
	}
	public void  put(String urlString) 
	{	
		try
		{
			URL url = new URL(urlString);
			String host = url.getHost();
//			SHA1 sha = new SHA1(host);
//			byte[] bytes = sha.getDigest();
			int queueNumber = ( (host+"salt").hashCode() % queueCount + queueCount)% queueCount;
			LinkedBlockingQueue<String> queue = urlQueues.get(queueNumber);
			queue.put(urlString);
			totalQueueSize ++;
		}
		catch (Exception  e)
		{
			e.printStackTrace();
		}
	}
	
	public String get(int queueNumber) throws InterruptedException
	{
		return urlQueues.get(queueNumber).take();
	}
	public UrlFrontier(int crawlerId,int queueCount)
	{
		this.crawlerId = crawlerId;
		this.queueCount = queueCount;
		load();                                         
	}

	public int getTotalQueueSize()
	{
		return totalQueueSize;
	}
}
