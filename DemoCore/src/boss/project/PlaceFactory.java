package boss.project;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import org.apache.shiro.util.SoftHashMap;

public class PlaceFactory
{	
	//根据需要，该map可以使用SoftHashMap或最近最少使用的哈希表，以免不常用的地点信息占用大量内存
	final static private Map<String,Place> map=new SoftHashMap<String,Place>();
	final private static HashSet<String> lock=new HashSet<String>();
	
	public static Place get(String s) throws IOException, InterruptedException
	{
		Place place=null;
		
		synchronized(lock)
		{
			//假设有两个线程同时申请“北京”这个城市，但同时没有。此时应该一个线程等待另一个线程生成北京的Place，之后同时返回这个对象,
			//而非都生成一个Place对象，然后后面的覆盖前面的。
			while(lock.contains(s))
				lock.wait();
			
			place=map.get(s);
			if(place!=null)
				return place;
			
			lock.add(s);
		}
		
		//构造place的时间太长，不宜放到synchronized块中。
		place=new Place(s);
		
		synchronized(lock)
		{
			map.put(s, place);
			lock.remove(s);
			lock.notify();
		}
		
		return place;
	}
}
