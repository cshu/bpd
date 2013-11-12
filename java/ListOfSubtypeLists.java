package griddoor.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListOfSubtypeLists<E> implements List<E> {

	final ArrayList<List<? extends E>> lstOfLsts;
	public ListOfSubtypeLists(ArrayList<List<? extends E>> lstOfLsts) {
		this.lstOfLsts = lstOfLsts;
	}
	
	@Override
	public int size() {
		int valtoret = 0;
		for(List<? extends E> lst : lstOfLsts){
			valtoret+=lst.size();
		}
		return valtoret;
	}

	@Override
	public boolean isEmpty() {
		for(List<? extends E> lst : lstOfLsts){
			if(!lst.isEmpty())
				return false;
		}
		return true;
	}

	@Override
	public boolean contains(Object o) {
		for(List<? extends E> lst : lstOfLsts){
			if(lst.contains(o))
				return true;
		}
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		return new ListSubtypeIterators<E>(lstOfLsts);
	}

	@Override
	public Object[] toArray() {
		int totasiz = size();
		Object[] valtoret = new Object[totasiz];
		int count = 0;
		for(List<? extends E> lst : lstOfLsts){
			for(E o : lst){
				valtoret[count] = o;
				count++;
			}
		}
		return valtoret;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		int totasiz = size();
		T[] ph = Arrays.copyOf(a, 0);
		int off = 0;
		if (a.length < totasiz){
			T[] valtoret = Arrays.copyOf(ph, totasiz);
			for(List<? extends E> lst : lstOfLsts){
				T[] subarr = lst.toArray(ph);
				System.arraycopy(subarr, 0, valtoret, off, subarr.length);
				off+=subarr.length;
			}
			return valtoret;
		}
		for(List<? extends E> lst : lstOfLsts){
			T[] subarr = lst.toArray(ph);
			System.arraycopy(subarr, 0, a, off, subarr.length);
			off+=subarr.length;
		}
		if(a.length > totasiz)
			a[totasiz] = null;
		return a;
	}

	/**
	 * can only add null
	 */
	@Override
	public boolean add(E e) {
		return lstOfLsts.get(lstOfLsts.size()-1).add(null);
	}

	@Override
	public boolean remove(Object o) {
		for(List<? extends E> lst : lstOfLsts){
			if(lst.remove(o))
				return true;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
        for (Object e : c)
            if (!contains(e))
                return false;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean valtoret = false;
		for(List<? extends E> lst : lstOfLsts){
			if(lst.removeAll(c))
				valtoret = true;
		}
		return valtoret;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean valtoret = false;
		for(List<? extends E> lst : lstOfLsts){
			if(lst.retainAll(c))
				valtoret = true;
		}
		return valtoret;
	}

	@Override
	public void clear() {
		for(List<? extends E> lst : lstOfLsts){
			lst.clear();
		}
	}

	@Override
	public E get(int index) {
		int off = 0;
		for(List<? extends E> lst : lstOfLsts){
			int nextOff = lst.size()+off;
			if(nextOff>index){
				return lst.get(index-off);
			}
			off = nextOff;
		}
		throw new IndexOutOfBoundsException();
	}

	/**
	 * can only set null
	 */
	@Override
	public E set(int index, E element) {
		int off = 0;
		for(List<? extends E> lst : lstOfLsts){
			int nextOff = lst.size()+off;
			if(nextOff>index){
				return lst.set(index-off, null);
			}
			off = nextOff;
		}
		throw new IndexOutOfBoundsException();
	}

	/**
	 * can only add null
	 */
	@Override
	public void add(int index, E element) {
		int off = 0;
		for(List<? extends E> lst : lstOfLsts){
			int nextOff = lst.size()+off;
			if(nextOff>index){
				lst.add(index-off, null);
				return;
			}
			off = nextOff;
		}
		//? throw exception?
	}

	@Override
	public E remove(int index) {
		int off = 0;
		for(List<? extends E> lst : lstOfLsts){
			int nextOff = lst.size()+off;
			if(nextOff>index){
				return lst.remove(index-off);
			}
			off = nextOff;
		}
		throw new IndexOutOfBoundsException();
	}

	@Override
	public int indexOf(Object o) {
		int off = 0;
		for(List<? extends E> lst : lstOfLsts){
			int ind = lst.indexOf(o);
			if(ind!=-1)
				return off+ind;
			off+=lst.size();
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		for(int i = lstOfLsts.size()-1; i>=0; i--){
			int ind = lstOfLsts.get(i).lastIndexOf(o);
			if(ind==-1)
				continue;
			for(int j = i-1; j>=0; j--)
				ind+=lstOfLsts.get(j).size();
			return ind;
		}
		return -1;
	}

	@Override
	public ListIterator<E> listIterator() {
		return new ListSubtypeListIterators<>(lstOfLsts, 0);
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return new ListSubtypeListIterators<>(lstOfLsts, index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		int off = 0;
		for(int i = 0; i<lstOfLsts.size(); i++){
			List<? extends E> lst = lstOfLsts.get(i);
			int nextOff = lst.size()+off;
			if(nextOff>fromIndex){
				if(nextOff>=toIndex){
					ArrayList<List<? extends E>> valtoret = new ArrayList<>(1);
					valtoret.add(lst.subList(fromIndex-off, toIndex-off));
					return new ListOfSubtypeLists<E>(valtoret);
				}else{
					ArrayList<List<? extends E>> valtoret = new ArrayList<>();
					valtoret.add(lst.subList(fromIndex-off, lst.size()));
					off = nextOff;
					for(int j = i+1; j<lstOfLsts.size(); j++){
						lst = lstOfLsts.get(j);
						nextOff = lst.size()+off;
						if(nextOff>=toIndex){
							valtoret.add(lst.subList(0, toIndex-off));
							return new ListOfSubtypeLists<E>(valtoret);
						}
						valtoret.add(lst);
						off = nextOff;
					}
					throw new IndexOutOfBoundsException();
				}
			}
			off = nextOff;
		}
		throw new IndexOutOfBoundsException();
	}

}
