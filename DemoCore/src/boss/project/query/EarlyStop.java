package boss.project.query;

import boss.project.network.RouteNotExistException;
import boss.project.network.Vertex;
import boss.project.query.POI.Type;
import boss.util.codehelper.Consumer;

public class EarlyStop
{
	@SuppressWarnings("deprecation")
	public static POISequence query(final Query query,int seconds,final Vertex start,final Vertex end,final double distance,final Type... types)
	{
		final Object lock=new Object();
		
		class Q extends Thread
		{
			private POISequence result=POISequence.empty;
			private RuntimeException e;
			
			public RuntimeException getException()
			{
				return e;
			}

			@SuppressWarnings("unused")
			public void setResult(POISequence result)
			{
				this.result=result;
			}
			
			public void run()
			{
				try
				{
					final Thread thread=this;
					query.query(new Consumer<POISequence>()	{
						public void accept(POISequence t)
						{
							result=t;
							if(result.getScore()>=types.length*900)
							{
								thread.stop();
							}
						}
					}, start, end, distance, types);
				}
				catch(RuntimeException e)
				{
					this.e=e;
				}
				finally
				{
					synchronized(lock)
					{
						lock.notifyAll();
					}
				}	
			}
		}
		
		Q q=new Q();
		q.start();
		
		try
		{
			synchronized(lock)
			{
				if(seconds==0)
					lock.wait();
				else
					lock.wait(seconds*1000);
			}
		}
		catch (InterruptedException e){}
		finally
		{
			q.stop();
		}
		
		RuntimeException e=q.getException();
		if(e != null)
			throw e;
		else if(q.result==POISequence.empty)
			throw new RouteNotExistException("No such Route");
		else
			return q.result;
	}
}