import model.CPU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CPUTest {
    CPU cpu;

    @BeforeEach
    public void setup() {
        cpu = new CPU();
    }


    @Test
    public void test16BitRegister() {
        cpu.setRb((byte) 0x58);
        cpu.setRc((byte) 0xea);
        assertEquals((short) 0x58ea,cpu.getRegisterBC());
        System.out.println("register BC value: " + String.format("0x%08X", cpu.getRegisterBC()));

        cpu.setRegisterBC((short) 0x78fa);
        assertEquals((byte) 0x78, cpu.getRb());
        assertEquals((byte) 0xfa, cpu.getRc());
        System.out.println("register B value: " + String.format("0x%08X", cpu.getRb()));
        System.out.println("register C value: " + String.format("0x%08X", cpu.getRc()));
    }

    @Test
    public void testFlags() {
        cpu.setRf((byte) 0b10100000);
        assertTrue( cpu.getZeroFlag());
        assertFalse(cpu.getSubtractionFlag());
        assertTrue(cpu.getHalfCarryFlag());
        assertFalse(cpu.getCarryFlag());

        cpu.setZeroFlag(false);
        assertFalse(cpu.getZeroFlag());
        cpu.setZeroFlag(true);
        assertTrue( cpu.getZeroFlag());

        cpu.setSubtractionFlag(true);
        assertTrue(cpu.getSubtractionFlag());
        cpu.setHalfCarryFlag(false);
        assertFalse( cpu.getHalfCarryFlag());
        cpu.setCarryFlag(true);
        assertTrue(cpu.getCarryFlag());
    }
}
