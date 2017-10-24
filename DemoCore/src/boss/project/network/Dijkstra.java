package boss.project.network;

import java.util.*;

import boss.util.*;
import boss.util.PriorityQueue.PriorityQueueEntry;
import boss.util.PriorityQueue;

public class Dijkstra
{
	private PriorityQueue queue=new PriorityQueue();
	//保存每个点及连接到此的路径
	private	LinkedHashMap<Vertex,Route> before=new LinkedHashMap<Vertex,Route>();
	public final Vertex source;
	
	public Dijkstra(Vertex source)
	{
		this.source=source;
		queue.add(new Tuple2<Vertex,Route>(source,null),0);
	}
	
	public Route run(Vertex end)
	{
		List<Vertex> s=new ArrayList<Vertex>();
		s.add(end);
		return run(s).get(0);
	}
	
	//用了A*算法加速。若路径不存在或原点就是终点则抛出异常。
	@SuppressWarnings("unchecked")
	public static Route run(Vertex source,Vertex end)
	{
		if(source.equals(end))
			throw new IllegalArgumentException("Source equals end");
		//保存每个点的前一个点
		HashMap<Vertex,Route> before=new HashMap<Vertex,Route>();
		
		PriorityQueue queue=new PriorityQueue();
		queue.add(new Tuple3<Vertex,Route,Double>(source,null,(double)0),
				Graph.getDistance(source,end));
		
		while(!queue.isEmpty())
		{
			PriorityQueueEntry o=queue.remove();
			
			//Tuple3<现在的位置，连接到当前点的路径，现在到原点的路径长度>
			Tuple3<Vertex,Route,Double> t=(Tuple3<Vertex,Route,Double>)(o.obj);
			
			Vertex v=t.a;
			
			if(before.containsKey(v))
				continue;
			else
				before.put(v, t.b);
			if(t.a==end)
			{
				break;
			}
			
			List<Edge> edges=v.getOutAdjacentEdges();
			for(Edge e : edges)
			{
				Vertex vv=e.vEnd;
				if(!before.containsKey(vv))
				{
					double distance=t.c+e.iLength;
					queue.add(new Tuple3<Vertex,Route,Double>(vv,new Route(t.b,e),distance), 
							distance+Graph.getDistance(end, vv));
				}
			}
		}
		
		Route result=before.get(end);
		if(result==null)
			throw new RouteNotExistException("Route doesn't exist！source："+source.iVertexID+" end:"+end.iVertexID);
	
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Route> run(List<Vertex> ends)
	{
		ArrayList<Route> routes=new ArrayList<Route>();
		
		for(Vertex end: ends)
		{
			if(source.equals(end))
				throw new IllegalArgumentException("Source equals end");
			
			if(!before.containsKey(end))
			{
				while(!queue.isEmpty())
				{
					PriorityQueueEntry o=queue.remove();
					
					//Tuple2<现在的位置，连接到当前点的路径>
					Tuple2<Vertex,Route> t=(Tuple2<Vertex,Route>)(o.obj);
					
					Vertex v=t.a;
					
					if(before.containsKey(v))
						continue;
					else
						before.put(v, t.b);
					
					//即使要break也得先把邻接点放到优先队列中，否则下次查询时会有信息丢失（相当于过度剪枝）
					List<Edge> edges=v.getOutAdjacentEdges();
					for(Edge e : edges)
					{
						Vertex vv=e.vEnd;
						if(!before.containsKey(vv))
							queue.add(new Tuple2<Vertex,Route>(vv,new Route(t.b,e)),o.value+e.iLength);
					}
					
					if(t.a==end)
					{
						break;
					}
				}
			}
			
			Route result=before.get(end);
			if(result==null)
				throw new RouteNotExistException("Route doesn't exist！source："+source.iVertexID+" end:"+end.iVertexID);
			
			routes.add(result);
		}
		
		return routes;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Route> run(double distance)
	{
		ArrayList<Route> routes=new ArrayList<Route>();
		boolean breakFlag=false;
		for(Route t:before.values())
		{
			if(t==null)
				continue;
			if(t.getLength()>distance)
			{
				breakFlag=true;
				break;
			}
			routes.add(t);
		}
		
		if(!breakFlag)
		{
			while(!queue.isEmpty())
			{
				PriorityQueueEntry o=queue.remove();
				
				//Tuple2<现在的位置，连接到当前点的边>
				Tuple2<Vertex,Route> t=(Tuple2<Vertex,Route>)(o.obj);
				
				Vertex v=t.a;
				
				if(before.containsKey(v))
					continue;
				else
				{
					before.put(v, t.b);
					routes.add(t.b);
				}
				
				//这里的优先队列不是全局的优先队列，所以break之前不需要先把元素放入队列中
				List<Edge> edges=v.getOutAdjacentEdges();
				for(Edge e : edges)
				{
					Vertex vv=e.vEnd;
					if(!before.containsKey(vv))
						queue.add(new Tuple2<Vertex,Route>(vv,new Route(t.b,e)),o.value+e.iLength);
				}
				
				if(o.value>distance)
					break;
			}
		}
		
		routes.remove(0);
		return routes;
	}
}
