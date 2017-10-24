package boss.util;

import java.util.*;

import boss.util.PriorityQueue.PriorityQueueEntry;


public class GraphUtil 
{
	//本来应该要检查合法性的，比如weight维度是否相同，dests里的值是否超过weight里的某一维度。不过是给自己用的，所以就没必要搞这些了
	//不能到达的点会返回，距离为Double.MAX_VALUE
	public static Map<Integer,Double> Dijkstra(double[][] weight,int src,HashSet<Integer> dests)
	{
		int dimension=weight.length,count=dests.size();
		double distance[]=new double[dimension];
		Map<Integer,Double> result=new HashMap<Integer,Double>();
		boolean found[]=new boolean[dimension];
		int v=-1,w;
		System.arraycopy(weight[src], 0, distance, 0, dimension);
		
		while(count>0)
		{
			double min=Double.MAX_VALUE;
			for(w=0;w<dimension;w++)
			{
				if(!found[w])
				{
					if(distance[w]<=min)
					{
						v=w;
						min=distance[w];
					}
				}
			}
			found[v]=true;
			if(dests.contains(v))
			{
				count--;
				result.put(v, min);
			}
			
			for(w=0;w<dimension;w++)
			{
				if(!found[w])
				{
					if(min+weight[v][w]<distance[w])
					{
						distance[w] = min + weight[v][w];
					}
				}
			}
		}
		
		return result;
	}

	public static double Dijkstra(double[][] weight,int src,int dest)
	{
		HashSet<Integer> set=new HashSet<Integer>();
		set.add(dest);
		return GraphUtil.Dijkstra(weight, src, set).get(dest);
	}
	
	//不能到达的点不会返回
	public static Map<Integer,Double> SPFA(double[][] weight,int src,HashSet<Integer> dests)
	{
		Map<Integer,Double> result=new HashMap<Integer,Double>();
		
		int num=weight.length;
		int count=dests.size();
		boolean found[]=new boolean[num];
		PriorityQueue pq=new PriorityQueue();
		
		for(int i=0;i<num;i++)
		{
			if(weight[src][i]!=Double.MAX_VALUE)
			{
				pq.add(i, weight[src][i]);
			}
		}
		
		while(count>0 && !pq.isEmpty())
		{
			PriorityQueueEntry pqe=pq.remove();
			double distance=pqe.value;
			int now=(Integer)pqe.obj;
			
			if(found[now])
				continue;
			
			found[now]=true;
			if(dests.contains(now))
			{
				count--;
				result.put(now, distance);
			}
			
			for(int i=0;i<num;i++)
			{
				if(!found[i] && weight[now][i]!=Double.MAX_VALUE)
				{
					double value=distance+weight[now][i];
					pq.add(i, value);
				}
			}
				
			//这样是错的，必须是从优先队列里出来的才能算被找过，否则逻辑有误
//			PriorityQueueEntry pqe=pq.remove();
//			double distance=pqe.value;
//			int now=(Integer)pqe.obj;
//	
//			if(now==dest)
//				return distance;
//			
//			for(int i=0;i<num;i++)
//			{
//				if(!found[i] && weight[now][i]!=Double.MAX_VALUE)
//				{
//					found[i]=true;
//					double value=distance+weight[now][i];
//					pq.add(i, value);
//				}
//			}
		}
		
		return result;
	}
	
	public static double SPFA(double[][] weight,int src,int dest)
	{
		HashSet<Integer> set=new HashSet<Integer>();
		set.add(dest);
		return GraphUtil.SPFA(weight, src, set).get(dest);
	}	
}