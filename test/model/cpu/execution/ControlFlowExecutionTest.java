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
}
