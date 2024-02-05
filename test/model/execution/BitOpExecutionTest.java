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
    public void testRLC() {
        cpu.setZeroFlag(1);
        cpu.setSubtractionFlag(1);
        cpu.setHalfCarryFlag(1);
        cpu.setCarryFlag(0);
        cpu.setRb((byte) 0b10101010);
        byte instruction = (byte) 0b00000000;
        System.out.println("RLC " + TestUtil.convertByteToBinaryString(cpu.getRb()));
        cpu.decodeCBInstruction(instruction);
        System.out.println("Register: " + TestUtil.convertByteToBinaryString(cpu.getRb()) +
                ", carry: " + cpu.getCarryFlag());
        assertEquals((byte) 0b01010101, cpu.getRb());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(1, cpu.getCarryFlag());
    }

    @Test
    public void testRRC() {
        cpu.setZeroFlag(1);
        cpu.setSubtractionFlag(1);
        cpu.setHalfCarryFlag(1);
        cpu.setCarryFlag(1);
        cpu.setRc((byte) 0b11110000);
        byte instruction = (byte) 0b00001001;
        System.out.println("RRC " + TestUtil.convertByteToBinaryString(cpu.getRc()));
        cpu.decodeCBInstruction(instruction);
        System.out.println("Register: " + TestUtil.convertByteToBinaryString(cpu.getRc()) +
                ", carry: " + cpu.getCarryFlag());
        assertEquals((byte) 0b01111000, cpu.getRc());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(0, cpu.getCarryFlag());
    }

    @Test
    public void testRL() {
        cpu.setZeroFlag(0);
        cpu.setSubtractionFlag(1);
        cpu.setHalfCarryFlag(1);
        cpu.setCarryFlag(0);
        cpu.setRd((byte) 0b10000000);
        byte instruction = (byte) 0b00010010;
        System.out.println("RL " + TestUtil.convertByteToBinaryString(cpu.getRd()) +
                ", carry: " + cpu.getCarryFlag());
        cpu.decodeCBInstruction(instruction);
        System.out.println("Register: " + TestUtil.convertByteToBinaryString(cpu.getRd()) +
                ", carry: " + cpu.getCarryFlag());
        assertEquals((byte) 0b00000000, cpu.getRd());
        assertEquals(1, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(1, cpu.getCarryFlag());
    }

    @Test
    public void testRR() {
        cpu.setZeroFlag(1);
        cpu.setSubtractionFlag(1);
        cpu.setHalfCarryFlag(1);
        cpu.setCarryFlag(1);
        cpu.setRd((byte) 0b00110100);
        byte instruction = (byte) 0b00011010;
        System.out.println("RR " + TestUtil.convertByteToBinaryString(cpu.getRd()) +
                ", carry: " + cpu.getCarryFlag());
        cpu.decodeCBInstruction(instruction);
        System.out.println("Register: " + TestUtil.convertByteToBinaryString(cpu.getRd()) +
                ", carry: " + cpu.getCarryFlag());
        assertEquals((byte) 0b10011010, cpu.getRd());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(0, cpu.getCarryFlag());
    }

    @Test
    public void testSLA() {
        cpu.setZeroFlag(1);
        cpu.setSubtractionFlag(1);
        cpu.setHalfCarryFlag(1);
        cpu.setCarryFlag(0);
        cpu.setRd((byte) 0b11000011);
        byte instruction = (byte) 0b00100010;
        System.out.println("SLA " + TestUtil.convertByteToBinaryString(cpu.getRd()) +
                ", carry: " + cpu.getCarryFlag());
        cpu.decodeCBInstruction(instruction);
        System.out.println("Register: " + TestUtil.convertByteToBinaryString(cpu.getRd()) +
                ", carry: " + cpu.getCarryFlag());
        assertEquals((byte) 0b10000110, cpu.getRd());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(1, cpu.getCarryFlag());
    }

    @Test
    public void testSRA() {
        cpu.setZeroFlag(1);
        cpu.setSubtractionFlag(1);
        cpu.setHalfCarryFlag(1);
        cpu.setCarryFlag(0);
        cpu.setRd((byte) 0b10000001);
        byte instruction = (byte) 0b00101010;
        System.out.println("SRA " + TestUtil.convertByteToBinaryString(cpu.getRd()) +
                ", carry: " + cpu.getCarryFlag());
        cpu.decodeCBInstruction(instruction);
        System.out.println("Register: " + TestUtil.convertByteToBinaryString(cpu.getRd()) +
                ", carry: " + cpu.getCarryFlag());
        assertEquals((byte) 0b11000000, cpu.getRd());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(1, cpu.getCarryFlag());
    }

    @Test
    public void testSWAP() {
        cpu.setZeroFlag(1);
        cpu.setSubtractionFlag(1);
        cpu.setHalfCarryFlag(1);
        cpu.setCarryFlag(1);
        cpu.setRl((byte) 0b01001010);
        byte instruction = (byte) 0b00110101;
        System.out.println("SWAP " + TestUtil.convertByteToBinaryString(cpu.getRl()));
        cpu.decodeCBInstruction(instruction);
        System.out.println(TestUtil.convertByteToBinaryString(cpu.getRl()));
        assertEquals((byte) 0b10100100, cpu.getRl());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(0, cpu.getCarryFlag());
    }

    @Test
    public void testSRL() {
        cpu.setZeroFlag(0);
        cpu.setSubtractionFlag(1);
        cpu.setHalfCarryFlag(1);
        cpu.setCarryFlag(0);
        cpu.setRd((byte) 0b00000001);
        byte instruction = (byte) 0b00111010;
        System.out.println("SRL " + TestUtil.convertByteToBinaryString(cpu.getRd()) +
                ", carry: " + cpu.getCarryFlag());
        cpu.decodeCBInstruction(instruction);
        System.out.println("Register: " + TestUtil.convertByteToBinaryString(cpu.getRd()) +
                ", carry: " + cpu.getCarryFlag());
        assertEquals((byte) 0b00000000, cpu.getRd());
        assertEquals(1, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(1, cpu.getCarryFlag());
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
