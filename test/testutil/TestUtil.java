package testutil;

import util.GameBoyUtil;

public class TestUtil {

    /**
     * @return arg as a string in base 16
     */
    public static String convertToHexString(Object arg) {
        return String.format("0x%08X", arg);
    }

    /**
     * @return b as a string in base 2
     */
    public static String convertByteToBinaryString(Byte b) {
        return "0b" +
                String.format("%8s", Integer.toBinaryString(GameBoyUtil.zeroExtendByte(b))).replace(' ', '0');
    }

    /**
     * @return b as a string in unsigned base 10
     */
    public static String convertByteToUnsignedString(Byte b) {
        return Integer.toString(GameBoyUtil.zeroExtendByte(b));
    }

    /**
     * @return s as a string in unsigned base 10
     */
    public static String convertShortToUnsignedString(Short s) {
        return Integer.toString(GameBoyUtil.zeroExtendShort(s));
    }


}
