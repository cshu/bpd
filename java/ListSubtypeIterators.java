package griddoor.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ListSubtypeIterators<E> implements Iterator<E> {
	final LinkedList<Iterator<? extends E>> linkedIte;
	public ListSubtypeIterators(Iterable<List<? extends E>> iterables) {
		linkedIte = new LinkedList<>();
		for(List<? extends E> iter : iterables){
			linkedIte.add(iter.iterator());
		}
	}
	
	@Override
	public boolean hasNext() {
		for(Iterator<? extends E> elem : linkedIte){
			if(elem.hasNext())
				return true;
		}
		return false;
	}

	@Override
	public E next() {
		while(true){
			Iterator<? extends E> elem = linkedIte.peek();
//			if(elem == null)//note change null exception to NoSuchElementException, not necessary unless you want to handle different exceptions
//				throw new NoSuchElementException();
			if(elem.hasNext())
				return elem.next();
			linkedIte.removeFirst();
		}
	}

	@Override
	public void remove() {
		//note changing null exception to NoSuchElementException is not necessary unless you want to handle different exceptions
		linkedIte.peek().remove();
	}
}
