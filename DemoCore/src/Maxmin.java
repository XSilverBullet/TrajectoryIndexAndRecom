import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import boss.project.network.DijkstraForDistance;
import boss.project.network.Network;
import boss.project.query.Cluster;
import boss.project.query.POI;


public class Maxmin
{
	public static void main(String args[]) throws IOException, InterruptedException
	{
		Network nw=new Network("E:\\vertices.txt","E:\\edges.txt");
		Cluster cs[]=Maxmin.getClusters("E:\\clusterResult15.0c0.8\\",nw);
		int n=cs.length;
		double result[][][]=new double[n][n][2];
		
		for(int i=0;i<n;i++)
		{
			for(int j=0;j<n;j++)
			{
				result[i][j][1]=Double.NEGATIVE_INFINITY;
				result[i][j][0]=Double.POSITIVE_INFINITY;
			}
		}
		
		ExecutorService pool = Executors.newFixedThreadPool(3);
		for(int i=0;i<n;i++)
		{
			pool.execute(new Run(cs,i,result));
		}
		pool.shutdown();
		pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		
		BufferedWriter br=new BufferedWriter(new FileWriter("E:\\maxmin22.txt"));
		try
		{
			br.write(String.valueOf(n));
			br.newLine();
			for(int i=0;i<n;i++)
			{
				for(int j=0;j<n;j++)
				{
					br.write(i+" "+j+" "+result[i][j][1]+" "+result[i][j][0]);
					br.newLine();
				}
			}
		}
		finally
		{
			try
			{
				br.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	private static Cluster[] getClusters(String file,Network nw) throws IOException
	{
		List<Cluster> list=new ArrayList<Cluster>();
		File dictionary=new File(file);
		
		for(File f : dictionary.listFiles())
		{
			list.add(new Cluster(Integer.valueOf(f.getName().split("\\.")[0]),f.getAbsolutePath(),nw.vertices));
		}
		
		return list.toArray(new Cluster[0]);
	}
}

class Run extends Thread
{
	Cluster cs[];
	int i;
	double result[][][];
	
	Run(Cluster cs[],int i,double result[][][])
	{
		this.cs=cs;
		this.i=i;
		this.result=result;
	}
	
	public void run()
	{
		int n=cs.length;
		for(POI pi : cs[i].getAllPOI())
		{
			DijkstraForDistance d=new DijkstraForDistance(pi.v);
			for(int j=0;j<n;j++)
			{
				double max=Double.MIN_VALUE,min=Double.MAX_VALUE;
				
				for(POI pj : cs[j].getAllPOI())
				{
					double distance=d.run(pj.v);
					
					if(distance>max)
						max=distance;
					if(distance<min)
						min=distance;
				}
				
				if(max>result[cs[i].id][cs[j].id][1])
					result[cs[i].id][cs[j].id][1]=max;
				if(min<result[cs[i].id][cs[j].id][0])
					result[cs[i].id][cs[j].id][0]=min;
			}
		}
		
		System.out.println(i);
	}
}
