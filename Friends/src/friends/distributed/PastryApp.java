package friends.distributed;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;

import friends.common.RPC.VirtualRpcErrorMessage;
import friends.common.RPC.VirtualRpcRequest;
import friends.common.RPC.VirtualRpcResponse;
import rice.p2p.commonapi.Application;
import rice.p2p.commonapi.Endpoint;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;
import rice.p2p.commonapi.NodeHandle;
import rice.p2p.commonapi.RouteMessage;

public  class PastryApp implements Application,Runnable
{
	private DHTNode dhtNode = null;
	private Endpoint endpoint  = null;
	private String  endPointName  = null;
	private DHTRing ring;
	private NodeHandle bootNodeHandle;
	private PastryAppWrapper wrapper;
	private NodeIdInfo nodeIdInfo = null;
	protected boolean isShowingReceivedMessage = true;
	private static final int DELIVER_THREAD_NUMBER = 10;
	
	
	private class MessageTuple
	{
		Message message;
		Id id;
		
		public MessageTuple(Message message, Id id)
		{
			super();
			this.message = message;
			this.id = id;
		}
	}
	private LinkedBlockingQueue<MessageTuple> recvMessageQueue = new LinkedBlockingQueue<MessageTuple>();
	private Thread[] deliverThreads ;
	private boolean stop = false;
	
	public static int TIME_OUT = 3000;
	
	public PastryApp(PastryAppWrapper wrapper)
	{
		this.wrapper = wrapper;
		deliverThreads = new Thread[DELIVER_THREAD_NUMBER];
		for(int i = 0;i<DELIVER_THREAD_NUMBER;i++)
			deliverThreads[i]= new Thread(this);
	}
	
	
	private Hashtable<Long,Long> requestTable = new Hashtable<Long,Long>();
	private Hashtable<Long,VirtualRpcResponse> responseTable =
							new Hashtable<Long,VirtualRpcResponse>();
	
	
	public VirtualRpcResponse getResponse(Id idToSendto,VirtualRpcRequest m) throws RemoteException
	{
		return getResponse(null,idToSendto,m);
	}
	
	public VirtualRpcResponse getResponse(NodeHandle handle,VirtualRpcRequest m) throws RemoteException
	{
	
		return getResponse(handle,null,m);
	}
	
	private VirtualRpcResponse getResponse(NodeHandle handle,Id idToSendto,VirtualRpcRequest m) throws RemoteException
	{
		if(requestTable.containsKey(m.Id()))//repeat request
		{
			throw new RemoteException("Repeat request with same packet is prohibited");
		}
		this.requestTable.put(m.Id(),m.Id());
		
		if(handle != null)
			this.sendGenericMessage(handle, m);
		else if (idToSendto != null)
			this.sendGenericMessage(idToSendto, m);
		else
			throw new RemoteException("Destination can not be null");
		
		VirtualRpcResponse rm;
		
		rm =  responseTable.remove(m.Id());//deal with local response,which arrives immediately(before wait)
		if(rm == null)
		{
			synchronized (m.Id())
			{
				try
				{
					m.Id().wait(TIME_OUT);
				}
				catch (InterruptedException e)
				{
					throw new RemoteException("Thread Interrupted.");
				}
			}
			rm = responseTable.remove(m.Id());
		}
		
		if(rm == null)
		{
			throw new RemoteException("Request Timeout.");	
		}
		else if (rm instanceof VirtualRpcErrorMessage)
		{
			VirtualRpcErrorMessage em = (VirtualRpcErrorMessage)rm;
			throw new RemoteException(em.message == null ? "No further info.":em.message);
		}
		else
		{
			return rm;
		}
	}
	
	public void setNodeIdInfo(NodeIdInfo info)
	{
		this.nodeIdInfo = info;
	}
	
	public void joinNetwork(String host, int bootStrapPort,int localPort,String endPointName)
	{
		for(int i = 0;i<DELIVER_THREAD_NUMBER;i++)
			deliverThreads[i].start();
		ring = new DHTRing(host,bootStrapPort,localPort);
		bootNodeHandle = ring.getBootHandle();
		
		dhtNode = new DHTNode(ring,this.nodeIdInfo);
		if(bootNodeHandle == null)
		{
			bootNodeHandle = dhtNode.getPastryNodeHandle();
		}
		attachNode(dhtNode,endPointName);
	}
	protected void attachNode(DHTNode dhtNode, String endPointName)
	{
		this.dhtNode = dhtNode;
		this.endPointName = endPointName;
		this.endpoint = dhtNode.buildPastryEndpoint(this,endPointName);
		this.endpoint.register();
	}
	public DHTRing getRing()
	{
		return ring;
	}

	
	public void sendMessage(Id idToSendTo, Message message)
	{
		endpoint.route(idToSendTo, message, null);
	}
	public void sendMessage(NodeHandle handle,Message message)
	{
		endpoint.route(null, message, handle);
	}
	public void sendObject(Id idToSendTo,Serializable object)
	{
		ObjectMessage m = new ObjectMessage();
		m.setContentObject(object);
		sendGenericMessage(idToSendTo,m);
	}
	public void sendObject(NodeHandle handle,Serializable object)
	{
		ObjectMessage m = new ObjectMessage();
		m.setContentObject(object);
		sendGenericMessage(handle,m);
	}
	
	public void sendGenericMessage(rice.p2p.commonapi.NodeHandle handle,GenericMessage m)
	{
		m.setFrom(this.getNode().getPastryNodeHandle());
		sendMessage(handle,m);
	}
	public void sendGenericMessage(Id idToSendTo,GenericMessage m)
	{
		m.setFrom(this.getNode().getPastryNodeHandle());
		sendMessage(idToSendTo,m);
	}

	public Endpoint getEndpoint()
	{
		return endpoint;
	}
	public String getEndPointName()
	{
		return endPointName;
	}
	public DHTNode getNode()
	{
		return dhtNode;
	}

	public boolean forward(RouteMessage message)
	{
		return true;
	}

	public NodeHandle getBootNodeHandle()
	{
		return bootNodeHandle;
	}

	public void update(NodeHandle handle, boolean joined)
	{
		
	}

	public void deliver(Id id, Message message)
	{
		try
		{
			recvMessageQueue.put(new MessageTuple(message,id));
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
	}

	/**
	 * 
	 * @param message UniqueMessageResponse to process
	 * @return true if corresponding UniqueMessage is found, false otherwise
	 */
	private boolean processVirtualRpcResponse(VirtualRpcResponse message)
	{
		VirtualRpcResponse m = (VirtualRpcResponse)message;
		Long localRequestId = requestTable.remove( m.getRequestId());
		
		if(localRequestId!= null)
		{
			responseTable.put(localRequestId, m);
			synchronized (localRequestId)
			{
				localRequestId.notify();
			}				
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void close()
	{
		stop = true;
		for(int i = 0;i<DELIVER_THREAD_NUMBER;i++)
			deliverThreads[i].interrupt();
		this.getNode().destroy();
		this.getRing().close();
	}

	private void processDeliveredMessage(Id id, Message message)
	{
		if(isShowingReceivedMessage && message instanceof GenericMessage)
		{
			((GenericMessage)message).printReceiveInfo();
		}

		if(message instanceof VirtualRpcResponse)
		{
//			let the wrapper deal with it if corresponding UniqueMessage is not found
			if( processVirtualRpcResponse((VirtualRpcResponse)message))
			{
				return;
			}
		}
		try
		{
			wrapper.process(id, message);
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			if(message instanceof VirtualRpcRequest)
			{
				VirtualRpcRequest m = (VirtualRpcRequest)message;
				VirtualRpcErrorMessage em = new VirtualRpcErrorMessage(m);
				em.message = e.getMessage();
				sendGenericMessage(m.getOriginHandle(), em);
			}
		}
	}
	 
	public void run()
	{
		while(!stop)
		{
			try
			{
				MessageTuple tuple = recvMessageQueue.take();
				processDeliveredMessage(tuple.id,tuple.message);
			}
			catch (InterruptedException e)
			{
				continue;
			}
			
		}
		
	}
}

