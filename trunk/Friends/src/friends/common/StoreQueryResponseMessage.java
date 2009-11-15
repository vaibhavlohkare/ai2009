package friends.common;
import friends.common.RPC.VirtualRpcResponse;
import rice.p2p.commonapi.NodeHandle;

public class StoreQueryResponseMessage extends VirtualRpcResponse
{

	private static final long serialVersionUID = -2379577618933318704L;
	public String url;
	public long docID;
	public boolean isExist;
	public NodeHandle storePeerHandle;
	public byte repositoryID;

	public String toString()
	{
		return (isExist?"Old":"New")+" Doc: "+ docID +" URL: "+url +" -> Repository: " + repositoryID;
	}
}
