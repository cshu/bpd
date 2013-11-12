package griddoor.util;

import java.util.ArrayList;
import java.util.Arrays;

public class StringPrepender {

	protected ArrayList<char[]> value = new ArrayList<>();

	public int calculateLength(){
		int valtoret=0;
		for(char[] chars : value){
			valtoret+=chars.length;
		}
		return valtoret;
	}

	public StringPrepender prepend(String str){
		value.add(str.toCharArray());
		return this;
	}

	/**
	 * new array, then call getChars(), and add the array
	 * @param str
	 * @param srcBegin
	 * @param srcEnd
	 * @return
	 */
	public StringPrepender prepend(String str, int srcBegin, int srcEnd){
		char[] arrToAdd = new char[srcEnd-srcBegin];
		str.getChars(srcBegin, srcEnd, arrToAdd, 0);
		value.add(arrToAdd);
		return this;
	}
	
	public StringPrepender prepend(long l) {
		return prepend(Long.toString(l));
	}
	public StringPrepender prepend(int l) {
		return prepend(Integer.toString(l));
	}
	public StringPrepender prepend(short l){
		return prepend(Short.toString(l));
	}
	public StringPrepender prepend(double l){
		return prepend(Double.toString(l));
	}
	public StringPrepender prepend(float l){
		return prepend(Float.toString(l));
	}
	public StringPrepender prepend(char l){
		value.add(new char[]{l});
		return this;
	}
	/**
	 * simply add Arrays.copyOf
	 * @param original
	 * @param newLength
	 * @return
	 */
	public StringPrepender prepend(char[] original, int newLength){
		value.add(Arrays.copyOf(original, newLength));
		return this;
	}
	/**
	 * simply add Arrays.copyOfRange
	 * @param original
	 * @param from
	 * @param to
	 * @return
	 */
	public StringPrepender prepend(char[] original, int from, int to){
		value.add(Arrays.copyOfRange(original, from, to));
		return this;
	}

	public StringPrepender prependInstance(char[] member){
		value.add(member);
		return this;
	}

	public char[] toCharArray(){
		int arrLen = calculateLength();
		char[] arr = new char[arrLen];
		for(char[] chars : value){
			arrLen-=chars.length;
			System.arraycopy(chars, 0, arr, arrLen, chars.length);
		}
		return arr;
	}

	public String toString(){
		char[] arr = toCharArray();
		return new String(arr, 0, arr.length);
	}

	public void clear(){
		value.clear();
	}
}
