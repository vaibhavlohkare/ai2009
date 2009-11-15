package friends.common.RPC;

import friends.distributed.GenericMessage;

public abstract class VirtualRpcResponse extends GenericMessage
{

	private static final long serialVersionUID = 8925022806433486843L;
	
	private Long requestId;
	
	
	public Long getRequestId()
	{
		return requestId;
	}
	public void setRequestId(Long requestId)
	{
		this.requestId = requestId;
	}
}
