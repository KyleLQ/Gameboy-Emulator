package model.memory;

import model.cpu.CPU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Constants;

import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

public class MemoryTest {
    CPU cpu;

    @BeforeEach
    public void setup() {
        cpu = new CPU();
    }

    @Test
    public void testIERegister() {
        byte ie = (byte) 0b10101010;
        cpu.getMemory().setByte(ie, Constants.IE_ADDRESS);
        assertEquals(0, cpu.getMemory().getJoypadIE());
        assertEquals(1, cpu.getMemory().getSerialIE());
        assertEquals(0, cpu.getMemory().getTimerIE());
        assertEquals(1, cpu.getMemory().getLcdIE());
        assertEquals(0, cpu.getMemory().getVBlankIE());

        cpu.getMemory().setJoypadIE(1);
        cpu.getMemory().setSerialIE(0);
        cpu.getMemory().setTimerIE(1);
        cpu.getMemory().setLcdIE(0);
        cpu.getMemory().setVBlankIE(1);

        assertEquals(1, cpu.getMemory().getJoypadIE());
        assertEquals(0, cpu.getMemory().getSerialIE());
        assertEquals(1, cpu.getMemory().getTimerIE());
        assertEquals(0, cpu.getMemory().getLcdIE());
        assertEquals(1, cpu.getMemory().getVBlankIE());
    }

    @Test
    public void testIFRegister() {
        byte IF = (byte) 0b10101010;
        cpu.getMemory().setByte(IF, Constants.IF_ADDRESS);
        assertEquals(0, cpu.getMemory().getJoypadIF());
        assertEquals(1, cpu.getMemory().getSerialIF());
        assertEquals(0, cpu.getMemory().getTimerIF());
        assertEquals(1, cpu.getMemory().getLcdIF());
        assertEquals(0, cpu.getMemory().getVBlankIF());

        cpu.getMemory().setJoypadIF(1);
        cpu.getMemory().setSerialIF(0);
        cpu.getMemory().setTimerIF(1);
        cpu.getMemory().setLcdIF(0);
        cpu.getMemory().setVBlankIF(1);

        assertEquals(1, cpu.getMemory().getJoypadIF());
        assertEquals(0, cpu.getMemory().getSerialIF());
        assertEquals(1, cpu.getMemory().getTimerIF());
        assertEquals(0, cpu.getMemory().getLcdIF());
        assertEquals(1, cpu.getMemory().getVBlankIF());
    }

    @Test
    public void testPendingInterrupts() {
        byte IE = (byte) 0b11110111;
        byte IF = (byte) 0b11110101;
        cpu.getMemory().setByte(IE, Constants.IE_ADDRESS);
        cpu.getMemory().setByte(IF, Constants.IF_ADDRESS);
        Queue<Integer> pendingInterrupts = cpu.getMemory().getPendingInterrupts();
        assertEquals(3, pendingInterrupts.size());
        assertEquals(Constants.VBLANK, pendingInterrupts.poll());
        assertEquals(2, pendingInterrupts.size());
        assertEquals(Constants.TIMER, pendingInterrupts.poll());
        assertEquals(1, pendingInterrupts.size());
        assertEquals(Constants.JOYPAD, pendingInterrupts.poll());
        assertEquals(0, pendingInterrupts.size());
    }
}
