package boss.util.codehelper;

import boss.util.PriorityQueue;
import boss.util.PriorityQueue.PriorityQueueEntry;

public class CodeHelper
{	
	//抽取优先队列使用的公共部分，但不实用，也没测试。
	public static void priorityQueue(PriorityQueueEntry first,IPriorityQueueEntry1 p) throws Exception
	{
		final PriorityQueue queue=new PriorityQueue();
		queue.add(first);
		
		while(!queue.isEmpty())
		{
			PriorityQueueEntry o=queue.remove();
			
			if(!p.invoke(o,new Consumer1<PriorityQueueEntry>()
					{
						public void accept(PriorityQueueEntry t)
						{
							queue.add(t);
						}
					}))
				break;
		}
	}
	
	//抽取优先队列使用的公共部分，但不实用，也没测试。
	public static void priorityQueue(Object obj,double value,IPriorityQueueEntry2 p) throws Exception
	{
		final PriorityQueue queue=new PriorityQueue();
		queue.add(obj,value);
		
		while(!queue.isEmpty())
		{
			PriorityQueueEntry o=queue.remove();
			
			if(!p.invoke(o.obj,o.value,new Consumer2<Object>(){
				public void accept(Object t, double value)
				{
					queue.add(t,value);
				}
			}))
				break;
		}
	}
	
	public static <T> T tryCatch(Supplier<T> s)
	{
		try
		{
			return s.get();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static <T> T tryFinally(Supplier<T> s,Empty e)throws Exception
	{
		try
		{
			return s.get();
		}
		finally
		{
			e.invoke();
		}
	}
	
	public static <T> T tryCatchFinally(Supplier<T> s,Empty e)
	{
		try
		{
			return s.get();
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return null;
		}
		finally
		{
			e.invoke();
		}
	}
}

interface Function<T, R> 
{
    R apply(T t) throws Exception;
}

interface IPriorityQueueEntry1
{
	boolean invoke(PriorityQueueEntry p,Consumer1<PriorityQueueEntry> pfunc);
}

interface IPriorityQueueEntry2
{
	boolean invoke(Object obj,double value,Consumer2<Object> pfunc);
}

interface Supplier<T>
{
    T get() throws Exception;
}