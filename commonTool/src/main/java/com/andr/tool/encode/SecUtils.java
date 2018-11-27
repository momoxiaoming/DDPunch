package com.andr.tool.encode;


import android.util.Base64;

import com.andr.tool.log.LogUtil;
import com.andr.tool.util.StringUtil;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public class SecUtils
{
    private static final int      GET_YIHUO_ENCRYPT_KEY = 6;
    private static final int      CACHE_BUFFER_SIZE      = 8 * 1024;

    /**
     * 获取 infoStr 字串的 md5指纹摘要值
     *
     * @param infoStr     ： 要获取指纹摘要的字串
     * @param charsetType ： 输入字串的编码格式
     * @return ：获取到的指纹摘要，当输入信息为null时,返回值有可能为null
     */
    public static String getInformationFingerprintByMD5(String infoStr, StrCharset charsetType) {
        return getInformationFingerprint(infoStr, charsetType, "MD5");
    }

    /**
     * 根据msgDgstAlgrthm指定的算法 获取 infoStr 字串的 指纹摘要值
     *
     * @param infoStr        ： 要获取指纹摘要的字串
     * @param charsetType    ： 输入字串的编码格式
     * @param msgDgstAlgrthm ： 指纹摘要算法
     * @return ：获取到的指纹摘要，当输入信息为null时,返回值有可能为null
     */
    public static String getInformationFingerprint(String infoStr, StrCharset charsetType, String msgDgstAlgrthm) {
        String rlt = null;

        if (null != infoStr)
        {
            try
            {
                MessageDigest msgDgst = MessageDigest.getInstance(msgDgstAlgrthm);
                msgDgst.update(infoStr.getBytes(charsetType.value()));
                byte[] fingerprints = msgDgst.digest();
                rlt = HexUtil.encodeHexStr(fingerprints);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return rlt;
    }

    public static String getInformationFingerprint(String fileName, MessageDigestAlgorithm msgDgstAlgrthm) {
        String rlt = null;

        try {
            FileInputStream fileInStream = new FileInputStream(fileName);
            rlt = getInformationFingerprint(fileInStream, msgDgstAlgrthm);
            fileInStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rlt;
    }

    public static String getInformationFingerprint(InputStream inStream, MessageDigestAlgorithm msgDgstAlgrthm) {
        String rlt = null;

        if (null != inStream) {
            try {
                MessageDigest msgDgst = MessageDigest.getInstance(msgDgstAlgrthm.getMessageDigestAlgorithmName());
                DigestInputStream dgstInStream = new DigestInputStream(inStream, msgDgst);
                // 该处只做指纹摘要计算，所以在while中不对读取值做任何计算
                byte[] byteBuf = new byte[CACHE_BUFFER_SIZE];
                while (dgstInStream.read(byteBuf) != -1) ;
                byte[] fingerprints = msgDgst.digest();
                rlt = HexUtil.bytes2HexString(fingerprints);
                dgstInStream.close();
            } catch (Exception e) {
            }
        }

        return rlt;
    }

    /**
     * 根据需要,对前端上传数据是否加密
     * 先用RC4加密数据，再做16进制编码，再做Base64编码
     *
     * @param data 需要加密的数据
     * @return 加密完的数据
     */
    public static String doEncryptIfNeed(String data)
    {
        if (StringUtil.isStringEmpty(data))
        {
            return data;
        }

        LogUtil.e("加密上传数据");
        //return doBase64Encrypt(RC4.encry_RC4_string(data,RC4.RC4_KEY));
        return RC4.encry_RC4_string(Base64.encodeToString(data.getBytes(), Base64.DEFAULT), RC4.RC4_KEY);
    }

    /**
     * 根据需要,对前段下传数据是否解密
     * 先用Base64解密，在用16进制编码，再用Rc4解密
     *
     * @param data 需要加密的数据
     * @return 加密完的数据
     */
    public static String doDecryptIfNeed(String data)
    {
        if ( StringUtil.isStringEmpty(data))
        {
            return data;
        }

        LogUtil.d("解密下传数据");
        //return RC4.decry_RC4(doBase64Decrypt(data),RC4.RC4_KEY);
        return new String(Base64.decode(RC4.decry_RC4(data, RC4.RC4_KEY).getBytes(), Base64.DEFAULT));
    }

    /**
     * 做Base64编码
     *
     * @param data 需要做Base64解码的数据
     * @return 解码后的数据
     */
    public static String doBase64Decrypt(String data)
    {
        String rlt = data;

        if (StringUtil.isValidate(data))
        {
            try {
                rlt = new String(Base64.decode(data.getBytes(), Base64.DEFAULT));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return rlt != null ? rlt : null;
    }

    /**
     * 需要做Base64编码的数据
     *
     * @param data 需要做Base64编码的数据
     * @return 编码后的字符串
     */
    public static String doBase64Encrypt(String data)
    {
        String rlt = data;

        if (StringUtil.isValidate(data))
        {
            try {
                rlt = new String(Base64.encode(data.getBytes(), Base64.NO_WRAP));
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return rlt != null ? rlt : null;
    }

    /**
     * 获取回调给商场的加密信息， 先异或6，再base64
     *
     * @param data 需要加密的信息
     * @return 返回String
     */
    public static String getCallbackData(String data)
    {
        String rlt = null;
        if (StringUtil.isValidate(data))
        {
            rlt = doBase64Encrypt(getYiHuoEncrypt(data));
        }
        return rlt;
    }

    /**
     * 异或加密字符串
     *
     * @param data 需要加密的字符串
     * @return 返回String
     */
    public static String getYiHuoEncrypt(String data)
    {
        String rlt = null;
        if (StringUtil.isValidate(data))
        {
            try
            {
                byte[] strArray = data.getBytes();
                for (int i = 0; i < strArray.length; i++)
                {
                    strArray[i] = (byte) (strArray[i] ^ GET_YIHUO_ENCRYPT_KEY);
                }
                rlt = new String(strArray, "utf-8");
            } catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }
        return rlt;
    }

    public enum MessageDigestAlgorithm
    {
        MD2("MD2"), MD5("MD5"), SHA_1("SHA-1"), SHA_256("SHA-256"), SHA_384("SHA-384"), SHA_512("SHA-512");
        private String m_alg = "MD5";

        private MessageDigestAlgorithm(String alg)
        {
            m_alg = alg;
        }

        public String getMessageDigestAlgorithmName() {
            return m_alg;
        }
    }
}
