package boss.project;

import java.io.*;
import java.util.*;

import boss.project.network.*;
import boss.project.query.*;

public class Place
{
	enum Config
	{
		北京("Beijing","D:/Demo3/Beijing/vertices.txt","D:/Demo3/Beijing/edges.txt",
				"D:/Demo3/Beijing/clusterResult15.0c0.8","D:/Demo3/Beijing/maxmin22.txt");
		
		final public String EnglishName,vertices,edges,clusters,distance;

		private Config(String EnglishName,String vertices, String edges,String clusters, String distance)
		{
			this.EnglishName=EnglishName;
			this.vertices = vertices;
			this.edges = edges;
			this.clusters = clusters;
			this.distance = distance;
		}
	}
	
	public final Network nw;
	public final ClusterDistance cd;
	public final Cluster[] clusters;
	
	public Place(String place) throws IOException
	{
		this(Config.valueOf(place));
	}
	
	public Place(Config c) throws IOException
	{
		this.nw=new Network(c.vertices,c.edges);
		this.cd=new ClusterDistance(c.distance);
		this.clusters=this.getClusters(c.clusters,this.nw);
//		}
	}
	
	public double getMaxDistance(Cluster i,Cluster j)
	{
		return this.cd.getMaxDistance(i.id, j.id);
	}
	
	public double getMinDistance(Cluster i,Cluster j)
	{
		return this.cd.getMinDistance(i.id, j.id);
	}
	
	private Cluster[] getClusters(String file,Network nw) throws IOException
	{
		List<Cluster> list=new ArrayList<Cluster>();
		File dictionary=new File(file);
		
		for(File f : dictionary.listFiles())
		{
			list.add(new Cluster(Integer.valueOf(f.getName().split("\\.")[0]),f.getAbsolutePath(),nw.vertices));
		}
		
		return list.toArray(new Cluster[0]);
	}
	
	public static Map<String,String> getAllPalces()
	{
		Map<String,String> m=new HashMap<String,String>();
		
		for(Config c:Config.values())
		{
			m.put(c.toString(), c.EnglishName);
		}
		
		return m;
	}
}
