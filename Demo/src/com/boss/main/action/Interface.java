package com.boss.main.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import boss.project.Place;
import boss.project.PlaceFactory;
import boss.project.network.Dijkstra;
import boss.project.network.Location;
import boss.project.network.Route;
import boss.project.network.Vertex;
import boss.project.query.EarlyStop;
import boss.project.query.POI;
import boss.project.query.POI.Type;
import boss.project.query.POISequence;
import boss.project.query.Query;
import boss.util.Tuple2;

public class Interface
{
	final Query q;
	final int maxTime=5;//函数最多执行5秒
	
	public Interface(String place) throws Exception
	{
		Place p=PlaceFactory.get(place);
		q=new Query(p);
	}
	
	public double getMinDistance(double sLng,double sLat,double dLng,double dLat) throws IOException
	{
		Location s=new Location(sLat,sLng),e=new Location(dLat,dLng);
		Vertex start=q.place.nw.getNearestVertex(s),end=q.place.nw.getNearestVertex(e);
		return q.getMinDistance(start, end);		
	}
	
	public List<Map<String,Object>> query(String topics[],String s[],String e[],double distance)
	{
		int n=topics.length;
		POI.Type types[]=new POI.Type[n];
		for(int i=0;i<n;i++)
		{
			types[i]=Interface.toType(topics[i]);
		}
		
		Location start=new Location(Double.valueOf(s[1]),Double.valueOf(s[0]));//注意经纬度顺序
		Location end=new Location(Double.valueOf(e[1]),Double.valueOf(e[0]));//注意经纬度顺序
		
		Vertex sV=q.place.nw.getNearestVertex(start),eV=q.place.nw.getNearestVertex(end);
		POISequence ps=EarlyStop.query(q,maxTime,sV,eV,distance,types);
		
		List<Map<String,Object>> pois=new ArrayList<Map<String,Object>>();
		for(int i=0;i<n;i++)
		{
			POI p=ps.getPOI(i);
			Location l=p.l;
			Map<String,Object> m=new HashMap<String,Object>();
			m.put("location", new double[]{l.getLng(),l.getLat()});
			m.put("type", toString(p.type));
			m.put("name", p.name);
			m.put("address", p.address);
			m.put("phone", p.phone);
			m.put("telephone", p.telephone);
			m.put("userRating", p.rating);
			pois.add(m);	
		}
		
		//处理终点
		Map<String,Object> destination=new HashMap<String,Object>();
		destination.put("location", new double[]{end.getLng(),end.getLat()});
		destination.put("type", "Destination");
		destination.put("name", "");
		pois.add(destination);
		//终点特殊处理
		Tuple2<List<double[]>,Double> t=Interface.getPoints(ps.getPOI(n-1).v,eV);
		destination.put("length", t.b);
		destination.put("route", t.a);
		
		//起点特殊处理
		t=Interface.getPoints(sV, ps.getPOI(0).v);
		pois.get(0).put("length", t.b);
		pois.get(0).put("route", t.a);
		
		for(int i=1;i<n;i++)
		{
			t=Interface.getPoints(ps.getPOI(i-1).v, ps.getPOI(i).v);
			pois.get(i).put("length", t.b);
			pois.get(i).put("route", t.a);
		}
		
		return pois;
	}
	
	private static Tuple2<List<double[]>,Double> getPoints(Vertex start,Vertex end)
	{
		Tuple2<List<double[]>,Double> result=new Tuple2<List<double[]>,Double>(null,(double) 0);
		List<double[]> list=new LinkedList<double[]>();
		
		if(start!=end)
		{
			Route r=new Dijkstra(start).run(end);
			int length=r.edges.length;
			for(int j=0;j<length;j++)
			{
				Location l=r.edges[j].vStart.gpPoint;
				double point[]=new double[]{l.getLng(),l.getLat()};
				list.add(point);
			}
			Location l=r.edges[length-1].vEnd.gpPoint;
			double point[]=new double[]{l.getLng(),l.getLat()};
			list.add(point);
			result.a=list;
			result.b=r.getLength();
		}
		else//如果源点和终点相等，那么不能跑迪杰斯特拉，否则会报错。起点和终点可能就和POI对应的点相等。另外，不同的POI可能对应同一个点。这些都可能使得start==end
		{
			Location l=start.gpPoint;
			double point[]=new double[]{l.getLng(),l.getLat()};
			list.add(point);
			list.add(point);
			result.a=list;
		}
		
		return result;
	}
	
	public String getPlace()
	{
		return q.place.toString();
	}
	
	public static Map<String,String> getAllPalces()
	{
		return Place.getAllPalces();
	}
	
	//不包含NONE
	public static List<String> getTypes()
	{
		List<String> list=POI.getTypes();
		ListIterator<String> i=list.listIterator();
		for(String s;i.hasNext();)
		{
			s=i.next();
			if(s.equals("NONE"))
				i.remove();
			else if(s.equals("ParkingLot"))
				i.set("Parking lot");
		}
		
		return list;
	}
	
	public static Type toType(String type)
	{
		return type.equals("Parking lot")?Type.ParkingLot:Type.valueOf(type);
	}
	
	public static String toString(Type type)
	{
		return type.equals(Type.ParkingLot)?"Parking lot":type.toString();
	}
}