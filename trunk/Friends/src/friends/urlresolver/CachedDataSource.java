package friends.urlresolver;

public  class CachedDataSource<K,V> implements DataSource<K, V>
{
	private Cache<K,V> cache;
	private DataSource<K, V> dataSource;
	
	public  CachedDataSource( DataSource<K, V> dataSource, Cache<K,V> cache)
	{
		this.cache = cache;
		this.dataSource = dataSource;
	}

	public  V  get(K k) throws Exception
	{
		V ret = cache.get(k);
		if(ret == null)
		{
			ret = dataSource.get(k);
			cache.put(k, ret);
		}
		return ret;
	}

	public  V put(K k, V v)throws Exception
	{
		dataSource.put(k, v);
		return cache.put(k, v);

	}
	
	
}
