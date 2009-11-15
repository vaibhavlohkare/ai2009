package friends.distributed;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import rice.p2p.commonapi.Node;
import rice.environment.Environment;
import rice.pastry.NodeHandle;
import rice.pastry.Id;
import rice.pastry.NodeIdFactory;
import rice.pastry.PastryNode;
import rice.pastry.socket.SocketPastryNodeFactory;
import rice.pastry.standard.RandomNodeIdFactory;
import java.security.*;

/**
 * A simple class for creating multiple Pastry nodes in the same
 * ring
 * 
 * 
 * @author Nick Taylor
 *
 */
public class NodeFactory 
{
	
	public static void main(String[] args)
	{
		try
		{
			NodeFactory f = new NodeFactory(12345);
			f.env.destroy();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
	Environment env;
	NodeIdFactory nidFactory;
	ConstantNodeIdFactory constFactory;
	SocketPastryNodeFactory factory;
	SocketPastryNodeFactory factoryForConstNode;
	NodeHandle bootHandle;
	int createdCount = 0;
	int port;
	
	NodeFactory(int port)
	{
		this(new Environment(), port);
	}	
	
	NodeFactory(int port, InetSocketAddress bootPort) 
	{
		this(port);
		bootHandle = factory.getNodeHandle(bootPort);
	}
	
	NodeFactory(Environment env, int port) 
	{
		this.env = env;
		this.port = port;
		nidFactory = new RandomNodeIdFactory(env);	
		constFactory = new ConstantNodeIdFactory();
		try 
		{
			factory = new SocketPastryNodeFactory(nidFactory, port, env);
			factoryForConstNode = new SocketPastryNodeFactory(constFactory, port, env);
		} 
		catch (java.io.IOException ioe) 
		{
			throw new RuntimeException(ioe.getMessage(), ioe);
		}
		
	}
	
	
	public Node getNode()
	{
		return getNode(null);
	}
	
	public Node getNode(NodeIdInfo info) 
	{
		try 
		{
			synchronized (this) 
			{
				if (bootHandle == null && createdCount > 0) 
				{
					InetAddress localhost = InetAddress.getLocalHost();
					InetSocketAddress bootaddress = new InetSocketAddress(localhost, port);
					bootHandle = factory.getNodeHandle(bootaddress);
				}
			}
			
			PastryNode node;
			if(info !=null)
			{
				constFactory.setId(info.total, info.position);
				node = factoryForConstNode.newNode(bootHandle);
			}
			else
			{
				node =  factory.newNode(bootHandle);
			}

			while (! node.isReady()) 
			{
				Thread.sleep(50);
			}
			synchronized (this) 
			{
				++createdCount;
			}
			return node;
		} catch (Exception e) 
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public void shutdownNode(Node n) 
	{
		((PastryNode) n).destroy();
		
	}
	
	public static Id getIdFromString(String s)
	{	 
		 try 
		 {
			 MessageDigest md = MessageDigest.getInstance("SHA-1");
		     md.update(s.getBytes("UTF-8"));
		     byte[] bytes = md.digest();
		     return getIdFromBytes(bytes);
		 } 
		 catch (Exception e)
		 {
			 e.printStackTrace();
		 }
		 return  null;

	}
	public static Id getIdFromBytes(byte[] material) 
	{
		return Id.build(material);
	}

	public NodeHandle getBootHandle()
	{
		return bootHandle;
	}
}
