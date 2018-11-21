package com.andr.tool.encode;

import android.annotation.SuppressLint;

import com.andr.tool.util.StringUtil;


@SuppressLint("UseValueOf")
public class DataConvert {

    public static int byte2Int(byte[] inByteArray, boolean isBigEndian) {
        int sum = 0;
        int tmp = 0;

        for (int i = 0; i < 4; i++) {
            tmp = ((int) inByteArray[i]) & 0xff;

            if (isBigEndian) {
                tmp <<= (3 - i) * 8;
            } else {
                tmp <<= i * 8;
            }

            sum = tmp + sum;
        }

        return sum;
    }

    public static byte[] int2byteArray(int value, boolean isBigEndian) {
        byte[] targets = new byte[4];

        if (isBigEndian) {
            targets[3] = (byte) (value & 0xff); // ���λ
            targets[2] = (byte) ((value >> 8) & 0xff); // �ε�λ
            targets[1] = (byte) ((value >> 16) & 0xff); // �θ�λ
            targets[0] = (byte) (value >>> 24); // ���λ,�޷������
        } else {
            targets[0] = (byte) (value & 0xff); // ���λ
            targets[1] = (byte) ((value >> 8) & 0xff); // �ε�λ
            targets[2] = (byte) ((value >> 16) & 0xff); // �θ�λ
            targets[3] = (byte) (value >>> 24); // ���λ,�޷������
        }

        return targets;
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

    public static String string2Unicode(String srcStr) {
        String rlt = null;
        if (!StringUtil.isStringEmpty(srcStr)) {
            StringBuffer buf = new StringBuffer();
            char[] utfBytes = srcStr.toCharArray();
            for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
                String hexB = Integer.toHexString(utfBytes[byteIndex]);
                if (hexB.length() <= 2) {
                    hexB = "00" + hexB;
                }
                buf.append("\\u");
                buf.append(hexB);
            }
            rlt = buf.toString();
        }

        return (StringUtil.isStringEmpty(rlt) ? StringUtil.UNKNOWN : rlt);
    }

    public static String unicode2String(String unicodeStr) {
        String rlt = "";
        if (!StringUtil.isStringEmpty(unicodeStr)) {
            int start = 0;
            int end = 0;
            StringBuffer buffer = new StringBuffer();
            while (start > -1) {
                end = unicodeStr.indexOf("\\u", start + 2);
                String charStr = "";
                if (end == -1) {
                    charStr = unicodeStr.substring(start + 2, unicodeStr.length());
                } else {
                    charStr = unicodeStr.substring(start + 2, end);
                }
                char letter = (char) Integer.parseInt(charStr, 16);
                buffer.append(new Character(letter).toString());
                start = end;
            }
            rlt = buffer.toString();
        }

        return rlt;
    }
}
