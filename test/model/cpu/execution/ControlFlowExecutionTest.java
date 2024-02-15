package model.cpu.execution;

import model.cpu.CPU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutil.TestUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ControlFlowExecutionTest {
    CPU cpu;
    @BeforeEach
    public void setup() {
        cpu = new CPU();
    }

    @Test
    public void testJR_UNCONDITIONAL() {
        byte instruction = (byte) 0b00011000;
        short startAddress = (short) 0xC100;
        byte offset = (byte) 0x24;
        cpu.setProgramCounter(startAddress);
        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(offset, (short) (startAddress + 1));
        System.out.println("JR UNCONDITIONAL, pc = " + TestUtil.convertShortToUnsignedString(cpu.getProgramCounter()) +
                ", offset = " + offset);
        cpu.doInstructionCycle();
        System.out.println("pc = " + TestUtil.convertShortToUnsignedString(cpu.getProgramCounter()));
        assertEquals((short) 0xC126, cpu.getProgramCounter());

        instruction = (byte) 0b00011000;
        startAddress = (short) 0xC200;
        offset = (byte) 0xF0;
        cpu.setProgramCounter(startAddress);
        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(offset, (short) (startAddress + 1));
        System.out.println("JR UNCONDITIONAL, pc = " + TestUtil.convertShortToUnsignedString(cpu.getProgramCounter()) +
                ", offset = " + offset);
        cpu.doInstructionCycle();
        System.out.println("pc = " + TestUtil.convertShortToUnsignedString(cpu.getProgramCounter()));
        assertEquals((short) 49650, cpu.getProgramCounter());
    }

    @Test
    public void testJR_CONDITIONAL() {
        cpu.setZeroFlag(1);
        cpu.setCarryFlag(0);

        byte instruction = (byte) 0b00100000;
        short startAddress = (short) 0xC100;
        byte offset = (byte) 0x24;
        cpu.setProgramCounter(startAddress);
        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(offset, (short) (startAddress + 1));
        System.out.println("JR CONDITIONAL, pc = " + TestUtil.convertShortToUnsignedString(cpu.getProgramCounter()) +
                ", offset = " + offset + ", condition NZ = " + ((cpu.getZeroFlag() == 1) ? 0 : 1));
        cpu.doInstructionCycle();
        System.out.println("pc = " + TestUtil.convertShortToUnsignedString(cpu.getProgramCounter()));
        assertEquals((short) 0xC102, cpu.getProgramCounter());

        instruction = (byte) 0b00101000;
        startAddress = (short) 0xC100;
        offset = (byte) 0x24;
        cpu.setProgramCounter(startAddress);
        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(offset, (short) (startAddress + 1));
        System.out.println("JR CONDITIONAL, pc = " + TestUtil.convertShortToUnsignedString(cpu.getProgramCounter()) +
                ", offset = " + offset + ", condition Z = " + cpu.getZeroFlag());
        cpu.doInstructionCycle();
        System.out.println("pc = " + TestUtil.convertShortToUnsignedString(cpu.getProgramCounter()));
        assertEquals((short) 0xC126, cpu.getProgramCounter());

        instruction = (byte) 0b00111000;
        startAddress = (short) 0xC200;
        offset = (byte) 0xF0;
        cpu.setProgramCounter(startAddress);
        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(offset, (short) (startAddress + 1));
        System.out.println("JR CONDITIONAL, pc = " + TestUtil.convertShortToUnsignedString(cpu.getProgramCounter()) +
                ", offset = " + offset + ", condition C = " + cpu.getCarryFlag());
        cpu.doInstructionCycle();
        System.out.println("pc = " + TestUtil.convertShortToUnsignedString(cpu.getProgramCounter()));
        assertEquals((short) 0xC202, cpu.getProgramCounter());

        instruction = (byte) 0b00110000;
        startAddress = (short) 0xC200;
        offset = (byte) 0xF0;
        cpu.setProgramCounter(startAddress);
        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(offset, (short) (startAddress + 1));
        System.out.println("JR CONDITIONAL, pc = " + TestUtil.convertShortToUnsignedString(cpu.getProgramCounter()) +
                ", offset = " + offset + ", condition NC = " + ((cpu.getCarryFlag() == 1) ? 0 : 1));
        cpu.doInstructionCycle();
        System.out.println("pc = " + TestUtil.convertShortToUnsignedString(cpu.getProgramCounter()));
        assertEquals((short) 49650, cpu.getProgramCounter());
    }

    @Test
    public void testJP_UNCONDITIONAL() {
        byte instruction = (byte) 0b11000011;
        short startAddress = (short) 0xC100;
        byte lsb = (byte) 0xFF;
        byte msb = (byte) 0xD0;
        cpu.setProgramCounter(startAddress);
        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(lsb, (short) (startAddress + 1));
        cpu.getMemory().setByte(msb, (short) (startAddress + 2));
        System.out.println("JP UNCONDITIONAL, pc = " + TestUtil.convertToHexString(cpu.getProgramCounter()) +
                ", lsb = " + TestUtil.convertToHexString(lsb) + ", msb = " + TestUtil.convertToHexString(msb));
        cpu.doInstructionCycle();
        System.out.println("pc = " + TestUtil.convertToHexString(cpu.getProgramCounter()));
        assertEquals((short) 0xD0FF, cpu.getProgramCounter());
    }

    @Test
    public void testJP_CONDITIONAL() {
        cpu.setZeroFlag(1);
        byte instruction = (byte) 0b11000010;
        short startAddress = (short) 0xC100;
        byte lsb = (byte) 0xFF;
        byte msb = (byte) 0xD0;
        cpu.setProgramCounter(startAddress);
        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(lsb, (short) (startAddress + 1));
        cpu.getMemory().setByte(msb, (short) (startAddress + 2));
        System.out.println("JP CONDITIONAL, pc = " + TestUtil.convertToHexString(cpu.getProgramCounter()) +
                ", lsb = " + TestUtil.convertToHexString(lsb) + ", msb = " + TestUtil.convertToHexString(msb) +
                ", NZ = " + ((cpu.getZeroFlag() == 1) ? 0 : 1));
        cpu.doInstructionCycle();
        System.out.println("pc = " + TestUtil.convertToHexString(cpu.getProgramCounter()));
        assertEquals((short) 0xC103, cpu.getProgramCounter());

        instruction = (byte) 0b11001010;
        cpu.setProgramCounter(startAddress);
        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(lsb, (short) (startAddress + 1));
        cpu.getMemory().setByte(msb, (short) (startAddress + 2));
        System.out.println("JP CONDITIONAL, pc = " + TestUtil.convertToHexString(cpu.getProgramCounter()) +
                ", lsb = " + TestUtil.convertToHexString(lsb) + ", msb = " + TestUtil.convertToHexString(msb) +
                ", Z = " + cpu.getZeroFlag());
        cpu.doInstructionCycle();
        System.out.println("pc = " + TestUtil.convertToHexString(cpu.getProgramCounter()));
        assertEquals((short) 0xD0FF, cpu.getProgramCounter());
    }

    @Test
    public void testCALL_UNCONDITIONAL() {
        byte instruction = (byte) 0b11001101;
        short startAddress = (short) 0xC000;
        byte u16_lsb = (byte) 0x89;
        byte u16_msb = (byte) 0x67;
        short u16 = (short) 0x6789;
        short sp = (short) 0xD002;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(u16_lsb, (short) (startAddress + 1));
        cpu.getMemory().setByte(u16_msb, (short) (startAddress + 2));
        cpu.setProgramCounter(startAddress);
        cpu.setStackPointer(sp);

        System.out.println("CALL Unconditional, u16 = " + TestUtil.convertToHexString(u16) +
                ", sp = " + TestUtil.convertToHexString(sp));
        cpu.doInstructionCycle();
        System.out.println("pc = " + TestUtil.convertToHexString(cpu.getProgramCounter()) +
                ", sp = " + TestUtil.convertToHexString(cpu.getStackPointer()) +
                ", (sp) = " + TestUtil.convertToHexString(cpu.getMemory().getByte(cpu.getStackPointer())) +
                ", (sp + 1) = " + TestUtil.convertToHexString(cpu.getMemory().getByte((short) (cpu.getStackPointer() + 1))));
        assertEquals(u16, cpu.getProgramCounter());
        assertEquals((short) (sp - 2), cpu.getStackPointer());
        assertEquals((byte) 0x03, cpu.getMemory().getByte(cpu.getStackPointer()));
        assertEquals((byte) 0xC0, cpu.getMemory().getByte((short) (cpu.getStackPointer() + 1)));
    }

    @Test
    public void testCALL_CONDITIONAL() {
        cpu.setZeroFlag(0);
        byte instruction = (byte) 0b11001100;
        short startAddress = (short) 0xC000;
        byte u16_lsb = (byte) 0x89;
        byte u16_msb = (byte) 0x67;
        short sp = (short) 0xD002;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(u16_lsb, (short) (startAddress + 1));
        cpu.getMemory().setByte(u16_msb, (short) (startAddress + 2));
        cpu.setProgramCounter(startAddress);
        cpu.setStackPointer(sp);

        System.out.println("CALL conditional, pc = " + TestUtil.convertToHexString(cpu.getProgramCounter()) +
                ", Z = " + cpu.getZeroFlag());
        cpu.doInstructionCycle();
        System.out.println("pc = " + TestUtil.convertToHexString(cpu.getProgramCounter()));
        assertEquals((short) (startAddress + 3), cpu.getProgramCounter());
        assertEquals(sp, cpu.getStackPointer());
    }

    @Test
    public void testExecuteRET_UNCONDITIONAL() {
        byte instruction = (byte) 0b11001001;
        short startAddress = (short) 0xC000;
        byte memoryVal_msb = (byte) 0x34;
        byte memoryVal_lsb = (byte) 0x12;
        short sp = (short) 0xD000;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(memoryVal_lsb, sp);
        cpu.getMemory().setByte(memoryVal_msb, (short) (sp + 1));
        cpu.setStackPointer(sp);
        cpu.setProgramCounter(startAddress);

        System.out.println("RET; (SP) = " + TestUtil.convertToHexString(memoryVal_lsb) +
                ", (SP + 1) = " + TestUtil.convertToHexString(memoryVal_msb) +
                ", SP = " + TestUtil.convertToHexString(sp));
        cpu.doInstructionCycle();
        System.out.println("PC = " + TestUtil.convertToHexString(cpu.getProgramCounter()));
        assertEquals((short) 0x3412, cpu.getProgramCounter());
        assertEquals((short) 0xD002, cpu.getStackPointer());
    }

    @Test
    public void testExecuteJP_HL() {
        byte instruction = (byte) 0b11101001;
        short startAddress = (short) 0xC000;
        short hl = (short) 0x6789;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.setProgramCounter(startAddress);
        cpu.setRegisterHL(hl);

        System.out.println("JP HL, HL = " + TestUtil.convertToHexString(cpu.getRegisterHL()));
        cpu.doInstructionCycle();
        System.out.println("PC = " + TestUtil.convertToHexString(cpu.getProgramCounter()));
        assertEquals(hl, cpu.getProgramCounter());
    }

    @Test
    public void testExecuteLD_SP_HL() {
        byte instruction = (byte) 0b11111001;
        short startAddress = (short) 0xC000;
        short hl = (short) 0x6789;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.setProgramCounter(startAddress);
        cpu.setRegisterHL(hl);

        System.out.println("LD SP, HL, HL = " + TestUtil.convertToHexString(cpu.getRegisterHL()));
        cpu.doInstructionCycle();
        System.out.println("SP = " + TestUtil.convertToHexString(cpu.getStackPointer()));
        assertEquals(hl, cpu.getStackPointer());
    }
}
