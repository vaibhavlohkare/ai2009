package friends.distributed;
import rice.pastry.*;
public class ConstantNodeIdFactory implements NodeIdFactory
{
	private static final long intRange= 4294967295L;
	
	private int position;
	private int totalNodes;
	
	public Id generateNodeId()
	{
		return ConstantNodeIdFactory.generateNodeId(this.totalNodes,this.position);
	}

	public void setId(int totalNodes,int position)
	{
		if(position >= totalNodes)
			throw new IllegalArgumentException("position must be smaller than total nodes");
		this.position = position;
		this.totalNodes = totalNodes;
	}
	
	public static Id generateNodeId(int totalNodes,int position)
	{
		int[] material = new int[5];
		for(int i = 0;i< 5;i++)
			material[i] = (int)((intRange/totalNodes) * ( position ));
		return Id.build(material);
	}
	
}
