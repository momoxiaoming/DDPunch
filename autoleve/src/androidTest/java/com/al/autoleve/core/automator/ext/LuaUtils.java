package com.al.autoleve.core.automator.ext;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.test.uiautomator.UiDevice;

import com.al.autoleve.core.automator.exception.UIException;
import com.andr.tool.log.LogUtil;
import com.andr.tool.util.StringUtil;

import org.keplerproject.luajava.JavaFunction;
import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaState;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class LuaUtils
{
    private static final String MATCH_LUA_FILE_PATTERN_MATCH_NUMBER             = "^[1-9]\\d*$";
    private static final String LUA_FILE_SPECIAL_CHAR                           = "_";

    /**
     * 执行lua文件
     * @param workLuaPath       可执行的lua文件路径
     * @param exceptionLuaPath  异常文件路径
     * @return                  是否执行成功
     * @throws LuaException
     */
    public static boolean exeLuaFile(LuaState L, String workLuaPath, final String exceptionLuaPath) throws UIException
    {
        boolean rlt = false;
        if(StringUtil.isValidate(workLuaPath) && StringUtil.isValidate(exceptionLuaPath))
        {
            try
            {
                rlt = evalLua(L, workLuaPath);
            } catch (LuaException e)
            {
                execExceptionLua(L,exceptionLuaPath);
                throw new UIException(Status.StatueCode.EXE_LUA_FAIL,e.getMessage());
            }
        }
        return rlt;
    }

    public static void execExceptionLua(LuaState L, String exceptionLuaPath)
    {
        File exceptionFile = new File(exceptionLuaPath);
        if (exceptionFile.exists())
        {
            LogUtil.d("执行exception lua文件");
            try
            {
                evalLua(L, exceptionLuaPath);
            } catch (LuaException e1)
            {
                e1.printStackTrace();
            }
        }
    }

    public static void setLuaState(final Context context, UiDevice mDevice, LuaState L, String workPath)
    {
        L.openLibs();

        try
        {
            L.pushJavaObject(context);
            L.setGlobal("context");
            L.pushJavaObject(mDevice);
            L.setGlobal("uiDevice");

            JavaFunction assetLoader = new JavaFunction(L)
            {
                @Override
                public int execute() throws LuaException
                {
                    String name = L.toString(-1);

                    AssetManager am = context.getAssets();
                    try
                    {
                        InputStream is = am.open(name + ".lua");
                        byte[] bytes = readAll(is);
                        L.LloadBuffer(bytes, name);
                        return 1;
                    } catch (Exception e)
                    {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        e.printStackTrace(new PrintStream(os));
                        L.pushString("Cannot load module "+name+":\n"+os.toString());
                        return 1;
                    }
                }
            };

            L.getGlobal("package");            // package
            L.getField(-1, "loaders");         // package loaders
            int nLoaders = L.objLen(-1);       // package loaders

            L.pushJavaFunction(assetLoader);   // package loaders loader
            L.rawSetI(-2, nLoaders + 1);       // package loaders
            L.pop(1);                          // package

            L.getField(-1, "path");            // package path
            String customPath = context.getFilesDir() + "/?.lua";
            //customPath += ";/mnt/sdcard/GodHand/lua/?.lua";
            customPath += ";"+workPath+"/?.lua";
            L.pushString(";" + customPath);    // package path custom
            L.concat(2);                       // package pathCustom
            L.setField(-2, "path");            // package
            L.pop(1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @param L
     * @param filename
     * @return
     * @throws LuaException
     */
    public static boolean evalLua(LuaState L, String filename) throws LuaException
    {
        L.setTop(0);
        int ok = L.LloadFile(filename);

        if (ok == 0)
        {
            L.getGlobal("debug");
            L.getField(-1, "traceback");
            L.remove(-2);
            L.insert(-2);
            ok = L.pcall(0, 0, -2);
            if (ok == 0) {
                return true;
            }
        }
        throw new LuaException(errorReason(ok) + ": " + L.toString(-1));
    }

    /**
     * @param input
     * @return
     * @throws Exception
     */
    private static byte[] readAll(InputStream input) throws Exception
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream(4096);
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer)))
        {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    /**
     * @param error
     * @return
     */
    private static String errorReason(int error)
    {
        switch (error)
        {
            case 4:
                return "Out of memory";
            case 3:
                return "Syntax error";
            case 2:
                return "Runtime error";
            case 1:
                return "Yield error";
        }
        return "Unknown error " + error;
    }

    /**
     * 获取排序好的可执行lua文件
     * @param path      文件目录
     * @return          排序好的可执行lua文件
     */
    public static List<File> getSortExeLuaFiles(String path)
    {
        File workPlaceFile = new File(path);
        List<File> exeLuaFile = null;
        if(workPlaceFile.exists())
        {
            File[] luaFiles = workPlaceFile.listFiles(new FilenameFilter()
            {
                @Override
                public boolean accept(File dir, String filename)
                {
                    return LuaUtils.ifExeLuaFile(filename);
                }
            });
            exeLuaFile = sortList(Arrays.asList(luaFiles));
        }
        return exeLuaFile;
    }

    private static List<File> sortList(List<File> list)
    {
        if(null != list && list.size() > 0)
        {
            Collections.sort(list, new Comparator<File>()
            {
                @Override
                public int compare(File f1, File f2)
                {
                    Integer f1PreName = Integer.valueOf(getFilePrefixName(f1.getName(),LUA_FILE_SPECIAL_CHAR));
                    Integer f2PreName = Integer.valueOf(getFilePrefixName(f2.getName(),LUA_FILE_SPECIAL_CHAR));
                    return f1PreName - f2PreName;
                }
            });
        }
        return list;
    }

    /**
     * 过滤出文件夹中，是可执行的lua文件
     * @param filename    文件的名字
     * @return          是否可执行的lua文件
     */
    private static boolean ifExeLuaFile(String filename)
    {
        String preFileName = getFilePrefixName(filename,LUA_FILE_SPECIAL_CHAR);
        return StringUtil.isValidate(preFileName) && preFileName.matches(MATCH_LUA_FILE_PATTERN_MATCH_NUMBER);
    }

    /**
     * 根据一定的规则，截取出字符串的前缀
     * @param fileName      字符串的名字
     * @param preChar        前缀特殊符号
     * @return              前缀
     */
    private static String getFilePrefixName(String fileName, String preChar)
    {
        String rlt = null;
        if(StringUtil.isValidates(preChar,fileName))
        {
            int preIndex = fileName.indexOf(preChar);
            if(preIndex > 0)
            {
                rlt = fileName.substring(0,preIndex);
            }
        }
        return rlt;
    }
}
