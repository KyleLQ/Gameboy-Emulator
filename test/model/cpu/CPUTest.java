package model.cpu;

import exception.CPUException;
import model.memory.Memory;
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

    /*
    Tests that interrupts will be serviced, and in the correct order.
    The IF and IE registers for VBLANK and JOYPAD will be set.

    The following instructions will be executed:
    EI (to set IME = 1)
    NOP

    VBLANK interrupt:
    INC b
    RETI

    JOYPAD interrupt:
    ADD a,b
    RETI
     */
    @Test
    public void testInterruptHandling() {
        byte nopInstruction = (byte) 0b00000000;
        byte eiInstruction = (byte) 0b11111011;
        byte retiInstruction = (byte) 0b11011001;
        byte inc_bInstruction = (byte) 0b00000100;
        byte add_b_to_aInstruction = (byte)  0b10000000;

        short startAddress = (short) 0xC000;
        cpu.getMemory().setByte(eiInstruction, startAddress);
        cpu.getMemory().setByte(nopInstruction, (short) (startAddress + 1));

        cpu.getMemory().setByte(inc_bInstruction, Memory.VBLANK_HANDLER_ADDRESS);
        cpu.getMemory().setByte(retiInstruction, (short) (Memory.VBLANK_HANDLER_ADDRESS + 1));

        cpu.getMemory().setByte(add_b_to_aInstruction, Memory.JOYPAD_HANDLER_ADDRESS);
        cpu.getMemory().setByte(retiInstruction, (short) (Memory.JOYPAD_HANDLER_ADDRESS + 1));

        cpu.setProgramCounter(startAddress);
        cpu.setStackPointer((short) 0xFFFE);
        cpu.setRa((byte) 3);
        cpu.setRb((byte) 5);

        cpu.getMemory().setByte((byte) 0b00010011, Memory.IF_ADDRESS);
        cpu.getMemory().setByte((byte) 0b00010001, Memory.IE_ADDRESS);

        cpu.doInstructionCycle(); // EI
        cpu.doInstructionCycle(); // NOP
        cpu.doInstructionCycle(); // INC b (start VBLANK interrupt)
        cpu.doInstructionCycle(); // RETI
        cpu.doInstructionCycle(); // ADD a,b (start JOYPAD interrupt)
        cpu.doInstructionCycle(); // RETI

        assertEquals((short) (startAddress + 2), cpu.getProgramCounter());
        assertEquals((byte) 6, cpu.getRb());
        assertEquals((byte) 9, cpu.getRa());
    }
}
