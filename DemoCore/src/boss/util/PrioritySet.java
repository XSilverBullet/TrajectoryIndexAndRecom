package boss.util;

import java.util.HashSet;

public class PrioritySet extends PriorityQueue
{
	private final boolean remain;
	private HashSet<Object> set=new HashSet<Object>();
	
	public PrioritySet()
	{
		this(100,false,true);
	}
	
	public PrioritySet(boolean desc)
	{
		this(100,desc,true);
	}
	
	public PrioritySet(boolean desc,boolean remain)
	{
		this(100,desc,remain);
	}
	
	public PrioritySet(int initialCapacity,boolean desc,boolean remain) 
	{
        super(initialCapacity,desc);
        this.remain=remain;
    }
	
	public void add(PriorityQueueEntry o)
	{
		if(!set.contains(o.obj))
		{
			super.add(o);
			set.add(o.obj);
		}
	}
	
	public void add(Object o, double value)
	{
		this.add(new PriorityQueueEntry(o,value));
	}
	
	public PriorityQueueEntry remove()
	{
		PriorityQueueEntry result=super.remove();
		if(!remain)
			set.remove(result.obj);
		return result;
	}
}
