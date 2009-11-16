package friends.crawler;
import java.io.*;

public class FetchedDoc 
{
	String urlString;
	String encoding;
	ByteArrayInputStream memoryStream;
	public String getUrlString() {
		return urlString;
	}

	public void setUrlString(String urlString) {
		this.urlString = urlString;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public ByteArrayInputStream getMemoryStream() {
		return memoryStream;
	}

	public void setMemoryStream(ByteArrayInputStream memoryStream) {
		this.memoryStream = memoryStream;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	byte[] data;
	FetchedDoc(String urlString,String encoding, byte[] data)
	{
		this.memoryStream = new ByteArrayInputStream(data);
		this.data = data;
		this.urlString = urlString;
		this.encoding = encoding;
	}
	
	protected void finalize()
	{
		try
		{
			data =null;
			memoryStream.close();
		}
		catch(Exception e)
		{
			
		}
	}
}
