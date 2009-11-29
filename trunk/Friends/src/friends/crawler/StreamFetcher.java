package friends.crawler;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;



public class StreamFetcher 
{
	private static int defaultMaxLineLength = 4096;
	private InputStream inputStream;
	private OutputStream outputStream;
	private DataInputStream dataInputStream;
	private String redirectLocation;
	private String urlString; 
	private URL url;
	private String contentType;
	private int contentLength;
	private String encoding;
	private CrawlerWorker worker;
	private boolean chunked = false;
	public static int MAX_PAGE_SIZE = 100000;
	
	
	public StreamFetcher(CrawlerWorker worker)
	{
		super();
		this.worker = worker;
	}
	private void ProcessHeader() throws Exception
	{
		
		String firstLine = ReadLine();
		if(firstLine != null)
		{
			String firstLineElements[] = firstLine.split("( |\t)+");
//
//			try
//			{
				int code = Integer.valueOf(firstLineElements[1]);

				if(code >= 300 && code <400)//redirect
				{
					ParseHeaderLines();
					if(redirectLocation != null)
					{
						Crawler.urlFilter.Process(AbsUrlConstructor.construct(urlString, redirectLocation));
						throw new Exception(code + " redirect");
					}
				}
				else if(code >= 100 && code < 300)//success
				{
					ParseHeaderLines();
				}
//			}
//			catch(Exception e)
//			{
//				if(!firstLine.toLowerCase().contains("bad request"))
//				{
//					throw e;
//				}
//			}
			
		}
	}

	public FetchedDoc Fetch(String urlString1)
	{
		redirectLocation = null;
		urlString = urlString1;
		url = null;
		contentType = null;
		contentLength = -1;
		encoding = "ISO-8859-1";
		Socket socket = null;
		chunked = false;
		
		try
		{
			urlString = urlString1;
			url = new URL(urlString);
			String host = url.getHost();
			int port = (url.getPort()==-1?80:url.getPort());
			
			
			if(!url.getProtocol().startsWith("http"))//check the protocal
					return null;

			InetSocketAddress localSocketAddr = new InetSocketAddress(33000+worker.ThreadID*10 + worker.rotatingCount	);
			
			InetSocketAddress remoteSocketAddress = null;
			try
			{
				remoteSocketAddress = new InetSocketAddress(host,port);
			}
			catch(Exception e)
			{
				
				return null;
			}
			
			
			
			socket = new Socket();
			socket.setSoLinger(true, 0);
			socket.setReuseAddress(true);
			socket.setSoTimeout(2000);
			socket.bind(localSocketAddr);
			socket.connect(remoteSocketAddress);
			outputStream = socket.getOutputStream();
			inputStream = socket.getInputStream();
			dataInputStream = new DataInputStream(inputStream);

			
			String query = url.getQuery()==null?"":"?"+url.getQuery();
			String pathAndQuery = url.getPath()+query;
			byte[] bytes= (new String("GET "+pathAndQuery+" HTTP/1.1\r\n"+"Host:"+url.getHost()+"\r\n\r\n")).getBytes("UTF-8");
			outputStream.write(bytes);
			outputStream.flush();
			ProcessHeader();
			
//			if(Crawler.showLog)
//			{
//				System.out.println(this.contentType +" : "+this.contentLength);
//			}
			if(contentType== null ||!contentType.equalsIgnoreCase("text/html"))
			{
				socket.close();
				return null;
			}
			else
			{
				byte[] body;
				//if(contentLength > MAX_PAGE_SIZE)
			//	{
				//	return null;
				//}
			  if(contentLength > 0)
				{
					body = new byte[contentLength];
					 dataInputStream.readFully(body);
					 
				}
				else if(chunked)
				{
					body = getChunkedBody();
				}
				else
				{
					return null;
					//body = getBody();
				}
				
				if(body == null)
					return null;

				
				Crawler.statistics.increaseVolume(body.length);
				if(!Crawler.shutDown && Crawler.maxVolume >0 & Crawler.statistics.getVolumeInBytes()>Crawler.maxVolume)
				{
					System.out.println("Maximum Volume Reached: "+Crawler.statistics.getVolumeInBytes());
					Crawler.ShutDown();
					return null;
				}
				else
				{
					return new FetchedDoc(urlString1,encoding,body);
				}
			}
		}
		catch(SocketTimeoutException ste)
		{
			return null;
		}
		catch(Exception e)
		{
			System.out.println("BLAH!");
			return null;
		}
		finally
		{
			
			if(socket != null)
			{
				try
				{
					socket.close();	
					
				}
				catch(Exception e)
				{
					
				}
				socket = null;
			}
			
			
		}
		
	}
	
	private void ProcessHeaderEntry(String key,String value)
	{
		if (key.compareToIgnoreCase("Location") == 0)
		{
			redirectLocation  = value;
		}
		else if(key.compareToIgnoreCase("Content-Type") == 0 )
		{
			String type = value.trim();
			int index = type.indexOf(';');
			if(index != -1)
			{
				contentType = type.substring(0,index).trim().toLowerCase();
				String[] strings = type.substring(index + 1).split("=");
				if(strings.length == 2 && strings[0].trim().equalsIgnoreCase("charset"))
				{
					strings[1].replace(';', ' ');
					encoding = strings[1].trim().toUpperCase().replaceAll("\"|;", "");;
					
				}
			}
			else
			{
				contentType = type.toLowerCase();
			}
		}
		else if(key.compareToIgnoreCase("Content-Length") == 0)
		{
			try
			{
				contentLength = Integer.parseInt(value);
			}
			catch(Exception e)
			{
				//tolerate it
				return;
			}
		}
		else if(key.compareToIgnoreCase("Transfer-Encoding") == 0)
		{
			if(value.trim().equalsIgnoreCase("chunked"))
			{
				this.chunked = true;
			}
		}
	}
	private void ParseHeaderLines() throws IOException
	{
		
		int index;
		String key = null;
		String value = null;
		boolean seeNewline = false;
		String s;
		
		while(!seeNewline)
		{
			s = ReadLine();
			if(s == null || s.equals(""))
			{
				seeNewline = true;
				if(key != null)
					ProcessHeaderEntry(key,value);
			}
			else if(s.indexOf(':') == -1) //continuing value of previous header
			{
				if(value != null)
					value += s.trim();
			}
			else
			{
				if(key != null)
				{
					ProcessHeaderEntry(key,value);
					key = null;
					value = null;
				}
				index = s.indexOf(':');
			    if (index > 0)
			    {		    	
			    	key = s.substring(0,index).trim();
				   	value = s.substring(index + 1).trim();
			    }
			    else //bad header,let's tolerate it
			    {
			    	key = null;
			    	value = null;
			    }
			}	
		}
	}
	
	private byte[] getChunkedBody()throws IOException
	{
		
		int len;
		
		ByteArrayOutputStream os = null;
		
		os = new ByteArrayOutputStream();
		
		int totalLen = 0;
		while(totalLen < MAX_PAGE_SIZE)
		{
			String s = ReadLine();
			int index = s.indexOf(';');
			if(index == -1)
			{
				len = Integer.parseInt(s.trim(), 16);
			}
			else
			{
				len = Integer.parseInt(s.substring(0,index).trim(),16);
			}
			if(len == 0)
				return os.toByteArray();
			
			byte[] buffer = new byte[len];
			this.dataInputStream.readFully(buffer);
			os.write(buffer);
			totalLen += buffer.length;
			ReadLine();
		}
		return null;
		
		
	}
	private byte[] getBody() throws IOException, InterruptedException
	{
		byte[] buffer = new byte[20000];
		int ret;
		int count = 0;
		ByteArrayOutputStream os = null;
		try
		{
			os = new ByteArrayOutputStream();
			while(true)
			{	
				if(inputStream.available() <= 0)
				{
					if(count >= 10)//1sec
					{
						return os.toByteArray();
					}
					count ++;
					Thread.sleep(100);
					continue;
				}
				ret = inputStream.read(buffer);
				
				if(ret > 0)
				{
					os.write(buffer, 0, ret);
				}
				else if(ret == -1)
				{
					return os.toByteArray();
				}
			}
		}
		finally
		{
			if(os != null)
			{
				try
				{
					os.close();
				}
				catch (Exception e)
				{
					
				}
			}
		}
		
		
		
	}
	private String ReadLine()throws IOException
	{
		return ReadLine(StreamFetcher.defaultMaxLineLength);
	}
	private String ReadLine(int max) throws IOException
	{
		StringBuffer sb = new StringBuffer(32);
		byte[] b = new byte[1];
		int count = 0;
		try
		{
			char c;
			int ret;
			while(count < max)
			{
				 ret = inputStream.read(b,0,1);
				 if(ret == -1)
				 {
					break;
				 }
				 count ++;
				 c = (char)b[0];
				if(c ==  '\r')
					continue;
				else if (c == '\n')
				{
					break;
				}
				else
				{
					sb.append(c);
				}	
			}

			return sb.toString();

		}
		catch(IOException ioe)
		{
			throw ioe;
		}
	}
}
