package friends.distributed;
import java.rmi.RemoteException;

import friends.common.RPC.VirtualRpcRequest;
import friends.common.RPC.VirtualRpcResponse;
import rice.p2p.commonapi.*;

public  abstract class PastryAppWrapper 
{
	protected abstract void process(Id id, Message message) throws Exception;
	private PastryApp app;
	public PastryAppWrapper()
	{
		this.app = new PastryApp(this);
	}
	public PastryApp getPastryApp()
	{
		return app;
	}
	public void joinNetwork(String host, int bootStrapPort,int localPort, String endPointName)
	{
		app.joinNetwork(host, bootStrapPort, localPort, endPointName);
	}
	
	public void setNodeIdInfo(int total ,int position)
	{
		app.setNodeIdInfo(new NodeIdInfo(total,position));
	}
	public void sendGenericMessage(rice.p2p.commonapi.NodeHandle handle,GenericMessage m)
	{
		app.sendGenericMessage(handle, m);
	}
	public void sendGenericMessage(Id idToSendTo,GenericMessage m)
	{
		app.sendGenericMessage(idToSendTo, m);
	}
	public NodeHandle getBootNodeHandle()
	{
		return app.getBootNodeHandle();
	}
	public VirtualRpcResponse getResponse(Id idToSendto, VirtualRpcRequest m) throws RemoteException
	{
		return app.getResponse(idToSendto, m);
	}
	public VirtualRpcResponse getResponse(NodeHandle handle, VirtualRpcRequest m) throws RemoteException
	{
		return app.getResponse(handle, m);
	}
	
	public void close()
	{
		this.getPastryApp().close();
		
	}
	
	public void showMessageInfo()
	{
		this.app.isShowingReceivedMessage = true;
	}
	public void hideMessageInfo()
	{
		this.app.isShowingReceivedMessage = false;
	}
	public void sendMessage(Id idToSendTo, Message message)
	{
		app.sendMessage(idToSendTo, message);
	}
	public void sendMessage(NodeHandle handle, Message message)
	{
		app.sendMessage(handle, message);
	}
	
	
	
	
}

