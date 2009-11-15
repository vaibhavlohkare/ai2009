package friends.distributed;

import rice.p2p.commonapi.*;
import rice.pastry.PastryNode;
public class DHTNode
{
	private DHTRing ring;
	private Node node;
	
	public DHTNode(DHTRing ring,NodeIdInfo info)
	{
		this.ring = ring;
		this.node = ring.getNodeFactory().getNode(info);
		System.out.println("Node Created: " + node.getLocalNodeHandle());
	}
	public DHTNode(DHTRing ring)
	{
		this(ring , null);
	}
	
	public Endpoint buildPastryEndpoint(Application application,String instance)
	{
		return node.buildEndpoint(application, instance);
	}
	public Id getPastryNodeID()
	{
		return node.getId();
	}
	public DHTRing getRing()
	{
		return ring;
	}
	public NodeHandle getPastryNodeHandle()
	{
		return node.getLocalNodeHandle();
	}
	public void destroy()
	{
		if(node != null)
		{
			((PastryNode)node).destroy();
			node = null;
		}
	}
	protected void finalize()throws Throwable
	{
		if(node != null)
			destroy();
		super.finalize();
	}
}
