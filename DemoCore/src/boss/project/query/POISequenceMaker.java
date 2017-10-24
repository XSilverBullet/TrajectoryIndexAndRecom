package boss.project.query;

import java.util.ArrayList;
import java.util.Arrays;

import boss.project.query.POI.Type;
import boss.util.Tuple3;

public class POISequenceMaker
{
	final Tuple3<Cluster,POI,Integer> pois[];
	final int position[];//方便根据type直接转换到所在的位置

	@SuppressWarnings("unchecked")
	POISequenceMaker(Tuple3<Cluster,Type,Integer> pois[])
	{
		int n=pois.length;
		this.pois=new Tuple3[n];
		
		for(int i=0;i<n;i++)
		{
			Tuple3<Cluster,Type,Integer> t=pois[i];
			this.pois[i]=new Tuple3<Cluster,POI,Integer>(t.a,t.a.pois[t.b.ordinal()][0],0);
		}
		
		this.position=new int[Type.values().length];
		Arrays.fill(position, -1);
		
		for(int i=0;i<n;i++)
		{
			position[this.pois[i].b.type.ordinal()]=i;
		}
	}
	
	private POISequenceMaker(POISequenceMaker p,int position)
	{
		this.pois= Arrays.copyOf(p.pois, p.pois.length);
		
		//虽然后面3行是BUG，但是这个BUG可以大大提高效率
//		Tuple3<Cluster,POI,Integer> origin=p.pois[position];
//		Tuple3<Cluster,POI,Integer> t=new Tuple3<Cluster,POI,Integer>(origin.a,origin.a.pois[origin.b.type.ordinal()][origin.c+1],origin.c+1);
//		this.pois[position]=t;
		
		Tuple3<Cluster,POI,Integer> t=pois[position];
		t.c=++t.c;
		t.b=t.a.pois[t.b.type.ordinal()][t.c];
		
		this.position=p.position;//位置是不变的
	}
	
	ArrayList<POISequenceMaker> getNext()
	{
		int n=this.pois.length;
		ArrayList<POISequenceMaker> result=new ArrayList<POISequenceMaker>(n);
		
		for(int i=0;i<n;i++)
		{
			Cluster c=pois[i].a;
			Type type=pois[i].b.type;
			if(pois[i].c<c.pois[type.ordinal()].length-1)
			{
				result.add(new POISequenceMaker(this,i));
			}
		}
		
		return result;
	}
	
	Cluster getCluster(Type type)
	{
		return pois[position[type.ordinal()]].a;
	}
	
	POI getPOI(Type type)
	{
		return pois[position[type.ordinal()]].b;
	}
	
	POI getPOI(int position)
	{
		Cluster c=this.pois[position].a;
		Type t=this.pois[position].b.type;
		
		return c.pois[t.ordinal()][this.pois[position].c];
	}
	
	POI[] getPOIs()
	{
		int n=this.pois.length;
		POI[] result=new POI[n];
		
		for(int i=0;i<n;i++)
		{
			result[i]=this.getPOI(i);
		}
		
		return result;
	}
	
	POISequence getPOISequence(ClusterSequence cs)
	{
		int n=cs.c.length;
		POI result[]=new POI[n];
		
		
		for(int i=0;i<n;i++)
		{
			result[i]=this.getPOI(cs.c[i].b);
		}
		
		return new POISequence(result,cs.max,cs.min);
	}
	
	double getScore()
	{
		double result=0;
		
		for(int i=0;i<pois.length;i++)
		{
			result+=this.getPOI(i).rating;
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
		for(Tuple3<Cluster,POI,Integer> t : pois)
		{
			sb.append(t.b.v.iVertexID);
			sb.append("#");
		}
		
		return sb.toString();
	}
}
