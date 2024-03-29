package util;

import exception.CPUException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GBUtilTest {
    @Test
    public void testGetBitPosFromByte() {
        byte b = (byte) 0b10010010;
        assertEquals(1, GBUtil.getBit(b,7));
        assertEquals(0, GBUtil.getBit(b,6));
        assertEquals(0, GBUtil.getBit(b,5));
        assertEquals(1, GBUtil.getBit(b,4));
        assertEquals(0, GBUtil.getBit(b,3));
        assertEquals(0, GBUtil.getBit(b,2));
        assertEquals(1, GBUtil.getBit(b,1));
        assertEquals(0, GBUtil.getBit(b,0));

        try {
            GBUtil.getBit(b, -1);
            fail("pos is out of bounds, but didn't fail");
        } catch (CPUException e) {

        } catch (Exception e) {
            fail("pos is out of bounds, and failed with wrong exception");
        }
    }

    @Test
    public void testGetBitPosFromShort() {
        short s = (short) 0b1111000010010010;
        assertEquals(1, GBUtil.getBit(s,15));
        assertEquals(1, GBUtil.getBit(s,14));
        assertEquals(1, GBUtil.getBit(s,13));
        assertEquals(1, GBUtil.getBit(s,12));
        assertEquals(0, GBUtil.getBit(s,11));
        assertEquals(0, GBUtil.getBit(s,10));
        assertEquals(0, GBUtil.getBit(s,9));
        assertEquals(0, GBUtil.getBit(s,8));
        assertEquals(1, GBUtil.getBit(s,7));
        assertEquals(0, GBUtil.getBit(s,6));
        assertEquals(0, GBUtil.getBit(s,5));
        assertEquals(1, GBUtil.getBit(s,4));
        assertEquals(0, GBUtil.getBit(s,3));
        assertEquals(0, GBUtil.getBit(s,2));
        assertEquals(1, GBUtil.getBit(s,1));
        assertEquals(0, GBUtil.getBit(s,0));

        try {
            GBUtil.getBit(s, 16);
            fail("pos is out of bounds, but didn't fail");
        } catch (CPUException e) {

        } catch (Exception e) {
            fail("pos is out of bounds, and failed with wrong exception");
        }
    }

    @Test
    public void testModifyBitOnPosInByte() {
        byte b = (byte) 0b10101010;
        b = GBUtil.modifyBit(b,0, 1);
        assertEquals((byte) 0b10101011, b);
        b = GBUtil.modifyBit(b,1, 0);
        assertEquals((byte) 0b10101001, b);
        b = GBUtil.modifyBit(b,2, 1);
        assertEquals((byte) 0b10101101, b);
        b = GBUtil.modifyBit(b,3, 0);
        assertEquals((byte) 0b10100101, b);
        b = GBUtil.modifyBit(b,4, 1);
        assertEquals((byte) 0b10110101, b);
        b = GBUtil.modifyBit(b,5, 0);
        assertEquals((byte) 0b10010101, b);
        b = GBUtil.modifyBit(b,6, 1);
        assertEquals((byte) 0b11010101, b);
        b = GBUtil.modifyBit(b,7, 0);
        assertEquals((byte) 0b01010101, b);

        try {
            GBUtil.modifyBit(b, 8, 1);
            fail("pos is out of bounds, but didn't fail");
        } catch (CPUException e) {

        } catch (Exception e) {
            fail("pos is out of bounds, and failed with wrong exception");
        }

        try {
            GBUtil.modifyBit(b,1,2);
            fail("bit is out of bounds, but didn't fail");
        } catch (CPUException e) {

        } catch (Exception e) {
            fail("bit is out of bounds, and failed with wrong exception");
        }
    }

    @Test
    public void testZeroExtensionByte() {
        byte b = (byte) 0xff;
        int i = GBUtil.zeroExtend(b);
        System.out.println(TestUtil.convertToHexString(i));
        assertEquals(i, 0xff);

        b = (byte) 0x11;
        i = GBUtil.zeroExtend(b);
        System.out.println(TestUtil.convertToHexString(i));
        assertEquals(i, 0x11);
    }

    @Test
    public void testZeroExtensionShort() {
        short s = (short) 0xffff;
        int i = GBUtil.zeroExtend(s);
        System.out.println(TestUtil.convertToHexString(i));
        assertEquals(i, 0xffff);

        s = (short) 0x1111;
        i = GBUtil.zeroExtend(s);
        System.out.println(TestUtil.convertToHexString(i));
        assertEquals(i, 0x1111);
    }

    @Test
    public void testGetNibble() {
        byte b = (byte) 0b10100101;
        int lowerNibble = GBUtil.getNibble(true, b);
        int upperNibble = GBUtil.getNibble(false, b);

        assertEquals(10, upperNibble);
        assertEquals(5, lowerNibble);
    }

    @Test
    public void testGetShortFromBytes() {
        byte lsb = (byte) 0b11001100;
        byte msb = (byte) 0b01010101;
        short result = GBUtil.getShortFromBytes(lsb, msb);
        assertEquals((short) 0b0101010111001100, result);
    }

    @Test
    public void testGetByteFromShort() {
        short u16 = (short) 0x92FA;
        byte lsb = GBUtil.getByteFromShort(true, u16);
        byte msb = GBUtil.getByteFromShort(false, u16);
        assertEquals((byte) 0xFA, lsb);
        assertEquals((byte) 0x92, msb);
    }
}
