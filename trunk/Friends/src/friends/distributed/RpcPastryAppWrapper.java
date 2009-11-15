package friends.distributed;

import friends.common.RPC.VirtualRpcRequest;
import friends.common.RPC.VirtualRpcResponse;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;

public abstract class RpcPastryAppWrapper extends PastryAppWrapper
{

	protected void process(Id id, Message message) throws Exception
	{
		if(message instanceof VirtualRpcRequest)
		{
			VirtualRpcRequest m = (VirtualRpcRequest)message;
			VirtualRpcResponse rm = processRpcRequestMessage(id,m);
			if(rm != null)
			{
				rm.setRequestId( m.Id());
				sendGenericMessage(m.getOriginHandle(), rm);
			}
		}
		else
		{
			processNonRpcMessage(id, message);
		}
	}
	
	public abstract void processNonRpcMessage(Id id, Message message)throws Exception;
	public abstract VirtualRpcResponse processRpcRequestMessage(Id id, VirtualRpcRequest message)throws Exception;

}
