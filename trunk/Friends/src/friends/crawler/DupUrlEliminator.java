package friends.crawler;
import com.sleepycat.je.DatabaseException;

import friends.util.SHA1;
import myGoogle.Common.Cache.LRUCachedDb;

public class DupUrlEliminator 
{
private LRUCachedDb<SHA1, Long> urlCachedDb ;
	
	public DupUrlEliminator(String path,int cacheSize) throws Exception
	{
		urlCachedDb = new LRUCachedDb<SHA1, Long>(SHA1.class,Long.class,path,cacheSize);
	}

	public Long get(SHA1 k) throws Exception
	{
		return urlCachedDb.get(k);
	}

	public void put(SHA1 k, Long v) throws Exception
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
			SHA1 sha = new SHA1(urlString);
			try
			{
				Long docID = get(sha);
				if(docID == null)
				{
					put(sha,-1L);
					Crawler.urlFrontier.put(urlString);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	 
}
