package friends.crawler;

import java.rmi.RemoteException;
import java.util.ArrayList;
import myGoogle.Common.Packet;
import myGoogle.Common.SHA1;
import myGoogle.Common.Message.StorePeerAppendRequestMessage;
import myGoogle.Common.Message.StoreQueryMessage;
import myGoogle.Common.Message.StoreQueryResponseMessage;
import myGoogle.Distributed.PastryAppWrapper;
import myGoogle.StoreCoordinator.RepositoryInfo;
import rice.p2p.commonapi.Id;
import rice.p2p.commonapi.Message;

public class StoreChannel extends PastryAppWrapper
{
	public static void main(String[] args)
	{
//		try
//		{
//			StoreChannel crawler = new StoreChannel();
//			crawler.joinNetwork("ChenXiao", 10000, 15000, "store");
//			Packet packet = new Packet();
//			packet.encode = "UTF-8";
//			packet.page = new byte[50];
//			packet.url = "www.myGoogle.com";
//			crawler.storePacket(packet);
//		}
//		catch (RemoteException e)
//		{
//			e.printStackTrace();
//		}
	}
	
	protected RepositoryInfo[] repositoryInfoVector= new RepositoryInfo[256];
	protected ArrayList<RepositoryInfo> repositoryInfoList= new ArrayList<RepositoryInfo>(10);

	protected StoreQueryResponseMessage storeDocument(FetchedDoc doc) throws RemoteException
	{
		StoreQueryMessage m = new StoreQueryMessage();
		m.pageHash = new SHA1(doc.data);
		m.url = doc.urlString;
		
		StoreQueryResponseMessage rm = (StoreQueryResponseMessage)getResponse(getBootNodeHandle(), m);
		
		if(!rm.isExist)
		{
			Packet packet = new Packet();
			packet.docID = rm.docID;
			packet.encode = doc.encoding;
			packet.page = doc.data;
			packet.url = doc.urlString;
			
			StorePeerAppendRequestMessage am = new StorePeerAppendRequestMessage();
			am.packet = packet;
			am.repositoryId = rm.repositoryID;
			sendGenericMessage(rm.storePeerHandle, am);	
		}
		return rm;
	}
	
	public void process(Id id, Message message)
	{
		System.out.println("Warning, unhandled " + message.getClass());
	}
}

