package friends.common;
import friends.distributed.*;


public class StorePeerAppendNotificationMessage extends GenericMessage
{
	private static final long serialVersionUID = -7971511377477144669L;
	public byte repositoryId;
	public long docID;
	public long pointer;

	public StorePeerAppendNotificationMessage(byte repositoryId, long docID, long pointer)
	{
		super();
		this.repositoryId = repositoryId;
		this.docID = docID;
		this.pointer = pointer;
	}

	public StorePeerAppendNotificationMessage() {};
	
	public String toString()
	{
		return "RepositoryID: "+this.repositoryId + " docID: " + this.docID + " Pointer: " +pointer;
	}

}
