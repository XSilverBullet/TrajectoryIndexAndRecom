package boss.project.network;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spatialindex.rtree.RTree;
import spatialindex.spatialindex.*;
import spatialindex.storagemanager.*;

//其实应该使用类似于构造者模式，构造过程应该分离开来
public class Network
{
	public final Map<Integer, Edge> edges = new HashMap<Integer, Edge>();	
		
	
	
	public final Map<Integer, Vertex> vertices= new HashMap<Integer, Vertex>();
	//public final RTree edgeIndex=new RTree(new PropertySet(),new MemoryStorageManager());
	public final RTree vertexIndex=new RTree(new PropertySet(),new MemoryStorageManager());//构建R树可以考虑用多线程，它可以和edges同时构建
	
	public Network(String vertexFile, String edgeFile) throws IOException 
	{
		loadVertices(vertexFile);
		loadEdges(edgeFile);
	}

	private void loadVertices(String fileName) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		
		try
		{
			for(String line=reader.readLine();line != null;line = reader.readLine()) 
			{
				String[] fields = line.split("\t");
	
				int id = Integer.valueOf(fields[0]).intValue();
				double lat = Double.valueOf(fields[1]).doubleValue();
				double lng = Double.valueOf(fields[2]).doubleValue();
				Vertex v = new Vertex(id, lat, lng);
				vertices.put(id, v);
				
				double point[]=new double[]{lng,lat};
				Region r=new Region(point,point);
				vertexIndex.insertData(null, r, id);
			}
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public Vertex getVertex(int id)
	{
		return this.vertices.get(id);
	}
	
	private void loadEdges(String fileName) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		try
		{
			for(String line = reader.readLine();line != null;line = reader.readLine())
			{
				String[] fields = line.split("\t");
				Integer id = Integer.valueOf(fields[0]).intValue();
				Integer startId = Integer.valueOf(fields[1]).intValue();
				Integer endId = Integer.valueOf(fields[2]).intValue();
				Double length = Double.valueOf(fields[3]).doubleValue();
	
				Vertex start = vertices.get(startId);
				Vertex end = vertices.get(endId);
				
				Edge e=new Edge(id, start, end, length);
				start.registerEdge(e);
				end.registerEdge(e);
				/////////////////////////////////////////////////////////////////
				edges.put(id, e);
			}
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	// =======================================================================
//	public Set<Edge> rangeQueryEdges(Location p, double radius)
//	{
//		Region r=new Region(new double[]{p.iLng-radius,p.iLat-radius},new double[]{p.iLng+radius,p.iLat+radius});
//		
//		Visitor v=new Visitor();
//		this.edgeIndex.containmentQuery(r,v);
//		HashSet<Edge> set=new HashSet<Edge>();
//		
//		for(int id : v.getIds())
//		{
//			set.add(this.edges.get(id));
//		}
//		
//		return set;
//	}

//	public ArrayList<Vertex> rangeQueryVertices(Vertex v,double distance)
//	{
//		distance/=Constants.D_GPS_DISTANCE;
//		return rangeQueryVertices(new Region(new double[]{v.gpPoint.getLng()-distance,v.gpPoint.getLat()-distance},
//				new double[]{v.gpPoint.getLng()+distance,v.gpPoint.getLat()+distance}));
//	}
//	public ArrayList<Vertex> rangeQueryVertices(Region r)
//	{
//		Visitor v=new Visitor();
//		this.vertexIndex.containmentQuery(r,v);
//		ArrayList<Vertex> set=new ArrayList<Vertex>();
//		
//		for(int id : v.getIds())
//		{
//			set.add(this.vertices.get(id));
//		}
//		
//		return set;
//	}
	
	public Vertex getNearestVertex(Location l)
	{
		Visitor v=new Visitor();
		double temp[]=new double[]{l.getLng(),l.getLat()};
		this.vertexIndex.nearestNeighborQuery(1, new Region(temp,temp), v);
		
		return this.vertices.get(v.getIds().get(0));
	}

	public Map<Integer, Edge> getEdges() 
	{
		return edges;
	}

	public Map<Integer, Vertex> getVertices()
	{
		return vertices;
	}
}

class Visitor implements IVisitor
{
	private List<Integer> ids=new ArrayList<Integer>();
	
	@Override
	public void visitNode(INode n)
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public void visitData(IData d)
	{
		ids.add(d.getIdentifier());
	}

	public List<Integer> getIds()
	{
		return ids;
	}
}