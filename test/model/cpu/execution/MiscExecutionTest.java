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
}
