package friends.util;
import rice.p2p.commonapi.*;

public class RepositoryInfo
{
	protected byte id;
	protected long initSize;
	protected NodeHandle handle;
	/*
	public int compareTo(Object o)
	{
		if(o instanceof RepositoryInfo)
		{
			RepositoryInfo info = (RepositoryInfo)o;
			return ( (Byte)id).compareTo( ((Byte)info.id) );
		}
		else
		{
			return this.toString().compareTo(o.toString());
		}
	};*/
}
