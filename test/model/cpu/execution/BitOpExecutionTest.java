package model.cpu.execution;

import model.cpu.CPU;
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
        cpu.decodeExecuteCBInstruction(instruction);
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
        cpu.decodeExecuteCBInstruction(instruction);
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
        cpu.decodeExecuteCBInstruction(instruction);
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
        cpu.decodeExecuteCBInstruction(instruction);
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
        cpu.decodeExecuteCBInstruction(instruction);
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
        cpu.decodeExecuteCBInstruction(instruction);
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
        cpu.decodeExecuteCBInstruction(instruction);
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
        cpu.decodeExecuteCBInstruction(instruction);
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
        cpu.decodeExecuteCBInstruction(instruction);
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
        cpu.decodeExecuteCBInstruction(instruction);
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
        cpu.decodeExecuteCBInstruction(instruction);
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
        cpu.decodeExecuteCBInstruction(instruction);
        System.out.println(TestUtil.convertByteToBinaryString(cpu.getRa()));
        assertEquals(cpu.getRa(), (byte) 0b11110100);
        assertEquals(1, cpu.getZeroFlag()); // flags all should not be modified
        assertEquals(1, cpu.getSubtractionFlag());
        assertEquals(1, cpu.getHalfCarryFlag());
        assertEquals(1, cpu.getCarryFlag());
    }

    @Test
    public void testRLCA() {
        cpu.setZeroFlag(1);
        cpu.setSubtractionFlag(1);
        cpu.setHalfCarryFlag(1);
        cpu.setCarryFlag(0);
        cpu.setRa((byte) 0b10101010);
        byte instruction = (byte) 0b00000111;
        System.out.println("RLCA " + TestUtil.convertByteToBinaryString(cpu.getRa()));
        cpu.decodeExecuteInstruction(instruction);
        System.out.println("Register: " + TestUtil.convertByteToBinaryString(cpu.getRa()) +
                ", carry: " + cpu.getCarryFlag());
        assertEquals((byte) 0b01010101, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(1, cpu.getCarryFlag());
    }

    @Test
    public void testRRCA() {
        cpu.setZeroFlag(1);
        cpu.setSubtractionFlag(1);
        cpu.setHalfCarryFlag(1);
        cpu.setCarryFlag(1);
        cpu.setRa((byte) 0b11100000);
        byte instruction = (byte) 0b00001111;
        System.out.println("RRCA " + TestUtil.convertByteToBinaryString(cpu.getRa()));
        cpu.decodeExecuteInstruction(instruction);
        System.out.println("Register: " + TestUtil.convertByteToBinaryString(cpu.getRa()) +
                ", carry: " + cpu.getCarryFlag());
        assertEquals((byte) 0b01110000, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(0, cpu.getCarryFlag());
    }

    @Test
    public void testRLA() {
        cpu.setZeroFlag(0);
        cpu.setSubtractionFlag(1);
        cpu.setHalfCarryFlag(1);
        cpu.setCarryFlag(0);
        cpu.setRa((byte) 0b10000000);
        byte instruction = (byte) 0b00010111;
        System.out.println("RLA " + TestUtil.convertByteToBinaryString(cpu.getRa()) +
                ", carry: " + cpu.getCarryFlag());
        cpu.decodeExecuteInstruction(instruction);
        System.out.println("Register: " + TestUtil.convertByteToBinaryString(cpu.getRa()) +
                ", carry: " + cpu.getCarryFlag());
        assertEquals((byte) 0b00000000, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(1, cpu.getCarryFlag());
    }

    @Test
    public void testRRA() {
        cpu.setZeroFlag(1);
        cpu.setSubtractionFlag(1);
        cpu.setHalfCarryFlag(1);
        cpu.setCarryFlag(1);
        cpu.setRa((byte) 0b00110100);
        byte instruction = (byte) 0b00011111;
        System.out.println("RRA " + TestUtil.convertByteToBinaryString(cpu.getRa()) +
                ", carry: " + cpu.getCarryFlag());
        cpu.decodeExecuteInstruction(instruction);
        System.out.println("Register: " + TestUtil.convertByteToBinaryString(cpu.getRa()) +
                ", carry: " + cpu.getCarryFlag());
        assertEquals((byte) 0b10011010, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(0, cpu.getCarryFlag());
    }

    @Test
    public void testDAAAddition() {
        /*
        0x37 + 0x78 = 0x115, but this is past 0x99, so truncate to 0x15
         */
        cpu.setRa((byte) 0x37);
        cpu.setRb((byte) 0x78);
        byte instruction = (byte) 0b10000000;
        System.out.println(TestUtil.convertToHexString(cpu.getRa()) + " + " +
                TestUtil.convertToHexString(cpu.getRb()) + " =");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertToHexString(cpu.getRa()));

        System.out.println("Apply DAA on " + TestUtil.convertToHexString(cpu.getRa()) + " to convert to BCD");
        instruction = (byte) 0b00100111;
        int prevSubtractionValue = cpu.getSubtractionFlag();
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertToHexString(cpu.getRa()));
        assertEquals((byte) 0x15, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(prevSubtractionValue, cpu.getSubtractionFlag()); // should be unchanged
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(1, cpu.getCarryFlag());

        /*
            0x19 + 0x24 = 0x43
         */
        cpu.setRa((byte) 0x19);
        cpu.setRb((byte) 0x24);
        instruction = (byte) 0b10000000;
        System.out.println(TestUtil.convertToHexString(cpu.getRa()) + " + " +
                TestUtil.convertToHexString(cpu.getRb()) + " =");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertToHexString(cpu.getRa()));

        System.out.println("Apply DAA on " + TestUtil.convertToHexString(cpu.getRa()) + " to convert to BCD");
        instruction = (byte) 0b00100111;
        prevSubtractionValue = cpu.getSubtractionFlag();
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertToHexString(cpu.getRa()));
        assertEquals((byte) 0x43, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(prevSubtractionValue, cpu.getSubtractionFlag()); // should be unchanged
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(0, cpu.getCarryFlag());
    }

    @Test
    public void testDAASubtraction() {
        /*
        0x41 - 0x19 = 0x22
         */
        cpu.setRa((byte) 0x41);
        cpu.setRb((byte) 0x19);
        byte instruction = (byte) 0b10010000;
        System.out.println(TestUtil.convertToHexString(cpu.getRa()) + " - " +
                TestUtil.convertToHexString(cpu.getRb()) + " =");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertToHexString(cpu.getRa()));

        System.out.println("Apply DAA on " + TestUtil.convertToHexString(cpu.getRa()) + " to convert to BCD");
        instruction = (byte) 0b00100111;
        int prevSubtractionValue = cpu.getSubtractionFlag();
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertToHexString(cpu.getRa()));
        assertEquals((byte) 0x22, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(prevSubtractionValue, cpu.getSubtractionFlag()); // should be unchanged
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(0, cpu.getCarryFlag());

        /*
        0x89 - 0x72 = 0x17
         */
        cpu.setRa((byte) 0x89);
        cpu.setRb((byte) 0x72);
        instruction = (byte) 0b10010000;
        System.out.println(TestUtil.convertToHexString(cpu.getRa()) + " - " +
                TestUtil.convertToHexString(cpu.getRb()) + " =");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertToHexString(cpu.getRa()));

        System.out.println("Apply DAA on " + TestUtil.convertToHexString(cpu.getRa()) + " to convert to BCD");
        instruction = (byte) 0b00100111;
        prevSubtractionValue = cpu.getSubtractionFlag();
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertToHexString(cpu.getRa()));
        assertEquals((byte) 0x17, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(prevSubtractionValue, cpu.getSubtractionFlag()); // should be unchanged
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(0, cpu.getCarryFlag());
    }

    @Test
    public void testExtraDAA() {
        cpu.setRa((byte) 0x00);
        cpu.setCarryFlag(1);
        cpu.setHalfCarryFlag(1);
        cpu.setZeroFlag(1);
        cpu.setSubtractionFlag(1);

        System.out.println("Apply DAA on " + TestUtil.convertToHexString(cpu.getRa()) + " to convert to BCD");
        byte instruction = (byte) 0b00100111;
        int prevSubtractionValue = cpu.getSubtractionFlag();
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertToHexString(cpu.getRa()));
        assertEquals((byte) 0x9A, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(prevSubtractionValue, cpu.getSubtractionFlag()); // should be unchanged
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(1, cpu.getCarryFlag());
    }

    @Test
    public void testCPL() {
        cpu.setZeroFlag(0);
        cpu.setSubtractionFlag(0);
        cpu.setHalfCarryFlag(0);
        cpu.setCarryFlag(0);
        cpu.setRa((byte) 0b00110100);
        byte instruction = (byte) 0b00101111;
        System.out.println("CPL " + TestUtil.convertByteToBinaryString(cpu.getRa()));
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertByteToBinaryString(cpu.getRa()));
        assertEquals((byte) 0b11001011, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(1, cpu.getSubtractionFlag());
        assertEquals(1, cpu.getHalfCarryFlag());
        assertEquals(0, cpu.getCarryFlag());
    }

    @Test
    public void testSCF() {
        cpu.setZeroFlag(1);
        cpu.setSubtractionFlag(1);
        cpu.setHalfCarryFlag(1);
        cpu.setCarryFlag(0);
        byte instruction = (byte) 0b00110111;
        System.out.println("SCF " + cpu.getCarryFlag());
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(cpu.getCarryFlag());
        assertEquals(1, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(1, cpu.getCarryFlag());
    }

    @Test
    public void testCCF() {
        cpu.setZeroFlag(1);
        cpu.setSubtractionFlag(1);
        cpu.setHalfCarryFlag(1);
        cpu.setCarryFlag(1);
        byte instruction = (byte) 0b00111111;
        System.out.println("CCF " + cpu.getCarryFlag());
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(cpu.getCarryFlag());
        assertEquals(1, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(0, cpu.getCarryFlag());
    }

    @Test
    public void testCB_PREFIX() {
        byte instruction = (byte) 0b11001011;
        byte nextInstruction = (byte) 0b11010111;
        short startAddress = (short) 0xC000;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(nextInstruction, (short) (startAddress + 1));
        cpu.setProgramCounter(startAddress);

        cpu.setZeroFlag(1);
        cpu.setSubtractionFlag(1);
        cpu.setHalfCarryFlag(1);
        cpu.setCarryFlag(1);
        cpu.setRa((byte) 0b11110000);
        System.out.println("Testing CB prefix: Bit at pos 2 of " + TestUtil.convertByteToBinaryString(cpu.getRa()) + " is set to one: ");
        cpu.doInstructionCycle();
        System.out.println(TestUtil.convertByteToBinaryString(cpu.getRa()));
        assertEquals(cpu.getRa(), (byte) 0b11110100);
        assertEquals(1, cpu.getZeroFlag()); // flags all should not be modified
        assertEquals(1, cpu.getSubtractionFlag());
        assertEquals(1, cpu.getHalfCarryFlag());
        assertEquals(1, cpu.getCarryFlag());
        assertEquals((short) 0xC002, cpu.getProgramCounter());
    }
}
