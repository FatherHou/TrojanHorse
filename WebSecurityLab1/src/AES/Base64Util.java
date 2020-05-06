package AES;

import java.util.Base64;

/**
 * Convert invisible characters to visible characters with Base64 encoding rules
 * make it easy to print non printable and invisible characters
 * @author Jordon
 */
public class Base64Util {

    /**
     * Encoding a byte array (AES)
     * encode byte array
     * @param bytes byte array
     * @return encoded string
     */
    public static String encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Decode a Base64 encoded string into a short array (AES)
     * decode a encoded string to short array
     * @param encodedText encoded text
     * @return short array
     */
    public static short[] decodeToShorts(String encodedText) {
        return ArrayUtil.byteToShorts(Base64.getDecoder().decode(encodedText));
    }

    /**
     * encode encrypted text bits by Base64 for processing unprintable (DES)
     * and invisible character
     * @param chars encrypted text bits
     * @return encoded text
     */
    public static String encode(char[] chars) {
        char[][] segmentedChars = ArrayUtil.segmentDimension(chars, chars.length / 8, 8);
        byte[] bytes = new byte[0];
        for (char[] segmentedChar : segmentedChars) {
            bytes = ArrayUtil.concat(bytes, ArrayUtil.int2Bytes(
                    Integer.parseInt(String.valueOf(segmentedChar), 2)));
        }
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * decode decoded encrypted text bits in chars format (DES)
     * @param encodedText encoded text
     * @return encrypted text bits in chars format
     */
    public static char[] decodeToChars(String encodedText) {
        return ArrayUtil.bytesToChars(Base64.getDecoder().decode(encodedText));
    }

    /**
     * Unprocessed strings are encoded in Base64 (RC4)
     * @param rawString Unprocessed string
     * @return Encoded string
     */
    public static String encode(String rawString) {
        char[] chars = rawString.toCharArray();
        byte[] bytes = new byte[0];
        for (char c : chars) {
            bytes = ArrayUtil.concat(bytes, ArrayUtil.int2Bytes(c));
        }
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Decoding a Base64 encoded string (RC4)
     * @param encodedText Base64 encoded string
     * @return Decoding result
     */
    public static String decode(String encodedText) {
        StringBuilder builder = new StringBuilder();
        byte[] bytes = Base64.getDecoder().decode(encodedText);
        for (byte b : bytes) {
            builder.append((char) (b & 0xff));
        }
        return builder.toString();
    }
}