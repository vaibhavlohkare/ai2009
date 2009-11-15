package friends.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.Serializable;

public class SHA1 implements Serializable
{
	private static final long serialVersionUID = -1921388664039552812L;
	private static MessageDigest md ;
	static
	{
		try
		{
			md = MessageDigest.getInstance("SHA-1");
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private byte[] digest;
	public SHA1 (String s)
	{
		try
		{
			byte[] data = s.getBytes("UTF-8");
			md.reset();
			md.update(data);
			this.digest = md.digest();
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

	}
	public SHA1(byte[] data)
	{
		md.reset();
		md.update(data);
		this.digest = md.digest();
	}
	public byte[] getDigest()
	{
		return digest;
	}

	public int hashCode()
	{
		return this.toString().hashCode();
	}
	
	public boolean equals(Object obj)
	{
		if(obj instanceof SHA1)
		{
			SHA1 s= (SHA1)obj;
			return this.toString().equals(s.toString());
		}
		else
		{
			return false;
		}
	}
	

	public String toString()
	{
		return new String(this.digest);
	}
}
