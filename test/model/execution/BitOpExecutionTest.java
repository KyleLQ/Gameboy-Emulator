package model.execution;

import model.CPU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutil.TestUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BitOpExecutionTest {
    CPU cpu;
    @BeforeEach
    public void setup() {
        cpu = new CPU();
    }

    @Test
    public void testBIT() {
        cpu.setCarryFlag(0);
        cpu.setZeroFlag(0);
        cpu.setRb((byte) 0b11110000);
        byte instruction = (byte) 0b01111000;
        System.out.println("Bit at pos 7 of  " + TestUtil.convertByteToBinaryString(cpu.getRb()) + " is zero: ");
        cpu.decodeCBInstruction(instruction);
        System.out.println(cpu.getZeroFlag() == 1);
        assertEquals(0, cpu.getZeroFlag()); // unchanged
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(1, cpu.getHalfCarryFlag());
        assertEquals(0, cpu.getCarryFlag()); // shouldn't be modified
        assertEquals(cpu.getRb(), (byte) 0b11110000); // shouldn't be modified

        cpu.setCarryFlag(0);
        cpu.setZeroFlag(0);
        cpu.setRe((byte) 0b01010101);
        instruction = (byte) 0b01001011;
        System.out.println("Bit at pos 1 of " + TestUtil.convertByteToBinaryString(cpu.getRe()) + " is zero: ");
        cpu.decodeCBInstruction(instruction);
        System.out.println(cpu.getZeroFlag() == 1);
        assertEquals(1, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(1, cpu.getHalfCarryFlag());
        assertEquals(0, cpu.getCarryFlag()); // shouldn't be modified
        assertEquals(cpu.getRe(), (byte) 0b01010101); // shouldn't be modified
    }

    @Test
    public void testRES() {
        cpu.setZeroFlag(0);
        cpu.setSubtractionFlag(1);
        cpu.setHalfCarryFlag(0);
        cpu.setCarryFlag(1);
        cpu.setRb((byte) 0b11110000);
        byte instruction = (byte) 0b10110000;
        System.out.println("Bit at pos 6 of " + TestUtil.convertByteToBinaryString(cpu.getRb()) + " is set to zero: ");
        cpu.decodeCBInstruction(instruction);
        System.out.println(TestUtil.convertByteToBinaryString(cpu.getRb()));
        assertEquals(cpu.getRb(), (byte) 0b10110000);
        assertEquals(0, cpu.getZeroFlag()); // flags all should not be modified
        assertEquals(1, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(1, cpu.getCarryFlag());
    }

    @Test
    public void testSET() {
        cpu.setZeroFlag(1);
        cpu.setSubtractionFlag(1);
        cpu.setHalfCarryFlag(1);
        cpu.setCarryFlag(1);
        cpu.setRa((byte) 0b11110000);
        byte instruction = (byte) 0b11010111;
        System.out.println("Bit at pos 2 of " + TestUtil.convertByteToBinaryString(cpu.getRa()) + " is set to one: ");
        cpu.decodeCBInstruction(instruction);
        System.out.println(TestUtil.convertByteToBinaryString(cpu.getRa()));
        assertEquals(cpu.getRa(), (byte) 0b11110100);
        assertEquals(1, cpu.getZeroFlag()); // flags all should not be modified
        assertEquals(1, cpu.getSubtractionFlag());
        assertEquals(1, cpu.getHalfCarryFlag());
        assertEquals(1, cpu.getCarryFlag());
    }
}
