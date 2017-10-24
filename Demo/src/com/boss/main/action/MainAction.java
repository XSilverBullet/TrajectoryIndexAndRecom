package com.boss.main.action;

import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.boss.common.BaseAction;

public class MainAction extends BaseAction
{
	private static final long serialVersionUID = -8949934832160978018L;
	private static final Map<String,Object> application=ActionContext.getContext().getApplication();
	static
	{
		application.put("places", Interface.getAllPalces());
		application.put("topics", Interface.getTypes());
	}
	
	public String execute()
	{
		this.setResponse("places", application.get("places"));
		this.setResponse("topics", application.get("topics"));
		return SUCCESS;
	}
	
	public String getMinDistance()
	{
		try
		{
			double sLat=Double.valueOf(this.getRequest("sLat"));
			double sLng=Double.valueOf(this.getRequest("sLng"));
			double dLat=Double.valueOf(this.getRequest("dLat"));
			double dLng=Double.valueOf(this.getRequest("dLng"));
			String place=this.getRequest("place");
			
			Interface i=this.getInterface(place);
			double d=i.getMinDistance(sLng, sLat, dLng, dLat);
			this.setCallback("distance",d);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			this.setCallback("msg", e.getMessage());
		}
		
		return SUCCESS;
	}
	
	private Interface getInterface(String place) throws Exception
	{
		Interface i=(Interface)this.getSession("interface");
		if(i==null || !i.getPlace().equals(place))
		{
			i=new Interface(place);
			this.setSession("interface", i);
		}
		return i;
	}
	
	public String run()
	{
		try
		{
			Map<String,String[]> m=this.request.getParameterMap();
			
			String place=m.get("place")[0];
			double threshold=Double.valueOf(m.get("threshold")[0]);
			String topics[]=m.get("topics[]"),s[]=m.get("s[]"),e[]=m.get("e[]");
			
			if(topics.length>8)
				throw new RuntimeException("Eight topics can be chosen at most!");
			else if(topics.length==0)
				throw new RuntimeException("One topic must be chosen at least!");
			
			Interface i=this.getInterface(place);
			this.setCallback("pois",i.query(topics, s, e, threshold));
		}
		catch(Exception e)
		{
			this.setCallback("msg", e.getMessage());
		}
		
		return SUCCESS;
	}
}
