package friends.common;
import friends.distributed.*;
import friends.util.Packet;


public class StorePeerAppendRequestMessage extends GenericMessage
{
	private static final long serialVersionUID = 891744832041661163L;
	public byte repositoryId;
	public Packet packet;
	public StorePeerAppendRequestMessage(byte repositoryId,Packet packet)
	{
		this.repositoryId = repositoryId;
		this.packet = packet;
	}
	public StorePeerAppendRequestMessage() {}
	
	public String toString()
	{
		return packet.toString();
	}
}
