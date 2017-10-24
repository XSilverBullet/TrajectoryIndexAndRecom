package boss.project.network.helper;

import java.util.*;

import boss.project.network.DijkstraForDistance;
import boss.project.network.Vertex;
import boss.util.LRUMap;

public class DijkstraHelper
{
	private final LRUMap<Vertex,DijkstraForDistance> mapForDijkstra;
	//private final LRUMap<String,Double> mapForResult;
	
	public DijkstraHelper(int sizeForDijkstra,int sizeForResult)
	{
		this.mapForDijkstra=new LRUMap<Vertex,DijkstraForDistance>(sizeForDijkstra);
		//this.mapForResult=new LRUMap<String,Double>(sizeForResult);
	}
	
	public synchronized DijkstraForDistance getDijkstra(Vertex v)
	{
		DijkstraForDistance d=mapForDijkstra.get(v);
		if(d==null)
		{
			d=new DijkstraForDistance(v);
			mapForDijkstra.put(v, d);
		}
		
		return d;
	}
	
	public double run(Vertex start,Vertex end)
	{
//		double result=this.getDijkstra(start).run(end);
//		this.mapForResult.put(start.iVertexID+"#"+end.iVertexID, result);
//		
//		return result;
		return this.getDijkstra(start).run(end);
	}
	
	public ArrayList<Double> run(Vertex start,List<Vertex> end)
	{
		return this.getDijkstra(start).run(end);
	}
	
//	public Double get(Vertex start,Vertex end)
//	{
//		return mapForResult.get(start.iVertexID+"#"+end.iVertexID);
//	}
}
