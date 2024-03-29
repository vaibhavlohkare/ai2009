package friends.crawler;
import friends.crawler.Robot;

import friends.util.SHA1;

public class CrawlerWorker implements Runnable
{	
	StreamFetcher fetcher = new StreamFetcher(this);
	//Robot robotChecker = new Robot();
	Thread thread;
	int ThreadID;
	int rotatingCount = 0;
	public void run()
	{
		this.thread = Thread.currentThread();
		this.ThreadID = Integer.valueOf(thread.getName());
		String urlString = null;
		while(!Crawler.shutDown)
		{
			try
			{
				urlString = Crawler.urlFrontier.get(ThreadID);
//				if(Crawler.showLog)
//				{
//					System.out.println("@@@ get and ready to work on url: " + urlString);
//				}
			}
			catch(InterruptedException e)
			{
				continue;
			}
			
			if(Crawler.shutDown)
			{
				return;
			}
			doWork(urlString);
			rotatingCount = ( rotatingCount +1  )% 10 ;
			urlString = null;
		}
	}
	
	private void doWork(String urlString)
	{
		if(urlString == null)
			return;
		try
		{		
//			if(!robotChecker.process(urlString))
//				return;
			
			FetchedDoc doc = fetcher.Fetch(urlString);
			

			if(doc == null)
			{
				if(Crawler.showLog)
				{
					System.out.println("Downloading: " + urlString +" ...failed");
				}

				return;
			}

			if(Crawler.showLog)
			{
				System.out.println("Downloading: " + urlString +" ...done");
				//System.out.print(new String(doc.data,"UTF-8"));
			}
			
			SHA1 pageHash = new SHA1(doc.data);
			SHA1 urlHash = new SHA1(urlString);
			// not parse the page twice, unless we only crawl this page
			if(!Crawler.due.docExist(urlHash, pageHash)) // (!rm.isExist)
			{
//				if(Crawler.showLog)
//					System.out.println();
//				if(doc.data.length> 100000)
//				{
//					System.err.println(doc.urlString);
//				}
					
				Crawler.due.put(urlHash, pageHash);
				
				Crawler.linkExtractor.Process(doc);
				Crawler.statistics.increaseHtmlNumber();
			}
			else
			{
//				if(Crawler.showLog)
//					System.out.println("(old)");
			}
			
			doc = null;
			
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
		
	}
}

