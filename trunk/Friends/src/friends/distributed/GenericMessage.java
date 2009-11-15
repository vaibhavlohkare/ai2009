package friends.distributed;

import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.NodeHandle;

public  abstract class GenericMessage implements rice.p2p.commonapi.Message, java.io.Serializable
{
	private NodeHandle from = null;

	public void setFrom(NodeHandle from)
	{
		this.from = from;
	}
	
	public NodeHandle getOriginHandle()
	{
		return from;
	}

	public int getPriority() 
	{
		return Message.MEDIUM_PRIORITY;
	}
	
	public void printReceiveInfo()
	{
	//	System.out.println("Receiving From "+this.from +"\n"+getClass().getSimpleName()   + ":( "  + this.toString()+" )");
		System.out.println("Receiving "+getClass().getSimpleName()   + ":( "  + this.toString()+" )");
	}
}