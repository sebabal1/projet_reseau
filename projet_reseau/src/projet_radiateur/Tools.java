package projet_radiateur;

public class Tools{
	public static byte[] intToBytes(int i)
	{
	  byte[] result = new byte[4];

	  result[0] = (byte) (i >> 24);
	  result[1] = (byte) (i >> 16);
	  result[2] = (byte) (i >> 8);
	  result[3] = (byte) (i /*>> 0*/);

	  if(result[0] < 0)
	  	result[0] += 256;
	  if(result[1] < 0)
	  	result[1] += 256;
	  if(result[2] < 0)
	  	result[2] += 256;
	  if(result[3] < 0)
	  	result[3] += 256;

	  return result;
	}

	public static int bytesToInt(byte[] b) {
	    if (b.length == 4)
	      return b[0] << 24 | (b[1] & 0xff) << 16 | (b[2] & 0xff) << 8
	          | (b[3] & 0xff);
	    else if (b.length == 2)
	      return 0x00 << 24 | 0x00 << 16 | (b[0] & 0xff) << 8 | (b[1] & 0xff);

	    return 0;
	}
}