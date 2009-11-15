package friends.util;

public class LinkedListEx<K>
{
	private  Node<K> head = null;
	private  Node<K> last = null;
	protected int size = 0;
	
	public static class  Node<K>
	{
		private Node<K> previous ;
		private Node<K> next ;
		private K k;
		private LinkedListEx<K> list = null;
		public Node (K k)
		{
			this.k = k;
		}
		public void remove()
		{
			if(this.list != null && this.list.size >0)
			{
				if(this.list.size ==1)
				{
					this.list.removeFirst();
				}
				else
				{
					if(this.list.head == this)
					{
						this.list.removeFirst();
					}
					else if(this.list.last == this)
					{
						this.list.removeLast();
					}
					else
					{
						if(this.previous != null)
						{
							this.previous.next = this.next;
						}
						if(this.next != null)
						{
							this.next.previous = this.previous;
						}
						this.list.size --;
					}
				}
				this.list = null;
			}
		}
		public K getValue()
		{
			return k;
		}
		public void setValue(K k)
		{
			this.k = k;
		}
	}
	
	public Node<K> removeFirst()
	{
		if(this.head == null)
		{
			return null;
		}
		else
		{
			Node<K> node = this.head;
			if(node.next == null)//single node
			{
				this.head = null;
				this.last = null;
			}
			else
			{
				this.head = node.next;
				this.head.previous = null;
			}
			size --;
			return node;
		}
	}
	
	public Node<K> removeLast()
	{
		if(this.last == null)
		{
			return null;
		}
		else
		{
			Node<K> node = this.last;
			if(node.previous == null)//single node
			{
				this.head = null;
				this.last = null;
			}
			else
			{
				this.last = node.previous;
				this.last.next = null;
			}
			size --;
			return node;
		}
	}
	public void addFirst(Node<K> node)
	{
		node.list = this;
		node.previous = null;
		if(this.head == null)
		{
			node.next = null;
			this.head = node;
			this.last = node;
		}
		else
		{
			node.next = this.head;
			this.head.previous = node;
			this.head = node;
		}
		size ++;
	}
	
	public void addLast(Node<K> node)
	{
		node.list = this;
		node.next = null;
		if(this.last == null)
		{
			node.previous = null;
			this.head = node;
			this.last = node;
		}
		else
		{
			node.previous = this.last;
			this.last.next = node;
			this.last= node;
		}
		size ++;
	}

	public int getSize()
	{
		return size;
	}
	
	
}
