package friends.urlresolver;

import friends.util.BerkeleyDBAccess;
import com.sleepycat.je.DatabaseException;

public class BerkeleyDBSource<K, V> implements DataSource<K, V>
{

	BerkeleyDBAccess<K, V> DB;

	public BerkeleyDBSource(BerkeleyDBAccess<K, V> db)
	{
		super();
		DB = db;
	}

	public V get(K k) throws DatabaseException
	{
		// TODO Auto-generated method stub
		return DB.get(k);
	}

	public V put(K k, V v) throws DatabaseException
	{
		// TODO Auto-generated method stub
		DB.add(k, v);
		return null;
	}

	public void sync() throws DatabaseException
	{
		this.DB.Sync();
	}

}
