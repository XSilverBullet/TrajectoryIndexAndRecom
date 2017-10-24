package boss.project.query;

import java.util.*;

import boss.project.Place;
import boss.project.network.*;
import boss.project.network.helper.*;
import boss.project.query.POI.Type;
import boss.util.PriorityQueue.PriorityQueueEntry;
import boss.util.PrioritySet;
import boss.util.codehelper.Consumer;
public class Query
{
	public final Place place;
	private DijkstraHelper dm;
	private static int limit=50; 
	
	public double getMinDistance(Vertex start,Vertex end)
	{
		return dm.run(start, end);
	}
	
	public Query(Place place) throws Exception
	{
		this.place=place;
		dm=new DijkstraHelper(30,1000);
	}
	
	public POISequence query(Vertex start,Vertex end,double distance,Type... types)
	{		
		return this.query(new Consumer<POISequence>()
		{
			public void accept(POISequence t)
			{
				return;
			}
		}, start, end, distance, types);//可以直接用匿名内部类
	}
	
	public POISequence query(Consumer<POISequence> event,Vertex start,Vertex end,double distance,Type... types)
	{
		DijkstraForDistance ds=new DijkstraForDistance(start);
		double minDistance=ds.run(end);
		if(minDistance>distance)
		{
			throw new RouteNotExistException("The starting point is too far away from the end point.");
		}		
		
		//表示起点到聚类的距离。如startToAll[3][0]表示起点到id为3的聚类的最小距离，startToAll[3][1]表示起点到id为3的聚类的最大距离
		double startToAll[][]= getStartToAll(ds,place.clusters),
				//表示聚类到终点的距离。如allToEnd[3][0]表示id为3的聚类到终点的最小距离，allToEnd[3][1]表示id为3的聚类到终点的最大距离
				allToEnd[][]= getAllToEnd(new DijkstraForDistance(end),place.clusters);
		
		int n=types.length;
		int kindsOfTypes=POI.Type.values().length;
		ClusterRanking cr=getClusterRanking(place.clusters,startToAll,allToEnd,distance,types);
		POISequence result=POISequence.empty;//找一个返回值最小的数，省得代码中一直判断是不是null
		
		//PrioritySet与优先队列的差别是若元素已经出现过，则调用add不产生作用
		PrioritySet queue=new PrioritySet(true);
		ClusterSequenceMaker first=cr.getFirst(types);
		queue.add(first,first.getScore());
		
		while(queue.notEmpty())
		{
			PriorityQueueEntry pqe=queue.remove();
			ClusterSequenceMaker csm=(ClusterSequenceMaker)pqe.obj;
			
			if(pqe.value<result.getScore())
				return result;
			
			//得到的ClusterSequence的min都小于distance
			ClusterSequence cs[]=csm.getFullArray(limit, distance, place.cd, startToAll, allToEnd);
			if(cs.length!=0)
			{
				POISequenceMaker psm=csm.getPOISequenceMaker();
				for(int i=0;i<cs.length;i++)
				{				
					if(cs[i].max<=distance)
					{
						//前面已经判断过，所以分数比lowbound高。
						//由于这里得到的是该clusterSequence中分数最高的POISequence，所以以后找到的POISequence分数都不会更高，可以直接返回
						result=psm.getPOISequence(cs[i]);
						event.accept(result);
						return result;
					}
				}
				
				PrioritySet sequences=new PrioritySet(true);
				sequences.add(psm,psm.getScore());
				
				label:while(sequences.notEmpty())
				{
					PriorityQueueEntry queueEntry=sequences.remove();
					psm=(POISequenceMaker)queueEntry.obj;
					//System.out.println(sequences.size()+" "+csm.toString()+"   "+psm.toString());
					if(psm.getScore()<=result.getScore())//如果结果不会比较好，那么没有必要继续计算了
						break label;
										
					//倒排索引.第一维的kindsOfTypes+1表示起始点到当前点.第二维的kindsOfType+1表示当前点到终点
					@SuppressWarnings("unchecked")
					List<POISequence> list[][]=new LinkedList[kindsOfTypes+1][kindsOfTypes+1];
					for(int i=0;i<kindsOfTypes;i++)
					{
						for(int j=0;j<kindsOfTypes+1;j++)
						{
							list[i][j]=new LinkedList<POISequence>();
						}
					}
					
					HashSet<Segment> segments=new HashSet<Segment>();
					for(int i=0;i<cs.length;i++)
					{				
						POISequence ps=psm.getPOISequence(cs[i]);
						
						//起点到第一点的数据已经计算过，可以直接带入
						int clusterId=cs[i].c[0].a.id;
						double d=ds.run(ps.pois[0].v);;
						
						ps.maxDistance-=startToAll[clusterId][1];
						ps.maxDistance+=d;
						if(ps.maxDistance<=distance)
						{
							result=ps;//前面已经判断过，所以p的分数比lowbound高
							event.accept(result);
							break label;
						}
						
						ps.minDistance-=startToAll[clusterId][0];
						ps.minDistance+=d;
						if(ps.minDistance>distance)
							continue;
						
						POI pois[]=ps.pois;
						//list[kindsOfTypes][pois[0].type.ordinal()].add(ps);//起点到第一点的数据已经计算过，可以直接带入
						list[pois[n-1].type.ordinal()][kindsOfTypes].add(ps);
						
						for(int j=0;j<n-1;j++)
						{
							list[pois[j].type.ordinal()][pois[j+1].type.ordinal()].add(ps);
						}
						
						for(int j=0;j<n-1;j++)
						{
							segments.add(new Segment(ps.pois[j],ps.pois[j+1],
									place.cd.getMaxDistance(cs[i].c[j].a.id, cs[i].c[j+1].a.id),place.cd.getMinDistance(cs[i].c[j].a.id, cs[i].c[j+1].a.id)));
						}
						
						//由于起点到任何点的数据都已经算过，所以数值已经是确定的，这里没必要在放到set里面
						//segments.add(new Segment(null,ps.pois[0],startToAll[cs[i].c[0].a.id][1],startToAll[cs[i].c[0].a.id][0]));
						segments.add(new Segment(ps.pois[n-1],null,allToEnd[cs[i].c[n-1].a.id][1],allToEnd[cs[i].c[n-1].a.id][0]));
					}
					
					//总的思路是尽量在调用迪杰斯特拉的时间最少的情况下排除掉整个序列（POISequenceMaker）。
					//迪杰斯特拉时间最少包含两个：1、调用次数少 2、每次调用时间少
					//因此，应该选择某种策略，选择合适的semgent来计算。
					//每次选择某个segment来计算后都删掉原来的segment。
					//然后在新的情况下(list[i][j]元素的数量可能少了,每个POISequence的maxDistance和minDistance可能变了)继续使用原来的策略选择新的segment
					while(!segments.isEmpty())
					{
						Segment mostPossible=null;
						double maxPossibility=Double.NEGATIVE_INFINITY;
						//Double d=null;
						for(Segment segment : segments)
						{
							int i=segment.start.type.ordinal(),j=kindsOfTypes;
							//Vertex s=segment.start.v,e=end;
							if(segment.end!=null)
							{
								j=segment.end.type.ordinal();
								//e=segment.end.v;
							}
//							d=dm.get(s, e);//实验证明，直接缓存迪杰斯特拉结果效果不大，因为由于大部分sequence只计算一点点就可以排除，缓存命中率低
//							if(d!=null)//因为计算迪杰斯特拉速度很慢，所以已经有值的先计算
//							{
//								//System.out.println("s");
//								mostPossible=segment;
//								break;
//							}
							
							//本来是选取比重最大的先计算segment.range/（p.maxDistance-p.minDistance）
							//由于出现多次segment的比出现一次的更有益，所以进行累加
							//实验证明，并非迪杰斯特拉次数越少总时间就越少，因为每次迪杰斯特拉的时间有长有短。
							//总的来讲距离越远，迪杰斯特拉的时间越长。所以这里再除以segment.max（也可以使segment.min或取平均）
							double possibility=0;
							for(POISequence p:list[i][j])
							{
								possibility+=segment.range/(p.maxDistance-p.minDistance)/segment.max;
							}
							if(possibility>maxPossibility)
							{
								maxPossibility=possibility;
								mostPossible=segment;
							}
						}
						segments.remove(mostPossible);
						
						int i=mostPossible.start.type.ordinal(),j=kindsOfTypes;
						Vertex e=end,s=mostPossible.start.v;
						if(mostPossible.end!=null)
						{
							j=mostPossible.end.type.ordinal();
							e=mostPossible.end.v;
						}
						
						if(!list[i][j].isEmpty())
						{
							double preciseDistance;
							//确定list[i][j]不为空后再计算迪杰斯特拉可提升十倍左右的效率（list[i][j]经常是空的。当然，不判断是否为空也不影响结果）
							//if(d!=null)
								//preciseDistance=d;
							//else
								preciseDistance=dm.run(s,e);
							Iterator<POISequence> it=list[i][j].iterator();
							//已经判断过是否为空后可以使用do-while语句。不过没差别。
							//这里是更新含有该segment的POISequence的maxDistance和minDistance
							while(it.hasNext())
							{
								POISequence p=it.next();
								
								p.maxDistance-=mostPossible.max;
								p.maxDistance+=preciseDistance;
								if(p.maxDistance<=distance)
								{
									result=p;//前面已经判断过，所以p的分数比lowbound高
									event.accept(result);
									break label;
								}
								
								
								p.minDistance-=mostPossible.min;
								p.minDistance+=preciseDistance;
								//remove不仅提高效率，也使得环境得到及时的更新，及时排除已经无用的POISequence
								if(p.minDistance>distance)
								{
									//it.remove必须在前面执行，否则后面的list[i][j].remove()会造成当前list的变动，导致不安全（java.util.ConcurrentModificationException）
									//这也是显式使用Iterator的原因。若用for(XX:XXX)会导致因下面list[i][j].remove()抛出java.util.ConcurrentModificationException
									it.remove();
									//list[kindsOfTypes][p.pois[0].type.ordinal()].remove(p);
									list[p.pois[n-1].type.ordinal()][kindsOfTypes].remove(p);
									
									for(int l=0;l<n-1;l++)
									{
										list[p.pois[l].type.ordinal()][p.pois[l+1].type.ordinal()].remove(p);
									}
								}
							}
						}
					}
					
					for(POISequenceMaker p : psm.getNext())
					{
						if(p.getScore()>=result.getScore())
							sequences.add(p,p.getScore());
					}
				}
			}
			
			for(ClusterSequenceMaker c : csm.getNext())
			{
				queue.add(c,c.getScore());
			}
		}
		
		if(result!=POISequence.empty)
			return result;
		
		throw new RouteNotExistException("No such Route");
	}
	
	private ClusterRanking getClusterRanking(Cluster[] clusters,double startToAll[][],double allToEnd[][],double limit,Type...types)
	{
		ArrayList<Cluster> list=new ArrayList<Cluster>(clusters.length);
		
		for(Cluster c : clusters)
		{
			if(startToAll[c.id][0]+allToEnd[c.id][0]<=limit)
				list.add(c);
		}
		
		return new ClusterRanking(list.toArray(new Cluster[0]),types);
	}
	
	private double[][] getVertexToAll(DijkstraForDistance v,Cluster[] clusters)
	{
		double distance[][]=new double[clusters.length][2];
		for(int i=0;i<clusters.length;i++)
		{
			ArrayList<Vertex> list=new ArrayList<Vertex>();
			for(POI p : clusters[i].getAllPOI())
			{
				list.add(p.v);
			}
			ArrayList<Double> result=v.run(list);
			
			double max=Double.NEGATIVE_INFINITY,min=Double.POSITIVE_INFINITY;
			for(double d:result)
			{
				if(d>max)
					max=d;
				if(d<min)
					min=d;
			}
			distance[clusters[i].id][1]=max;
			distance[clusters[i].id][0]=min;
		}
		
		return distance;
	}
	
	private double[][] getStartToAll(DijkstraForDistance start,Cluster[] clusters)
	{
		return this.getVertexToAll(start, clusters);
	}
	
	//若有些道路是单行，则该方法得修改
	private double[][] getAllToEnd(DijkstraForDistance end,Cluster[] clusters)
	{
		return this.getVertexToAll(end, clusters);
	}
}
