package boss.project.query;

import boss.project.query.POI.Type;
import boss.util.Tuple2;

public class ClusterSequence implements Comparable<ClusterSequence>
{
	Tuple2<Cluster,Type>[] c;
	double ratio;
	double max=0;
	double min=0;
	
	public ClusterSequence(Tuple2<Cluster,Type>[] c,double distance,ClusterDistance cd,double[][] toAll,double[][] toEnd)
	{
		this.c=c;
		int n=c.length;
		
		max=toAll[c[0].a.id][1]+toEnd[c[n-1].a.id][1];
		min=toAll[c[0].a.id][0]+toEnd[c[n-1].a.id][0];
		
		for(int i=0;i<n-1;i++)
		{
			max+=cd.getMaxDistance(c[i].a.id, c[i+1].a.id);
			min+=cd.getMinDistance(c[i].a.id, c[i+1].a.id);
		}
		
		if(max<=distance)
			ratio=1;
		else if(min>distance)
			ratio=0;
		else
			ratio=(distance-min)/(max-min);
	}

	@Override
	public int compareTo(ClusterSequence arg0)
	{
		if(ratio>arg0.ratio)
			return 1;
		else if(ratio==arg0.ratio)
			return 0;
		else
			return -1;
	}
	
	public String toString()
	{
		StringBuilder sb=new StringBuilder();
		
		for(Tuple2<Cluster,Type>  p:c)
		{
			sb.append(p.a.id);
			sb.append("#");
		}
		
		return sb.toString();
	}
}