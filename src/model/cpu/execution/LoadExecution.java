package model.cpu.execution;

import model.cpu.CPU;
import util.GameBoyUtil;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static util.GameBoyUtil.INSTRUCTION_TO_SET_R16_SP_MAP;

public class LoadExecution {

    private static final List<Function<CPU, Short>> INSTRUCTION_TO_GET_R16_INC_DEC_MAP = Arrays.asList(
            CPU::getRegisterBC,
            CPU::getRegisterDE,
            (CPU cpu) -> {
                short hl = cpu.getRegisterHL();
                cpu.setRegisterHL((short) (hl + 1)); // I think technically you need to do this after the memory read
                return hl;
            },
            (CPU cpu) -> {
                short hl = cpu.getRegisterHL();
                cpu.setRegisterHL((short) (hl - 1));
                return hl;
            }
    );

    /**
     * executes the instruction LD (u16), SP.
     * Loads data from the stack pointer into the address specified by
     * the 16 bit intermediate u16.
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

    /**
     * executes the instruction LD r16, u16.
     * loads the intermediate value u16 into the register r16
     */
    public static void executeLD_r16_u16(byte instruction, CPU cpu) {
        short pc = cpu.getProgramCounter();
        pc = (short) (pc + 1);
        byte u16_lsb = cpu.getMemory().getByte(pc);
        pc = (short) (pc + 1);
        byte u16_msb = cpu.getMemory().getByte(pc);
        short u16 = GameBoyUtil.getShortFromBytes(u16_lsb, u16_msb);
        cpu.setProgramCounter(pc);

        BiConsumer<Short, CPU> setR16 = INSTRUCTION_TO_SET_R16_SP_MAP.get(GameBoyUtil.get2BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4)
        ));
        setR16.accept(u16, cpu);
    }

    /**
     * Executes the instruction LD (r16), A.
     * Writes the value in register A to the memory address specified by register r16
     */
    public static void executeLD_Memory_r16_A(byte instruction, CPU cpu) {
        Function<CPU, Short> getR16 = INSTRUCTION_TO_GET_R16_INC_DEC_MAP.get(GameBoyUtil.get2BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4)
        ));
        short address = getR16.apply(cpu);

        cpu.getMemory().setByte(cpu.getRa(), address);
    }

    /**
     * Executes the instruction LD A, (r16).
     * Reads the byte from the memory address specified by register r16 into register A.
     */
    public static void executeLD_A_Memory_r16(byte instruction, CPU cpu) {
        Function<CPU, Short> getR16 = INSTRUCTION_TO_GET_R16_INC_DEC_MAP.get(GameBoyUtil.get2BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4)
        ));
        short address = getR16.apply(cpu);
        byte value = cpu.getMemory().getByte(address);
        cpu.setRa(value);
    }
}
