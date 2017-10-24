package boss.util;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class PriorityQueue
{
	//这里不用泛型的原因是为了方便。否则声明一次优先队列得声明泛型，获得优先队列的元素有得又得声明泛型。
	//若直接用object，只要在获得优先队列元素的时候转化一下即可
	public class PriorityQueueEntry
	{
		final public Object obj;
		final public double value;
		
		public PriorityQueueEntry(Object obj,double value)
		{
			this.obj=obj;
			this.value=value;
		}
	}
	
	protected PriorityQueueEntry[] queue;//由于java不能有泛型数组，所以这边不用泛型
	protected int size=0;
	protected final boolean desc;
	
	public PriorityQueue() 
	{
        this(100,false);
    }
	
	public PriorityQueue(boolean desc) 
	{
        this(100,desc);
    }
	
	public PriorityQueue(int initialCapacity,boolean desc) 
	{
        queue=new PriorityQueueEntry[initialCapacity];
        this.desc=desc;
    }
	
	public void add(PriorityQueueEntry o)
	{		
		if(o==null)
			throw new NullPointerException();
		
		if (size >= queue.length)
			grow();
		
		queue[size]=o;
        siftUp(size++);
	}
	
	public void add(Object o,double value)
	{
		//注意，若重写该函数，这这里的add调用的是子类的add
		this.add(new PriorityQueueEntry(o,value));
	}
	
    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Increases the capacity of the array.
     *
     * @param minCapacity the desired minimum capacity
     */
    private void grow()
    {
        int oldCapacity = queue.length;
        // Double size if small; else grow by 50%
        int newCapacity = oldCapacity + ((oldCapacity < 64) ?
                                         (oldCapacity + 2) :
                                         (oldCapacity >> 1));
        // overflow-conscious code
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(oldCapacity+1);
        queue = Arrays.copyOf(queue, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }
	
	protected void siftUp(int k)
	{
		PriorityQueueEntry key = queue[k];
        while (k > 0)
        {
            int parent = (k - 1) >>> 1;
            PriorityQueueEntry e = queue[parent];
            if (desc^(key.value>=e.value))
                break;
            queue[k] = e;
            k = parent;
        }
        queue[k] = key;
    }
	
	protected void siftDown(int k) 
    {
		PriorityQueueEntry key = queue[k];
        int half = size >>> 1;        // loop while a non-leaf
        while (k < half)
        {
            int child = (k << 1) + 1; // assume left child is least
            PriorityQueueEntry c = queue[child];
            int right = child + 1;
            if (right < size && (desc^(c.value > queue[right].value)))
                c = queue[child = right];
            if (desc^(key.value<=c.value))
                break;
            queue[k] = c;
            k = child;
        }
        queue[k] = key;
    }
    
	public PriorityQueueEntry remove()
	{
		if (size == 0)
            throw new NoSuchElementException("This queue is empty!");
		
        --size;
        
        PriorityQueueEntry result = queue[0];
        queue[0]=queue[size];
        queue[size] = null;
        
        siftDown(0);
        
        return result;
	}
	
	public int size()
	{
		return size;
	}
	
	public boolean isEmpty()
	{
		return size==0;
	}
	
	public boolean notEmpty()
	{
		return size!=0;
	}
}
