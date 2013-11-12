package griddoor.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class FortyBitSecTime {
	//FortyBitSecTime is byte array with first 40 bits as a number comprising year,month,day,min,sec.
	//Granularity is second. Though you can extend it with more trailing bits for smaller granularity.
	//But adding bits at the head part might be troublesome because memcmp cannot be used on different formats.
	//tz is unspecified, normally should default to UTC
	
	//Why unix time/posix time/epoch time is no good?
	//it's not counting leap seconds and can not be converted to human readable date without infrastructure lib like libc
	//and there are some horror stories heard about quirks of quantitative time on computers....

	
	/**
	 * parameters ranges are imitating struct tm in C standard
	 * 
	 * @param tmyear
	 *            0-33648 (33648 might overflow when mon, day, etc. are too big. 33647 is safe.)
	 *            (since 1900, but for now integers greater than
	 *            200 are reserved, may be used as years before 1900)
	 * @param tmmon
	 *            0-11
	 * @param tmmday
	 *            1-31 (stored as 0-30)
	 * @param tmhour
	 *            0-23
	 * @param tmmin
	 *            0-59
	 * @param tmsec
	 *            0-60
	 * @return 5-byte array
	 */
	public static byte[] makeBytes(int tmyear, int tmmon, int tmmday, int tmhour, int tmmin, int tmsec) {
		long val = tmyear * tmmonB + tmmon * tmmdayB + (tmmday - 1) * tmhourB + tmhour * tmminB + tmmin * tmsecB + tmsec;
		byte[] valtoret = new byte[5];
		ByteBuffer bb = ByteBuffer.allocate(Long.BYTES).order(ByteOrder.BIG_ENDIAN).putLong(val);
		bb.position(3);
		bb.get(valtoret);
		return valtoret;
	}
	
	static final long tmsecB = 61L;
	static final long tmminB = 60L * tmsecB;
	static final long tmhourB = 24L * tmminB;
	static final long tmmdayB = 31L * tmhourB;
	static final long tmmonB = 12L * tmmdayB;
	
	public static GregorianCalendar makeGregorianCalendar(byte[] fortyBitSecTime){
		byte[] arr = new byte[8];
		System.arraycopy(fortyBitSecTime, 0, arr, 3, 5);
		long val = ByteBuffer.wrap(arr).order(ByteOrder.BIG_ENDIAN).getLong();
		int tmsec = (int) (val % tmsecB);
		int tmmin = (int) (val % tmminB /tmsecB);
		int tmhour = (int) (val%tmhourB /tmminB);
		int tmmday = (int) (val%tmmdayB/tmhourB) + 1;
		int tmmon = (int) (val%tmmonB/tmmdayB);
		int tmyear = (int) (val/tmmonB);
		return new GregorianCalendar(tmyear+1900,tmmon,tmmday,tmhour,tmmin,tmsec);
	}
	
	public static byte[] convertFromCalendar(Calendar gc){
		//? gc.get(Calendar.YEAR) must >= 1900?
		return makeBytes(gc.get(Calendar.YEAR)-1900,gc.get(Calendar.MONTH),gc.get(Calendar.DAY_OF_MONTH),gc.get(Calendar.HOUR_OF_DAY),gc.get(Calendar.MINUTE),gc.get(Calendar.SECOND));
	}
}
