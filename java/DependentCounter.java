package griddoor.util;

public class DependentCounter {
	public DependentCounter(int size){
		value = new int[size];
	}
	public int[] value;
	public void Reset(int[] value){
		this.value = value;
	}
	public void Increment(int elemInd){
		value[elemInd]++;
	}
	public void Add(int elemInd, int augend){
		value[elemInd]+=augend;
	}
	public int getEndIndOf(int elemInd){
		int valtoret = value[0];
		for(int i = 1; i<=elemInd; i++){
			valtoret+=value[i];
		}
		return valtoret;
	}
	public int getBeginIndOf(int elemInd){
		int valtoret = 0;
		for(int i = 0; i<elemInd; i++){
			valtoret+=value[i];
		}
		return valtoret;
	}
	public void selectInd(int elemInd){
		begin = 0;
		for(int i = 0; i<elemInd; i++){
			begin+=value[i];
		}
		end = begin + value[elemInd];
	}
	public int begin;
	public int end;
}
