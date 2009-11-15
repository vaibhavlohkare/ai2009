package friends.common;
import friends.common.RPC.VirtualRpcRequest;
import friends.util.SHA1;

public class StoreQueryMessage extends VirtualRpcRequest
{

	private static final long serialVersionUID = 6155097323293771282L;
	public SHA1 pageHash;
	public String url;
	public StoreQueryMessage(String url, SHA1 pageHash)
	{
		this.pageHash = pageHash;
		this.url = url;
	}
	
	public StoreQueryMessage() {};
	
	public String toString()
	{
		return "URL: "+url+" Hash: "+pageHash.toString();
	}

}
