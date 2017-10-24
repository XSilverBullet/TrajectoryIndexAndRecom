package boss.project.network;

import java.util.ArrayList;
import java.util.List;

//import spatialindex.spatialindex.Point;
//import spatialindex.spatialindex.Region;

public class Edge 
{
	final public int iEdgeID;
	final public Vertex vStart;
	final public Vertex vEnd;
	final public double iLength;
	final public List<Location> points;
	//final public Region mbr;
	
	public Edge(Integer id, Vertex start, Vertex end, double length) 
	{
		this.iEdgeID = id;
		this.vStart = start;
		this.vEnd = end;
		this.iLength = length;
		points=new ArrayList<Location>();
		//mbr=new Region();
	}

	public int getiEdgeID() 
	{
		return iEdgeID;
	}

	public Vertex getvStart() 
	{
		return vStart;
	}

	public Vertex getvEnd()
	{
		return vEnd;
	}

	public double getiLength() 
	{
		return iLength;
	}

	public List<Location> getPoints() 
	{
		return points;
	}
	
	public void setPoints(String pointsStr) 
	{
		String[] fields = pointsStr.split("\t");
		for (int i = 0; i < fields.length; i += 2)
		{
			double lat = Double.valueOf(fields[i]).doubleValue();
			double lng = Double.valueOf(fields[i + 1]).doubleValue();
			Location p=new Location(lat, lng);
			points.add(p);
			//this.mbr.combine(new Point(new double[]{p.iLng,p.iLat}));
		}
	}

//	public Region getMbr() 
//	{
//		return mbr;
//	}
//	
//	public double getMinDistanceNoSqrt(Location p)
//	{
//		double minDist=Double.MAX_VALUE;
//		for(Location loc:points)
//		{
//			double newDist=Location.getDistanceNoSqrt(loc, p);
//			if(newDist<minDist)
//			{
//				minDist=newDist;
//			}
//		}
//		
//		return minDist;
//	}
	
	@Override
	public int hashCode()
	{
		return this.iEdgeID;
	}
}
