package com.andr.tool.encode;

import android.annotation.SuppressLint;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class UtilSecurity {
    private static final int      CACHE_BUFFER_SIZE      = 8 * 1024;
    private static final int      SECR_VERSION           = 1;                                          // 密钥版本控制
    private static final int      DEFAULT_KEYTABLE_INDEX = 0;                                          // 默认密码的密钥表索引位置，该值要小于SECR_KEY_TABLE中密码表的个数
    private static final int      SECR_KEY_SIZE          = 112;                                        // 密钥长度
    private static final String SECR_ALGORITHM         = KeyAlgorithm.DES_EDE.getKeyAlgorithmName(); // 密钥算法
    private static final byte[][] SECR_KEY_TABLE         = {                                           // 密钥表
                            { 14, -42, 122, -2, 122, 13, 62, 31, 8, 103, 44, -80, -123, -43, -2, 11, 14, -42, 122, -2, 122, 13, 62, 31 },
                            { 25, 82, 117, -3, 52, 31, -32, 21, 42, 56, 104, 91, 91, -62, -9, 93, 25, 82, 117, -3, 52, 31, -32, 21 },
                            { 4, 88, 19, 64, -50, 91, -123, -77, -108, -113, 93, 1, -75, -116, -48, -39, 4, 88, 19, 64, -50, 91, -123, -77 },
                            { 41, -36, -83, -38, 81, -3, 56, -51, -56, -111, 1, -42, -15, -108, -95, 121, 41, -36, -83, -38, 81, -3, 56, -51 },
                            { 38, -43, 107, 8, 115, -42, -75, 11, 117, 14, -92, 38, -51, 21, 55, 115, 38, -43, 107, 8, 115, -42, -75, 11 },
                            { -36, 117, -3, -42, 56, 121, 31, -128, -125, -118, 13, -2, -8, -75, -42, -9, -36, 117, -3, -42, 56, 121, 31, -128 },
                            { 4, 49, -110, -2, 25, -9, -53, 28, 59, -110, 47, -43, 127, -23, -39, -99, 4, 49, -110, -2, 25, -9, -53, 28 },
                            { 104, 73, 2, -98, -89, 93, -104, 16, 25, -27, 41, 97, -50, 88, 41, 121, 104, 73, 2, -98, -89, 93, -104, 16 },
                            { -92, 13, 84, 13, -17, 121, -27, -68, 87, -3, -50, -95, 79, 124, 31, -62, -92, 13, 84, 13, -17, 121, -27, -68 },
                            { 26, -14, 121, -15, 49, -62, 87, 118, 13, 74, -26, -50, -53, -68, -104, 87, 26, -14, 121, -15, 49, -62, 87, 118 } };
    private static final int      DISP_BYTE_LEN          = 4;                                          // 保存加密信息内容长度的byte字节数
    private static final int      KEY_INDEX_BYTE_LEN     = 4;                                          // 密钥索引信息字节长度
    private static final int      VERIFY_BYTE_LEN        = 4;                                          // 验证是否为加密信息的验证信息字节数
    private static final int      VERSION_BYTE_LEN       = 4;                                          // 版本信息字节长度
    private static final int      BUFFER_SIZE            = 4 * 1024;                                   // 读取缓冲区大小

    /* 加密密钥算法名称 */
    public enum KeyAlgorithm {
        DES_EDE("DESede");
        private String m_alg = "DESede";

        private KeyAlgorithm(String alg) {
            m_alg = alg;
        }

        public String getKeyAlgorithmName() {
            return m_alg;
        }
    };

    /**
     * 签名算法名称
     * 
     * @author Alex
     */
    public enum MessageDigestAlgorithm {
        MD2("MD2"), MD5("MD5"), SHA_1("SHA-1"), SHA_256("SHA-256"), SHA_384("SHA-384"), SHA_512("SHA-512");
        private String m_alg = "MD5";

        private MessageDigestAlgorithm(String alg) {
            m_alg = alg;
        }

        public String getMessageDigestAlgorithmName() {
            return m_alg;
        }
    }

    /**
     * 字符编码格式
     * 
     * @author Alex
     */
    public enum CharsetType {
        GBK("GBk"), US_ASCII("US-ASCII"), ISO_8859_1("ISO-8859-1"), UTF_8("UTF-8"), UTF_16BE("UTF-16BE"), UTF_16LE("UTF-16LE"), UTF_16("UTF-16");

        private String charsetTypeName = null;

        private CharsetType(String charsetTypeName) {
            this.charsetTypeName = charsetTypeName;
        }

        public String getCharsetTypeName() {
            return charsetTypeName;
        }
    }

    /**
     * 根据 传入的 md5Value 值判断 infoStr 信息是否没有被篡改，保证数据的完整性
     * 
     * @param infoStr
     *            ：要验证的字串信息
     * @param charsetType
     *            ：要验证的字串信息编码格式
     * @param md5Value
     *            ：md5值，当传入该值为null时将不做md5完整性计算，并返回true
     * @return : 当信息完整时返回true, 否则返回false
     */
    public static boolean isInfomationIntegrityJudgeByMD5(String infoStr, CharsetType charsetType, String md5Value) {
        boolean rlt = false;

        if (null != md5Value) {
            String fingeprintStr = getInformationFingerprintByMD5(infoStr, charsetType);
            rlt = isInformationIntegrity(fingeprintStr, md5Value);
        } else {
            rlt = true;
        }

        return rlt;
    }

    /**
     * 根据 传入的 md5Value 值判断 inStream 输入流信息是否没有被篡改，保证数据的完整性
     * 
     * @param inStream
     *            ：要验证的信息输入流
     * @param md5Value
     *            ：md5值，当传入该值为null时将不做md5完整性计算，并返回true
     * @return : 当信息完整时返回true, 否则返回false
     */
    public static boolean isInfomationIntegrityJudgeByMD5(InputStream inStream, String md5Value) {
        boolean rlt = false;

        if (null != md5Value) {
            String fingeprintStr = getInformationFingerprintByMD5(inStream);
            rlt = isInformationIntegrity(fingeprintStr, md5Value);
        } else {
            rlt = true;
        }

        return rlt;
    }

    /**
     * 从图片中获取隐藏信息
     * 
     * @param imgByte
     *            ：图片字节数组
     * @return ：返回影藏信息字串，默认编码格式(ANSI)
     */
    public static byte[] getHideInformationByteFromImage(byte[] imgByte) {
        byte[] rltInfo = null;
        int version = 0;

        final int minLen = DISP_BYTE_LEN + KEY_INDEX_BYTE_LEN + VERIFY_BYTE_LEN + VERSION_BYTE_LEN;
        if (null != imgByte && imgByte.length >= minLen) // 最后四个字节说明隐藏信息的长度，所以imgByte长度必须大于等于minLen的长度
        {
            int offLen = 0;

            // 获取隐藏信息的长度
            offLen = DISP_BYTE_LEN;
            byte[] lenByte = new byte[DISP_BYTE_LEN];
            System.arraycopy(imgByte, imgByte.length - offLen, lenByte, 0, DISP_BYTE_LEN);
            int hideInfoLen = DataConvert.byte2Int(lenByte, true);

            // 获取密钥索引信息
            offLen += KEY_INDEX_BYTE_LEN;
            byte[] keyIndexBytes = new byte[KEY_INDEX_BYTE_LEN];
            System.arraycopy(imgByte, imgByte.length - offLen, keyIndexBytes, 0, KEY_INDEX_BYTE_LEN);
            int keyIndex = DataConvert.byte2Int(keyIndexBytes, true);

            // 验证信息
            offLen += VERIFY_BYTE_LEN;
            byte[] encLenByteArray = dealBytes(lenByte, true, keyIndex);
            byte[] verifyByteArray = new byte[VERIFY_BYTE_LEN];
            System.arraycopy(imgByte, imgByte.length - offLen, verifyByteArray, 0, VERIFY_BYTE_LEN);
            boolean isEqu = isEquals(verifyByteArray, encLenByteArray, VERIFY_BYTE_LEN);

            if (isEqu) // 判断是为带加密隐藏信息的图片时才执行解密操作
            {
                // 获取版本信息
                offLen += VERSION_BYTE_LEN;
                byte[] versionBytes = new byte[VERSION_BYTE_LEN];
                System.arraycopy(imgByte, imgByte.length - offLen, versionBytes, 0, VERSION_BYTE_LEN);
                version = DataConvert.byte2Int(versionBytes, true);

                if (SECR_VERSION == version) {
                    // 获取加密的隐藏信息
                    offLen += hideInfoLen;
                    byte[] secHideInfoByte = new byte[hideInfoLen];
                    System.arraycopy(imgByte, imgByte.length - offLen, secHideInfoByte, 0, hideInfoLen);

                    // 解密隐藏信息
                    rltInfo = dealBytes(secHideInfoByte, false, keyIndex);
                }
            }
        }

        return rltInfo;
    }

    /**
     * 根据 传入的 md5Value 值判断 fileName 文件信息是否没有被篡改，保证数据的完整性
     * 
     * @param fileName
     *            ： 包含要验证信息的文件名
     * @param md5Value
     *            ：md5值，当传入的该值为null时将不做md5完整性计算，并返回true
     * @return : 当信息完整时返回true, 否则返回false
     */
    public static boolean isInfomationIntegrityJudgeByMD5(String fileName, String md5Value) {
        boolean rlt = false;

        if (null != md5Value)
        {
            String fingeprintStr = getInformationFingerprintByMD5(fileName);
            rlt = isInformationIntegrity(fingeprintStr, md5Value);
        } else {
            rlt = true;
        }

        return rlt;
    }

    /**
     * 获取 infoStr 字串的安全md5指纹摘要值
     * 
     * @param infoStr
     *            ： 要获取指纹摘要的字串
     * @param charsetType
     *            ： 输入字串的编码格式
     * @return ：获取到的指纹摘要，当输入信息为null时,返回值有可能为null
     */
    public static String getInformationFingerprintBySecMD5(String infoStr, CharsetType charsetType) {
        return getInformationFingerprint(DataConvert.bytes2HexString(UtilSecurity.encryptString(infoStr)), charsetType, MessageDigestAlgorithm.MD5);
    }

    /**
     * 获取 infoStr 字串的 md5指纹摘要值
     * 
     * @param infoStr
     *            ： 要获取指纹摘要的字串
     * @param charsetType
     *            ： 输入字串的编码格式
     * @return ：获取到的指纹摘要，当输入信息为null时,返回值有可能为null
     */
    public static String getInformationFingerprintByMD5(String infoStr, CharsetType charsetType) {
        return getInformationFingerprint(infoStr, charsetType, MessageDigestAlgorithm.MD5);
    }

    /**
     * 获取 inStream 流 的 md5指纹摘要值
     * 
     * @param inStream
     *            ： 要获取指纹摘要的输入流
     * @return ：获取到的指纹摘要，当输入信息为null时,返回值有可能为null
     */
    public static String getInformationFingerprintByMD5(InputStream inStream) {
        return getInformationFingerprint(inStream, MessageDigestAlgorithm.MD5);
    }

    /**
     * 获取 fileName 文件中信息 的 md5指纹摘要值
     * 
     * @param fileName
     *            ： 要获取指纹摘要的文件名
     * @return ：获取到的指纹摘要，当输入信息为null时,返回值有可能为null
     */
    public static String getInformationFingerprintByMD5(String fileName) {
        return getInformationFingerprint(fileName, MessageDigestAlgorithm.MD5);
    }

    /**
     * 根据msgDgstAlgrthm指定的算法 获取 infoStr 字串的 指纹摘要值
     * 
     * @param infoStr
     *            ： 要获取指纹摘要的字串
     * @param charsetType
     *            ： 输入字串的编码格式
     * @param msgDgstAlgrthm
     *            ： 指纹摘要算法
     * @return ：获取到的指纹摘要，当输入信息为null时,返回值有可能为null
     */
    public static String getInformationFingerprint(String infoStr, CharsetType charsetType, MessageDigestAlgorithm msgDgstAlgrthm) {
        String rlt = null;

        if (null != infoStr) {
            try {
                MessageDigest msgDgst = MessageDigest.getInstance(msgDgstAlgrthm.getMessageDigestAlgorithmName());
                msgDgst.update(infoStr.getBytes(charsetType.getCharsetTypeName()));
                byte[] fingerprints = msgDgst.digest();
                rlt = DataConvert.bytes2HexString(fingerprints);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return rlt;
    }

    /**
     * 根据msgDgstAlgrthm指定的算法 获取 inStream 输入流的 指纹摘要值
     * 
     * @param inStream
     *            ： 要获取指纹摘要的输入流
     * @param msgDgstAlgrthm
     *            ： 指纹摘要算法
     * @return ：获取到的指纹摘要，当输入信息为null时,返回值有可能为null
     */
    public static String getInformationFingerprint(InputStream inStream, MessageDigestAlgorithm msgDgstAlgrthm) {
        String rlt = null;

        if (null != inStream) {
            try {
                MessageDigest msgDgst = MessageDigest.getInstance(msgDgstAlgrthm.getMessageDigestAlgorithmName());
                DigestInputStream dgstInStream = new DigestInputStream(inStream, msgDgst);
                // 该处只做指纹摘要计算，所以在while中不对读取值做任何计算
                byte[] byteBuf = new byte[CACHE_BUFFER_SIZE];
                while (dgstInStream.read(byteBuf) != -1);
                byte[] fingerprints = msgDgst.digest();
                rlt = DataConvert.bytes2HexString(fingerprints);
                dgstInStream.close();
            } catch (Exception e) {
            }
        }

        return rlt;
    }

    /**
     * 根据msgDgstAlgrthm指定的算法 获取fileName 文件中信息的 指纹摘要值
     * 
     * @param fileName
     *            ： 要获取指纹摘要的文件名
     * @param msgDgstAlgrthm
     *            ： 指纹摘要算法
     * @return ：：获取到的指纹摘要，当输入信息为null时,返回值有可能为null
     */
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

    /**
     * 用来生成对称密钥
     * 
     * @return ：返回生成的密钥
     */
    @SuppressLint("TrulyRandom")
    public static String createSecretKey() {
        byte[] kb = null;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(SECR_ALGORITHM);
            keyGen.init(SECR_KEY_SIZE);
            SecretKey secKey = keyGen.generateKey();
            kb = secKey.getEncoded();
        } catch (Exception e) {
        }

        StringBuffer keyStr = new StringBuffer();
        keyStr.append("{ ");
        if (null != kb) {
            for (int i = 0; i < kb.length; i++) {
                if (i != kb.length - 1) {
                    keyStr.append(kb[i] + ", ");
                } else {
                    keyStr.append(kb[i] + " };");
                }
            }
        }

        return keyStr.toString();
    }

    /**
     * 使用默认密钥，加密或解密一个文件到另外一个文件
     * 
     * @param srcFileName
     *            : 要处理的源文件
     * @param dstFileName
     *            ： 保存处理后的目的文件，目标文件存在时，会先删除，再创建
     * @param isEncrypt
     *            ：处理方式，true: 解密; false: 解密
     * @return ：返回处理的字节个数
     */
    public static long dealFile(String srcFileName, String dstFileName, boolean isEncrypt) {
        return dealFile(srcFileName, dstFileName, isEncrypt, DEFAULT_KEYTABLE_INDEX);
    }

    /**
     * 使用指定密钥，加密或解密一个文件到另外一个文件
     * 
     * @param srcFileName
     *            : 要处理的源文件
     * @param dstFileName
     *            ： 保存处理后的目的文件，目标文件存在时，会先删除，再创建
     * @param isEncrypt
     *            ：处理方式，true: 解密; false: 解密
     * @param secTableIndex
     *            : 指定密钥在密钥表中的索引，该值超过密钥表中密钥个数时，使用默认密钥
     * @return ：返回处理的字节个数
     */
    public static long dealFile(String srcFileName, String dstFileName, boolean isEncrypt, int secTableIndex) {
        long readCount = 0;

        do {
            if (null == srcFileName || null == dstFileName) {
                break;
            }

            // 源文件有效性判断
            File srcFile = new File(srcFileName);
            if (!srcFile.exists() || !srcFile.isFile() || !srcFile.canRead()) {
                break;
            }

            // 目标文件存在性处理
            File dstFile = new File(dstFileName);
            if (dstFile.exists() && dstFile.isFile()) {
                dstFile.delete();
            }

            try {
                FileInputStream srcFileStream = new FileInputStream(srcFileName);
                FileOutputStream dstFileStream = new FileOutputStream(dstFileName);
                InputStream cin = dealInputStream(srcFileStream, isEncrypt, secTableIndex);

                readCount = diversionStream(cin, dstFileStream);

                cin.close();
                srcFileStream.close();
                dstFileStream.close();
            } catch (Exception e) {
            }
        } while (false);

        return readCount;
    }

    /**
     * 使用默认密钥， 加密或解密输入流, 调用方在调用该函数后需在关闭传入的输入流的前面一行关闭该函数返回的输入流,
     * 使用DEFAULT_KEY_INDEX 指定的默认密钥
     * 
     * @param secretInputStream
     *            ：需处理的输入流
     * @param isEncrypt
     *            : 输入流处理方式，true: 解密输入流; false: 解密输入流
     * @return ：处理后的输入流
     */
    public static InputStream dealInputStream(InputStream inputStream, boolean isEncrypt) {
        return dealInputStream(inputStream, isEncrypt, DEFAULT_KEYTABLE_INDEX);
    }

    /**
     * 加密或解密输入流, 调用方在调用该函数后需在关闭传入的输入流的前面一行关闭该函数返回的输入流, 使用指定密钥
     * 
     * @param secretInputStream
     *            ：需处理的输入流
     * @param isEncrypt
     *            : 输入流处理方式，true: 解密输入流; false: 解密输入流
     * @param secTableIndex
     *            : 指定密钥在密钥表中的索引，该值超过密钥表中密钥个数时，使用默认密钥
     * @return ：处理后的输入流
     */
    public static InputStream dealInputStream(InputStream inputStream, boolean isEncrypt, int secTableIndex) {
        CipherInputStream cis = null;

        try {
            Cipher inCipher = getDataEncrypDecryptCipher(isEncrypt, secTableIndex);
            cis = new CipherInputStream(inputStream, inCipher);
        } catch (Exception e) {
        }

        return cis;
    }

    /**
     * 使用默认密钥，加密或解密输出流，调用方在调用该函数后需在关闭传入的输出流的前面一行关闭该函数返回的输出流, 使用DEFAULT_KEY_INDEX
     * 指定的默认密钥
     * 
     * @param lawsOutputStream
     *            ：需处理的输出流
     * @param isEncrypt
     *            :输出流处理方式，true: 解密输出流; false: 解密输出流
     * @return ：处理后的输出流
     */
    public static OutputStream dealOutputStream(OutputStream lawsOutputStream, boolean isEncrypt) {
        return dealOutputStream(lawsOutputStream, isEncrypt, DEFAULT_KEYTABLE_INDEX);
    }

    /**
     * 加密或解密输出流，调用方在调用该函数后需在关闭传入的输出流的前面一行关闭该函数返回的输出流, 使用指定密钥
     * 
     * @param lawsOutputStream
     *            ：需处理的输出流
     * @param isEncrypt
     *            :输出流处理方式，true: 解密输出流; false: 解密输出流
     * @param secTableIndex
     *            : 指定密钥在密钥表中的索引，该值超过密钥表中密钥个数时，使用默认密钥
     * @return ：处理后的输出流
     */
    public static OutputStream dealOutputStream(OutputStream lawsOutputStream, boolean isEncrypt, int secTableIndex) {
        CipherOutputStream cos = null;

        try {
            Cipher outCipher = getDataEncrypDecryptCipher(isEncrypt, secTableIndex);
            cos = new CipherOutputStream(lawsOutputStream, outCipher);
        } catch (Exception e) {
        }

        return cos;
    }

    /**
     * 使用默认密钥，加密字串
     * 
     * @param inStr
     *            ：输入字串
     * @return ：加密后的字节流
     */
    public static byte[] encryptString(String inStr) {
        return encryptString(inStr, DEFAULT_KEYTABLE_INDEX);
    }

    /**
     * 使用指定密钥，加密字串
     * 
     * @param inStr
     *            ：输入字串
     * @param secTableIndex
     *            : 指定密钥在密钥表中的索引，该值超过密钥表中密钥个数时，使用默认密钥
     * @return ：加密后的字节流
     */
    public static byte[] encryptString(String inStr, int secTableIndex) {
        byte[] rltBytes = null;

        if (null != inStr) {
            rltBytes = dealBytes(inStr.getBytes(), true, secTableIndex);
        }

        return rltBytes;
    }

    /**
     * 使用默认密钥，解密字串，前提需知道被加密的字节数组是为字串, 且为utf-8编码
     * 
     * @param inByte
     *            ：字串被加密后的字节数组，utf-8编码的字串被加密
     * @return ：解密后的字串
     */
    public static String decryptString(byte[] inByte) {
        return decryptString(inByte, DEFAULT_KEYTABLE_INDEX);
    }

    /**
     * 使用指定密钥，解密字串，前提需知道被加密的字节数组是为字串, 且为utf-8编码
     * 
     * @param inByte
     *            ：字串被加密后的字节数组，utf-8编码的字串被加密
     * @param secTableIndex
     *            : 指定密钥在密钥表中的索引，该值超过密钥表中密钥个数时，使用默认密钥
     * @return ：解密后的字串
     */
    public static String decryptString(byte[] inByte, int secTableIndex) {
        String rlt = null;

        if (null != inByte && inByte.length > 0) {
            byte[] rltBytes = dealBytes(inByte, false, secTableIndex);

            try {
                rlt = null != rltBytes ? new String(rltBytes, "utf-8") : null;
            } catch (Exception e) {
            }
        }

        return rlt;
    }

    /**
     * 加密或解密字节数组, 使用默认密钥
     * 
     * @param input
     *            ： 输入字节数组
     * @param isEncrypt
     *            ：处理方式，，true: 解密; false: 解密
     * @return ：处理后的数组
     */
    public static byte[] dealBytes(byte[] input, boolean isEncrypt) {
        return dealBytes(input, isEncrypt, DEFAULT_KEYTABLE_INDEX);
    }

    /**
     * 使用指定密钥加密或解密字节数组
     * 
     * @param input
     *            ： 输入字节数组
     * @param isEncrypt
     *            ：处理方式，，true: 解密; false: 解密
     * @param secTableIndex
     *            : 指定密钥在密钥表中的索引，该值超过密钥表中密钥个数时，使用默认密钥
     * @return ：处理后的数组
     */
    public static byte[] dealBytes(byte[] input, boolean isEncrypt, int secTableIndex) {
        byte[] result = null;

        if (null != input && input.length > 0) {
            try {
                Cipher outCipher = getDataEncrypDecryptCipher(isEncrypt, secTableIndex);

                if (null != outCipher) {
                    result = outCipher.doFinal(input);
                }
            } catch (Exception e) {
            }
        }

        return result;
    }

    /**
     * 使用默认密钥把信息加密或解密到输出流
     * 
     * @param inforInputStream
     *            ：信息输入流
     * @param outputStream
     *            ：信息输出流
     * @param isEncrypt
     *            ：输入流处理方式，true: 解密输入流到输出流; false: 加密输入流到输出流
     * @return ：返回加密或解密的字节数
     */
    public static int dealInformation(InputStream inforInputStream, OutputStream outputStream, boolean isEncrypt) {
        return dealInformation(inforInputStream, outputStream, isEncrypt, DEFAULT_KEYTABLE_INDEX);
    }

    /**
     * 使用指定密钥把信息加密或解密到输出流
     * 
     * @param inforInputStream
     *            ：信息输入流
     * @param outputStream
     *            ：信息输出流
     * @param isEncrypt
     *            ：输入流处理方式，true: 解密输入流到输出流; false: 加密输入流到输出流
     * @param secTableIndex
     *            : 指定密钥在密钥表中的索引，该值超过密钥表中密钥个数时，使用默认密钥
     * @return ：返回加密或解密的字节数
     */
    public static int dealInformation(InputStream inforInputStream, OutputStream outputStream, boolean isEncrypt, int secTableIndex) {
        int inforLen = 0;

        try {
            InputStream secInfoStream = dealInputStream(inforInputStream, isEncrypt, secTableIndex);
            inforLen = diversionStream(secInfoStream, outputStream);
            secInfoStream.close();
        } catch (Exception e) {
        }

        return inforLen;
    }

    /**
     * 使用默认密钥，加密字串信息到输出流中
     * 
     * @param inforStr
     *            ： 要加密的字串信息
     * @param outputStream
     *            ： 输出流
     * @return ：返回加密的字节数
     */
    public static int dealInformation(String inforStr, OutputStream outputStream) {
        return dealInformation(inforStr, outputStream, DEFAULT_KEYTABLE_INDEX);
    }

    /**
     * 使用指定密钥，加密字串信息到输出流中
     * 
     * @param inforStr
     *            ： 要加密的字串信息
     * @param outputStream
     *            ： 输出流
     * @param secTableIndex
     *            : 指定密钥在密钥表中的索引，该值超过密钥表中密钥个数时，使用默认密钥
     * @return ：返回加密的字节数
     */
    public static int dealInformation(String inforStr, OutputStream outputStream, int secTableIndex) {
        int inforLen = 0;
        byte[] encBytes = encryptString(inforStr, secTableIndex);

        if (null != encBytes && null != outputStream) {
            inforLen = encBytes.length;

            try {
                outputStream.write(encBytes);
            } catch (Exception e) {
            }
        }

        return inforLen;
    }

    /**
     * 从图片中获取隐藏信息
     * 
     * @param imgByte
     *            ：图片字节数组
     * @return ：返回影藏信息字串，默认编码格式(ANSI)
     */
    public static String getHideInformationStringFromImage(byte[] imgByte) {
        String rltInfo = null;
        int version = 0;

        final int minLen = DISP_BYTE_LEN + KEY_INDEX_BYTE_LEN + VERIFY_BYTE_LEN + VERSION_BYTE_LEN;
        if (null != imgByte && imgByte.length >= minLen) // 最后四个字节说明隐藏信息的长度，所以imgByte长度必须大于等于minLen的长度
        {
            int offLen = 0;

            // 获取隐藏信息的长度
            offLen = DISP_BYTE_LEN;
            byte[] lenByte = new byte[DISP_BYTE_LEN];
            System.arraycopy(imgByte, imgByte.length - offLen, lenByte, 0, DISP_BYTE_LEN);
            int hideInfoLen = DataConvert.byte2Int(lenByte, true);

            // 获取密钥索引信息
            offLen += KEY_INDEX_BYTE_LEN;
            byte[] keyIndexBytes = new byte[KEY_INDEX_BYTE_LEN];
            System.arraycopy(imgByte, imgByte.length - offLen, keyIndexBytes, 0, KEY_INDEX_BYTE_LEN);
            int keyIndex = DataConvert.byte2Int(keyIndexBytes, true);

            // 验证信息
            offLen += VERIFY_BYTE_LEN;
            byte[] encLenByteArray = dealBytes(lenByte, true, keyIndex);
            byte[] verifyByteArray = new byte[VERIFY_BYTE_LEN];
            System.arraycopy(imgByte, imgByte.length - offLen, verifyByteArray, 0, VERIFY_BYTE_LEN);
            boolean isEqu = isEquals(verifyByteArray, encLenByteArray, VERIFY_BYTE_LEN);

            if (isEqu) // 判断是为带加密隐藏信息的图片时才执行解密操作
            {
                // 获取版本信息
                offLen += VERSION_BYTE_LEN;
                byte[] versionBytes = new byte[VERSION_BYTE_LEN];
                System.arraycopy(imgByte, imgByte.length - offLen, versionBytes, 0, VERSION_BYTE_LEN);
                version = DataConvert.byte2Int(versionBytes, true);

                if (SECR_VERSION == version) {
                    // 获取加密的隐藏信息
                    offLen += hideInfoLen;
                    byte[] secHideInfoByte = new byte[hideInfoLen];
                    System.arraycopy(imgByte, imgByte.length - offLen, secHideInfoByte, 0, hideInfoLen);

                    // 解密隐藏信息
                    rltInfo = decryptString(secHideInfoByte, keyIndex);
                }
            }
        }

        return rltInfo;
    }

    /**
     * 从图片中获取隐藏信息
     * 
     * @param imgInputStream
     *            : 带影藏信息的图片输入流
     * @return ： 返回影藏信息字串，默认编码格式(ANSI)
     */
    public static String getHideInformationStringFromImage(InputStream imgInputStream) {
        String rltInfo = null;

        try {
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
            diversionStream(imgInputStream, byteOutStream);

            rltInfo = getHideInformationStringFromImage(byteOutStream.toByteArray());
            byteOutStream.close();
        } catch (Exception e) {
        }

        return rltInfo;
    }

    /**
     * 从图片中获取隐藏信息
     * 
     * @param imgInfoFileName
     *            : 带影藏信息的图片全路径
     * @return ： 返回影藏信息字串，默认编码格式(ANSI)
     */
    public static String getHideInformationStringFromImage(String imgInfoFileName) {
        String result = null;

        try {
            FileInputStream imgInfoFileStream = new FileInputStream(imgInfoFileName);
            result = getHideInformationStringFromImage(imgInfoFileStream);
            imgInfoFileStream.close();
        } catch (Exception e) {
        }

        return result;
    }

    /**
     * 生成带隐藏信息的图片输出流，(重点注意：图片要用jpg格式图片，信息输入流中信息使用UTF8编码，不要带文件BOM头）
     * 
     * @param inputStreamImg
     *            ： 原始图片输入流
     * @param informationStream
     *            ：要隐藏的信息输入流
     * @param outputStream
     *            ：带隐藏信息的输出流
     * @return ：生成后输出流总字节大小
     */
    public static int generateImageHasHideInformation(InputStream inputStreamImg, InputStream informationStream, OutputStream outputStream) {
        return generateImageHasHideInformation(inputStreamImg, informationStream, outputStream, "isInputStream");
    }

    /**
     * 生成带隐藏信息的图片输出流，(重点注意：图片要用jpg格式图片，信息输入字串中信息使用UTF8编码）
     * 
     * @param inputStreamImg
     *            ： 原始图片输入流
     * @param informationStr
     *            : 要隐藏的信息字串
     * @param outputStream
     *            ：带隐藏信息的输出流
     * @return ：生成后输出流总字节大小
     */
    public static int generateImageHasHideInformation(InputStream inputStreamImg, String informationStr, OutputStream outputStream) {
        return generateImageHasHideInformation(inputStreamImg, informationStr, outputStream, "isString");
    }

    /**
     * 生成带隐藏信息的图片，(重点注意：图片要用jpg格式图片，信息输入字串中信息使用UTF8编码）
     * 
     * @param imgFileName
     *            ： 原始图片输入流
     * @param infroFileName
     *            ：要隐藏的信息输入流
     * @param outFileName
     *            ：带隐藏信息的输出流
     * @return
     */
    public static int generateImageHasHideInformation(String imgFileName, String infroFileName, String outFileName) {
        int rlt = 0;

        try {
            FileInputStream inputStreamImg = new FileInputStream(imgFileName);
            FileInputStream informationStream = new FileInputStream(infroFileName);
            FileOutputStream outputStream = new FileOutputStream(outFileName);

            rlt = generateImageHasHideInformation(inputStreamImg, informationStream, outputStream);

            inputStreamImg.close();
            informationStream.close();
            outputStream.close();

        } catch (Exception e) {
        }

        return rlt;
    }

    /**
     * 生成带隐藏信息的图片输出流，(重点注意：图片要用jpg格式图片，信息输入流中信息使用UTF8编码，不要带文件BOM头）
     * 
     * @param inputStreamImg
     *            ： 原始图片输入流
     * @param information
     *            ：要隐藏的信息对象
     * @param outputStream
     *            ：带隐藏信息的输出流
     * @param objType
     *            ： 描述信息对象的类型
     * @return ：生成后输出流总字节大小
     */
    private static int generateImageHasHideInformation(InputStream inputStreamImg, Object information, OutputStream outputStream, String objType) {
        int readCount = 0;
        int inforLen = 0;

        if (null != inputStreamImg && null != information && null != outputStream) {
            try {
                readCount += diversionStream(inputStreamImg, outputStream);

                int secTableIndex = new Random().nextInt(SECR_KEY_TABLE.length);
                if (objType.equals("isString")) {
                    inforLen = dealInformation((String) information, outputStream, secTableIndex);
                } else if (objType.equals("isInputStream")) {
                    inforLen = dealInformation((InputStream) information, outputStream, true, secTableIndex);
                }
                readCount += inforLen;
                byte[] lenByte = DataConvert.int2byteArray(inforLen, true);

                // 版本信息
                byte[] verBytes = DataConvert.int2byteArray(SECR_VERSION, true);
                outputStream.write(verBytes, 0, VERSION_BYTE_LEN);
                readCount += verBytes.length;

                // 验证信息
                byte[] encLenByte = dealBytes(lenByte, true, secTableIndex); // //
                                                                             // 验证信息用默认密钥加密
                outputStream.write(encLenByte, 0, VERIFY_BYTE_LEN);
                readCount += VERIFY_BYTE_LEN; // 改处以VERIFY_BYTE_LEN设置为准，不要用encLenByte.length的值，因为该值为8的倍数，考虑到流量等问题，目前只取部分即可

                // 密钥索引信息
                byte[] keyIdexBytes = DataConvert.int2byteArray(secTableIndex, true);
                outputStream.write(keyIdexBytes, 0, KEY_INDEX_BYTE_LEN);
                readCount += keyIdexBytes.length;

                // 长度信息
                outputStream.write(lenByte);
                readCount += lenByte.length;
            } catch (Exception e) {
            }
        }

        return readCount;
    }

    /**
     * 获取密码处理器，密钥表索引值超过密钥表中密钥个数时，使用默认密钥
     * 
     * @param isEncrypt
     *            ：处理方式，，true: 解密; false: 解密
     * @param secTableIdex
     *            : 密钥表索引，该值超过密钥表中密钥个数时，使用默认密钥
     * @return ：密码处理器
     */
    private static Cipher getDataEncrypDecryptCipher(boolean isEncrypt, int secTableIdex) {
        Cipher cipher = null;

        try {
            int mode = isEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;
            int rSecTableIdex = secTableIdex < SECR_KEY_TABLE.length ? secTableIdex : DEFAULT_KEYTABLE_INDEX;

            SecretKeySpec SecKey = new SecretKeySpec(SECR_KEY_TABLE[rSecTableIdex], SECR_ALGORITHM);
            cipher = Cipher.getInstance(SECR_ALGORITHM);
            cipher.init(mode, SecKey);
        } catch (Exception e) {
        }

        return cipher;
    }

    /**
     * 把输入流内容写入输出流
     * 
     * @param inStream
     *            ： 输入流
     * @param outStream
     *            ： 输出流
     * @return ：写入输出流的字节数
     */
    private static int diversionStream(InputStream inStream, OutputStream outStream) {
        int count = 0;

        try {
            int byteCnt = 0;
            byte[] byteBuf = new byte[BUFFER_SIZE];

            while ((byteCnt = inStream.read(byteBuf)) != -1) {
                count += byteCnt;
                outStream.write(byteBuf, 0, byteCnt);
            }
        } catch (Exception e) {
        }

        return count;
    }

    /**
     * 判断两个byte数组的指定长度内容是否相等
     * 
     * @param byte1
     *            ： 要比较数组1
     * @param byte2
     *            ：要比较的数组2
     * @param len
     *            ：要比较的长度
     * @return : 相等返回true, 否则返回false
     */
    private static boolean isEquals(byte[] byte1, byte[] byte2, int len) {
        boolean result = true;
        int minByteLen = byte1.length <= byte2.length ? byte1.length : byte2.length;

        if (len <= minByteLen) {
            for (int i = 0; i < len; i++) {
                if (byte1[i] != byte2[i]) {
                    result = false;
                    break;
                }
            }
        } else {
            result = false;
        }

        return result;
    }

    /**
     * 通过比较calculatedValue 和 srcValue
     * 是否相等判断信息是否完整，当scrValue为null时，认为不要判断，因此返回true
     * 
     * @param calculatedValue
     *            : 要判断的指纹摘要
     * @param srcValue
     *            ： 源指纹摘要
     * @return ： 完整时返回true, 否则返回false
     */
    private static boolean isInformationIntegrity(String calculatedValue, String srcValue) {
        boolean rlt = false;

        if (null != calculatedValue) {
            rlt = (null == srcValue ? true : srcValue.equalsIgnoreCase(calculatedValue));
        }

        return rlt;
    }
}
