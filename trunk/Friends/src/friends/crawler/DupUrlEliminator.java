package friends.crawler;
//import com.sleepycat.je.DatabaseException;

import friends.database.*;
import friends.util.*;

//import friends.util.SHA1;
//import myGoogle.Common.Cache.LRUCachedDb;

public class DupUrlEliminator 
{
	//private LRUCachedDb<SHA1, Long> urlCachedDb ;
	private Link urlCachedDb = new Link();
	//private Link<Long, String> urlCached;
	
	
	public DupUrlEliminator(String path,int cacheSize) throws Exception
	{
		//urlCachedDb = new LRUCachedDb<SHA1, Long>(SHA1.class,Long.class,path,cacheSize);
		urlCachedDb = new Link();
		urlCachedDb.setLink(path);
		int size = cacheSize;
	}

	public String get(String k) throws Exception
	{
		return urlCachedDb.getLink();
	}

	public void put(SHA1 k, Long v) throws Exception
	{
		//urlCachedDb.put(k, v);
	}
	
	public void close()
	{
		try
		{
			//this.urlCachedDb.sync();
			//this.urlCachedDb.close();
		}
		/*catch (DatabaseException e)
		{
			e.printStackTrace();
		}*/
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
				//Long docID = get(sha);
				Link l = new Link();
				int docID = l.getLinkID();
				if(docID == 0) // == null
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
