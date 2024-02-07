package model.cpu;

import exception.CPUException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testutil.TestUtil;

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
        System.out.println("register BC value: " + TestUtil.convertToHexString(cpu.getRegisterBC()));

        cpu.setRegisterBC((short) 0x78fa);
        assertEquals((byte) 0x78, cpu.getRb());
        assertEquals((byte) 0xfa, cpu.getRc());
        System.out.println("register B value: " + TestUtil.convertToHexString(cpu.getRb()));
        System.out.println("register C value: " + TestUtil.convertToHexString(cpu.getRc()));
    }

    @Test
    public void testFlags() {
        cpu.setRf((byte) 0b10100000);
        assertEquals(1, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(1, cpu.getHalfCarryFlag());
        assertEquals(0, cpu.getCarryFlag());

        cpu.setZeroFlag(0);
        assertEquals(0, cpu.getZeroFlag());
        cpu.setZeroFlag(1);
        assertEquals(1, cpu.getZeroFlag());

        cpu.setSubtractionFlag(1);
        assertEquals(1, cpu.getSubtractionFlag());
        cpu.setHalfCarryFlag(0);
        assertEquals(0, cpu.getHalfCarryFlag());
        cpu.setCarryFlag(1);
        assertEquals(1, cpu.getCarryFlag());
    }

    @Test
    public void testUnknownInstruction() {
        byte instruction = (byte) 0b11011011;
        try {
            cpu.decodeExecuteInstruction(instruction);
            fail("Expected to throw CPU exception!");
        } catch (CPUException e) {

        } catch (Exception e) {
            fail("Expected to throw CPU exception!");
        }
    }

    @Test
    public void testPCIncrementByOneByte() {
        // ADD A, r8 instruction, which is one byte in length
        byte instruction = (byte) 0b10000000;
        short startAddress = (short) 0xC000;
        cpu.setProgramCounter(startAddress);
        cpu.getMemory().setByte(instruction, startAddress);
        cpu.doInstructionCycle();
        assertEquals((short) 0xC001, cpu.getProgramCounter());
    }
}
