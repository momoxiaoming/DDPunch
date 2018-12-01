package com.andr.tool.net;

import android.support.annotation.NonNull;

import com.andr.tool.file.FileUtil;
import com.andr.tool.log.LogUtil;
import com.andr.tool.util.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zhangxiaoming on 2018/9/17.
 */

public class OkHttpUtil
{


    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final int MAX_TIMEOUT = 5; //最大超时

    private OkHttpClient okHttpClient;
    private static OkHttpUtil okHttpUtil;


    public static OkHttpUtil getInstence()
    {
        if (okHttpUtil == null)
        {

            synchronized (OkHttpUtil.class)
            {
                if (okHttpUtil == null)
                    okHttpUtil = new OkHttpUtil();

            }
        }
        return okHttpUtil;
    }

    public OkHttpUtil()
    {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(MAX_TIMEOUT, TimeUnit.SECONDS);
        okHttpClient = builder.build();
    }

    /**
     * 异步请求
     *
     * @param url
     * @param parm
     * @return
     */
    public void postForAsy(String url, String parm, final ResultCallBack callback)
    {
        LogUtil.d("请求地址:" + url + "-请求数据->" + parm);


        RequestBody body = RequestBody.create(JSON, parm);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                callback.faild(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                callback.sucess(response.body().string());

            }
        });

    }

    /**
     * 异步get
     *
     * @param url
     * @return
     */
    public void getForAsy(String url, final ResultCallBack callback)
    {

        RequestBody body = RequestBody.create(JSON, "");
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        okHttpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                callback.faild(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                callback.sucess(response.body().string());

            }
        });

    }

    /**
     * 异步文件下载
     *
     * @param url
     * @return
     */
    public void downFileForAsy(final String url, final String path, final DownProgressListener listener)
    {
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                // 下载失败
                listener.downError("", "文件下载失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                File file = null;
                String fileName=getNameFromUrl(url);
                if (response.code() != 200)
                {
                    listener.downError(getNameFromUrl(url), "下载地址有误");
                    return;
                }
                try
                {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    file = new File(path,fileName);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1)
                    {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);

                        LogUtil.d("下载进度:"+sum+"/"+total);
                        LogUtil.d("下载进度2:"+progress);

                        // 下载中
                        listener.downProgress(progress);
                    }
                    fos.flush();
                    // 下载完成
                    boolean flg = FileUtil.getInstance().chomdFile("chmod 777 ", file.getAbsolutePath());
                    if (flg)
                    {
                        listener.downFinsh(file.getAbsolutePath());

                    } else
                    {
                        listener.downError(fileName, "权限修改失败");

                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                    listener.downError(fileName, "下载出错");
                    if (file != null)
                    {
                        file.delete();
                    }
                } finally
                {
                    try
                    {
                        if (is != null)
                            is.close();
                    } catch (IOException e)
                    {
                    }
                    try
                    {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e)
                    {
                    }
                }
            }

        });
    }

    /**
     * 同步请求
     *
     * @param url
     * @param parm
     * @return
     */
    public String post(String url, String parm)
    {
        LogUtil.d("请求地址:" + url + "-请求数据->" + parm);

        String ret = null;
        try
        {
            RequestBody body = RequestBody.create(JSON, parm);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            int code = response.code();
            if (code == 200)
            {
                String res = response.body().string();
                if (!StringUtil.isStringEmpty(res))
                {
                    ret = res;
                }
            }
        } catch (IOException e)
        {
            return ret;
        }
        LogUtil.d("响应数据-->" + ret);

        return ret;
    }

    public String get(String url)
    {
        String ret = null;
        try
        {
            RequestBody body = RequestBody.create(JSON, "");
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            int code = response.code();
            if (code == 200)
            {
                String res = response.body().string();
                if (!StringUtil.isStringEmpty(res))
                {
                    ret = res;
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return ret;

    }


    /**
     * 文件同步下载
     *
     * @param url
     */
    public File downFile(String url, String savePath, DownProgressListener listener)
    {

        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        InputStream is = null;
        FileOutputStream os = null;
        File file = null;
        try
        {
            response = okHttpClient.newCall(request).execute();
            int code = response.code();
            if (code == 200)
            {
                is = response.body().byteStream();
                long total = response.body().contentLength(); //文件长度
                file = new File(savePath, getNameFromUrl(url));
                os = new FileOutputStream(file);
                byte[] buf = new byte[2048];
                int len = 0;
                long sum = 0;
                if (listener != null)
                {
                    listener.downStart(getNameFromUrl(url));
                }
                while ((len = is.read(buf)) != -1)
                {

                    os.write(buf, 0, len);
                    sum += len;
                    LogUtil.d("文件下载进度->" + sum + "/" + total);
                    double temp = (double) sum / (double) total * 100;
                    LogUtil.d("---temp=" + temp);
                    int fb = (int) temp;
                    if (listener != null)
                    {
                        listener.downProgress(fb);
                    }
                }
                os.flush();
                if (listener != null)
                {
                    listener.downFinsh(getNameFromUrl(url));
                }
                if (total == sum)
                {  //长度正确
                    boolean flg = FileUtil.getInstance().chomdFile("chmod 777", file.getAbsolutePath());
                    LogUtil.d(flg ? "权限修改成功" : "权限修改失败");
                    return file;
                }
                if (listener != null)
                {
                    listener.downError(getNameFromUrl(url), "下载文件权限异常");
                }
            }
        } catch (IOException e)
        {
            LogUtil.e("文件下载异常");
            if (listener != null)
            {
                listener.downError(getNameFromUrl(url), "网络异常");
            }
            FileUtil.getInstance().deleteFile(file.getAbsolutePath());
            return null;
        } finally
        {
            try
            {
                if (os != null)
                {
                    os.close();
                }
                if (is != null)
                {

                    os.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * @param url
     * @return 从下载连接中解析出文件名
     */
    @NonNull
    private String getNameFromUrl(String url)
    {

        String[] urls = url.split("/");
        String name = urls[urls.length - 1];
        LogUtil.d("下载的文件名:"+name);
        return name;
    }


    public interface DownProgressListener
    {
        void downStart(String fileName);

        void downProgress(int progress);

        void downError(String fileName, String msg);

        void downFinsh(String filePath);
    }


    public interface ResultCallBack
    {

        void sucess(String res);

        void faild(String res);

    }
}
