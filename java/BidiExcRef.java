package griddoor.util;

public class BidiExcRef<T>{
	private T obj;
	public T get(){
		return obj;
	}
	private BidiExcRef<?> counterpart;
	public static <V,T> void makeRef(V o1, BidiExcRef<T> r1, T o2, BidiExcRef<V> r2){
		if(r1.counterpart!=null){
			r1.counterpart.obj = null;
			r1.counterpart.counterpart = null;
		}
		if(r2.counterpart!=null){
			r2.counterpart.obj = null;
			r2.counterpart.counterpart = null;
		}
		r1.obj = o2;
		r2.obj = o1;
		r1.counterpart = r2;
		r2.counterpart = r1;
	}
	public <V> void makeBidiExcRef(V o1, T o2, BidiExcRef<V> r2) {
		makeRef(o1, this, o2, r2);
	}
}
