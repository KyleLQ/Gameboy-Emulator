package model.cpu.execution;

import model.cpu.CPU;
import model.memory.Memory;
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

    @Test
    public void testExecuteHALTBug() {
        // todo, if you want to
    }

    /*
    Tests that executing halt with IME = 0 and no pending interrupts will
    resume execution once any interrupts become pending.

    Instructions to execute:
    DI (to set IME = 0)
    HALT
    <Some interrupts become pending>
    NOP (test that CPU doesn't get stuck)
     */
    @Test
    public void testExecuteHALT_IME_0() {
        byte diInstruction = (byte) 0b11110011;
        byte haltInstruction = (byte) 0b01110110;
        byte nopInstruction = (byte) 0b00000000;

        short startAddress = (short) 0xC000;
        cpu.getMemory().setByte(diInstruction, startAddress);
        cpu.getMemory().setByte(haltInstruction, (short) (startAddress + 1));
        cpu.getMemory().setByte(nopInstruction, (short) (startAddress + 2));

        cpu.setProgramCounter(startAddress);

        cpu.doInstructionCycle(); // DI
        cpu.doInstructionCycle(); // HALT

        cpu.getMemory().setByte((byte) 0b00010001, Memory.IF_ADDRESS); // give pending interrupts
        cpu.getMemory().setByte((byte) 0b00010001, Memory.IE_ADDRESS);

        cpu.doInstructionCycle(); // NOP

        assertEquals((short) (startAddress + 3), cpu.getProgramCounter());
    }


    /*
      Tests that executing HALT with IME = 1 and no pending interrupts will handle interrupts
      when interrupts are pending, and will resume normal execution afterward.

      The IF and IE registers for VBLANK and JOYPAD will be set.

      The following instructions will be executed:
      EI (to set IME = 1)
      NOP
      HALT
      <Some interrupts become pending>

      VBLANK interrupt:
      INC b
      RETI

      JOYPAD interrupt:
      ADD a,b
      RETI
     */
    @Test
    public void testExecuteHALT_IME_1() {
        byte nopInstruction = (byte) 0b00000000;
        byte eiInstruction = (byte) 0b11111011;
        byte haltInstruction = (byte) 0b01110110;
        byte retiInstruction = (byte) 0b11011001;
        byte inc_bInstruction = (byte) 0b00000100;
        byte add_b_to_aInstruction = (byte)  0b10000000;

        short startAddress = (short) 0xC000;
        cpu.getMemory().setByte(eiInstruction, startAddress);
        cpu.getMemory().setByte(nopInstruction, (short) (startAddress + 1));
        cpu.getMemory().setByte(haltInstruction, (short) (startAddress + 2));

        cpu.getMemory().setByte(inc_bInstruction, Memory.VBLANK_HANDLER_ADDRESS);
        cpu.getMemory().setByte(retiInstruction, (short) (Memory.VBLANK_HANDLER_ADDRESS + 1));

        cpu.getMemory().setByte(add_b_to_aInstruction, Memory.JOYPAD_HANDLER_ADDRESS);
        cpu.getMemory().setByte(retiInstruction, (short) (Memory.JOYPAD_HANDLER_ADDRESS + 1));

        cpu.setProgramCounter(startAddress);
        cpu.setStackPointer((short) 0xFFFE);
        cpu.setRa((byte) 3);
        cpu.setRb((byte) 5);

        cpu.doInstructionCycle(); // EI
        cpu.doInstructionCycle(); // NOP
        cpu.doInstructionCycle(); // HALT

        cpu.getMemory().setByte((byte) 0b00010011, Memory.IF_ADDRESS); // pending interrupts
        cpu.getMemory().setByte((byte) 0b00010001, Memory.IE_ADDRESS);

        cpu.doInstructionCycle(); // INC b (start VBLANK interrupt)
        cpu.doInstructionCycle(); // RETI
        cpu.doInstructionCycle(); // ADD a,b (start JOYPAD interrupt)
        cpu.doInstructionCycle(); // RETI

        assertEquals((short) (startAddress + 3), cpu.getProgramCounter());
        assertEquals((byte) 6, cpu.getRb());
        assertEquals((byte) 9, cpu.getRa());
    }
}
