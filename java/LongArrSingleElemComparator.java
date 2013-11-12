package griddoor.util;

import java.util.Comparator;

public class LongArrSingleElemComparator implements Comparator<long[]> {
	public int compareInd;
	@Override
	public int compare(long[] o1, long[] o2) {
		if(o1[compareInd]>o2[compareInd])
			return 1;
		if(o1[compareInd]<o2[compareInd])
			return -1;
		return 0;
	}

}
