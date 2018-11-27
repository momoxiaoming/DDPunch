package com.andr.tool.encode;


/**
 * @Description: 十六进制转换类
 */
public class HexUtil {
    /**
     * 用于建立十六进制字符的输出的小写字符数组
     */
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 用于建立十六进制字符的输出的大写字符数组
     */
    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 将字节数组转换为十六进制字符数组, 字母小写
     *
     * @param data byte[]
     * @return 十六进制char[]
     */
    public static char[] encodeHex(byte[] data) 
    {
        return encodeHex(data, true);
    }

    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data        byte[]
     * @param toLowerCase <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
     * @return 十六进制char[]
     */
    public static char[] encodeHex(byte[] data, boolean toLowerCase) 
    {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER, null);
    }

    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data     byte[]
     * @param toDigits 用于控制输出的char[]
     * @return 十六进制char[]
     */
    protected static char[] encodeHex(byte[] data, char[] toDigits, char[] splitChar)
    {
        int l = data.length;
        int spChrLen = splitChar != null && 0 < splitChar.length ? splitChar.length : 0;
        int spChrNum = 0 < spChrLen ? (l - 1) * spChrLen : 0;
        char[] out = new char[(l << 1) + spChrNum];
        
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++)
        {
            out[j++] = toDigits[0x0F & (data[i]>>>4)];
            out[j++] = toDigits[0x0F & data[i]];

            int k = 0;
            while( k < spChrLen && i < l - 1)
            {
                out[j++] = splitChar[k++];
            }
        }
        return out;
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param data byte[]
     * @return 十六进制String
     */
    public static String encodeHexStr(byte[] data)
    {
        if(null != data)
        {
            return encodeHexStr(data, true);
        }
        else
        {
            return null;
        }
    }

    public static String encodeHexStrWithSplitChars(byte[] data, char[] splitChars)
    {
        return new String(encodeHex(data, DIGITS_LOWER, splitChars));
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param data        byte[]
     * @param toLowerCase <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
     * @return 十六进制String
     */
    public static String encodeHexStr(byte[] data, boolean toLowerCase)
    {
        return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param data     byte[]
     * @param toDigits 用于控制输出的char[]
     * @return 十六进制String
     */
    protected static String encodeHexStr(byte[] data, char[] toDigits)
    {
        return new String(encodeHex(data, toDigits, null));
    }


    /**
     * 将十六进制字符数组转换为字节数组
     *
     * @param data 十六进制char[]
     * @return byte[]
     * @throws RuntimeException 如果源十六进制字符数组是一个奇怪的长度，将抛出运行时异常
     */
    public static byte[] decodeHex(char[] data) 
    {

        int len = data.length;

        if ((len & 0x01) != 0) 
        {
            throw new RuntimeException("Odd number of characters.");
        }

        byte[] out = new byte[len >> 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) 
        {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }

    public static boolean isHex(char c)
    {
        return (('0' <= c && c <= '9') || ('A' <= c && c <= 'F') || ('a' <= c && c <= 'f')) ? true : false;
    }
    
    /**
     * 将十六进制字符转换成一个整数
     *
     * @param ch    十六进制char
     * @param index 十六进制字符在字符数组中的位置
     * @return 一个整数
     * @throws RuntimeException 当ch不是一个合法的十六进制字符时，抛出运行时异常
     */
    protected static int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }

    public static boolean isDevChkBytesEqual(byte[] src1, byte[] rdSrc2)
    {
        boolean rlt = false;

        if(src1 != null && rdSrc2 != null)
        {
            int i = 0;
            int len = rdSrc2.length;
            for(i = 0; i < len; i++)
            {
                if(src1[i] != rdSrc2[i])
                    break;
            }

            if(i == src1.length)
            {
                rlt = true;
            }
            else if (i == rdSrc2.length)
            {
                rlt = true;
                for(; i < src1.length; i++)
                {
                    if(src1[i] != 0)
                    {
                        rlt = false;
                        break;
                    }
                }
            }
        }
        return rlt;
    }

    public static String bytes2HexString(byte[] inBytes) {
        String rlt = "";

        if (null != inBytes && inBytes.length > 0) {
            StringBuffer result = new StringBuffer();
            for (int i = 0; i < inBytes.length; i++) {
                result.append(Integer.toHexString((0x000000ff & inBytes[i]) | 0xffffff00).substring(6));
            }

            rlt = result.toString();
        }

        return rlt;
    }
}
