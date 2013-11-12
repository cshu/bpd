package griddoor.util;

import java.util.ArrayList;
import java.util.LinkedList;

public class LinkedListTable extends ListTable<LinkedList<long[]>> {
	public LinkedListTable(int numOfCols){
		super(numOfCols);
		lists = new ArrayList<LinkedList<long[]>>(numOfCols);
		do{
			lists.add(new LinkedList<long[]>());
			numOfCols--;
		}while(numOfCols>0);
	}
}
