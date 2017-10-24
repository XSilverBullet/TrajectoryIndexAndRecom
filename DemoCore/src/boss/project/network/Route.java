package boss.project.network;

import java.util.Arrays;

public class Route
{
	public final Edge[] edges;
	
	public Route(Edge[] edges)
	{
		this.edges=Arrays.copyOf(edges, edges.length);
	}
	
	public Route(Edge[] original,Edge last)
	{
		if(original==null)
			this.edges=new Edge[1];
		else
			this.edges=Arrays.copyOf(original, original.length+1);
		
		//original可能是null，所以不能用来获得长度
		this.edges[this.edges.length-1]=last;
	}
	
	public Route(Route original,Edge last)
	{
		if(original==null)
			this.edges=new Edge[1];
		else
			this.edges=Arrays.copyOf(original.edges, original.edges.length+1);
		
		this.edges[this.edges.length-1]=last;
	}
	public double getLength()
	{
		double length=0;
		for(Edge e:edges)
		{
			length+=e.iLength;
		}
		return length;
	}
	
	public Vertex getStartVertex()
	{
		return edges[0].vStart;
	}
	
	public Vertex getEndVertex()
	{
		return edges[edges.length-1].vEnd;
	}
}
