package friends.urlresolver;

public interface DataSource<K,V>
{
	public V put(K k, V v) throws Exception;
	public V get (K k)throws Exception;
}
