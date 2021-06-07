package youssef.kecheima.topchat_v12.Model;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils
{
    private static final byte[] keyValues= new byte[]{'c','o','d','i','n','g','a','f','f','a','i','r','s','c','o','m'};

    public static String encrypt(String clearText){
        byte[] rawKey=getRawKey();
        byte[] result=encrypt(rawKey,clearText.getBytes());
        return toHex(result);
    }

    private static byte[] getRawKey()  {
        SecretKey key = new SecretKeySpec(keyValues,"AES");
        byte[] raw=key.getEncoded();
        return raw;
    }
    private static byte[] encrypt(byte[] raw,byte[] clear) {
        try {

            SecretKey skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(clear);
            return encrypted;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static String toHex(byte[] buf){
        if(buf==null)
            return "";
        StringBuffer result= new StringBuffer(2*buf.length);
        for(int i=0;i<buf.length;i++){
            appendHex(result,buf[i]);
        }
        return result.toString();
    }
    private final static String HEX="0123456789ABCDEF";
    private static void appendHex(StringBuffer sb,byte b){
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }



    public static String decrypt(String encrypted)
    {
        byte[] enc=toByte(encrypted);
        byte[] result=decrypt(enc);
        return new String(result);
    }

    private static byte[] decrypt (byte[] encrypted)
    {
        try {

            SecretKey skeySpec = new SecretKeySpec(keyValues, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] decrypted = cipher.doFinal(encrypted);
            return decrypted;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] toByte(String hexString){
        int len =hexString.length() / 2;
        byte[] result = new byte[len];
        for(int i=0;i<len;i++)
            result[i]=Integer.valueOf(hexString.substring(2*i,2*i+2),16).byteValue();

        return result;
    }
}
