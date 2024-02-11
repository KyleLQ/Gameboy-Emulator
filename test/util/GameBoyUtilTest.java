package util;

import exception.CPUException;
import org.junit.jupiter.api.Test;
import testutil.TestUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GameBoyUtilTest {
    @Test
    public void testGetBitPosFromByte() {
        byte b = (byte) 0b10010010;
        assertEquals(1, GameBoyUtil.getBitFromPosInByte(b,7));
        assertEquals(0, GameBoyUtil.getBitFromPosInByte(b,6));
        assertEquals(0, GameBoyUtil.getBitFromPosInByte(b,5));
        assertEquals(1, GameBoyUtil.getBitFromPosInByte(b,4));
        assertEquals(0, GameBoyUtil.getBitFromPosInByte(b,3));
        assertEquals(0, GameBoyUtil.getBitFromPosInByte(b,2));
        assertEquals(1, GameBoyUtil.getBitFromPosInByte(b,1));
        assertEquals(0, GameBoyUtil.getBitFromPosInByte(b,0));

        try {
            GameBoyUtil.getBitFromPosInByte(b, -1);
            fail("pos is out of bounds, but didn't fail");
        } catch (CPUException e) {

        } catch (Exception e) {
            fail("pos is out of bounds, and failed with wrong exception");
        }
    }

    @Test
    public void testModifyBitOnPosInByte() {
        byte b = (byte) 0b10101010;
        b = GameBoyUtil.modifyBitOnPosInByte(b,0, 1);
        assertEquals((byte) 0b10101011, b);
        b = GameBoyUtil.modifyBitOnPosInByte(b,1, 0);
        assertEquals((byte) 0b10101001, b);
        b = GameBoyUtil.modifyBitOnPosInByte(b,2, 1);
        assertEquals((byte) 0b10101101, b);
        b = GameBoyUtil.modifyBitOnPosInByte(b,3, 0);
        assertEquals((byte) 0b10100101, b);
        b = GameBoyUtil.modifyBitOnPosInByte(b,4, 1);
        assertEquals((byte) 0b10110101, b);
        b = GameBoyUtil.modifyBitOnPosInByte(b,5, 0);
        assertEquals((byte) 0b10010101, b);
        b = GameBoyUtil.modifyBitOnPosInByte(b,6, 1);
        assertEquals((byte) 0b11010101, b);
        b = GameBoyUtil.modifyBitOnPosInByte(b,7, 0);
        assertEquals((byte) 0b01010101, b);

        try {
            GameBoyUtil.modifyBitOnPosInByte(b, 8, 1);
            fail("pos is out of bounds, but didn't fail");
        } catch (CPUException e) {

        } catch (Exception e) {
            fail("pos is out of bounds, and failed with wrong exception");
        }

        try {
            GameBoyUtil.modifyBitOnPosInByte(b,1,2);
            fail("bit is out of bounds, but didn't fail");
        } catch (CPUException e) {

        } catch (Exception e) {
            fail("bit is out of bounds, and failed with wrong exception");
        }
    }

    @Test
    public void testZeroExtensionByte() {
        byte b = (byte) 0xff;
        int i = GameBoyUtil.zeroExtendByte(b);
        System.out.println(TestUtil.convertToHexString(i));
        assertEquals(i, 0xff);

        b = (byte) 0x11;
        i = GameBoyUtil.zeroExtendByte(b);
        System.out.println(TestUtil.convertToHexString(i));
        assertEquals(i, 0x11);
    }

    @Test
    public void testZeroExtensionShort() {
        short s = (short) 0xffff;
        int i = GameBoyUtil.zeroExtendShort(s);
        System.out.println(TestUtil.convertToHexString(i));
        assertEquals(i, 0xffff);

        s = (short) 0x1111;
        i = GameBoyUtil.zeroExtendShort(s);
        System.out.println(TestUtil.convertToHexString(i));
        assertEquals(i, 0x1111);
    }

    @Test
    public void testGetNibble() {
        byte b = (byte) 0b10100101;
        int lowerNibble = GameBoyUtil.getNibble(true, b);
        int upperNibble = GameBoyUtil.getNibble(false, b);

        assertEquals(10, upperNibble);
        assertEquals(5, lowerNibble);
    }

    @Test
    public void testGetShortFromBytes() {
        byte lsb = (byte) 0b11001100;
        byte msb = (byte) 0b01010101;
        short result = GameBoyUtil.getShortFromBytes(lsb, msb);
        assertEquals((short) 0b0101010111001100, result);
    }

    @Test
    public void testGetByteFromShort() {
        short u16 = (short) 0x92FA;
        byte lsb = GameBoyUtil.getByteFromShort(true, u16);
        byte msb = GameBoyUtil.getByteFromShort(false, u16);
        assertEquals((byte) 0xFA, lsb);
        assertEquals((byte) 0x92, msb);
    }
}
