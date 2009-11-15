package friends.util;

import java.io.File;

public class Util
{
	public static  void main(String[] args)
	{
		long l = 0x1234567890123456L;
		System.out.println(l);
		System.out.println(Util.bytesToLong (Util.LongToBytes(l)));
		
	}

	public static byte[] subArray(byte[] input,int start,int len)
	{
		byte[] output = new byte[len];
		for(int i = 0;i<len;i++)
		{
			output[i] = input[start+i];
		}
		return output;
	}
	
	public static String padToSize(String s,int size)
	{
		int padsize = size - s.length();
		if(padsize <= 0)
			return s;
		char[] c = new char[padsize];
		return s.concat(new String(c));
		
	}
	
	public static void createDir(String path)
	{
		File dir = new File(path);
		if(!dir.isDirectory())
			 dir.mkdirs();
	}
	
	
	//little endian bytes,number converstion
	public static long bytesToLong(byte[] bytes)
	{
		if(bytes.length > 8)
			throw new IllegalArgumentException();
		
		long ret = 0;
		for(int i = 0;i < bytes.length;i++)
		{
			ret |= ( bytes[i] & 0xFFL ) << (i * 8);
		}
		return ret;
	}
	
	public static int bytesToInt(byte[] bytes)
	{
		if(bytes.length > 4)
			throw new IllegalArgumentException();
		
		int ret = 0;
		for(int i = 0;i < bytes.length;i++)
		{
			ret |= ( bytes[i] & 0xFF ) << (i * 8);
		}
		return ret;
	}
	public static byte[] LongToBytes(long number)
	{
		int lowBits = (int)number;
		int highBits = (int)(number >> 32);
		return Util.concatByteArray(IntToBytes(lowBits), IntToBytes(highBits));
	}
	public static byte[] IntToBytes(int number)
	{
		short lowBits = (short)(number);
		short highBits = (short)(number >> 16);
		byte[] ret = new byte[4];
		ret[0] = (byte)lowBits;
		ret[1] = (byte)(lowBits>>8);
		ret[2] = (byte)highBits;
		ret[3] = (byte)(highBits>>8);
		return ret;
	}
	
	public static byte[] concatByteArray(byte[] input1, byte[] input2)
	{
		int len1 = input1.length;
		int len2 = input2.length;
		byte[] output = new byte[len1+len2];
		for(int i = 0;i< len1;i++)
		{
			output[i] = input1[i];
		}
		for(int i = 0;i< len2;i++)
		{
			output[len1 + i] = input2[i];
		}
		return output;
	}
}
