package griddoor.util;

import java.util.ArrayList;

public class ArrayListTable extends ListTable<ArrayList<long[]>> {
	public ArrayListTable(int numOfCols){
		super(numOfCols);
		lists = new ArrayList<ArrayList<long[]>>(numOfCols);
		do{ 
			lists.add(new ArrayList<long[]>());
			numOfCols--;
		}while(numOfCols>0);
	}
}
