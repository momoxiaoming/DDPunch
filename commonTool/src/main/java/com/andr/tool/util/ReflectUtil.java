package com.andr.tool.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射调用工具类
 */
public class ReflectUtil 
{
	public static Object newInstance(String className, Class<?>[] params, Object[] paramsValue)
	{
		Object rlt = null;
		try 
		{
			Class<?> cls = ReflectUtil.getCls(className);
			Constructor<?> cons = cls.getDeclaredConstructor(params);
			rlt = cons.newInstance(paramsValue);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return rlt;
	}
	
	public static Class<?> getCls(String className)
	{
		try 
		{
			return Class.forName(className);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static Object invokeStaticMethod(String className, String methodName, Class<?>[] parameterTypes, Object... args)
	{
		return invokeStaticMethod(getCls(className),methodName,parameterTypes,args);
	}
	
	public static Object invokeStaticMethod(Class<?> cls, String methodName, Class<?>[] parameterTypes, Object... args)
	{
		if(null == cls)
		{
			return null;
		}
		
		Object rlt = null;
		try
		{
			Method m = cls.getDeclaredMethod(methodName, parameterTypes);
			m.setAccessible(true);
			rlt = m.invoke(cls, args);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return rlt;
	}
	
	public static Object invokeMethod(Object obj, String clsName, String methodName, Class<?>[] parameterTypes, Object... args)
	{
		if(null == obj)
		{
			return null;
		}
		
		Object rlt = null;
		try
		{
			Method m = getCls(clsName).getDeclaredMethod(methodName, parameterTypes);
			m.setAccessible(true);
			rlt = m.invoke(obj, args);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return rlt;
	}
	
	
	public static void setFieldValue(Object obj, String clsName, String filedName, Object value)
	{
		try 
		{
			Class<?> cls = getCls(clsName);
			Field f = cls.getDeclaredField(filedName);
			f.set(obj, value);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static Object getStaticFieldValue(String clsName, String filedName)
	{
		try 
		{
			Class<?> cls = getCls(clsName);
			Field f = cls.getDeclaredField(filedName);
			return f.get(null);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static Object getFieldValue(Object obj, String clsName, String filedName)
	{
		try 
		{
			Class<?> cls = getCls(clsName);
			Field f = cls.getDeclaredField(filedName);
			return f.get(obj);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}