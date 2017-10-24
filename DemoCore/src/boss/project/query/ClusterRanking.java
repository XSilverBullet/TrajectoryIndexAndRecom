package boss.project.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class ClusterRanking
{
	/**
	 * 二维数组。每行的cluster根据每行的type进行排序。
	 */
	Cluster ranking[][];
	
	public ClusterRanking(Cluster c[],POI.Type... types)
	{
		int length=POI.Type.values().length;
		
		ranking=new Cluster[length][];
		
		for(int i=0;i<types.length;i++)
		{
			int position=types[i].ordinal();
			ArrayList<Cluster> list=new ArrayList<Cluster>();
			for(int j=0;j<c.length;j++)
			{
				if(c[j].pois[position].length!=0)//长度为零说明该聚类不包含types[i]的poi
					list.add(c[j]);
			}
			ranking[position]=list.toArray(new Cluster[0]);
			
			Arrays.sort(ranking[position], new ClusterComparator(types[i]));
		}
	}
	
	public ClusterSequenceMaker getFirst(POI.Type... types)
	{
		int n=types.length;
		
		for(int i=0;i<n;i++)
		{
			if(ranking[types[i].ordinal()].length==0)
				throw new NoSuchElementException("There isn't a poi of "+types[i]+".");
		}
		
		return new ClusterSequenceMaker(this,types);
	}
}

class ClusterComparator implements Comparator<Cluster>
{
	int typeId;
	
	public ClusterComparator(POI.Type type)
	{
		typeId=type.ordinal();
	}
	
	public int compare(Cluster arg0, Cluster arg1)//应为Arrays.sort是升序，所以这个应该是arg0>arg1时返回-1
	{
		double rating0=arg0.pois[typeId][0].rating,
				rating1=arg1.pois[typeId][0].rating;
				
		if(rating0<rating1)
			return 1;
		else if(rating0==rating1)
			return 0;
		else
			return -1;
	}
}