package friends.distributed;

import java.io.Serializable;

public  class ObjectMessage extends GenericMessage
{
	private static final long serialVersionUID = 1618258580015448970L;
	private Serializable object = null;


	public void setContentObject(Serializable object)
	{
		this.object = object;
	}

	public Serializable getContentObject()
	{
		return object;
	}
	
	public String toString()
	{
		
		return (object == null)?"null":object.toString();
	}
}