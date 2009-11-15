package friends.util;

import java.io.Serializable;
import java.util.TreeSet;
public class Packet implements Serializable
{
	private static final long serialVersionUID = 7661796280674660916L;
	public long docID;
	public String encode;
	public String url;
	public TreeSet<String> urlList;
	public byte[] page;
	
	public String toString()
	{
		return "Packet: " + docID +" : "+url;
	}

}
