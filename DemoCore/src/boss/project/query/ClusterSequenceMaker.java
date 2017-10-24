package boss.project.query;

import java.util.ArrayList;
import java.util.Arrays;

import boss.project.query.POI.Type;
import boss.util.ArrayUtil;
import boss.util.Tuple2;
import boss.util.Tuple3;

public class ClusterSequenceMaker
{
	//可以改成Tuple2<Type,Integer>,然后写一个getCluster(int position)方法
	final Tuple3<Cluster,Type,Integer> clusters[];
	final ClusterRanking cr;
	
	@SuppressWarnings("unchecked")
	public ClusterSequenceMaker(ClusterRanking cr,POI.Type types[])
	{
		this.cr=cr;
		
		int n=types.length;
		clusters=new Tuple3[n];
		for(int i=0;i<n;i++)
		{			
			clusters[i]=new Tuple3<Cluster,Type,Integer>(cr.ranking[types[i].ordinal()][0],types[i],0);
		}
	}
	
	private ClusterSequenceMaker(ClusterSequenceMaker csm,int position)
	{
		clusters=Arrays.copyOf(csm.clusters, csm.clusters.length);
		this.cr=csm.cr;
		
		//不能直接在origin上操作，因为clusters和csm.clusters虽然不是同一个数组，但数组里面的对象却是同一个
		//虽然后面3行是BUG，但是这个BUG可以大大提高效率
		/*Tuple3<Cluster,Type,Integer> origin = clusters[position];
		Tuple3<Cluster,Type,Integer> t = new Tuple3<Cluster,Type,Integer>(cr.ranking[origin.b.ordinal()][origin.c+1],origin.b,origin.c+1);
		clusters[position] = t;*/
		Tuple3<Cluster,Type,Integer> t=clusters[position];
		t.c=++t.c;
		t.a=cr.ranking[t.b.ordinal()][t.c];
	}
	
	public double getScore()
	{
		double score=0;
		for(Tuple3<Cluster,Type,Integer> cluster : clusters)
		{
			score+=cluster.a.getScore(cluster.b);
		}
		
		return score;
	}
	
	//不同的ClusterSequence执行getNext所得到的ClusterSequence中可能有几个是以前出现过的
	//所以要注意去重
	public ArrayList<ClusterSequenceMaker> getNext()
	{
		int n=clusters.length;
		ArrayList<ClusterSequenceMaker> result=new ArrayList<ClusterSequenceMaker>(n);
		
		for(int i=0;i<n;i++)
		{
			Type type=clusters[i].b;
			int index=clusters[i].c+1;
			if(cr.ranking[type.ordinal()].length>index)
			{
				result.add(new ClusterSequenceMaker(this,i));
			}
		}
		
		return result;
	}

	@Override
	public int hashCode()
	{
		return this.toString().hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return this.toString().equals(obj.toString());
	}
	
	public String toString()
	{
		StringBuilder sb=new StringBuilder();
		for(Tuple3<Cluster,Type,Integer> t : clusters)
		{
			sb.append(t.a.id);
			sb.append("#");
		}
		
		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<Tuple2<Cluster,Type>[]> getFullArray()
	{
		Tuple2<Cluster,Type> c[]=new Tuple2[clusters.length];
		
		for(int i=0;i<clusters.length;i++)
		{
			c[i]=new Tuple2<Cluster,Type>(clusters[i].a,clusters[i].b);
		}
		
		return ArrayUtil.getFullArray(c);
	}
	
	public ClusterSequence[] getFullArray(int num,double distance,ClusterDistance cd,double[][] toAll,double[][] toEnd)
	{
		ArrayList<Tuple2<Cluster,Type>[]> list=this.getFullArray();
		int n=list.size();
		ArrayList<ClusterSequence> total=new ArrayList<ClusterSequence>();
		
		for(int i=0;i<n;i++)
		{
			ClusterSequence cs=new ClusterSequence(list.get(i),distance,cd,toAll,toEnd);
			//只考虑有可能出现的序列
			if(cs.min<distance)
				total.add(cs);
		}
		
		//return ArrayUtil.getMax_Quick(Arrays.copyOf(total, count), num);
		ClusterSequence[] result=total.toArray(new ClusterSequence[0]);
		Arrays.sort(result);
		return result;
	}
	
	public POISequenceMaker getPOISequenceMaker()
	{
		return new POISequenceMaker(clusters);
	}
}
