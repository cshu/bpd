package griddoor.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TmTime {

	//this is a bad time format, use FortyBitSecTime instead

	/**
	 *
	 * @param tmyear
	 *            14 bits 0-16383 (since 1900, but for now integers greater than
	 *            200 are reserved, may be used for years before 1900)
	 * @param tmmon
	 *            4 bits 0-11
	 * @param tmmday
	 *            5 bits 1-31
	 * @param tmhour
	 *            5 bits 0-23
	 * @param tmmin
	 *            6 bits 0-59
	 * @param tmsec
	 *            6 bits 0-60
	 * @return 5-byte array
	 */
	public static byte[] makeBytes(int tmyear, int tmmon, int tmmday, int tmhour, int tmmin, int tmsec) {
		byte[] valtoret = new byte[5];
		valtoret[0] = (byte) (tmyear >>> 6);
		valtoret[1] = (byte) (tmyear << 2 | tmmon >>> 2);
		valtoret[2] = (byte) (tmmon << 6 | tmmday << 1 | tmhour >>> 4);
		valtoret[3] = (byte) (tmhour << 4 | tmmin >>> 2);
		valtoret[4] = (byte) (tmmin << 6 | tmsec);
//		valtoret[0] = (byte) (tmsec << 2 | tmmin >>> 4);
//		valtoret[1] = (byte) (tmmin << 4 | tmhour >>> 1);
//		valtoret[2] = (byte) (tmhour << 7 | tmmday << 2 | tmmon >>> 2);
//		valtoret[3] = (byte) (tmmon << 6 | tmyear >>> 8);
//		valtoret[4] = (byte) tmyear;
		return valtoret;
	}

	public static GregorianCalendar makeGregorianCalendar(byte[] tmTime){
		int tm0 = tmTime[0] & 0xFF;
		int tm1 = tmTime[1] & 0xFF;
		int tm2 = tmTime[2] & 0xFF;
		int tm3 = tmTime[3] & 0xFF;
		int tm4 = tmTime[4] & 0xFF;
		int tmyear  = tm0<<6 | tm1 >>> 2;
		int tmmon = (tm1 & 0b11) << 2 | tm2 >>> 6;
		int tmmday = (tm2 >>> 1) & 0b11111;
		int tmhour = (tm2 & 0b1) << 4 | tm3 >>> 4;
		int tmmin = (tm3 & 0b1111) << 2 | tm4 >>> 6;
		int tmsec = tm4 & 0b111111;
		return new GregorianCalendar(tmyear+1900,tmmon,tmmday,tmhour,tmmin,tmsec);
	}

	public static byte[] convertGregorianCalendarToTmTime(GregorianCalendar gc){
		//? gc.get(Calendar.YEAR) must >= 1900?
		return makeBytes(gc.get(Calendar.YEAR)-1900,gc.get(Calendar.MONTH),gc.get(Calendar.DAY_OF_MONTH),gc.get(Calendar.HOUR_OF_DAY),gc.get(Calendar.MINUTE),gc.get(Calendar.SECOND));
	}
}
