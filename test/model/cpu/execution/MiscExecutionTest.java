package model.cpu.execution;

import model.cpu.CPU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MiscExecutionTest {
    CPU cpu;
    @BeforeEach
    public void setup() {
        cpu = new CPU();
    }

    @Test
    public void testExecuteNOP() {
        byte instruction = (byte) 0b00000000;
        short startAddress = (short) 0xC000;

        cpu.getMemory().setByte(instruction, startAddress);
        cpu.setProgramCounter(startAddress);

        System.out.println("NOP");
        cpu.doInstructionCycle();
        assertEquals((short) (startAddress + 1), cpu.getProgramCounter());
    }

    @Test
    public void testExecuteEI() {
        cpu.setIME(0);
        byte instructionEI = (byte) 0b11111011;
        byte instructionNOP = (byte) 0b00000000;
        short startAddress = (short) 0xC000;

        cpu.getMemory().setByte(instructionEI, startAddress);
        cpu.getMemory().setByte(instructionNOP, (short) (startAddress + 1));
        cpu.setProgramCounter(startAddress);

        System.out.println("Execute EI");
        cpu.doInstructionCycle();
        System.out.println("IME = " + cpu.getIME());
        assertEquals(0, cpu.getIME());

        System.out.println("Execute NOP");
        cpu.doInstructionCycle();
        System.out.println("IME = " + cpu.getIME());
        assertEquals(1, cpu.getIME());
    }

    @Test
    public void testExecuteDIPending() {
        byte instructionEI = (byte) 0b11111011;
        byte instructionDI = (byte) 0b11110011;
        short startAddress = (short) 0xC000;

        cpu.getMemory().setByte(instructionEI, startAddress);
        cpu.getMemory().setByte(instructionDI, (short) (startAddress + 1));
        cpu.setProgramCounter(startAddress);

        System.out.println("Execute EI");
        cpu.doInstructionCycle();
        System.out.println("IME = " + cpu.getIME());
        assertEquals(0, cpu.getIME());

        System.out.println("Execute DI");
        cpu.doInstructionCycle();
        System.out.println("IME = " + cpu.getIME());
        assertEquals(0, cpu.getIME());
    }

    @Test
    public void testExecuteDI() {
        byte instructionEI = (byte) 0b11111011;
        byte instructionNOP = (byte) 0b00000000;
        byte instructionDI = (byte) 0b11110011;
        short startAddress = (short) 0xC000;

        cpu.getMemory().setByte(instructionEI, startAddress);
        cpu.getMemory().setByte(instructionNOP, (short) (startAddress + 1));
        cpu.getMemory().setByte(instructionDI, (short) (startAddress + 2));
        cpu.setProgramCounter(startAddress);

        System.out.println("Execute EI");
        cpu.doInstructionCycle();
        System.out.println("IME = " + cpu.getIME());
        assertEquals(0, cpu.getIME());

        System.out.println("Execute NOP");
        cpu.doInstructionCycle();
        System.out.println("IME = " + cpu.getIME());
        assertEquals(1, cpu.getIME());

        System.out.println("Execute DI");
        cpu.doInstructionCycle();
        System.out.println("IME = " + cpu.getIME());
        assertEquals(0, cpu.getIME());
    }
}
