package friends.crawler;
import com.sleepycat.je.DatabaseException;

import friends.util.SHA1;
import friends.urlresolver.LRUCachedDb;

public class DupUrlEliminator 
{
	private LRUCachedDb<SHA1, SHA1> urlCachedDb ;
	
	public DupUrlEliminator(String path,int cacheSize) throws Exception
	{
		urlCachedDb = new LRUCachedDb<SHA1, SHA1>(SHA1.class,SHA1.class,path,cacheSize);
	}

	public SHA1 get(SHA1 k) throws Exception
	{
		return urlCachedDb.get(k);
	}

	public void put(SHA1 k, SHA1 v) throws Exception
	{
			urlCachedDb.put(k, v);
	}
	
	public void close()
	{
		try
		{
			this.urlCachedDb.sync();
			this.urlCachedDb.close();
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	protected   void process(String urlString) 
	{
		if(urlString!= null)
		{
			try
			{
				SHA1 sha = new SHA1(urlString);
				SHA1 page = get(sha);
				if(page == null)
				{
					put(sha,new SHA1("seen"));
					Crawler.urlFrontier.put(urlString);
				}
				else
				{
					if (Crawler.showLog)
					{
						System.out.printf("@@@ DupUrlEliminator: duplicate url: %s\n", urlString);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public boolean docExist(SHA1 k, SHA1 v) throws Exception
	{
		SHA1 pv = get(k);
		return (pv == v);
	}
	
	public static void main(String[] args)
	{
		try
		{
			DupUrlEliminator due = new DupUrlEliminator("Data/Test/urlDB",10000);
			String url = "http:\\www.yelp.com";
			SHA1 urlHash = new SHA1(url);
			System.out.printf("The url has page: %s\n", due.get(new SHA1(url)));
			due.put(urlHash, new SHA1("seen"));
			System.out.printf("The url has page: %d\n", due.get(new SHA1(url)).hashCode());
			String page = "this is a good page!";
			SHA1 pageHash = new SHA1(page);
			System.out.printf("Is the url's page seen before? %s\n", due.docExist(new SHA1(url),pageHash));
			due.put(urlHash, pageHash);
			System.out.printf("Is the url's page seen before? %s\n", due.docExist(new SHA1(url),pageHash));
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	 
}
