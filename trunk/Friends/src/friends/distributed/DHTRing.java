package friends.distributed;

import rice.pastry.NodeHandle;
import java.net.InetSocketAddress;

public class DHTRing
{
	private InetSocketAddress bootaddress;
	private NodeFactory nodeFactory;
	public DHTRing(String bootStrapNodeIP,int bootStrapNodePort,int pastryPort)
	{
		this.bootaddress = new InetSocketAddress(bootStrapNodeIP, bootStrapNodePort);
		this.nodeFactory  = new NodeFactory(pastryPort,bootaddress);
	}
	public InetSocketAddress getBootaddress()
	{
		return bootaddress;
	}
	public NodeFactory getNodeFactory()
	{
		return nodeFactory;
	}
	
	public void close()
	{
		if(nodeFactory!= null)
		{
			nodeFactory.env.destroy();
			nodeFactory = null;
		}

	}
	protected void finalize() throws Throwable
	{
		if(nodeFactory != null)
			close();
		super.finalize();
	}
	public NodeHandle getBootHandle()
	{
		return nodeFactory.getBootHandle();
	}
}
