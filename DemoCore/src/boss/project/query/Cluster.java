package boss.project.query;

import java.io.*;
import java.util.*;

import boss.project.network.Location;
import boss.project.network.Vertex;
import boss.project.query.POI.Type;

public class Cluster
{
	final POI pois[][];
	public final int id;
	
	public Cluster(int id,String file,Map<Integer, Vertex> vs) throws IOException
	{
		this.id=id;
		this.pois=new POI[POI.Type.values().length][];
		
		ArrayList<POI> list=new ArrayList<POI>();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		
		try 
		{
			for(String tempString = reader.readLine();tempString != null;tempString = reader.readLine()) 
			{
				
				double pRating;
				int pVid;
				String[] split = tempString.split("★");
				
				pRating = Double.parseDouble(split[3]);
				pVid = Integer.parseInt(split[5]);
				double lat=Double.parseDouble(split[1]),lng=Double.parseDouble(split[2]);
				
				POI p = new POI(vs.get(pVid),pRating,POI.Type.valueOf(split[4]),
						new Location(lat,lng),split[0],split[8],split[6],split[7]);
				list.add(p);
			}
		}
		finally
		{
			reader.close();
		}
		
		this.rankPOI(list);
	}
	
	@SuppressWarnings("unchecked")
	private void rankPOI(Iterable<POI> pois)
	{
		int n=POI.Type.values().length;
		
		ArrayList<POI> list[]=new ArrayList[n];
		for(int i=0;i<n;i++)
			list[i]=new ArrayList<POI>();
		
		for(POI p : pois)
		{
			list[p.type.ordinal()].add(p);
		}
		
		for(int i=0;i<n;i++)
		{
			this.pois[i]=list[i].toArray(new POI[0]);
			
			Arrays.sort(this.pois[i]);
		}
	}
	
	double getScore(Type t)
	{
		int id=t.ordinal();
		if(this.pois[id].length==0)
			throw new RuntimeException("This Cluster doesn't have a poi of "+t+".");
		
		return this.pois[id][0].rating;
	}
	
	public ArrayList<POI> getAllPOI()
	{
		ArrayList<POI> list=new ArrayList<POI>();
		
		for(int i=0;i<pois.length;i++)
		{
			int n=pois[i].length;
			for(int j=0;j<n;j++)
			{
				list.add(pois[i][j]);
			}
		}
		
		return list;
	}
}
