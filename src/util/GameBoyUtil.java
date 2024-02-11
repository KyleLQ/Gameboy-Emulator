package util;

import exception.CPUException;
import model.cpu.CPU;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class GameBoyUtil {
    public static final int UNSIGNED_SHORT_MAX = 65535;
    public static final int UNSIGNED_12_BIT_MAX = 4095;
    public static final int UNSIGNED_BYTE_MAX = 255;
    public static final int UNSIGNED_NIBBLE_MAX = 15;

    // return (byte) 0 is just a placeholder for (HL) todo
    public static final List<Function<CPU, Byte>> INSTRUCTION_TO_GET_R8_MAP = Arrays.asList(
            CPU::getRb,
            CPU::getRc,
            CPU::getRd,
            CPU::getRe,
            CPU::getRh,
            CPU::getRl,
            (CPU cpu) -> {
                return (byte) 0;
            },
            CPU::getRa
    );

    // the empty method is a placeholder for (HL) todo
    public static final List<BiConsumer<Byte, CPU>> INSTRUCTION_TO_SET_R8_MAP = Arrays.asList(
            (Byte b, CPU cpu) -> cpu.setRb(b),
            (Byte b, CPU cpu) -> cpu.setRc(b),
            (Byte b, CPU cpu) -> cpu.setRd(b),
            (Byte b, CPU cpu) -> cpu.setRe(b),
            (Byte b, CPU cpu) -> cpu.setRh(b),
            (Byte b, CPU cpu) -> cpu.setRl(b),
            (Byte b, CPU cpu) -> {},
            (Byte b, CPU cpu) -> cpu.setRa(b)
    );

    public static final List<Function<CPU, Short>> INSTRUCTION_TO_GET_R16_SP_MAP = Arrays.asList(
            CPU::getRegisterBC,
            CPU::getRegisterDE,
            CPU::getRegisterHL,
            CPU::getStackPointer
    );

    public static final List<BiConsumer<Short, CPU>> INSTRUCTION_TO_SET_R16_SP_MAP = Arrays.asList(
            (Short s, CPU cpu) -> cpu.setRegisterBC(s),
            (Short s, CPU cpu) -> cpu.setRegisterDE(s),
            (Short s, CPU cpu) -> cpu.setRegisterHL(s),
            (Short s, CPU cpu) -> cpu.setStackPointer(s)
    );

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
     * @param b the byte to modify
     * @param pos the position of the bit in b to modify
     * @param bit the value to modify b[pos] to, 0 or 1
     * @return byte b, but with b[pos] modified to bit's value
     */
    public static byte modifyBitOnPosInByte(byte b, int pos, int bit) {
        if (pos < 0 || pos > 7) {
            throw new CPUException("pos is out of range!");
        }

        if (bit == 0) {
            return (byte) (b & ~(0b00000001 << pos));
        } else if (bit == 1) {
            return (byte) (b | 0b00000001 << pos);
        } else {
            throw new CPUException("bit out of bounds!");
        }
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

    /**
     * @param lsb the least significant byte
     * @param msb the most significant byte
     * @return a short constructed from lsb and msb
     */
    public static Short getShortFromBytes(byte lsb, byte msb) {
        return (short) (zeroExtendByte(lsb) + (zeroExtendByte(msb) << 8));
    }

    /**
     * @param lsb true if you want the least significant byte of u16
     * @param u16 the short to get bytes from
     * @return one of the two bytes that make up u16
     */
    public static byte getByteFromShort(boolean lsb, short u16) {
        if (lsb) {
            return (byte) u16;
        } else {
            return (byte) (u16 >> 8);
        }
    }
}
