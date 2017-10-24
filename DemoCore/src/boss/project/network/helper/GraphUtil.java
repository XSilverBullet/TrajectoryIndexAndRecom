package boss.project.network.helper;

import boss.project.network.Constants;
import boss.project.network.Edge;
import boss.project.network.Location;
import boss.project.network.Vertex;

public class GraphUtil
{
	public static double getDistanceNoSqrt(Location p1, Location p2) 
	{
		double lat = Math
				.abs(((p1.getLat() - p2.getLat()) * Constants.D_GPS_DISTANCE));
		double lng = Math
				.abs(((p1.getLng() - p2.getLng()) * Constants.D_GPS_DISTANCE));

		return lat * lat + lng * lng;
	}

	public static double getDistance(Location p1, Location p2)
	{
		return Math.sqrt(getDistanceNoSqrt(p1, p2));
	}
	
	public static Vertex getTheOtherSide(Vertex v,Edge e)
	{
		int id=v.iVertexID;
		boolean start=id==e.vStart.iVertexID,end=id==e.vEnd.iVertexID;
		
		if(start&&!end)
			return e.vEnd;
		else if(!start&&end)
			return e.vStart;
		else if(!start && !end)
			return null;
		else
			return v;
	}
}
