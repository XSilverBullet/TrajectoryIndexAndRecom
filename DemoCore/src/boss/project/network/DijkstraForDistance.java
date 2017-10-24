package boss.project.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import boss.util.PriorityQueue;
import boss.util.PriorityQueue.PriorityQueueEntry;

public class DijkstraForDistance
{
	private PriorityQueue queue=new PriorityQueue();
	//保存每个点及连接到此的距离
	private	HashMap<Vertex,Double> before=new HashMap<Vertex,Double>();
	public final Vertex source;
	
	public DijkstraForDistance(Vertex source)
	{
		this.source=source;
		queue.add(source,0);
	}
	
//	public double run(Vertex end)
//	{
//		List<Vertex> s=new ArrayList<Vertex>();
//		s.add(end);
//		return run(s).get(0);
//	}
	
	//迪杰斯特拉的计算是最耗时间的，所以应该想尽办法提高迪杰斯特拉的效率
	//对于一个用户，因为网速的原因可能会两个线程同时执行某个迪杰斯特拉对象的run方法
	//所以这里必须同步。由于before和queue都得同步，所以直接整个函数同步
	//而且一个线程执行run后很可能会使得另一个线程的执行速度变快
	public synchronized double run(Vertex end)
	{
		if(!before.containsKey(end))
		{
			while(!queue.isEmpty())
			{
				PriorityQueueEntry o=queue.remove();
				
				Vertex v=(Vertex)o.obj;
				
				if(!before.containsKey(v))
				{
					before.put(v, o.value);
				
					//即使要break也得先把邻接点放到优先队列中，否则下次查询时会有信息丢失（相当于过度剪枝）
					for(Edge e : v.getOutAdjacentEdges())
					{
						if(!before.containsKey(e.vEnd))
							queue.add(e.vEnd,o.value+e.iLength);
					}
					
					if(v==end)
					{
						break;
					}
				}
			}
		}
		
		Double d=before.get(end);
		if(d==null)
			throw new RouteNotExistException("Route doesn't exist！source："+source.iVertexID+" end:"+end.iVertexID);
		
		return d;
	}
	
	public synchronized ArrayList<Double> run(List<Vertex> ends)
	{
		ArrayList<Double> result=new ArrayList<Double>();
		
		for(Vertex end: ends)
		{			
			if(!before.containsKey(end))
			{
				while(!queue.isEmpty())
				{
					PriorityQueueEntry o=queue.remove();
					
					Vertex v=(Vertex)o.obj;
					
					if(before.containsKey(v))
						continue;
					else
						before.put(v, o.value);
					
					//即使要break也得先把邻接点放到优先队列中，否则下次查询时会有信息丢失（相当于经过剪枝）
					List<Edge> edges=v.getOutAdjacentEdges();
					for(Edge e : edges)
					{
						Vertex vv=e.vEnd;
						if(!before.containsKey(vv))
							queue.add(vv,o.value+e.iLength);
					}
					
					if(v==end)
					{
						break;
					}
				}
			}
			
			Double d=before.get(end);
			if(d==null)
				throw new RouteNotExistException("Route doesn't exist！source："+source.iVertexID+" end:"+end.iVertexID);
			
			result.add(d);
		}
		
		return result;
	}
}
