package friends.crawler;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;

import friends.datacollect.LinkExtractor;
import friends.util.Config;


public class Crawler 
{
	static boolean shutDown = false;
	static int MAXTHREADNUM = 20;
	static Vector<Thread> threads= new Vector<Thread>(MAXTHREADNUM);
	static Thread dispatcherThread;
	static CrawlerStatistics statistics= new CrawlerStatistics();
	static double maxVolume = -1;
	static boolean showLog = true;
	static long startTime ;
	
	static DupUrlEliminator due ;
	static UrlFrontier urlFrontier;
	
	static StoreChannel storeChannel = new StoreChannel();
	static LinkExtractor linkExtractor;
	public static UrlFilter urlFilter = new UrlFilter();
	static DistributeChannel distributeChannel = new DistributeChannel();
	static UrlDispatcher urlDispatcher ;
	static int dispatcherMessageSize = 200;
	
	
	
	static String startUrl ;
	static boolean started = false;
	static int id= 0;
	static int totalCrawler = 1;
	
	static Config config;
	static String hostName;
	static String bootStrapCrawler;
	
	
	public static class CrawlerStatistics
	{
		private long volumeCrawled = 0;
		private long htmlCrawled = 0;
		synchronized void increaseVolume(long bytes)
		{
			volumeCrawled+=bytes;
		}
		synchronized void increaseHtmlNumber()
		{
			htmlCrawled++;
		}
		long getHtmlNumber()
		{
			return htmlCrawled;
		}
		
		long getVolumeInMB()
		{
			return volumeCrawled/1048576;
		}
		long getVolumeInBytes()
		{
			return volumeCrawled;
		}
	}

	public static void printState()
	{
		System.out.println("Html Processed: "+statistics.htmlCrawled );
		System.out.println("Total Traffic: "+statistics.volumeCrawled);
		System.out.println("Time elapsed: "+(System.currentTimeMillis() - Crawler.startTime)/1000 +" secs");
	}
	private static void start()
	{
		if(!started)
		{
			started = true;
			
			System.out.println("Starting dispatcher thread");
			dispatcherThread = new Thread(urlDispatcher);
			dispatcherThread.setDaemon(true);
			dispatcherThread.setPriority(Thread.MAX_PRIORITY);
			dispatcherThread.setName("Dispatcher");
			dispatcherThread.start();
			System.out.println("Starting crawler worker threads...");
			
			for (int i=0; i < MAXTHREADNUM; i++)
			{
				Thread thread = new Thread(new CrawlerWorker());
				threads.add(thread);
				thread.setDaemon(true);
				thread.setName(String.valueOf(i));
				thread.start();
			}
			
			
			startTime = System.currentTimeMillis();
			
			if(!urlFrontier.loaded)
				urlFilter.Process(startUrl);
		}
	}
	public static void main(String[] args) 
	{
		
//		String url ="http://www.seas.upenn.edu/~cse45501/";
//		String path = ".."+File.separatorChar+".."+File.separatorChar+"dbEnv";
//		String url ="http://www.dental.upenn.edu/alumni/index.html";
		//String url = "http://localhost:8080/test.htm";
		try
		{
			if(args.length>0)
			{
				totalCrawler = Integer.parseInt(args[0]);
				if(args.length>1)
				{
					id = Integer.parseInt(args[1]);
					if(args.length>2)
					{
						try
						{
							Crawler.maxVolume = Double.valueOf(args[2])*1024*1024;
						}
						catch (Exception e)
						{
							System.out.println("Invalid Argument.");
						}
						
					}
				}
			}
			config = new Config("myGoogle.cfg");
			
			hostName = config.get("StoreCoordinatorAddress");
			if(hostName == null)
				hostName = "localhost";
			
			bootStrapCrawler = config.get("bootStrapCrawler");
			if(bootStrapCrawler == null)
				bootStrapCrawler = "localhost";
			
			startUrl = config.get("starturl");
			if(startUrl == null)
				startUrl = "http://www.seas.upenn.edu/";


			due  = new DupUrlEliminator("Data/UrlResolver"+id+"/urlDB",10000);
			urlFrontier = new UrlFrontier(id,Crawler.MAXTHREADNUM);
			linkExtractor = new LinkExtractor(id);
			urlDispatcher = new UrlDispatcher(Crawler.totalCrawler);

			System.out.println("Joining Storage network...");
			storeChannel.joinNetwork(hostName, 10000, 30000+id, "store");
			storeChannel.hideMessageInfo();
			
			System.out.println();
			System.out.println("Joining  URL distribution network...");
			distributeChannel.setNodeIdInfo(totalCrawler, id);
			distributeChannel.joinNetwork(hostName, 32000, 32000+id, "crawl");
			distributeChannel.hideMessageInfo();
			
			
			
//			Starting interative menu
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String input = null;
			try
			{
				while( true)
				{
					System.out.print("\nControl Panel:\n1)Start\n2)Toggle Log\n3)Print State\n4)Exit\n");
					input = reader.readLine();
					
					if (input.equals("1"))
					{
						start();
					}
					else if (input.equals("2"))
					{		
						Crawler.showLog = !Crawler.showLog;
					}
					else if (input.equals("3"))
					{		
						printState();
					}
					else if(input.equals("4"))
					{
						ShutDown();
						return;
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	static void ShutDown()
	{
		System.out.println("Shutting down...");
		Crawler.shutDown = true;
		try
		{
			Thread.sleep(1000);
			dispatcherThread.join(1000);
		}
		catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}
		
		printState();
		
		
		System.out.println("Shutting down threads...");
		for(int i =0;i<Crawler.MAXTHREADNUM;i++)
		{
			Thread thread = Crawler.threads.get(i);
			if(thread == null)
				break;
			try
			{
				Crawler.threads.get(i).join(500);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		System.out.println("Leaving Pastry Network...");
		Crawler.distributeChannel.close();
		Crawler.storeChannel.close();
		
		System.out.println("Closing databases...");
		try
		{
			due.close();
			linkExtractor.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Storing unprocessed urls");
		urlFrontier.save();
		config.save();
		System.exit(0);
	}
	
}
