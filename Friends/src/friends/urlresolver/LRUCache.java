package friends.urlresolver;

import java.util.LinkedHashMap;
import java.util.Map;


public class LRUCache<K,V> extends LinkedHashMap<K,V> implements Cache<K,V>
{
	
	private static final long serialVersionUID = 3195118200770076797L;
	private int maxCapacity;
	public LRUCache(int maxCapacity)
	{
		super(maxCapacity *3 /2, (float)0.75,true);
		this.maxCapacity = maxCapacity;
		
	}
	
	protected boolean removeEldestEntry(Map.Entry eldest)
	{
		return size() > maxCapacity;
	}
	
	public synchronized V put(K k, V v)
	{
		return super.put(k, v);
	}
	
	public synchronized V get(Object k)
	{
		return super.get(k);
	}
 
	/*
	private LinkedListEx<K> list ;
	private Hashtable<K,Pair> table ;
	private int capacity ;
	
	public synchronized V get(Object k)
	{
		Pair pair =  table.get(k);
		if(pair != null)
			return pair.value;
		else
			return null;
	}
	
	private class Pair
	{
		Node<K> node;
		V value;
	}

	public synchronized V put(K k, V v)
	{
		int size = table.size();
		
		Pair pair  = table.get(k);
		V ret;
		if(pair == null)
		{
			if(size == capacity)// remove the LRU element
			{
				K kLast = list.removeLast().getValue();
				table.remove(kLast);
			}
			pair = new Pair();
			pair.node = new Node<K>(k);
			pair.value = v;
			table.put(k, pair);
			list.addFirst(pair.node );
			ret = null;
		}
		else
		{
			ret = pair.value;
			pair.value = v;
			table.put(k, pair);
			pair.node.remove();
			list.addFirst(pair.node);
		}
		return ret;
	}
	public LRUCache (int capacity)
	{
		this.capacity = capacity;
		list = new LinkedListEx<K>();
		table = new Hashtable<K,Pair>(capacity);
	}
	
*/
}
