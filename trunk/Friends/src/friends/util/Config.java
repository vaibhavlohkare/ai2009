package friends.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class Config
{
	private LinkedHashMap<String,String> table = new LinkedHashMap<String,String>();
	private String path;
	private File file; 
	
	public String get(String key)
	{
		return table.get(key);
	}
	public Config(String path)
	{
		this.path = path;
		this.file = new File(path);
		if(file.exists())
		{
			
			FileInputStream fi = null;
			try
			{
				fi = new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(fi);
				BufferedReader reader = new BufferedReader(inputStreamReader);
				while(true)
				{
					String line = reader.readLine();
					if(line == null)
						break;
					int index = line.indexOf('=');
					if(index <= 0)
						break;
					String key = line.substring(0,index).trim();
					String value = line.substring(index+1).trim();
					this.table.put(key, value);
				}
			}
			catch(Exception e)
			{
				System.err.println(e);
			}
			finally
			{
				try
				{
					if(fi!= null)
						fi.close();
				}
				catch (IOException e)
				{
					
				}
			}
		}
	}
	
	public void put(String key ,String value)
	{
		this.table.put(key, value);
	}
	
	public void clear()
	{
		this.table.clear();
	}
	
	public void save()
	{
		PrintWriter pw = null;
		OutputStreamWriter osWriter = null;
		FileOutputStream fo = null;
		try
		{
			if(!file.exists())
				file.createNewFile();
			fo = new FileOutputStream(path);
			osWriter = new OutputStreamWriter(fo);
			pw = new PrintWriter (osWriter);
			
			for(Entry<String,String> entry:table.entrySet())
			{
				String line = entry.getKey() + "=" + entry.getValue();
				pw.println(line);
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(pw != null)
					pw.close();
				if(osWriter!= null)
					osWriter.close();
				if(fo!=null)
					fo.close();
			}
			catch (IOException e)
			{
				
			}
		}
	}
}
