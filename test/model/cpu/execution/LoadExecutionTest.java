package model.cpu.execution;

import model.cpu.CPU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutil.TestUtil;
import util.GameBoyUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoadExecutionTest {
    CPU cpu;
    @BeforeEach
    public void setup() {
        cpu = new CPU();
    }

    @Test
    public void testLD_Memory_u16_SP() {
        short sp = (short) 0x1234;
        short startAddress = (short) 0xC200;
        short u16 = (short) 0x5678;
        byte u16_lsb = (byte) 0x78;
        byte u16_msb = (byte) 0x56;
        byte instruction = (byte) 0b00001000;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(u16_lsb, (short) (startAddress + 1));
        cpu.getMemory().setByte(u16_msb, (short) (startAddress + 2));
        cpu.setStackPointer(sp);
        cpu.setProgramCounter(startAddress);

        System.out.println("Ld [u16], SP: SP = " + TestUtil.convertToHexString(cpu.getStackPointer()) +
                ", u16 = " + TestUtil.convertToHexString(u16));
        cpu.doInstructionCycle();
        System.out.println("Memory Address " + TestUtil.convertToHexString(u16) +
                ": " + TestUtil.convertToHexString(cpu.getMemory().getByte(u16)) +
                ", Memory Address " + TestUtil.convertToHexString((short) (u16 + 1)) +
                ": " + TestUtil.convertToHexString(cpu.getMemory().getByte((short) (u16 + 1))));
        assertEquals((byte) 0x34, cpu.getMemory().getByte(u16));
        assertEquals((byte) 0x12, cpu.getMemory().getByte((short) (u16 + 1)));
        assertEquals((short) 0xC203, cpu.getProgramCounter());
    }

    @Test
    public void testExecuteLD_r16_u16() {
        short startAddress = (short) 0xC300;
        short u16 = (short) 0x1357;
        byte u16_lsb = (byte) 0x57;
        byte u16_msb = (byte) 0x13;
        byte instruction = (byte) 0b00100001;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(u16_lsb, (short) (startAddress + 1));
        cpu.getMemory().setByte(u16_msb, (short) (startAddress + 2));
        cpu.setRegisterHL((short) 0);
        cpu.setProgramCounter(startAddress);

        System.out.println("Ld r16, u16: u16 = " + TestUtil.convertToHexString(u16));
        cpu.doInstructionCycle();
        System.out.println("register HL: " + TestUtil.convertToHexString(cpu.getRegisterHL()));
        assertEquals(u16, cpu.getRegisterHL());
        assertEquals((short) 0xC303, cpu.getProgramCounter());
    }

    @Test
    public void testExecuteLD_Memory_r16_A() {
        short hl = (short) 0xD000;
        byte a = (byte) 0x12;
        byte instruction = (byte) 0b00100010;
        short startAddress = (short) 0xC000;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte((byte) 0, hl);
        cpu.setRa(a);
        cpu.setRegisterHL(hl);
        cpu.setProgramCounter(startAddress);

        System.out.println("Ld (r16), A: HL = " + TestUtil.convertToHexString(cpu.getRegisterHL()) +
                ", A = " + TestUtil.convertToHexString(cpu.getRa()));
        cpu.doInstructionCycle();
        System.out.println("new HL = " + TestUtil.convertToHexString(cpu.getRegisterHL()) +
                ", (old HL) = " + TestUtil.convertToHexString(cpu.getMemory().getByte((short) (cpu.getRegisterHL() - 1))));
        assertEquals(a, cpu.getMemory().getByte(hl));
        assertEquals((short) (hl + 1), cpu.getRegisterHL());
    }

    @Test
    public void testExecuteLD_A_Memory_r16() {
        short hl = (short) 0xD000;
        byte hlVal = (byte) 0x34;
        byte instruction = (byte) 0b00111010;
        short startAddress = (short) 0xC000;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(hlVal, hl);
        cpu.setRa((byte) 0);
        cpu.setRegisterHL(hl);
        cpu.setProgramCounter(startAddress);

        System.out.println("Ld A, (r16): HL = " + TestUtil.convertToHexString(cpu.getRegisterHL()) +
                ", (HL) = " + TestUtil.convertToHexString(cpu.getMemory().getByte(cpu.getRegisterHL())));
        cpu.doInstructionCycle();
        System.out.println("new HL = " + TestUtil.convertToHexString(cpu.getRegisterHL()) +
                ", A = " + TestUtil.convertToHexString(cpu.getRa()));
        assertEquals(hlVal, cpu.getRa());
        assertEquals((short) (hl - 1), cpu.getRegisterHL());
    }

    // testing (HL) here
    @Test
    public void testExecuteLD_r8_u8() {
        short hl = (short) 0xCDEF;
        byte instruction = (byte) 0b00110110;
        short startAddress = (short) 0xC000;
        byte u8 = (byte) 0x89;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(u8, (short) (startAddress + 1));
        cpu.getMemory().setByte((byte) 0, hl);
        cpu.setRegisterHL(hl);
        cpu.setProgramCounter(startAddress);

        System.out.println("Ld r8, u8: u8 = " + TestUtil.convertToHexString(u8));
        cpu.doInstructionCycle();
        System.out.println("(HL) = " + TestUtil.convertToHexString(cpu.getMemory().getByte(hl)));
        assertEquals(u8, cpu.getMemory().getByte(hl));
        assertEquals((short) 0xC002, cpu.getProgramCounter());
    }

    @Test
    public void testExecuteLD_r8_r8() {
        // test C = A
        byte instruction = (byte) 0b01001111;
        short startAddress = (short) 0xC000;
        byte a = (byte) 0x12;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.setProgramCounter(startAddress);
        cpu.setRa(a);
        cpu.setRc((byte) 0);

        System.out.println("Ld C, A: , A = " + TestUtil.convertToHexString(cpu.getRa()));
        cpu.doInstructionCycle();
        System.out.println("C = " + TestUtil.convertToHexString(cpu.getRc()));

        assertEquals(a, cpu.getRc());
    }

    @Test
    public void testExecuteLD_Memory_FF00_plus_u8_A() {
        byte instruction = (byte) 0b11100000;
        short startAddress = (short) 0xC000;
        byte u8 = (byte) 0x11;
        byte a = (byte) 0x34;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(u8, (short) (startAddress + 1));
        cpu.setProgramCounter(startAddress);
        cpu.setRa(a);

        System.out.println("Ld (FF00 + u8), A, A = " + TestUtil.convertToHexString(cpu.getRa()) +
                ", u8 = " + TestUtil.convertToHexString(u8));
        cpu.doInstructionCycle();
        System.out.println("(FF11) = " + TestUtil.convertToHexString(cpu.getMemory().getByte((short) 0xFF11)));
        assertEquals(a, cpu.getMemory().getByte((short) 0xFF11));
        assertEquals((short) 0xC002, cpu.getProgramCounter());
    }

    @Test
    public void testExecuteLD_A_Memory_FF00_plus_u8() {
        byte instruction = (byte) 0b11110000;
        short startAddress = (short) 0xC000;
        byte u8 = (byte) 0x11;
        byte memoryVal = (byte) 0x34;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(u8, (short) (startAddress + 1));
        cpu.setProgramCounter(startAddress);
        cpu.getMemory().setByte(memoryVal, GameBoyUtil.getShortFromBytes(u8, (byte) 0xFF));

        System.out.println("Ld A, (FF00 + u8), (FF00 + u8) = " + TestUtil.convertToHexString(memoryVal));
        cpu.doInstructionCycle();
        System.out.println("A = " + TestUtil.convertToHexString(cpu.getRa()));
        assertEquals(memoryVal, cpu.getRa());
        assertEquals((short) 0xC002, cpu.getProgramCounter());
    }

    @Test
    public void testExecuteLD_Memory_FF00_plus_C_A() {
        byte instruction = (byte) 0b11100010;
        short startAddress = (short) 0xC000;
        byte c = (byte) 0x11;
        byte a = (byte) 0x34;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.setProgramCounter(startAddress);
        cpu.setRa(a);
        cpu.setRc(c);

        System.out.println("Ld (FF00 + C), A, A = " + TestUtil.convertToHexString(cpu.getRa()) +
                ", C = " + TestUtil.convertToHexString(cpu.getRc()));
        cpu.doInstructionCycle();
        System.out.println("(FF11) = " + TestUtil.convertToHexString(cpu.getMemory().getByte((short) 0xFF11)));
        assertEquals(a, cpu.getMemory().getByte((short) 0xFF11));
    }

    @Test
    public void testExecuteLD_A_Memory_FF00_plus_C() {
        byte instruction = (byte) 0b11110010;
        short startAddress = (short) 0xC000;
        byte c = (byte) 0x11;
        byte memoryVal = (byte) 0x34;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.setProgramCounter(startAddress);
        cpu.getMemory().setByte(memoryVal, GameBoyUtil.getShortFromBytes(c, (byte) 0xFF));
        cpu.setRc(c);

        System.out.println("Ld A, (FF00 + C), (FF00 + C) = " + TestUtil.convertToHexString(memoryVal) +
                ", C = " + TestUtil.convertToHexString(cpu.getRc()));
        cpu.doInstructionCycle();
        System.out.println("A = " + TestUtil.convertToHexString(cpu.getRa()));
        assertEquals(memoryVal, cpu.getRa());
    }

    @Test
    public void testExecuteLD_Memory_u16_A() {
        byte instruction = (byte) 0b11101010;
        short startAddress = (short) 0xD000;
        byte a = (byte) 0x67;
        byte u16_lsb = (byte) 0x34;
        byte u16_msb = (byte) 0x12;
        short u16 = (short) 0x1234;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(u16_lsb, (short) (startAddress + 1));
        cpu.getMemory().setByte(u16_msb, (short) (startAddress + 2));
        cpu.setProgramCounter(startAddress);
        cpu.setRa(a);

        System.out.println("Ld (u16), A, A = " + TestUtil.convertToHexString(a) +
                ", u16 = " + TestUtil.convertToHexString(u16));
        cpu.doInstructionCycle();
        System.out.println("(0x1234) = " + TestUtil.convertToHexString(cpu.getMemory().getByte(u16)));
        assertEquals(a, cpu.getMemory().getByte(u16));
        assertEquals((short) 0xD003, cpu.getProgramCounter());
    }

    @Test
    public void testExecuteLD_A_Memory_u16() {
        byte instruction = (byte) 0b11111010;
        short startAddress = (short) 0xD000;
        byte memoryVal = (byte) 0x67;
        byte u16_lsb = (byte) 0x34;
        byte u16_msb = (byte) 0x12;
        short u16 = (short) 0x1234;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(u16_lsb, (short) (startAddress + 1));
        cpu.getMemory().setByte(u16_msb, (short) (startAddress + 2));
        cpu.getMemory().setByte(memoryVal, u16);
        cpu.setProgramCounter(startAddress);

        System.out.println("Ld (u16), A, u16 = " + TestUtil.convertToHexString(u16) +
                ", (u16) = " + TestUtil.convertToHexString(cpu.getMemory().getByte(u16)));
        cpu.doInstructionCycle();
        System.out.println("A = " + TestUtil.convertToHexString(cpu.getRa()));
        assertEquals(memoryVal, cpu.getRa());
        assertEquals((short) 0xD003, cpu.getProgramCounter());
    }

    @Test
    public void testExecutePUSH_r16() {
        byte instruction = (byte) 0b11110101;
        short startAddress = (short) 0xC000;
        short af = (short) 0x3412;
        short sp = (short) 0xD002;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.setStackPointer(sp);
        cpu.setProgramCounter(startAddress);
        cpu.setRegisterAF(af);

        System.out.println("PUSH r16; AF = " + TestUtil.convertToHexString(af) +
                ", SP = " + TestUtil.convertToHexString(sp));
        cpu.doInstructionCycle();
        System.out.println("(0xD001) = " + TestUtil.convertToHexString(cpu.getMemory().getByte((short) 0xD001)) +
                ", (0xD000) = " + TestUtil.convertToHexString(cpu.getMemory().getByte((short) 0xD000)));
        assertEquals((byte) 0x34, cpu.getMemory().getByte((short) 0xD001));
        assertEquals((byte) 0x12, cpu.getMemory().getByte((short) 0xD000));
        assertEquals((short) 0xD000, cpu.getStackPointer());
    }

    @Test
    public void testExecutePOP_r16() {
        byte instruction = (byte) 0b11110001;
        short startAddress = (short) 0xC000;
        byte memoryVal_msb = (byte) 0x34;
        byte memoryVal_lsb = (byte) 0x12;
        short sp = (short) 0xD000;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.getMemory().setByte(memoryVal_lsb, sp);
        cpu.getMemory().setByte(memoryVal_msb, (short) (sp + 1));
        cpu.setStackPointer(sp);
        cpu.setProgramCounter(startAddress);

        System.out.println("POP r16; (SP) = " + TestUtil.convertToHexString(memoryVal_lsb) +
                ", (SP + 1) = " + TestUtil.convertToHexString(memoryVal_msb) +
                ", SP = " + TestUtil.convertToHexString(sp));
        cpu.doInstructionCycle();
        System.out.println("AF = " + TestUtil.convertToHexString(cpu.getRegisterAF()));
        assertEquals((short) 0x3412, cpu.getRegisterAF());
        assertEquals((short) 0xD002, cpu.getStackPointer());
    }
}
