package griddoor.util;

public class BidiRef<T> {
	private T obj;
	public T get(){
		return obj;
	}
	public static <V,T> void makeRef(V o1, BidiRef<T> r1, T o2, BidiRef<V> r2){
		r1.obj = o2;
		r2.obj = o1;
	}
	public <V> void makeBidiRef(V o1, T o2, BidiRef<V> r2){
		makeRef(o1, this, o2, r2);
	}
}
