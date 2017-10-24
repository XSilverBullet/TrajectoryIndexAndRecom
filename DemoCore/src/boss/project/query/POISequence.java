package boss.project.query;

public class POISequence
{
	final POI pois[];
	
	double maxDistance;
	double minDistance;
	
	static final POISequence empty=new POISequence(null,Double.NEGATIVE_INFINITY,0);//找一个返回值最小的数，省得代码中一直判断是不是null
	
	POISequence(POI pois[],double maxDistance,double minDistance)
	{
		this.pois=pois;
		this.maxDistance=maxDistance;
		this.minDistance=minDistance;
	}
	
	public POI getPOI(int position)
	{
		return pois[position];
	}
	double getScore()
	{
		if(this==POISequence.empty)
			return Double.MIN_VALUE;
		
		double result=0;
		int n=pois.length;
		for(int i=0;i<n;i++)
		{
			result+=pois[i].rating;
		}
		
		return result;
	}
	
//	public String toString()
//	{
//		StringBuilder sb=new StringBuilder();
//		
//		for(POI p:pois)
//		{
//			sb.append(p.type.ordinal());
//			sb.append("#");
//		}
//		
//		return sb.toString();
//	}
}
