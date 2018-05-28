package com.riddleandcode.nfcbasic.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * Created by Eyliss on 2/1/17.
 */

public class Util {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                  + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String charStringtoHexString(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    }

    public static byte[] concatArray(byte[] array1, byte[] array2) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write(array1);
        outputStream.write(array2);
        return outputStream.toByteArray();
    }

    public static byte[] hashString(String originalMessage) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(originalMessage.getBytes("UTF-8"));
        byte[] message = md.digest();
        return message;
    }

    public static byte[] convertIntToByteArray(int[] data){
        byte[] rowbyte = new byte[data.length];

        for (int i = 0; i <data.length ; i++) {
            rowbyte[i] = (byte) data[i];
        }

        return rowbyte;
    }

    /*
     * packing an array of 4 bytes to an int, big endian
     */
    public static int fromByteArray(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }
}
