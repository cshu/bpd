package griddoor.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class UtilFuncs {

	public static char outputStreamSendStrCriterionNEscChar(OutputStream os, String str) throws IOException{
		StringBuilder likeSb = new StringBuilder();
		char escChar = SqliteUtil.makeBindValToFindStrWithLike(str,likeSb);
		outputStreamSendLen24AndStrUtf8(os, likeSb.toString());
		os.write(escChar);
		return escChar;
	}
	
	public static String getFirstLine(String str){
		int in = str.indexOf('\n');
		int ir = str.indexOf('\r');
		if(in!=-1){
			if(ir!=-1)
				return str.substring(0, Math.min(in, ir));
			else
				return str.substring(0, in);
		}else if(ir!=-1)
			return str.substring(0, ir);
		else
			return str;
	}

	public static File getFileFromReadablePath(String path) {
		return new File(path.startsWith("\"") && path.endsWith("\"") ? path.substring(1, path.length() - 1) : path);
	}
	
	/**
	 * @param str
	 * @return null if str is not valid, array if successfully parsed
	 */
	public static byte[] convCommaSeparatedStrToBytea(String str){
		int len = str.length();
		if(len==0)
			return new byte[0];
		int l=1;
		for(int i=0;i<len;i++){
			if(str.charAt(i)==',')
				l++;
		}
		byte[] valtoret = new byte[l];
		int sum;
		int i=0;
		for(l=0;;l++){
			char c = str.charAt(i);
			if(c<'0'||c>'9')
				return null;
			sum=c-'0';
			i++; if(i==len) {valtoret[l]=(byte)sum; return valtoret;}
			c = str.charAt(i);
			if(c==','){
				valtoret[l]=(byte)sum;
				i++; if(i==len) return null;
				continue;
			}else if(c<'0'||c>'9')
				return null;
			sum=sum*10+c-'0';
			i++; if(i==len) {valtoret[l]=(byte)sum; return valtoret;}
			c = str.charAt(i);
			if(c==','){
				valtoret[l]=(byte)sum;
				i++; if(i==len) return null;
				continue;
			}else if(c<'0'||c>'9')
				return null;
			sum=sum*10+c-'0';
			if(sum>255) return null;
			valtoret[l]=(byte)sum;
			i++; if(i==len) return valtoret;
			if(str.charAt(i)!=',') return null;
			i++; if(i==len)  return null;
		}
	}
	
	public static String convByteaToCommaSeparatedStr(byte[] bytes){
		StringBuilder sb = new StringBuilder();
		if(bytes.length>0){
			int i=0;
			do{
				sb.append(bytes[i] & 0xFF);
				i++;
				if(i>=bytes.length)
					return sb.toString();
				sb.append(',');
			}while(true);
		}
		return "";
	}
	
	public static long inputStreamRecv40BitTimeAsLong(InputStream im)throws IOException{
		byte[] bytes = inputStreamRecvBytea(im);
		return FortyBitSecTime.makeGregorianCalendar(bytes).getTimeInMillis();
	}
	
	public static long inputStreamRecvUnsignedAsLong(InputStream im)throws IOException{
		byte[] bytes = new byte[Long.BYTES];
		inputStreamRecvBytea(im, bytes);
		return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getLong(0);//? use BigDecimal Instead?
	}

	public static void inputStreamRecvBytea(InputStream im, byte[] bytes)throws IOException{
		inputStreamReadBytesUntil(inputStreamRecvUInt24(im), im, bytes, 0);
	}
	
	public static byte[] inputStreamRecvBytea(InputStream im)throws IOException{
		byte[] bytes = new byte[inputStreamRecvUInt24(im)];
		inputStreamReadBytesUntil(bytes.length, im, bytes, 0);
		return bytes;
	}
	
	
	
	public static void outputStreamSendLen24AndBytes(OutputStream os, byte[] byteBuf) throws IOException{
		if(byteBuf.length > 0xFFFFFF)
			throw new IOException("Byte array is too long :"+byteBuf.length);
		os.write(byteBuf.length);//note 24 high-order bits of b are ignored.
		os.write(byteBuf.length / 256);
		os.write(byteBuf.length / 65536);
		os.write(byteBuf);
	}

	public static void outputStreamSendLen24AndStrUtf8(OutputStream os, String str) throws IOException{
		byte[] byteBuf = str.getBytes(StandardCharsets.UTF_8);
		outputStreamSendLen24AndBytes(os, byteBuf);
	}
	
	public static String inputStreamRecvUtf8String(InputStream im)throws IOException{
		byte[] bytes = inputStreamRecvBytea(im);
		return new String(bytes, StandardCharsets.UTF_8);
	}
	
	public static int inputStreamRecvInt32(InputStream im)throws IOException{
		byte[] bytes = new byte[4];
		inputStreamReadBytesUntil(4, im, bytes, 0);
		return getIntFromLE4Bytes(bytes, 0);
	}
	
	public static int inputStreamRecvUInt24(InputStream im)throws IOException{
		int value = im.read();
		value+=im.read()*256;
		return value+=im.read()*65536;
	}
	
	public static int inputStreamRecvUInt16(InputStream im)throws IOException{
		int value = im.read();
		return value+im.read()*256;
	}
	
	public static long inputStreamRecvInt64(InputStream im)throws IOException{
		byte[] bytes = new byte[8];
		inputStreamReadBytesUntil(8, im, bytes, 0);
		return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getLong(0);//? better?
		//return GetLongFromLE8Bytes(bytes, 0);
	}
	
	public static void outputStreamSendAsUnsigned(OutputStream os, long l)throws IOException{
		byte[] bytes = new byte[Long.BYTES];
		int i = 0;
		do{
			bytes[i] = (byte)l;
			i++;
			l/=256;
		}while(l!=0);
		os.write(i);
		os.write(0);
		os.write(0);
		os.write(bytes, 0, i);
	}
	
	public static void outputStreamSendInt64(OutputStream os, long l)throws IOException{
		os.write(ByteBuffer.allocate(Long.BYTES).order(ByteOrder.LITTLE_ENDIAN).putLong(l).array());
	}
	public static void outputStreamSendInt32(OutputStream os, int l)throws IOException{
		os.write(ByteBuffer.allocate(Integer.BYTES).order(ByteOrder.LITTLE_ENDIAN).putInt(l).array());
	}
	
//	public static void OutputStreamSendLongSignifBytes(OutputStream os, long l) throws IOException{
//		byte[] dig = ByteBuffer.allocate(Long.BYTES).order(ByteOrder.LITTLE_ENDIAN).putLong(l).array();
//		int lenOfD = lastIndexOfNot(dig, (byte) 0) + 1;
//		if(lenOfD==0)
//			lenOfD=1;
//		os.write(lenOfD);
//		os.write(dig, 0, lenOfD);
//	}

	public static boolean isNullOrWhiteSpace(String value) {
	    if (value == null) {
	        return true;
	    }
	    for (int i = 0; i < value.length(); i++) {
	        if (!Character.isWhitespace(value.charAt(i))) {
	            return false;
	        }
	    }
	    return true;
	}
	
	
	public static int lastIndexOfNot(byte[] bytes, byte byt) {
		for (int i = bytes.length - 1; i >= 0; i--)
			if (bytes[i] != byt)
				return i;
		return -1;
	}
	
	/**
	 * When l is 0, it STILL writes a byte of ZERO!!
	 * @param os
	 * @param l
	 * @throws IOException
	 */
	public static void outputStreamSendLen16AndLongSignifBytes(OutputStream os, long l) throws IOException{
		byte[] dig = ByteBuffer.allocate(Long.BYTES).order(ByteOrder.LITTLE_ENDIAN).putLong(l).array();
		int lenOfD = lastIndexOfNot(dig, (byte) 0) + 1;
		if(lenOfD==0)
			lenOfD=1;
		os.write(lenOfD);
		os.write(0);
		os.write(dig, 0, lenOfD);
	}
	
	public static void outputStreamSendLen16AndStrUtf8(OutputStream os, String str) throws IOException{
		byte[] byteBuf = str.getBytes(StandardCharsets.UTF_8);
		os.write(byteBuf.length % 256);
		os.write(byteBuf.length / 256);
		os.write(byteBuf);
	}

	public static void inputStreamReadBytesUntil(int endInd, InputStream is, byte[] buf, int off) throws IOException {
		while (off < endInd)
			off += is.read(buf, off, endInd - off);
	}
	
	public static long getLongFromLEBytes(byte[] byt, int startInd){
		long value=0;
		for (int endInd = byt.length; startInd < endInd; startInd++)
			value += ((long) byt[startInd] & 0xffL) << (8 * startInd);
		return value;
	}
	
	public static int getIntFromLE4Bytes(byte[] byt, int startInd) {
		int value = 0;
		for (int endInd = startInd + 4; startInd < endInd; startInd++)
			value += ((int) byt[startInd] & 0xff) << (8 * startInd);
		return value;
	}

	public static long getLongFromLE8Bytes(byte[] byt, int startInd) {
		long value = 0;
		for (int endInd = startInd + 8; startInd < endInd; startInd++)
			value += ((long) byt[startInd] & 0xffL) << (8 * startInd);
		return value;
	}

	public static long GetLongFromBE8Bytes(byte[] byt, int startInd) {
		long value = 0;
		for (int endInd = startInd + 8; startInd < endInd; startInd++)
			value = (value << 8) + (byt[startInd] & 0xff);
		return value;
	}

	public static long ConvertWinFiletimeLossilyToMsSince1970(byte[] byt, int startInd) {
		return (getLongFromLE8Bytes(byt, startInd) - 0x19db1ded53e8000L) / 10000;
	}

	public static byte[] GetWinFileTime(String fullFilename) throws UnsupportedEncodingException {
		byte[] utf16leBytes = fullFilename.getBytes("UTF-16le");
		byte[] fullFilenameTerminated = new byte[utf16leBytes.length + 2];
		System.arraycopy(utf16leBytes, 0, fullFilenameTerminated, 0, utf16leBytes.length);
		return WinApiWrapper.GetFileTime(fullFilenameTerminated);
	}

	public static String bytesToHex(byte[] in) {
		final StringBuilder builder = new StringBuilder();
		for (byte b : in) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}

	public static byte[] hexToBytes(String in) {
		byte[] out = new byte[in.length() / 2];
		for (int i = 0; i < out.length; i++) {
			out[i] = (byte) (Integer.parseInt(in.substring(i * 2, i * 2 + 2), 16));
		}
		return out;
	}

	public static long pow(long base, int exponent) {
		if (exponent == 0)
			return 1L;
		return base * pow(base, exponent - 1);
	}

	/**
	 * Reinventing the wheel in case toString breaks. long is converted to a
	 * char 0-9 or a-z
	 * 
	 * @param l
	 * @return
	 */
	public static char convLongToAlphanum(long l) {
		if (l < 10)
			return (char) (l + 48);
		else
			return (char) (l + 87);
	}

	/**
	 * Reinventing the wheel in case toString breaks. long is converted to a
	 * base 36
	 * 
	 * @param l
	 * @param fixedLen
	 * @return
	 */
	public static String convLongToEquiAlphanum(long l, int fixedLen) {
		if (fixedLen == 1)
			return String.valueOf(convLongToAlphanum(l));
		long divisor = pow(36L, fixedLen - 1);
		return convLongToAlphanum(l / divisor) + convLongToEquiAlphanum(l % divisor, fixedLen - 1);
	}

	public static String convLongToEquiAlphanum(long l) {
		long sum = 36L;
		for (int len = 1;;) {
			if (l < sum) {
				String str = Long.toString(l, 36);
				int diff = len - str.length();
				if (diff == 0)
					return str;
				char[] charr = new char[diff];
				Arrays.fill(charr, '0');
				return new String(charr) + str;
			} else {
				l -= sum;
				sum *= 36L;
				len++;
			}
		}
	}
}
