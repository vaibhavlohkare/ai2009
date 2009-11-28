package friends.urlresolver;

import friends.util.BerkeleyDBAccess;

import com.sleepycat.je.DatabaseException;

public class LRUCachedDb<K, V> implements DataSource<K, V>
{
	private BerkeleyDBAccess<K, V> DB;
	private BerkeleyDBSource <K, V> DBSource;
	private CachedDataSource<K, V>  cachedDataSource;
	private LRUCache<K, V> cache;
	boolean isClosed = false;
	
	public LRUCachedDb(Class classK,Class classV,String path,int cacheSize) throws Exception
	{
		DB = new BerkeleyDBAccess<K, V>(classK,classV);
		DB.createEnv(path);
		DB.openDB("UrlDB", false);
		DBSource = new BerkeleyDBSource<K, V>(DB);
		cache = new LRUCache<K, V>(cacheSize);
		cachedDataSource = new CachedDataSource<K, V>(DBSource,cache);
	}

	public void sync() throws DatabaseException
	{
		this.DB.Sync();
	}
	public V get(K k) throws Exception
	{
		return cachedDataSource.get(k);
	}

	public V put(K k, V v) throws Exception
	{
		return cachedDataSource.put(k, v);
	}
	
	public void close()throws Exception
	{
		if(!isClosed)
		{
			DB.closeDatabase();
			DB.closeEnv();
			isClosed = true;
		}
	}
	protected void finalize()
	{
		try
		{
			close();
		}
		catch (Exception e)
		{
			
		}
	}

}


	