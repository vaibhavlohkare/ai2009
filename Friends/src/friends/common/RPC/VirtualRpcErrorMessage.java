package friends.common.RPC;

public class VirtualRpcErrorMessage extends VirtualRpcResponse
{

	private static final long serialVersionUID = 7245566788737598753L;
	public String message = null;
	public VirtualRpcErrorMessage (VirtualRpcRequest m,String message)
	{
		setRequestId(m.Id());
		this.message = message;
	}
	public VirtualRpcErrorMessage(VirtualRpcRequest m)
	{
		setRequestId(m.Id());
		this.message = "No more info";
	}
	public String getMessage()
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}
	public String toString()
	{
		return this.message;
	}
}
