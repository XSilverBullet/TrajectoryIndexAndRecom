package boss.project.network;

public class Graph
{	public static double getDistanceNoSqrt(Location p1, Location p2)
	{
		double lat = Math
				.abs((p1.iLat - p2.iLat)/Location.DIVISOR * Constants.D_GPS_DISTANCE);
		double lng = Math
				.abs((p1.iLng - p2.iLng)/Location.DIVISOR * Constants.D_GPS_DISTANCE);

		return lat * lat + lng * lng;
	}
	
	public static double getDistanceNoSqrt(Vertex p1, Vertex p2)
	{
		return getDistanceNoSqrt(p1.gpPoint, p2.gpPoint);
	}

	public static double getDistance(Location p1, Location p2) 
	{
		return Math.sqrt(getDistanceNoSqrt(p1, p2));
	}
	
	public static double getDistance(Vertex p1, Vertex p2) 
	{
		return Math.sqrt(getDistanceNoSqrt(p1, p2));
	}
	
//	@SuppressWarnings("unchecked")
//	//启发式的SPFA算法
//	public static LinkedList<Vertex> aStar(int sour,int des,Vertex vertexes[])
//	{		
//		//保存每个点的前一个点
//		HashMap<Vertex,Vertex> before=new HashMap<Vertex,Vertex>();
//		
//		PriorityQueue queue=new PriorityQueue();
//		queue.add(new Tuple3<Vertex,Vertex,Double>(vertexes[sour],null,(double)0),
//				Graph.getDistance(vertexes[sour],vertexes[des]));
//		
//		while(!queue.isEmpty())
//		{
//			PriorityQueueEntry o=queue.remove();
//			
//			//Tuple3<现在的位置，之前的位置，现在到原点的路径长度>
//			Tuple3<Vertex,Vertex,Double> t=(Tuple3<Vertex,Vertex,Double>)(o.obj);
//			
//			Vertex v=t.a;
//			
//			if(before.containsKey(v))
//				continue;
//			
//			before.put(v, t.b);
//			if(t.a==vertexes[des])
//			{
//				break;
//			}
//			
//			List<Edge> edges=v.getOutAdjacentEdges();
//			for(Edge e : edges)
//			{
//				Vertex vv=e.vEnd;
//				if(!before.containsKey(vv))
//				{
//					double distance=t.c+e.iLength;
//					queue.add(new Tuple3<Vertex,Vertex,Double>(vv,v,distance), 
//							distance+Graph.getDistance(vertexes[des], vv));
//				}
//			}
//		}
//		
//		LinkedList<Vertex> result=new LinkedList<Vertex>();
//		if(!before.containsKey(vertexes[des]))
//			return result;
//		for(Vertex v=vertexes[des];v!=null;v=before.get(v))
//		{
//			result.addFirst(v);
//		}
//		
//		return result;
//	}
	
//	private static double[][] getAdjacencyMatrix(Vertex vs[])
//	{
//		int num=vs.length;
//		double matrix[][]=new double[num][num];
//		
//		for(int i=0;i<num;i++)
//		{
//			Arrays.fill(matrix[i],Double.MAX_VALUE);
//		}
//
//		//key是点的id，value是点在数组的位置
//		Map<Integer,Integer> map=new HashMap<Integer,Integer>();
//		for(int i=0;i<num;i++)
//		{
//			map.put(vs[i].iVertexID, i);
//		}
//		
//		for(int i=0;i<num;i++)
//		{
//			List<Edge> list=vs[i].getAdjacentEdges();
//			
//			for(Edge e : list)
//			{
//				Integer id=map.get(e.vEnd.iVertexID);
//				if(id!=null && vs[i].iVertexID!=id)
//				{
//					if(matrix[i][id]>e.iLength)
//					{
//						matrix[id][i]=matrix[i][id]=e.iLength;
//					}
//				}
//			}
//		}
//		
//		return matrix;
//	}
	
//	@SuppressWarnings("unchecked")
//	//启发式的SPFA算法。在路网条件下，使用邻接矩阵的效率远远低于邻接表效率，而且很耗内存（生成邻接矩阵耗费了大量时间，但即使扣去生成邻接矩阵的时间效率依然远远低于使用邻接表）
//	public static LinkedList<Vertex> getShortestPath(int sour,int des,Vertex vertexes[])
//	{
//		double adjacencyMatrix[][]=getAdjacencyMatrix(vertexes);
//		
//		//保存每个点的前一个点
//		int before[]=new int[adjacencyMatrix.length];
//		Arrays.fill(before, -1);
//		
//		PriorityQueue queue=new PriorityQueue();
//		queue.add(new Tuple3<Integer,Integer,Double>(sour,-2,(double)0),
//				Graph.getDistance(vertexes[sour],vertexes[des]));
//		
//		while(!queue.isEmpty())
//		{
//			PriorityQueueEntry o=queue.remove();
//			
//			//Tuple3<现在的位置，之前的位置，现在到原点的路径长度>
//			Tuple3<Integer,Integer,Double> t=(Tuple3<Integer,Integer,Double>)(o.obj);
//			int position=t.a;
//			
//			if(before[position]!=-1)
//				continue;
//			
//			before[position]=t.b;
//			if(position==des)
//			{
//				break;
//			}
//			
//			for(int i=0;i<vertexes.length;i++)
//			{
//				if(before[i]==-1 && adjacencyMatrix[position][i]!=Double.MAX_VALUE)
//				{
//					double distanceFromSour=t.c+adjacencyMatrix[position][i];
//					queue.add(new Tuple3<Integer,Integer,Double>(i,position,distanceFromSour),
//							distanceFromSour+getDistance(vertexes[des],vertexes[i]));
//				}
//			}
//		}
//		
//		LinkedList<Vertex> result=new LinkedList<Vertex>();
//		for(int i=des;i!=-2;i=before[i])
//		{
//			result.addFirst(vertexes[i]);
//		}
//		
//		return result;
//	}
	
//  把优先队列的使用抽象出来
//	public LinkedList<Vertex> getShortestPath(int sour,int des,Vertex vertexes[])
//	{
//		double adjacencyMatrix[][]=getAdjacencyMatrix(vertexes);
//		
//		//保存每个点的前一个点
//		int before[]=new int[adjacencyMatrix.length];
//		Arrays.fill(before, -1);
//		
//		try
//		{
//			CodeHelper.priorityQueue(new Tuple3<Integer,Integer,Double>(sour,-2,(double)0),
//				Graph.getDistance(vertexes[sour],vertexes[des]), (obj,value,queue)->{
//					//Tuple3<现在的位置，之前的位置，现在到原点的路径长度>
//					Tuple3<Integer,Integer,Double> t=(Tuple3<Integer,Integer,Double>)(obj);
//					int position=t.a;
//					
//					if(before[position]!=-1)
//						return true;
//					
//					before[position]=t.b;
//					if(position==des)
//					{
//						return false;
//					}
//					
//					for(int i=0;i<vertexes.length;i++)
//					{
//						if(before[i]==-1 && adjacencyMatrix[position][i]!=Double.MAX_VALUE)
//						{
//							double distanceFromSour=t.c+adjacencyMatrix[position][i];
//							queue.accept(new Tuple3<Integer,Integer,Double>(i,position,distanceFromSour),
//									distanceFromSour+getDistance(vertexes[des],vertexes[i]));
//						}
//					}
//					
//					return true;
//			});
//		}
//		catch (Exception e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		LinkedList<Vertex> result=new LinkedList<Vertex>();
//		for(int i=des;i!=-2;i=before[i])
//		{
//			result.addFirst(vertexes[i]);
//		}
//		
//		return result;
//	}
}