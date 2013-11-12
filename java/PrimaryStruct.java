package griddoor.util;

public abstract class PrimaryStruct extends PrimitiveStruct {
	public String gets(){
		return null;
	}
	public String gets(int i){
		return null;
	}
	public PrimaryStruct getstruct(){
		return null;
	}
	public PrimaryStruct getstruct(int i){
		return null;
	}
	public static PrimaryStruct make(long l, String s){
		Structls valtoret = new Structls();
		valtoret.l = l;
		valtoret.s = s;
		return valtoret;
	}
	public static PrimaryStruct make(byte bv){
		return new PrimaryStruct(){
			byte b = bv;
			@Override
			public byte getb() {
				return b;
			}
		};
	}
	public static PrimaryStruct make(long v){
		return new PrimaryStruct(){
			long l = v;
			@Override
			public long getl() {
				return l;
			}
		};
	}
	public static PrimaryStruct make(String v){
		return new PrimaryStruct(){
			String l = v;
			@Override
			public String gets() {
				return l;
			}
		};
	}
}
