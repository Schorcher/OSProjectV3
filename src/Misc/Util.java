package Misc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Created by davidmcfall on 3/9/16.
 */
public class Util
{
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";   // Actually Yellow
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public String hexToBinary(String hex)
    {
        String value = new BigInteger(removeHexPrefix(hex),16).toString(2);
        return String.format("%32s", value).replace(" ", "0");
    }

    public String hexToDecimal(String hex)
    {
        return new BigInteger(hex, 16).toString(10);
    }

    public String decimalToHex(String dec)
    {
        return String.format("0x%8S", Integer.toHexString(Integer.parseInt(dec))).replace(' ', '0');
    }

    public Integer parseInt(String str)
    {
        return Integer.parseUnsignedInt(str);
    }

    public Integer parseHexToDecimal(String hex)
    {
        return parseInt(new BigInteger(removeHexPrefix(hex), 16).toString(10));
    }

    public String removeHexPrefix(String hex)
    {
        return hex.replaceAll("x","");
    }

    public Integer parseBinaryToDecimal(String bin)
    {
        return Integer.parseUnsignedInt(bin,2);
    }

    public Integer binaryWordAddressToByteAddress(String binary)
    {
        return (parseBinaryToDecimal(binary) / 4);
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     *  Static short print method
     *
     * @param s
     * @param <T>
     */
    public static <T> void p(T s)
    {
        System.out.println(s);
    }


}
