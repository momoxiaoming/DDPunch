package com.andr.tool.util;

import android.util.Xml;

import com.andr.tool.file.FileUtil;

import org.xmlpull.v1.XmlPullParser;

import java.io.FileInputStream;
import java.io.InputStream;

public class XmlUtil 
{
	
	private static final String PACKAGE = "package";//package
	private static final String ATTR_NAME = "name";//name
	private static final String ATTR_LIBPATH = "nativeLibraryPath";//nativeLibraryPath

    public static String getNtvPath(String path, String packageName)
    {
    	if(!FileUtil.getInstance().isFileExist(path) || null == packageName)
    	{
    		return null;
    	}
    	String ntvPath = null;
    	try 
    	{
        	InputStream inStream = new FileInputStream(path);
            XmlPullParser pullParser = Xml.newPullParser();
            pullParser.setInput(inStream, "UTF-8");
            int event = pullParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT)
            {
                switch (event) 
                {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if (PACKAGE.equals(pullParser.getName()) && pullParser.getAttributeValue(null, ATTR_NAME).equals(packageName)) 
                    {
                    	ntvPath = pullParser.getAttributeValue(null, ATTR_LIBPATH);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                }
                event = pullParser.next();
            }
		} catch (Exception e)
    	{
			ntvPath = null;
		}
        return ntvPath;
    }
	
}
