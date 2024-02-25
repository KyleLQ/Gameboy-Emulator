package model.cpu.execution;

import model.cpu.CPU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutil.TestUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ALUExecutionTest {
    CPU cpu;
    @BeforeEach
    public void setup() {
        cpu = new CPU();
    }

    @Test
    public void testALU_A_r8_ADD() {
        cpu.setRa((byte) 2);
        cpu.setRb((byte) 4);
        byte instruction = (byte) 0b10000000;
        System.out.println(TestUtil.convertByteToUnsignedString(cpu.getRa()) + " + " +
                TestUtil.convertByteToUnsignedString(cpu.getRb()) + " = ");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertByteToUnsignedString(cpu.getRa()));
        assertEquals((byte) 6, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getCarryFlag());
        assertEquals(0, cpu.getHalfCarryFlag());

        cpu.setRa((byte) 0b11111111);
        cpu.setRb((byte) 0b1);
        instruction = (byte) 0b10000000;
        System.out.println("Test overflow");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertByteToUnsignedString(cpu.getRa()));
        assertEquals((byte) 0, cpu.getRa());
        assertEquals(1, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(1, cpu.getCarryFlag());
        assertEquals(1, cpu.getHalfCarryFlag());
    }

    @Test
    public void testALU_A_r8_ADC() {
        cpu.setRa((byte) 4);
        cpu.setRc((byte) 13);
        cpu.setCarryFlag(1);
        byte instruction = (byte) 0b10001001;
        System.out.println(TestUtil.convertByteToUnsignedString(cpu.getRa()) + " ADC " +
                TestUtil.convertByteToUnsignedString(cpu.getRc()) + " = ");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertByteToUnsignedString(cpu.getRa()));
        assertEquals((byte) 18, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getCarryFlag());
        assertEquals(1, cpu.getHalfCarryFlag());
    }

    @Test
    public void testALU_A_r8_SUB() {
        cpu.setRa((byte) 4);
        cpu.setRd((byte) 3);
        byte instruction = (byte) 0b10010010;
        System.out.println(TestUtil.convertByteToUnsignedString(cpu.getRa()) + " - " +
                TestUtil.convertByteToUnsignedString(cpu.getRd()) + " = ");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertByteToUnsignedString(cpu.getRa()));
        assertEquals((byte) 1, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(1, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getCarryFlag());
        assertEquals(0, cpu.getHalfCarryFlag());

        cpu.setRa((byte) 10);
        cpu.setRd((byte) 11);
        instruction = (byte) 0b10010010;
        System.out.println("Test underflow");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertByteToUnsignedString(cpu.getRa()));
        assertEquals((byte) 255, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(1, cpu.getSubtractionFlag());
        assertEquals(1, cpu.getCarryFlag());
        assertEquals(1, cpu.getHalfCarryFlag());
    }

    @Test
    public void testALU_A_r8_SUBC() {
        cpu.setRa((byte) 130);
        cpu.setRe((byte) 78);
        cpu.setCarryFlag(1);
        byte instruction = (byte) 0b10011011;
        System.out.println(TestUtil.convertByteToUnsignedString(cpu.getRa()) + " SUBC " +
                TestUtil.convertByteToUnsignedString(cpu.getRe()) + " = ");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertByteToUnsignedString(cpu.getRa()));
        assertEquals((byte) 51, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(1, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getCarryFlag());
        assertEquals(1, cpu.getHalfCarryFlag());
    }

    @Test
    public void testALU_A_r8_AND() {
        cpu.setRa((byte) 0b11001010);
        cpu.setRh((byte) 0b01100000);
        byte instruction = (byte) 0b10100100;
        System.out.println(TestUtil.convertByteToBinaryString(cpu.getRa()) + " AND " +
                TestUtil.convertByteToBinaryString(cpu.getRh()) + " = ");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertByteToBinaryString(cpu.getRa()));
        assertEquals((byte) 0b01000000, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getCarryFlag());
        assertEquals(1, cpu.getHalfCarryFlag());
    }

    @Test
    public void testALU_A_r8_XOR() {
        cpu.setRa((byte) 0b11001111);
        cpu.setRl((byte) 0b01100000);
        byte instruction = (byte) 0b10101101;
        System.out.println(TestUtil.convertByteToBinaryString(cpu.getRa()) + " XOR " +
                TestUtil.convertByteToBinaryString(cpu.getRl()) + " = ");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertByteToBinaryString(cpu.getRa()));
        assertEquals((byte) 0b10101111, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getCarryFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
    }

    @Test
    public void testALU_A_r8_OR() {
        cpu.setRa((byte) 0b10101010);
        cpu.setRl((byte) 0b01010000);
        byte instruction = (byte) 0b10110101;
        System.out.println(TestUtil.convertByteToBinaryString(cpu.getRa()) + " OR " +
                TestUtil.convertByteToBinaryString(cpu.getRl()) + " = ");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertByteToBinaryString(cpu.getRa()));
        assertEquals((byte) 0b11111010, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getCarryFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
    }

    @Test
    public void testALU_A_r8_CP() {
        cpu.setRa((byte) 0b10101010);
        byte instruction = (byte) 0b10111111;
        System.out.println(TestUtil.convertByteToUnsignedString(cpu.getRa()) + " CP " +
                TestUtil.convertByteToUnsignedString(cpu.getRa()) + " = ");
        int originalRa = cpu.getRa();
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertByteToUnsignedString(cpu.getRa()));
        assertEquals(originalRa, cpu.getRa());
        assertEquals(1, cpu.getZeroFlag());
        assertEquals(1, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getCarryFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
    }

    @Test
    public void testALU_A_u8() {
        cpu.setRa((byte) 2);
        short startAddress = (short) 0xC100;
        byte u8 = (byte) 4;
        byte instruction = (byte) 0b11000110;
        cpu.setProgramCounter(startAddress);
        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(u8, (short) (startAddress + 1));
        System.out.println(TestUtil.convertByteToUnsignedString(cpu.getRa()) + " + " +
                TestUtil.convertByteToUnsignedString(u8) + " = ");
        cpu.doInstructionCycle();
        System.out.println(TestUtil.convertByteToUnsignedString(cpu.getRa()));
        assertEquals((byte) 6, cpu.getRa());
        assertEquals((short) 0xC102, cpu.getProgramCounter());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getCarryFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
    }

    @Test
    public void testADD_HL_r16_normal() {
        cpu.setRegisterBC((short) 15);
        cpu.setRegisterHL((short) 2);
        byte instruction = (byte) 0b00001001;
        System.out.println(TestUtil.convertShortToUnsignedString(cpu.getRegisterBC()) + " + " +
                TestUtil.convertShortToUnsignedString(cpu.getRegisterHL()) + " = ");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertShortToUnsignedString(cpu.getRegisterHL()));
        assertEquals((short) 17, cpu.getRegisterHL());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getCarryFlag());
        assertEquals(0, cpu.getHalfCarryFlag());

        cpu.setRegisterDE((short) 204);
        cpu.setRegisterHL((short) 173);
        instruction = (byte) 0b00011001;
        System.out.println(TestUtil.convertShortToUnsignedString(cpu.getRegisterDE()) + " + " +
                TestUtil.convertShortToUnsignedString(cpu.getRegisterHL()) + " = ");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertShortToUnsignedString(cpu.getRegisterHL()));
        assertEquals((short) 377, cpu.getRegisterHL());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getCarryFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
    }

    @Test
    public void testADD_HL_r16_carry() {
        cpu.setRegisterHL((short) 32768);
        byte instruction = (byte) 0b00101001;
        System.out.println(TestUtil.convertShortToUnsignedString(cpu.getRegisterHL()) + " + " +
                TestUtil.convertShortToUnsignedString(cpu.getRegisterHL()) + " = ");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertShortToUnsignedString(cpu.getRegisterHL()));
        assertEquals((short) 0, cpu.getRegisterHL());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(1, cpu.getCarryFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
    }

    @Test
    public void testADD_HL_r16_half_carry() {
        cpu.setStackPointer((short) 4090);
        cpu.setRegisterHL((short) 6);
        byte instruction = (byte) 0b00111001;
        System.out.println(TestUtil.convertShortToUnsignedString(cpu.getStackPointer()) + " + " +
                TestUtil.convertShortToUnsignedString(cpu.getRegisterHL()) + " = ");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertShortToUnsignedString(cpu.getRegisterHL()));
        assertEquals((short) 4096, cpu.getRegisterHL());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getCarryFlag());
        assertEquals(1, cpu.getHalfCarryFlag());
    }

    @Test
    public void testINC_r8() {
        cpu.setCarryFlag(1);
        cpu.setRb((byte) 5);
        byte instruction = (byte) 0b00000100;
        System.out.println("INC " + TestUtil.convertByteToUnsignedString(cpu.getRb()) + " = ");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertByteToUnsignedString(cpu.getRb()));
        assertEquals((byte) 6, cpu.getRb());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(1, cpu.getCarryFlag()); // shouldn't be modified

        cpu.setCarryFlag(0);
        cpu.setRc((byte) 207);
        instruction = (byte) 0b00001100;
        System.out.println("INC " + TestUtil.convertByteToUnsignedString(cpu.getRc()) + " = ");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertByteToUnsignedString(cpu.getRc()));
        assertEquals((byte) 208, cpu.getRc());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(1, cpu.getHalfCarryFlag());
        assertEquals(0, cpu.getCarryFlag()); // shouldn't be modified
    }

    @Test
    public void testDEC_r8() {
        cpu.setCarryFlag(1);
        cpu.setRa((byte) 10);
        byte instruction = (byte) 0b00111101;
        System.out.println("DEC " + TestUtil.convertByteToUnsignedString(cpu.getRa()) + " = ");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertByteToUnsignedString(cpu.getRa()));
        assertEquals((byte) 9, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(1, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(1, cpu.getCarryFlag()); // shouldn't be modified

        cpu.setCarryFlag(0);
        cpu.setRh((byte) 64);
        instruction = (byte) 0b00100101;
        System.out.println("DEC " + TestUtil.convertByteToUnsignedString(cpu.getRh()) + " = ");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertByteToUnsignedString(cpu.getRh()));
        assertEquals((byte) 63, cpu.getRh());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(1, cpu.getSubtractionFlag());
        assertEquals(1, cpu.getHalfCarryFlag());
        assertEquals(0, cpu.getCarryFlag()); // shouldn't be modified
    }

    @Test
    public void testINC_r16() {
        cpu.setZeroFlag(1);
        cpu.setSubtractionFlag(1);
        cpu.setHalfCarryFlag(1);
        cpu.setCarryFlag(1);
        cpu.setRegisterBC((short) 580);
        byte instruction = (byte) 0b00000011;
        System.out.println("INC " + TestUtil.convertShortToUnsignedString(cpu.getRegisterBC()) + " = ");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertShortToUnsignedString(cpu.getRegisterBC()));
        assertEquals((short) 581, cpu.getRegisterBC());

        // check flags not modified
        assertEquals(1, cpu.getZeroFlag());
        assertEquals(1, cpu.getSubtractionFlag());
        assertEquals(1, cpu.getHalfCarryFlag());
        assertEquals(1, cpu.getCarryFlag());
    }

    @Test
    public void testDEC_r16() {
        cpu.setZeroFlag(0);
        cpu.setSubtractionFlag(0);
        cpu.setHalfCarryFlag(0);
        cpu.setCarryFlag(0);
        cpu.setRegisterHL((short) 362);
        byte instruction = (byte) 0b00101011;
        System.out.println("DEC " + TestUtil.convertShortToUnsignedString(cpu.getRegisterHL()) + " = ");
        cpu.decodeExecuteInstruction(instruction);
        System.out.println(TestUtil.convertShortToUnsignedString(cpu.getRegisterHL()));
        assertEquals((short) 361, cpu.getRegisterHL());

        // check flags not modified
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(0, cpu.getCarryFlag());
    }

    @Test
    public void testExecuteADD_SP_i8_negative() {
        byte instruction = (byte) 0b11101000;
        short startAddress = (short) 0xC000;
        byte i8 = (byte) -2;
        short sp = (short) 1234;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(i8, (short) (startAddress + 1));
        cpu.setProgramCounter(startAddress);
        cpu.setStackPointer(sp);
        cpu.setZeroFlag(1);
        cpu.setSubtractionFlag(1);
        cpu.setCarryFlag(1);
        cpu.setHalfCarryFlag(1);

        System.out.println("ADD SP, i8, SP = " + TestUtil.convertShortToUnsignedString(cpu.getStackPointer()) +
                ", i8 = " + i8);
        cpu.doInstructionCycle();
        System.out.println("SP = " + TestUtil.convertShortToUnsignedString(cpu.getStackPointer()));
        assertEquals((short) 1232, cpu.getStackPointer());
        assertEquals((short) 0xC002, cpu.getProgramCounter());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(1, cpu.getHalfCarryFlag());
        assertEquals(1, cpu.getCarryFlag());
    }

    @Test
    public void testExecuteADD_SP_i8_positive_no_carry() {
        byte instruction = (byte) 0b11101000;
        short startAddress = (short) 0xC000;
        byte i8 = (byte) 0x2;
        short sp = (short) 0x1234;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(i8, (short) (startAddress + 1));
        cpu.setProgramCounter(startAddress);
        cpu.setStackPointer(sp);
        cpu.setZeroFlag(1);
        cpu.setSubtractionFlag(1);
        cpu.setCarryFlag(1);
        cpu.setHalfCarryFlag(1);

        System.out.println("ADD SP, i8, SP = " + TestUtil.convertToHexString(cpu.getStackPointer()) +
                ", i8 = " + TestUtil.convertToHexString(i8));
        cpu.doInstructionCycle();
        System.out.println("SP = " + TestUtil.convertToHexString(cpu.getStackPointer()));
        assertEquals((short) 0x1236, cpu.getStackPointer());
        assertEquals((short) 0xC002, cpu.getProgramCounter());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(0, cpu.getCarryFlag());
    }

    @Test
    public void testExecuteADD_SP_i8_positive_carry() {
        byte instruction = (byte) 0b11101000;
        short startAddress = (short) 0xC000;
        byte i8 = (byte) 0x40;
        short sp = (short) 0x99C0;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(i8, (short) (startAddress + 1));
        cpu.setProgramCounter(startAddress);
        cpu.setStackPointer(sp);
        cpu.setZeroFlag(1);
        cpu.setSubtractionFlag(1);
        cpu.setCarryFlag(0);
        cpu.setHalfCarryFlag(1);

        System.out.println("ADD SP, i8, SP = " + TestUtil.convertToHexString(cpu.getStackPointer()) +
                ", i8 = " + TestUtil.convertToHexString(i8));
        cpu.doInstructionCycle();
        System.out.println("SP = " + TestUtil.convertToHexString(cpu.getStackPointer()));
        assertEquals((short) 0x9A00, cpu.getStackPointer());
        assertEquals((short) 0xC002, cpu.getProgramCounter());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(1, cpu.getCarryFlag());
    }

    @Test
    public void testExecuteADD_SP_i8_positive_half_carry() {
        byte instruction = (byte) 0b11101000;
        short startAddress = (short) 0xC000;
        byte i8 = (byte) 0x08;
        short sp = (short) 0x1108;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(i8, (short) (startAddress + 1));
        cpu.setProgramCounter(startAddress);
        cpu.setStackPointer(sp);
        cpu.setZeroFlag(1);
        cpu.setSubtractionFlag(1);
        cpu.setCarryFlag(1);
        cpu.setHalfCarryFlag(0);

        System.out.println("ADD SP, i8, SP = " + TestUtil.convertToHexString(cpu.getStackPointer()) +
                ", i8 = " + TestUtil.convertToHexString(i8));
        cpu.doInstructionCycle();
        System.out.println("SP = " + TestUtil.convertToHexString(cpu.getStackPointer()));
        assertEquals((short) 0x1110, cpu.getStackPointer());
        assertEquals((short) 0xC002, cpu.getProgramCounter());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(1, cpu.getHalfCarryFlag());
        assertEquals(0, cpu.getCarryFlag());
    }

    @Test
    public void testExecuteLD_HL_SP_plus_i8_positive_no_carry() {
        byte instruction = (byte) 0b11111000;
        short startAddress = (short) 0xC000;
        byte i8 = (byte) 0x2;
        short sp = (short) 0x1234;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(i8, (short) (startAddress + 1));
        cpu.setProgramCounter(startAddress);
        cpu.setStackPointer(sp);
        cpu.setZeroFlag(1);
        cpu.setSubtractionFlag(1);
        cpu.setCarryFlag(1);
        cpu.setHalfCarryFlag(1);

        System.out.println("LD HL, SP + i8, SP = " + TestUtil.convertToHexString(cpu.getStackPointer()) +
                ", i8 = " + TestUtil.convertToHexString(i8));
        cpu.doInstructionCycle();
        System.out.println("HL = " + TestUtil.convertToHexString(cpu.getRegisterHL()));
        assertEquals((short) 0x1236, cpu.getRegisterHL());
        assertEquals((short) 0xC002, cpu.getProgramCounter());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
        assertEquals(0, cpu.getCarryFlag());
    }
}
