import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.*;


/**
  *类描述：抽象数据库操作基类(通过数据源（数据连接池）)
  *@author: BOSS
  *@date： 日期：2014-4-23 时间：下午2:53:43
  *@version 1.2
  */
public class DatabaseHelper
{
	/*------------------------------------------------------
	 * private variables:
	 *------------------------------------------------------*/
	//con不应该在需要的时候临时获取，因为同一个con可以在不同的地方甚至是不同的线程使用
	protected Connection con = null;
	//同一个pstmt不能再不同的地方使用，但这里也不能需要的时候临时生成(现在可以用closeResultSet来解决这个问题)
	//因为在数据连接池下，con.close并不是真正关闭连接，而只是把连接交回连接池，此时数据库的游标资源并没被释放、
	//（若不用连接池，con.close时会把pstmt的资源一起释放）
	//由于返回Resultset后需要同时关闭con和pstmt，如果Helper对象不保存pstmt，调用者则无法关闭pstmt
	//Resutl会在pstmt关闭后自动关闭，所以临时生成即可
	
	/*------------------------------------------------------
	 * methods： 
	 *------------------------------------------------------*/
	/**
	 * 构造函数：初始化数据源（数据连接池）
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public DatabaseHelper() throws SQLException, ClassNotFoundException
	{
		String url = "jdbc:mysql://localhost:3306/test?"
                + "user=root&password=root";
		
		Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
		con = DriverManager.getConnection(url);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		this.closeConnection();
	}
	
	/**
	 * @throws SQLException
	 * 关闭连接，必须是public，否则把resultSet返回之后如何关闭Connection?
	 */
	public void closeConnection() throws SQLException
	{		
		con.close();
	}
	
	/**
	 * @param rs 需要关闭的结果集
	 * @throws SQLException
	 * 
	 * 该方法用于关闭由helper生成的结果集，如果传入的参数不是由helper传入的结果集会抛出异常
	 */
	public void closeResultSet(ResultSet rs) throws SQLException,IllegalArgumentException
	{		
		Statement stmt=rs.getStatement();
		
		if(stmt==null)
			throw new IllegalArgumentException("传入的结果集并非由此helper对象产生");
		
		Connection c=stmt.getConnection();
			
		if(c!=con)
			throw new IllegalArgumentException("传入的结果集并非由此helper对象产生");
		
		//rs.close();statement关闭的话相应的resultSet也会关闭
		stmt.close();
	}
	
	/**
	 * @param rs
	 * 方便在catch和finally语句块中关闭结果集，省得每次都得处理抛出的异常
	 */
	protected void closeRs(ResultSet rs)
	{
		try
		{
			this.closeResultSet(rs);
		}
		catch(SQLException e)
		{
			
		}
		catch(IllegalArgumentException e)
		{
			
		}
	}
	
	
	/**
	 * @param stmt
	 * 方便在catch和finally语句块中关闭Statement，省得每次都得处理抛出的异常
	 */
	protected void closeStmt(Statement stmt)
	{
		try
		{
			stmt.close();
		}
		catch(SQLException e)
		{
			
		}
	}
	
	/**
	 * 方便进行回滚操作
	 */
	protected void rollback()
	{
		try
		{
			this.con.rollback();
		}
		catch (SQLException ee)
		{
			ee.printStackTrace();
		}
	}
	
	/**
	 * @param sql
	 * @param params
	 * @throws SQLException
	 * 获得PreparedStatement
	 */
	protected PreparedStatement getPreparedStatement(String sql,Object params[]) throws SQLException
	{
		return this.getPreparedStatement(sql, params, Statement.NO_GENERATED_KEYS);
	}
	
	/**
	 * @param sql
	 * @param params
	 * @throws SQLException
	 * 获得PreparedStatement
	 */
	protected PreparedStatement getPreparedStatement(String sql,Object params[],int autoGeneratedKeys) throws SQLException
	{
		PreparedStatement pstmt = con.prepareStatement(sql,autoGeneratedKeys);
		
		try
		{
			if (params != null) 
			{
				int i;
				for (i = 0; i < params.length; i++)
				{
					pstmt.setObject(i + 1, params[i]);
				}
			}
		}
		catch(SQLException e)
		{
			this.closeStmt(pstmt);
			throw e;
		}
		
		return pstmt;
	}

	/**
	 * @return 执行select 等查询语句，获取查询结果返回结果集。由于返回的ResultSet以及对应的连接需要手动关闭，所以若非有必要，不要使用此函数
	 * @throws NamingException 
	 * @throws SQLException
	 */
	public ResultSet getResultSet(String sql,Object params[]) throws SQLException
	{
		PreparedStatement pstmt=getPreparedStatement(sql,params);
		
		ResultSet rs = null;
		
		try
		{
			rs = pstmt.executeQuery();
		}
		catch(SQLException e)
		{
			this.closeStmt(pstmt);
			
			throw e;
		}
		
		/*---------------------------------------------		
		 * 这里不能关闭Connection和PreparedStatement，否则返回的ResultSet无法使用
		 *-----------------------------------------------------------------*/
		return rs;
	}


	/**
	 * @param sql
	 * @param params
	 * @return 根据sql语句返回所有结果
	 * @throws SQLException
	 * @throws NamingException
	 */
	public List<Map<String, Object>> getResultList(String sql,Object params[]) throws SQLException, IllegalArgumentException
	{
		return this.getResultList(sql, params, 0, -1);
	}

	/**
	 * @param sql
	 * @param params
	 * @param startRow 从结果的第startRow条开始返回
	 * @param length 共返回length条结果
	 * @return 根据sql语句返回结果，用于分页
	 * @throws SQLException
	 * @throws NamingException
	 */
	public List<Map<String, Object>> getResultList(String sql,Object params[],int startRow, int length) throws SQLException, IllegalArgumentException
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		ResultSet rs = this.getResultSet(sql, params);
		
		try 
		{
			ResultSetMetaData rsmd = rs.getMetaData();
			int cols = rsmd.getColumnCount();

			if(startRow<0)
				throw new IllegalArgumentException("startRow 必须大于等于0！");
			
			/************
			 * 如果结果集记录为空则直接返回list。
			 * 虽然平时isAfterLast()会判断出来，但当结果集为空时isAfterLast（）会失效，所以必须在这里判断。
			 ****************/
			if (!rs.absolute(startRow + 1))
				return list;
			
			boolean lengthNeeded=length<0;
			for (int i = 0; (lengthNeeded || i < length) && !rs.isAfterLast(); i++) 
			{
				HashMap<String, Object> map = new LinkedHashMap<String, Object>();
				// 将每个字段的key/value对保存到HashMap中
				for (int j = 1; j <= cols; j++) 
				{
					String field = rsmd.getColumnLabel(j);
					Object value = rs.getObject(j);
					map.put(field, value);
				}
				// 将当前记录添加到List中
				list.add(map);
				rs.next();
			}
		} 
		finally 
		{
			this.closeRs(rs);
		}

		return list;
	}

	/**
	 * @param sql
	 * @param params
	 * @return 判断对应的SQL语句是否有记录
	 * @throws SQLException
	 * @throws NamingException
	 */
	public boolean isExist(String sql,Object params[]) throws SQLException, IllegalArgumentException
	{
		boolean result;
		ResultSet rs=getResultSet(sql,params);
		
		try
		{
			result = rs.next();
		}
		finally
		{
			this.closeRs(rs);
		}

		return result;
	}

	/**
	 * @param sql
	 * @param params
	 * @return 得到执行语句所获得的记录数量
	 * @throws SQLException
	 * @throws NamingException
	 */
	public int getResultNum(String sql,Object params[]) throws SQLException, IllegalArgumentException
	{
		ResultSet rs=getResultSet(sql,params);
		int rowNum=-1;
		
		try
		{
			rs.last();
			rowNum=rs.getRow();
		}
		finally
		{
			this.closeRs(rs);
		}
		
		return rowNum;
	}

	/**
	 * @return 当执行更新之类的操作：update,insert,delete，返回受影响的行数
	 * @throws NamingException 
	 * @throws SQLException
	 */
	public int updateRecord(String sql,Object[] params) throws SQLException
	{
		int rowsaffected=-1;
		PreparedStatement pstmt=this.getPreparedStatement(sql, params);
		
		try 
		{
			rowsaffected = pstmt.executeUpdate();
		}
		finally 
		{
			this.closeStmt(pstmt);
		}
		
		return rowsaffected;
	}

	/**
	 * @param sql 插入数据对应的SQL语句
	 * @param params 参数
	 * @return 插入数据所返回的id值
	 * @throws SQLException
	 * @throws NamingException
	 */
	public long insertARecord(String sql,Object[] params) throws SQLException, IllegalArgumentException
	{
		long id=-1;
		PreparedStatement pstmt=this.getPreparedStatement(sql, params, Statement.RETURN_GENERATED_KEYS);
		
		try 
		{
			pstmt.executeUpdate();
		}
		catch(SQLException e)
		{
			this.closeStmt(pstmt);
			
			throw e;
		}
		
		ResultSet rs=pstmt.getGeneratedKeys();
		try
		{
			rs.next();
			id=rs.getLong(1);
		}
		finally
		{
			this.closeRs(rs);
		}
		
		return id;
	}
	
	/**
	 * @param proc_name
	 * @param params
	 * @param level 事务的隔离级别
	 * @return 调用存储过程更新数据库(返回一个字符串参数，放在第一个)
	 * @throws SQLException
	 * @throws NamingException
	 */
	public String callProc(String proc_name,Object[] params,int level) throws SQLException
	{
		String result = null;
		if (null == proc_name || proc_name.equals(""))
			throw new IllegalArgumentException("存储过程名称不能为null或空");
		
		this.con.setAutoCommit(false);
		int originalLevel=con.getTransactionIsolation();
		this.con.setTransactionIsolation(level);
		
		CallableStatement cstmt = con.prepareCall("{ call " + proc_name + " }");
		
		try 
		{
			cstmt.registerOutParameter(1, Types.VARCHAR);//注意，参数从1开始
			if (params != null)
			{
				for (int i = 0; i < params.length; i++) 
				{
					cstmt.setObject(i + 2, params[i]);//注意是i+2而非i+1
				}
			}
			
			cstmt.execute();
			result = cstmt.getString(1);
			con.commit();
		}
		catch (SQLException e)
		{
			this.rollback();
			throw e;
		}
		finally 
		{
			this.closeStmt(cstmt);
			
			try
			{
				this.con.setTransactionIsolation(originalLevel);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			
			try
			{
				this.con.setAutoCommit(true);
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			
		}
		return result;
	}

	/**
	 * @param proc_name
	 * @param params
	 * @return 调用存储过程更新数据库(返回一个字符串参数，放在第一个)
	 * @throws SQLException
	 * @throws NamingException
	 * 隔离级别默认为READ_COMMITTED
	 */
	public String callProc(String proc_name,Object[] params) throws SQLException
	{
		return this.callProc(proc_name, params, Connection.TRANSACTION_READ_COMMITTED);
	}
	
	/**用于处理语句结构相同的处理事务 
	 * @param sql
	 * @param params
	 * @param level 事务的隔离级别
	 * @throws SQLException
	 * @throws NamingException
	 */
	public void processTransaction(String sql, List<Object[]> params,int level) throws SQLException 
	{
		this.con.setAutoCommit(false);
		int originalLevel=con.getTransactionIsolation();
		this.con.setTransactionIsolation(level);
		PreparedStatement pstmt=con.prepareStatement(sql);
		
		try 
		{			
			for (Object[] param : params)
			{
				for (int i = 0; i < param.length; i++) 
				{
					pstmt.setObject(i + 1, param[i]);
				}
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			// 事务提交
			con.commit();
		}
		catch (SQLException e)
		{
			this.rollback();
			throw e;
		}
		finally 
		{
			this.closeStmt(pstmt);
			
			try
			{
				this.con.setTransactionIsolation(originalLevel);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			
			try
			{
				this.con.setAutoCommit(true);
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**用于处理语句结构相同的处理事务 
	 * @param sql
	 * @param params
	 * @throws SQLException
	 * @throws NamingException
	 * 隔离级别默认为READ_COMMITTED
	 */
	public void processTransaction(String sql, List<Object[]> params) throws SQLException 
	{
		this.processTransaction(sql, params, Connection.TRANSACTION_READ_COMMITTED);
	}
	
	/**执行事务，要求传进来的函数第一个参数是DatabaseHelper或子类的对象，且所有的参数都不能是基本类型
	 * @param level 事务的隔离级别
	 * @param o 要执行事务的那个对象
	 * @param method 要执行事务的那个方法
	 * @param params 要执行事务的那个方法的参数（不包括第一个参数，第一个参数必须是DatabaseHelper或子类的对象）
	 * @throws SQLException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void processTransaction(int level,Object o,String method,Object params[]) throws SQLException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		Class<?> c=o.getClass();
		LinkedList<Class<?>> list=new LinkedList<Class<?>>();
		LinkedList<Object> objectList=new LinkedList<Object>();
		for(Object oo : params)
		{
			list.add(oo.getClass());
			objectList.add(oo);
		}
		list.addFirst(DatabaseHelper.class);
		objectList.addFirst(this);
		
		Method m=c.getDeclaredMethod(method, list.toArray(new Class<?>[0]));
		
		this.con.setAutoCommit(false);
		int originalLevel=con.getTransactionIsolation();
		this.con.setTransactionIsolation(level);
		
		try 
		{
			m.setAccessible(true);
			m.invoke(o, objectList.toArray(new Object[0]));
			this.con.commit();
		}
		catch (IllegalAccessException e)
		{
			this.rollback();
			throw e;
		}
		catch(IllegalArgumentException e)
		{
			this.rollback();
			throw e;
		}
		catch(InvocationTargetException e)
		{
			this.rollback();
			throw e;
		}
		catch(SQLException e)
		{
			this.rollback();
			throw e;
		}
		finally 
		{						
			try
			{
				this.con.setTransactionIsolation(originalLevel);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			
			try
			{
				this.con.setAutoCommit(true);
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**执行事务，要求传进来的函数第一个参数是DatabaseHelper或子类的对象，且所有的参数都不能是基本类型
	 * @param o 要执行事务的那个对象
	 * @param method 要执行事务的那个方法
	 * @param params 要执行事务的那个方法的参数（不包括第一个参数，第一个参数必须是DatabaseHelper或子类的对象）
	 * @throws SQLException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * 隔离级别默认为READ_COMMITTED
	 */
	public void processTransaction(Object o,String method,Object params[]) throws SQLException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		this.processTransaction(Connection.TRANSACTION_READ_COMMITTED, o, method, params);
	}
}
