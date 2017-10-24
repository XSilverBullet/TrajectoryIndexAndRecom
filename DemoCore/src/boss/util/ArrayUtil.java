package boss.util;

import java.util.ArrayList;
import java.util.Arrays;

public class ArrayUtil
{
	public static void copy(Object[][] source,Object[][] desc)
	{
		for(int i=source.length-1;i>=0;i--)
		{
			System.arraycopy(source[i], 0, desc[i], 0, source[i].length);
		}
	}
	
	//无法生成泛型数组，所以不能返回数组
	public static <T> ArrayList<T[]> getFullArray(T[] data)
	{
		int n=data.length;
		int num=1;
		
		if(data.length>30)
			throw new RuntimeException("The length of the array is too long.");
		
		for(int i=1;i<=n;i++)
			num*=n;
		
		ArrayList<T[]> result=new ArrayList<T[]>(num);
		fullArray(Arrays.copyOf(data, n),0,result);
		return result;
	}
	
	private static <T> void fullArray(T data[],int position,ArrayList<T[]> result)
	{
		int n=data.length;
		if (position == n-1)
	    {
			result.add(Arrays.copyOf(data, n));
	    }
	    else
	    {
	        for (int i = position; i < n; i++)
	        {
	        	T t=data[i];
	        	data[i]=data[position];
	        	data[position]=t;
	        	
	        	fullArray(data,position+1,result);
	            
	            t=data[i];
	            data[i]=data[position];
	            data[position]=t;
	        }
	    }
	}
	
	public static <T extends Comparable<T>> T[] getMax(T data[],int num)
	{
		data=Arrays.copyOf(data, data.length);
		//可以考虑插入排序
		Arrays.sort(data);
		if(num>data.length)
			num=data.length;
		return Arrays.copyOfRange(data, data.length-num, data.length);
	}
	
	public static <T extends Comparable<T>> T[] getMax_Quick(T data[],int num)
	{
		if(num==0)
			return Arrays.copyOf(data, 0);
		data=Arrays.copyOf(data, data.length);
		if(num>=data.length)
			return data;
		
		int low=0,high=data.length-1;
		int p=partitionForDESC(data,low,high),position=num-1;
		
		while(position!=p && position!=p-1)
		{
			if(p<position)
				low=p+1;
			else if(p>position)
				high=p-1;
			p=partitionForDESC(data,low,high);
		}
		
		return Arrays.copyOfRange(data, 0, num);
	}
	
	//返回：前面的大，后面的小
	public static <T extends Comparable<T>> int partitionForDESC(T data[],int low,int high)
	{
		T pivot;
		int last;
		int mid=(low+high)/2;
		
		T t=data[low];
		data[low]=data[mid];
		data[mid]=t;
		
		pivot=data[low];
		last=low;
		for(int i=low+1;i<=high;i++)
		{
			if(data[i].compareTo(pivot)>0)
			{
				last=last+1;
				
				t=data[last];
				data[last]=data[i];
				data[i]=t;
			}
		}
		
		t=data[last];
		data[last]=data[low];
		data[low]=t;
		
		return last;
	}
	
	public static void main(String args[])
	{
		Integer data[]={20,1,19,2,18,3,17,4,16,5,14,6,12,7,11,8,10,9,15,13};
		Integer t[]=getMax_Quick(data,19);
		
		for(Integer tt:t)
		{
			System.out.println(tt);
		}
	}
}
