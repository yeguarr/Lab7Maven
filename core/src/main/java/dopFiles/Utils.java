package dopFiles;

import exceptions.FailedCheckException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    /**
     * Проверка на тип Long
     */
    public static Checker<Long> routeDistanceCheck = (Long L) -> {
        if (L == null || L == 0) return null;
        else if (L > 1) return L;
        throw new FailedCheckException();
    };
    /**
     * Проверка на тип Integer
     */
    public static Checker<Integer> routeIdCheck = (Integer I) -> {
        if (I != null && I > 0) return I;
        else throw new FailedCheckException();
    };

    public static Checker<Integer> portCheck = (Integer I) -> {
        if (I != null && I >= 1 && I <= 65535) return I;
        else throw new FailedCheckException();
    };
    /**
     * Проверка на тип String
     */
    public static Checker<String> routeNameCheck = (String S) -> {
        if (S != null && S.length() != 0 && S.length() <= 20) return S;
        else throw new FailedCheckException();
    };
    /**
     * Проверка на тип String
     */
    public static Checker<String> loginCheck = (String S) -> {
        if (S != null && S.length() != 0 && S.length() <= 20) return S;
        else throw new FailedCheckException();
    };
    /**
     * Проверка на тип String
     */
    public static Checker<String> hashCheck = (String S) -> {
        if (S != null && S.length() == 40) return S;
        else throw new FailedCheckException();
    };
    /**
     * Проверка на тип String
     */
    public static Checker<String> passwordCheck = (String S) -> {
        if (S == null) return "password";
        else if (S.length() != 0) return S;
        else throw new FailedCheckException();
    };
    /**
     * Проверка на тип Long
     */
    public static Checker<Long> locationXYZCheck = (Long L) -> {
        if (L != null) return L;
        else throw new FailedCheckException();
    };
    /**
     * Проверка на тип String
     */
    public static Checker<String> locationNameCheck = (String s) -> {
        if (s == null) return null;
        else if (s.length() <= 867) return s;
        throw new FailedCheckException();
    };
    /**
     * Проверка для x Integer
     */
    public static Checker<Integer> coordinatesXCheck = (Integer I) -> {
        if (I != null) return I;
        else throw new FailedCheckException();
    };
    /**
     * Проверка для y Long
     */
    public static Checker<Long> coordinatesYCheck = (Long L) -> {
        if (L != null && L > -765) return L;
        else throw new FailedCheckException();
    };
    /**
     * Проверка для y Boolean
     */
    public static Checker<Boolean> boolCheck = (Boolean B) -> {
        if (B != null) return B;
        else throw new FailedCheckException();
    };

    public static String sha1(String input) {
        String sha1 = null;
        try {
            MessageDigest msdDigest = MessageDigest.getInstance("SHA-1");
            msdDigest.update(input.getBytes(StandardCharsets.UTF_8));
            sha1 = bytesToHex(msdDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sha1;
    }
    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }
}
