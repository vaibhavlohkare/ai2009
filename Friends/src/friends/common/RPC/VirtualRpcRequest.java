package friends.common.RPC;

import friends.distributed.GenericMessage;

public abstract class VirtualRpcRequest extends GenericMessage
{

	private static final long serialVersionUID = 892508806433486843L;
	private static long nextId;
	private Long Id;

	private synchronized static long getNextRequestId()
	{
		return ++nextId;
	}
	public VirtualRpcRequest()
	{
		super();
		this.Id = VirtualRpcRequest.getNextRequestId();
	}
	public Long Id()
	{
		return this.Id;
	}
	/*
	public void reGenerateId()
	{
		this.Id = UniqueMessage.getNextRequestId();
	}*/
}
