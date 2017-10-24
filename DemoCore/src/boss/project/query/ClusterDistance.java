package boss.project.query;

import java.io.*;

public class ClusterDistance
{
	//max[i][j]表示ID为i的cluster到ID为j的cluster的最大距离。对于min同理
	double max[][],min[][];
	
	public ClusterDistance(String distance) throws IOException
	{
		BufferedReader br=new BufferedReader(new FileReader(distance));
		String s=null;
		try
		{
			s=br.readLine();
			int n=Integer.valueOf(s);
			
			max=new double[n][n];
			min=new double[n][n];
			for(s=br.readLine();s!=null && !s.equals("");s=br.readLine())
			{
				String t[]=s.split("\\s");
				
				int row=Integer.parseInt(t[0]);
				int col=Integer.parseInt(t[1]);
				max[row][col]=Double.parseDouble(t[2]);
				min[row][col]=Double.parseDouble(t[3]);
			}
		}
		finally
		{
			try
			{
				br.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public double getMaxDistance(int i,int j)
	{
		return max[i][j];
	}
	
	public double getMinDistance(int i,int j)
	{
		return min[i][j];
	}
}
