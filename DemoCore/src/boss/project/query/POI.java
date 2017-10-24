package boss.project.query;

import java.util.ArrayList;
import java.util.List;

import boss.project.network.Location;
import boss.project.network.Vertex;

public class POI implements Comparable<POI>
{
	final public Vertex v;
	public double rating = -1;
	public Type type = Type.NONE;
	public Location l;
	public String name;
	public String address;
	public String phone;
	public String telephone;
	
	public POI(Vertex v,double rating,Type type,Location l)
	{
		this.v=v;
		this.rating=rating;
		this.type=type;
		this.l=l;
	}
	
	public POI(Vertex v,double rating,Type type,Location l,String name,String address,String phone,String telephone)
	{
		this.v=v;
		this.rating=rating;
		this.type=type;
		this.l=l;
		this.name=name;
		this.address=address;
		this.phone=phone;
		this.telephone=telephone;
	}
	public enum Type{NONE,Bank,ParkingLot,Company,Hospital,Hotel,Market,
		Restaurant,School,Shop,Toilet}

	@Override
	public int compareTo(POI arg0)
	{
		if(rating<arg0.rating)//应为Arrays.sort是升序，所以这里要返回1
			return 1;
		else if(rating==arg0.rating)
			return 0;
		else
			return -1;
	}
	
	//不包含NONE
	public static List<String> getTypes()
	{
		List<String> result=new ArrayList<String>();
		
		for(Type t:Type.values())
		{
			result.add(t.toString());
		}
		
		return result;
	}
	
	public String toString()
	{
		return v.iVertexID+":"+type.ordinal();
	}
}
