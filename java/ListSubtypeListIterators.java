package griddoor.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListSubtypeListIterators<E> implements ListIterator<E> {

	final ArrayList<ListIterator<? extends E>> linkedIte;
	int allTogetherInd;
	int inde;
	
	public ListSubtypeListIterators(Collection<List<? extends E>> iterables, int index) {
		linkedIte = new ArrayList<>(iterables.size());
		allTogetherInd = index;
		inde = 0;
		Iterator<List<? extends E>> cIte = iterables.iterator();
		while(true){
			List<? extends E> iter = cIte.next();
			if(iter.size()>=index){
				linkedIte.add(iter.listIterator(index));
				while(cIte.hasNext()){
					iter = cIte.next();
					linkedIte.add(iter.listIterator());
				}
				return;
			}else{
				inde++;
				index-=iter.size();
				linkedIte.add(iter.listIterator(iter.size()));
			}
		}
		//note not really need to check index exceed boundary, unless you want safe code
	}
	
	@Override
	public boolean hasNext() {
		int i = inde;//using another var only coz we have remove, add, set methods
		while(true){
			ListIterator<? extends E> elem = linkedIte.get(i);
			if(elem.hasNext())
				return true;
			if(i==linkedIte.size()-1)
				return false;
			i++;
		}
	}

	@Override
	public E next() {
		allTogetherInd++;
		while(true){
			ListIterator<? extends E> elem = linkedIte.get(inde);
			if(elem.hasNext())
				return elem.next();
			inde++;
		}
	}

	@Override
	public boolean hasPrevious() {
		int i = inde;//using another var only coz we have remove, add, set methods
		while(true){
			ListIterator<? extends E> elem = linkedIte.get(i);
			if(elem.hasPrevious())
				return true;
			if(i==0)
				return false;
			i--;
		}
	}

	@Override
	public E previous() {
		allTogetherInd--;
		while(true){
			ListIterator<? extends E> elem = linkedIte.get(inde);
			if(elem.hasPrevious())
				return elem.previous();
			inde--;
		}
	}

	@Override
	public int nextIndex() {
		return allTogetherInd;
	}

	@Override
	public int previousIndex() {
		return allTogetherInd-1;
	}

	@Override
	public void remove() {
		linkedIte.get(inde).remove();
	}

	/**
	 * can only set null
	 */
	@Override
	public void set(E e) {
		linkedIte.get(inde).set(null);
	}

	/**
	 * can only add null
	 */
	@Override
	public void add(E e) {
		linkedIte.get(inde).add(null);
	}

}
