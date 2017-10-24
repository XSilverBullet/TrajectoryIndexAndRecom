package boss.project.network;

import java.util.ArrayList;
import java.util.List;

public class Vertex 
{
	final public int iVertexID;
	final public Location gpPoint;
	final private List<Edge> out;
	final private List<Edge> in;
	
	public Vertex(int id, double lat, double lng) 
	{
		this.iVertexID = id;
		this.gpPoint = new Location(lat, lng);
		out = new ArrayList<Edge>();
		in = new ArrayList<Edge>();
	}

	public int getiVertexID() 
	{
		return iVertexID;
	}

	public Location getGpPoint() 
	{
		return gpPoint;
	}

	public List<Edge> getAdjacentEdges() 
	{
		List<Edge> result=new ArrayList<Edge>(out.size()+in.size());
		result.addAll(out);
		result.addAll(in);		
		
		return result;
	}
	
	public List<Edge> getInAdjacentEdges()
	{
		return in;
	}
	
	public List<Edge> getOutAdjacentEdges()
	{
		return out;
	}
	
	public void registerEdge(Edge e)
	{
		if(e.vStart==this)
		{
			this.in.add(new Edge(e.iEdgeID, e.vEnd, e.vStart, e.iLength));
			this.out.add(e);
		}
		else
		{
			this.in.add(e);
			this.out.add(new Edge(e.iEdgeID, e.vEnd, e.vStart, e.iLength));
		}
	}

	@Override
	public int hashCode()
	{
		return this.iVertexID;
	}
	
	public boolean equals(Object o)
	{
		if(!(o instanceof Vertex))
			return false;
		
		return this.iVertexID==((Vertex)o).iVertexID;
	}
}
