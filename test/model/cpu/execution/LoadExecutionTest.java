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
}
