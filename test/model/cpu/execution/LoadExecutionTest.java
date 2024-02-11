package model.cpu.execution;

import model.cpu.CPU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutil.TestUtil;

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
}
