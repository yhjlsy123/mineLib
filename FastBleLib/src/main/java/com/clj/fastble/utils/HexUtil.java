package com.clj.fastble.utils;

import android.util.Log;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class HexUtil {

    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        if (data == null)
            return null;
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }


    public static String encodeHexStr(byte[] data) {
        return encodeHexStr(data, true);
    }

    public static String encodeHexStr(byte[] data, boolean toLowerCase) {
        return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }


    protected static String encodeHexStr(byte[] data, char[] toDigits) {
        return new String(encodeHex(data, toDigits));
    }

    public static String formatHexString(byte[] data) {
        return formatHexString(data, false);
    }

    public static String formatHexString(byte[] data, boolean addSpace) {
        if (data == null || data.length < 1)
            return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
            if (addSpace)
                sb.append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Autor:Administrator
     * CreatedTime:2019/11/12 0012
     * UpdateTime:2019/11/12 0012 13:26
     * Des:单位0.1mm
     * UpdateContent:
     **/
    public static int getResult(byte[] data, boolean addSpace) {
        String strOrResutl = HexUtil.formatHexString(data, true);
        List<String> listResult = Arrays.asList(strOrResutl.split(" "));
        String hString = listResult.subList(4, 6).toString().replace(",", "").replace("[", "").replace("]", "").replaceAll("\\s*", "");
        String lString = listResult.subList(6, 8).toString().replace(",", "").replace("[", "").replace("]", "").replaceAll("\\s*", "");
        int hRes = new BigInteger("ffff", 16).intValue() - new BigInteger(hString, 16).intValue();
        int lRes = new BigInteger("ffff", 16).intValue() - new BigInteger(lString, 16).intValue();
        return (16 * 16 * 16 * 16 - 1) * hRes + hRes + lRes;
    }

    /**
     * Autor:Administrator
     * CreatedTime:2019/11/16 0016
     * UpdateTime:2019/11/16 0016 9:39
     * Des:获取体温计,字节长度11,位置2是02H
     * UpdateContent:
     **/
    public static int getThermometer(byte[] data, boolean addSpace) {
        String strOrResutl = HexUtil.formatHexString(data, true);
        Log.d("lsy", strOrResutl);
        List<String> listResult = Arrays.asList(strOrResutl.split(" "));
        if (listResult.size() == 12) {
            int resResult = new BigInteger("ff", 16).intValue() - new BigInteger(listResult.get(9), 16).intValue() + (new BigInteger("ff", 16).intValue() - new BigInteger(listResult.get(10), 16).intValue()) * 16 + (new BigInteger("ff", 16).intValue() - new BigInteger(listResult.get(11), 16).intValue()) * 16 * 16;
            return resResult;
        } else {
            return 0;
        }

    }


    public static byte[] decodeHex(char[] data) {

        int len = data.length;

        if ((len & 0x01) != 0) {
            throw new RuntimeException("Odd number of characters.");
        }

        byte[] out = new byte[len >> 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }


    protected static int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch
                    + " at index " + index);
        }
        return digit;
    }


    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.trim();
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String extractData(byte[] data, int position) {
        return HexUtil.formatHexString(new byte[]{data[position]});
    }

}
