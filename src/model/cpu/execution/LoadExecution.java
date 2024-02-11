package model.cpu.execution;

import model.cpu.CPU;
import util.GameBoyUtil;

public class LoadExecution {


    /**
     * executes the instruction LD (u16), SP.
     *
     * Loads data from the stack pointer into the address specified by
     * the 16 bit intermediate u16.
     *
     * lsb of SP is stored in (u16), msb of sp is stored in (u16 + 1)
     */
    public static void executeLD_Memory_u16_SP(byte instruction, CPU cpu) {
        short pc = cpu.getProgramCounter();
        pc = (short) (pc + 1);
        byte u16_lsb = cpu.getMemory().getByte(pc);
        pc = (short) (pc + 1);
        byte u16_msb = cpu.getMemory().getByte(pc);
        short u16 = GameBoyUtil.getShortFromBytes(u16_lsb, u16_msb);
        cpu.setProgramCounter(pc);

        short sp = cpu.getStackPointer();
        byte sp_lsb = GameBoyUtil.getByteFromShort(true, sp);
        byte sp_msb = GameBoyUtil.getByteFromShort(false, sp);
        cpu.getMemory().setByte(sp_lsb, u16);
        cpu.getMemory().setByte(sp_msb, (short) (u16 + 1));
    }
}
