package boss.util;

import java.util.*;

public class TimeRecorder implements Iterable<Long>
{
	protected List<List<Long>> list=new ArrayList<List<Long>>();
	protected List<Long> last;
	
	public TimeRecorder()
	{
		last= new ArrayList<Long>();
		list.add(last);
	}
	
	public void start()
	{	
		last.add(System.currentTimeMillis());
	}
	
	public void end()
	{
		long t=System.currentTimeMillis();
		if(last.size()==0)
			throw new RuntimeException("You must start before you end!");
		last.add(t);
		last=new ArrayList<Long>();
		list.add(last);
	}
	
	public long getTotal()
	{
		long t=0;
		for(List<Long> l:list)
		{
			if(!l.isEmpty())
				t+=l.get(l.size()-1)-l.get(0);
		}
		return t;
	}
	
	public long getPeriod(int i)
	{
		List<Long> l=list.get(i);
		return l.get(l.size()-1)-l.get(0);
	}
	
	public long get(int i,int j)
	{
		List<Long> l=list.get(i);
		return l.get(j+1)-l.get(j);
	}
	
	public long get(int i)
	{
		for(List<Long> l:list)
		{
			if(i<l.size()-1)
				return l.get(i+1)-l.get(i);
			i-=l.size()-1;
		}
		
		throw new NoSuchElementException();
	}
	
	@Override
	public Iterator<Long> iterator()
	{
		return new myIterator(this);
	}
}

class myIterator implements Iterator<Long>
{
	protected TimeRecorder tr;
	protected int i,j;
	
	public myIterator(TimeRecorder tr)
	{
		this.tr=tr;
		i=0;
		j=-1;//j指的是上次的位置,方便以后的remove
	}
	@Override
	public boolean hasNext()
	{
		//list.size()从一开始就至少为1,而且只要有end就一定有一个空的list
		return i<tr.list.size()-2 || j<tr.list.get(i).size()-2 || (i==tr.list.size()-2 && tr.list.get(tr.list.size()-1).size()>1);
	}

	@Override
	public Long next()
	{
		if(j<tr.list.get(i).size()-2)
			return tr.get(i,++j);
		else if(i<tr.list.size()-1 && tr.list.get(++i).size()>1)
		{
			j=0;
			return tr.get(i,0);
		}
		
		throw new NoSuchElementException();
	}
	
	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}