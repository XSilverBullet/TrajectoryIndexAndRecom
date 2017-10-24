package boss.project.network;

public class Location
{
	public static final double DIVISOR = 1000000;
	final public int iLat;
	final public int iLng;

	public Location() 
	{
		this.iLat = Integer.MAX_VALUE;
		this.iLng = Integer.MAX_VALUE;
	}

	public Location(double lat, double lng)
	{
		this.iLat = (int) (lat * DIVISOR);
		this.iLng = (int) (lng * DIVISOR);
	}

	public Location(Location location)
	{
		if (location != null) 
		{
			this.iLat = (int) (location.getLat() * DIVISOR);
			this.iLng = (int) (location.getLng() * DIVISOR);
		} 
		else 
		{
			this.iLat = Integer.MAX_VALUE;
			this.iLng = Integer.MAX_VALUE;
		}
	}

	public double getLat() 
	{
		return iLat / DIVISOR;
	}

	public double getLng() 
	{
		return iLng / DIVISOR;
	}
}
