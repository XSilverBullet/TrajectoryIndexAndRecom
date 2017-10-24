package com.boss.common;

import java.util.*;

import com.opensymphony.xwork2.ActionSupport;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

public abstract class BaseAction extends ActionSupport
{
	private static final long serialVersionUID = -8678949846002895149L;
	protected HttpServletRequest request = ServletActionContext.getRequest();
	protected HttpSession session = request.getSession();
	private HashMap<String, Object>  callback=new HashMap<String, Object>();
	private boolean bool;

	public static final String BOOL="bool";
	
	public void setCallback(boolean bool)
	{
		this.bool=bool;
	}
	
	public void setCallback(Map<String, Object> callback)
	{
		this.callback.putAll(callback);
	}
	
	public boolean getBool()
	{
		return bool;
	}

	public HashMap<String, Object> getCallback()
	{
		return callback;
	}
	
	protected void setCallback(String key,Object value)
	{
		callback.put(key, value);
	}
	
	protected String getRequest(String name)
	{
		return request.getParameter(name);
	}
	
	protected void setResponse(String name,Object value)//返回给前台的信息
	{
		request.setAttribute(name, value);
	}
	
	protected Object getSession(String name)
	{
		return session.getAttribute(name);
	}
	
	protected void setSession(String key,Object value)
	{
		session.setAttribute(key, value);
	}
	
	protected void removeFromSession(String name)
	{
		session.removeAttribute(name);
	}
}
