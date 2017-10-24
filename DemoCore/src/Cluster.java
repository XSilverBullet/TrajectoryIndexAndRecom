import java.io.*;
import java.sql.SQLException;
import java.util.*;

import spatialindex.rtree.RTree;
import spatialindex.spatialindex.IData;
import spatialindex.spatialindex.INode;
import spatialindex.spatialindex.IVisitor;
import spatialindex.spatialindex.Region;
import spatialindex.storagemanager.MemoryStorageManager;
import spatialindex.storagemanager.PropertySet;
import boss.project.network.Location;
import boss.project.network.Vertex;

//代码是从C++转过来的，所以接口设计以及程序的写法比较奇怪
public class Cluster
{
	static final double MinLat=39.416668;
	static final double MaxLat=39.99604;
	static final double MinLng=115.989601;
	static final double MaxLng=116.994789; // real data
	private static final double Eps=0.005;
	private static final int Row=(int)((MaxLat-MinLat)/Eps)+1;
	private static final int Col=(int)((MaxLng-MinLng)/Eps)+1;
	
	private double MinPts=30;
	private double threshold=0.9;// 越小，类内部点距离越远
	
	int ClusterSum = 0;
	int clusteredPOIsum=0;
	int totalPoint = 0;
	
	List<POI> SetOfPoints=new ArrayList<POI>();
	@SuppressWarnings("unchecked")
	List<Integer> mat[][]=new ArrayList[Row][Col];
	
	String fileForPOI;
	public Cluster(String fileForPOI)
	{
		this.fileForPOI=fileForPOI;
		
		for(int i=0;i<Row;i++)
		{
			for(int j=0;j<Col;j++)
			{
				mat[i][j]=new ArrayList<Integer>();
			}
		}
	}
	
	void Read() throws ClassNotFoundException, SQLException
	{
		String sql="select id,name,type,address,phone,telephone,longitude,latitude,vlat,vlng,vid,rating from reduction";

	    DatabaseHelper dbh=new DatabaseHelper();
	    List<Map<String,Object>> contents;
	    try
	    {
	    	contents=dbh.getResultList(sql, null);
	    }
	    finally
	    {
	    	dbh.closeConnection();
	    }
	    
	    for(Map<String,Object> record : contents)
	    {
			POI p=new POI();
			p.name=(String) record.get("name");
			p.lat= (double) record.get("latitude");
			p.lng= (double) record.get("longitude");
			p.phone = (String) record.get("phone");
			p.telephone = (String) record.get("telephone");
			p.rating = (int) record.get("rating");
			p.vlat=(double) record.get("vlat");
			p.vlng=(double) record.get("vlng");
			p.vid=(int) record.get("vid");
			p.address=(String) record.get("address");
			p.type=(String) record.get("type");
			
			if (p.rating < 600)
				continue;
			
			p.cid = -1;

			SetOfPoints.add(p);
	    }
	    
		System.out.println("The total number of POIs is "+SetOfPoints.size());
	}
	 
	boolean judgeDistance(POI p1, POI p2)
	{
		double similarity = ( 4*Eps - ( Math.abs(p1.vlat - p2.vlat) + Math.abs(p1.vlng - p2.vlng) ) ) / (4*Eps);
		
		if(similarity >= threshold) return true;
		else return false;
	}

	void ExpandCluster(int kernal)
	{
		List<Integer> que=new ArrayList<Integer>(),temp=new ArrayList<Integer>();
		
		int latertag = 0;
		int beforetag = 0;

		boolean flag[]=new boolean[SetOfPoints.size()];
		Arrays.fill(flag, false);
		
		latertag++;
		que.add(kernal);
		while(beforetag<latertag)
		{
			int n = que.get(beforetag++);
			int row = (int) ((SetOfPoints.get(n).vlat - MinLat) / Eps);
			int col = (int) ((SetOfPoints.get(n).vlng - MinLng) / Eps);
			
			temp.clear();

			// search the neighbour eight grids centered with kernal
			for(int i=row-1; i<=row+1; i++)
				for(int j=col-1; j<=col+1; j++)
				{
					if(i>=0 && i<Row && j>=0&& j<Col)
					{
						for(int k:mat[i][j])
						{
							if (!flag[k])
							{
								if (judgeDistance(SetOfPoints.get(k), SetOfPoints.get(n)))
									temp.add(k);
							}
						}
					}
				}
				
			//if the number of found POI is over the threshold, then make them a cluster
			int sum = temp.size();
			if(sum >= MinPts)
			{
				for(int i=0; i<sum; i++)
				{
					boolean is_exist = false;
					if (!flag[temp.get(i)])
					{
						if (!is_exist && SetOfPoints.get(temp.get(i)).cid < 0)
						{
							latertag++;
								
							que.add(temp.get(i));
							flag[temp.get(i)] = true;
						}
					}
				}
			}
			
			//聚类的数量太多最终效果不好
			if(latertag>=45)
				break;
		}
			
		if(latertag >= MinPts)
		{
			for(int i=0; i<latertag; i++)
			{
				SetOfPoints.get(que.get(i)).cid = ClusterSum;

				clusteredPOIsum++;
			}
			ClusterSum++;
		}

	}

	void DBSCAN()
	{
		for(int i=0; i<Row; i++)
			for(int j=0; j<Col; j++)
			{
				for(int k:mat[i][j])			
					if(SetOfPoints.get(k).cid < 0)// if the POI has not been classified, then do it now.
						ExpandCluster(k);			
			}
	}

	void GridIndex()
	{
		for(int i=0; i<SetOfPoints.size(); i++)
		{
			int row=(int) ((SetOfPoints.get(i).vlat-MinLat)/Eps);
			int col=(int) ((SetOfPoints.get(i).vlng-MinLng)/Eps);
			mat[row][col].add(i);
		}// put every POI into a specific grid
	}

	void WriteFile() throws IOException
	{
		String fileName=fileForPOI+"\\clusterResult"+MinPts+"c"+threshold;
		File f=new File(fileName);
		f.mkdirs();
		String separator="★";
		for(int i=0; i<SetOfPoints.size(); i++)
		{
			POI p=SetOfPoints.get(i);
			if(p.cid >= 0)
			{
				BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName+"\\"+p.cid+".txt",true)));
				try
				{
					StringBuffer sb=new StringBuffer();
					sb.append(p.name);
					sb.append(separator);
					sb.append(p.lat);
					sb.append(separator);
					sb.append(p.lng);
					sb.append(separator);
					sb.append(p.rating);
					sb.append(separator);
					sb.append(p.type);
					sb.append(separator);
					sb.append(p.vid);
					sb.append(separator);
					sb.append(p.phone);
					sb.append(separator);
					sb.append(p.telephone);
					sb.append(separator);
					sb.append(p.address);
					
					bw.append(sb.toString());
					bw.newLine();
				}
				finally
				{
					bw.close();
				}
			}
		}
	}

	void trimCluster() 
	{
		class Visitor implements IVisitor
		{
			public int id;
						
			@Override
			public void visitNode(INode n)
			{
				//   
			}
			
			@Override
			public void visitData(IData d)
			{
				this.id=d.getIdentifier();
			}
		}
		
		//寻找最近的POI，加入同一个聚类
		RTree tree=new RTree(new PropertySet(),new MemoryStorageManager());
		for(int i=0; i<SetOfPoints.size(); i++) 
		{
			POI p=SetOfPoints.get(i);
			if(p.cid >= 0) 
			{
				double point[]=new double[]{p.lng,p.lat};
				Region r=new Region(point,point);
				tree.insertData(null, r, i);
			}
		}
		
		totalPoint=clusteredPOIsum;
		Visitor v=new Visitor();
		for(int i=0; i<SetOfPoints.size(); i++) 
		{
			POI p=SetOfPoints.get(i);
			
			if(p.cid < 0) 
			{
				double temp[]=new double[]{p.lng,p.lat};
				
				tree.nearestNeighborQuery(1, new Region(temp,temp), v);
				
				if(p.rating>=900 && judgeDistance(p,SetOfPoints.get(v.id)))
				{
					p.cid=SetOfPoints.get(v.id).cid;
					totalPoint++;
				}
			}
		}
	}
	
	public static void main(String args[]) throws ClassNotFoundException, SQLException, IOException
	{
		//DatabaseHelper dbh=new DatabaseHelper();
//	    List<Map<String,Object>> contents;
//	    try
//	    {
//	    	String sql="select id,longitude,latitude from poi";
//	    	contents=dbh.getResultList(sql, null);
//	    	
//	    	List<Object[]> params=new NearVertex("E:\\vertices.txt").getNearestVertex(contents);
//	    	
//	    	sql="update poi set vid=?,vlat=?,vlng=? where id=? ";
//	    	dbh.processTransaction(sql, params);
//	    }
//	    finally
//	    {
//	    	dbh.closeConnection();
//	    }
		
		//Reduction.execute();
		
		long start, finish;
		Cluster c=new Cluster("E:");
		
		//for(int i=2;i<=5;i++)
		{
			//c.MinPts=i*10;
			//for(int j=0;j<7;j++)
			{
				System.out.println("s");
				c.MinPts=15;
				c.threshold=0.8;
				
				start = System.currentTimeMillis();
				c.Read();
				finish = System.currentTimeMillis();

				double duration = ((double)(finish-start)/1000);
				System.out.println("File Reading Cost : "+ duration);

				start = System.currentTimeMillis();
				c.GridIndex();
				finish = System.currentTimeMillis();

				duration = ((double)(finish-start)/1000);
				System.out.println("Grids Creating Time : "+ duration);

				start = System.currentTimeMillis();
				c.DBSCAN();
				finish = System.currentTimeMillis();
				System.out.println("The number of clusters is "+c.ClusterSum);

				duration = ( (double)(finish-start) / 1000 );
				System.out.println("DBSCAN runs "+ duration);

				System.out.println("Clustered Point Rate: "+ ((double)c.clusteredPOIsum)/c.SetOfPoints.size());
				System.out.println("Cluster Density: "+ ((double)c.ClusterSum)/Math.sqrt((double)c.clusteredPOIsum));
				
				c.trimCluster();
				System.out.println("Total Point Rate2: "+ ((double)c.totalPoint)/c.SetOfPoints.size());
				c.WriteFile();
				
				System.out.println();
				System.out.println();
			}
		}
	}
}


class POI
{
	String name;
	double lat;
	double lng;
	int rating;
	String type;
	int vid;
	double vlat;
	double vlng;
	String phone;
	String telephone;
	int cid;		//小于0说明还未被分配
	String address;
}

class NearVertex
{
	class Visitor implements IVisitor
	{
		public final long id;
		public double longitude,latitude;
		public int vid;
		
		public Visitor(int id)
		{
			this.id=id;
		}
		
		@Override
		public void visitNode(INode n)
		{
			// TODO Auto-generated method stub
		}
		
		@Override
		public void visitData(IData d)
		{
			this.vid=d.getIdentifier();
			Location l=vertices.get(this.vid).getGpPoint();
			longitude=l.getLng();
			latitude=l.getLat();
		}
	}
	
	RTree vertexIndex=new RTree(new PropertySet(),new MemoryStorageManager());
	public final Map<Integer, Vertex> vertices= new HashMap<Integer, Vertex>();
	
	public NearVertex(String fileForVertex) throws NumberFormatException, IOException
	{		
		BufferedReader reader = new BufferedReader(new FileReader(fileForVertex));
		
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
	
	public List<Object[]> getNearestVertex(List<Map<String,Object>> contents)
	{
		List<Object[]> result=new ArrayList<Object[]>();
		
		for(Map<String,Object> m :contents)
		{
			Visitor v=new Visitor((int)((long) m.get("id")));
			double temp[]=new double[]{(double)m.get("longitude"),(double)m.get("latitude")};
			vertexIndex.nearestNeighborQuery(1, new Region(temp,temp), v);
			result.add(new Object[]{v.vid,v.latitude,v.longitude,v.id});
		}
		
		return result;
	}
}

//只需保留具有相同vid,type的组中rating最大的记录
class Reduction
{
	static void execute() throws ClassNotFoundException, SQLException
	{
		DatabaseHelper dbh=new DatabaseHelper();
		
	    try
	    {
	    	//把具有相同vid,type的组中rating最大的记录插入reduction表
	    	String sql="insert into reduction SELECT id,name,poi.type,address,phone,telephone,longitude,latitude,poi.rating,poi.vid,vlng,vlat from "+
		"poi,( SELECT MAX(poi.rating) as r,type,vid from poi GROUP BY vid,type) as a where  poi.vid=a.vid and poi.type=a.type and poi.rating=a.r";
	    	
	    	dbh.updateRecord(sql, null);
	    	
	    	//具有相同vid,type,rating的记录仍然有多条，只需保留一条
	    	//用delete from reduction where id not in (select id from reduction group by vid,type)的话MYSQL会报错
	    	sql="delete from reduction where id not in (select id from (select id,vid,type from reduction group by vid,type) as a)";
	    	dbh.updateRecord(sql, null);
	    	
	    	sql="delete from reduction where longitude<? or longitude>? or latitude<? or latitude>?";
	    	dbh.updateRecord(sql, new Object[]{Cluster.MinLng,Cluster.MaxLng,Cluster.MinLat,Cluster.MaxLat});
	    }
	    finally
	    {
	    	dbh.closeConnection();
	    }
	}
}