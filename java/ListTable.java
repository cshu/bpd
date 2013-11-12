package griddoor.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public abstract class ListTable<T extends List<long[]>> {
	public final int numOfCols;
	public ListTable(int numOfCols){
		this.numOfCols = numOfCols;
		tupleForQuery = new long[numOfCols];
	}
	ArrayList<T> lists;
	public void insert(long... row){
		for(LongArrSingleElemComparator compar = new LongArrSingleElemComparator(); compar.compareInd<numOfCols; compar.compareInd++){
			T list = lists.get(compar.compareInd);
			int ind = Collections.binarySearch(list, row, compar);
			if(ind<0)
				list.add(-(ind+1), row);
			else
				list.add(ind, row);
		}
	}
	
	private long[] tupleForQuery;
	public ListIterator<long[]> select(int colInd, long criteria){//note not thread safe
		LongArrSingleElemComparator compar = new LongArrSingleElemComparator();
		compar.compareInd = colInd;
		tupleForQuery[colInd] = criteria;
		int ind = Collections.binarySearch(lists.get(colInd), tupleForQuery, compar);
		if(ind<0)
			return null;
		return lists.get(colInd).listIterator(ind);
	}
}
