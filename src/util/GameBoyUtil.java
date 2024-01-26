package util;

import exception.CPUException;

public class GameBoyUtil {
    public static final int UNSIGNED_SHORT_MAX = 65535;
    public static final int UNSIGNED_12_BIT_MAX = 4095;
    public static final int UNSIGNED_BYTE_MAX = 255;
    public static final int UNSIGNED_NIBBLE_MAX = 15;

    /**
     * @param b the byte to get the bit from
     * @param pos the position of the bit in byte b, in [0,7]
     * @return value of bit, in [0,1]
     */
    public static int getBitFromPosInByte(byte b, int pos) {
        if (pos < 0 || pos > 7) {
            throw new CPUException("pos is out of range!");
        }

        int bitmask = 0b00000001;
        bitmask = bitmask << pos;

        return (b & bitmask) >> pos;
    }

    /**
     * @param bit2 the bit at position 2 of return value
     * @param bit1 the bit at position 1 of return value
     * @param bit0 the bit at position 0 of return value
     * @return the value of a number with bit2 at position 2, bit1 at position 1,
     *         and bit0 at position 0, in [0,7]
     */
    public static int get3BitValue(int bit2, int bit1, int bit0) {
        return bit2 * 4 + bit1 * 2 + bit0;
    }

    /**
     * @param bit1 the bit at position 1 of return value
     * @param bit0 the bit at position 0 of return value
     * @return the value of a number with bit1 at position 1,
     *         and bit0 at position 0, in [0,3]
     */
    public static int get2BitValue(int bit1, int bit0) {
        return  bit1 * 2 + bit0;
    }

    /**
     * @param lower is true if we want the lower 4 bits of b, false if upper 4 bits
     * @param b is the byte
     * @return either the upper 4 or lower 4 bits of byte b as an int value
     *          e.g. 0b1010 0101 would return either 10 (upper) or 5 (lower)
     */
    public static int getNibble(boolean lower, byte b) {
        if (lower) {
            return getBitFromPosInByte(b, 3) * 8 + getBitFromPosInByte(b, 2) * 4 +
                    getBitFromPosInByte(b, 1) * 2 + getBitFromPosInByte(b, 0);
        } else {
            return getBitFromPosInByte(b, 7) * 8 + getBitFromPosInByte(b, 6) * 4 +
                    getBitFromPosInByte(b, 5) * 2 + getBitFromPosInByte(b, 4);
        }
    }

    /**
     * @param b the byte to zero extend
     * @return byte b zero extended to become an int
     */
    public static int zeroExtendByte(byte b) {
        return ((int) b) & 0x000000ff;
    }

    /**
     * @param s the short to zero extend
     * @return short s extended to become an int
     */
    public static int zeroExtendShort(short s) {
        return ((int) s) & 0x0000ffff;
    }

    /**
     * @param b the byte to convert
     * @return a string representing the value of b in binary. (A similar method exists in TestUtil, but
     * this doesn't add the prefix "0b")
     */
    public static String convertByteToBinaryString(byte b) {
        return String.format("%8s",
                        Integer.toBinaryString(GameBoyUtil.zeroExtendByte(b))).replace(' ', '0');
    }
}
