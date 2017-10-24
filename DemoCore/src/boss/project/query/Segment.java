package boss.project.query;

//表示两个POI之间的段
public class Segment implements Comparable<Segment>
{
	final POI start;//当start为null时，表示这个Segment是由起点到下一个Cluster
	final POI end;//当end为null时，表示这个Segment是由最后一个Cluster到终点
	
	final double max;//最大距离
	final double min;//最小距离
	double range;
	
	public Segment(POI start, POI end, double max,double min)
	{
		this.start = start;
		this.end = end;
		this.max=max;
		this.min=min;
		this.range=max-min;
	}

	@Override
	public int compareTo(Segment o)
	{
		if(range<o.range)//Arrays.sort是升序，所以这个是当this>o时返回-1
			return 1;
		else if(range==o.range)
			return 0;
		else
			return -1;
	}

	@Override
	public int hashCode()
	{
		return (start+"#"+end).hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		Segment other = (Segment) obj;
		if (end != other.end)
			return false;
		if (start != other.start)
			return false;
		return true;
	}
}
