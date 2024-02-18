package model.memory;

import model.cpu.CPU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

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
        cpu.getMemory().setByte(ie, Memory.IE_ADDRESS);
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
        cpu.getMemory().setByte(IF, Memory.IF_ADDRESS);
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
        byte IE = (byte) 0b11110110;
        byte IF = (byte) 0b11110101;
        cpu.getMemory().setByte(IE, Memory.IE_ADDRESS);
        cpu.getMemory().setByte(IF, Memory.IF_ADDRESS);
        Set<Integer> pendingInterrupts = cpu.getMemory().getPendingInterrupts();
        assertEquals(2, pendingInterrupts.size());
        assertTrue(pendingInterrupts.contains(Memory.JOYPAD));
        assertFalse(pendingInterrupts.contains(Memory.SERIAL));
        assertTrue(pendingInterrupts.contains(Memory.TIMER));
        assertFalse(pendingInterrupts.contains(Memory.LCD));
        assertFalse(pendingInterrupts.contains(Memory.VBLANK));
    }
}
