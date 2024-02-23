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

    private static final List<Function<CPU, Short>> INSTRUCTION_TO_GET_R16_AF_MAP = Arrays.asList(
            CPU::getRegisterBC,
            CPU::getRegisterDE,
            CPU::getRegisterHL,
            CPU::getRegisterAF
    );

    private static final List<BiConsumer<Short, CPU>> INSTRUCTION_TO_SET_R16_AF_MAP = Arrays.asList(
            (Short s, CPU cpu) -> cpu.setRegisterBC(s),
            (Short s, CPU cpu) -> cpu.setRegisterDE(s),
            (Short s, CPU cpu) -> cpu.setRegisterHL(s),
            (Short s, CPU cpu) -> cpu.setRegisterAF(s)
    );

    /**
     * executes the instruction LD (u16), SP.
     * Loads data from the stack pointer into the address specified by
     * the 16 bit immediate u16.
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
     * loads the immediate value u16 into the register r16
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

    /**
     * Executes the instruction LD r8, u8.
     * Loads the immediate 8 bit value u8 into the register r8
     */
    public static void executeLD_r8_u8(byte instruction, CPU cpu) {
        short pc = cpu.getProgramCounter();
        pc = (short) (pc + 1);
        byte u8 = cpu.getMemory().getByte(pc);
        cpu.setProgramCounter(pc);

        BiConsumer<Byte, CPU> setR8 = GameBoyUtil.INSTRUCTION_TO_SET_R8_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4),
                GameBoyUtil.getBitFromPosInByte(instruction, 3)
        ));

        setR8.accept(u8, cpu);
    }

    /**
     * Executes the instruction LD r8, r8
     * LD (HL), (HL) is not valid because its encoding overlaps with HALT
     */
    public static void executeLD_r8_r8(byte instruction, CPU cpu) {
        if (instruction == (byte) 0b01110110) {
            MiscExecution.executeHALT(instruction, cpu);
            return;
        }
        Function<CPU, Byte> getR8 = GameBoyUtil.INSTRUCTION_TO_GET_R8_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 2),
                GameBoyUtil.getBitFromPosInByte(instruction, 1),
                GameBoyUtil.getBitFromPosInByte(instruction, 0)
        ));

        byte r8 = getR8.apply(cpu);

        BiConsumer<Byte, CPU> setR8 = GameBoyUtil.INSTRUCTION_TO_SET_R8_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4),
                GameBoyUtil.getBitFromPosInByte(instruction, 3)
        ));

        setR8.accept(r8, cpu);
    }

    /**
     * Executes the instruction LD (0xFF00 + u8), A.
     * The memory address is given by the msb 0xFF and the lsb u8.
     * The value in register A is written to the memory address.
     */
    public static void executeLD_Memory_FF00_plus_u8_A(byte instruction, CPU cpu) {
        short pc = cpu.getProgramCounter();
        pc = (short) (pc + 1);
        byte u8 = cpu.getMemory().getByte(pc);
        cpu.setProgramCounter(pc);

        short address = GameBoyUtil.getShortFromBytes(u8, (byte) 0xFF);
        byte a = cpu.getRa();
        cpu.getMemory().setByte(a, address);
    }

    /**
     * Executes the instruction LD A, (0xFF00 + u8)
     * The value in the memory address given by msb = 0xFF and lsb = u8 is read to register A
     */
    public static void executeLD_A_Memory_FF00_plus_u8(byte instruction, CPU cpu) {
        short pc = cpu.getProgramCounter();
        pc = (short) (pc + 1);
        byte u8 = cpu.getMemory().getByte(pc);
        cpu.setProgramCounter(pc);

        short address = GameBoyUtil.getShortFromBytes(u8, (byte) 0xFF);
        byte memoryRead = cpu.getMemory().getByte(address);
        cpu.setRa(memoryRead);
    }

    /**
     * Executes the instruction LD (0xFF00+C), A.
     * The memory address is given by the msb 0xFF and the lsb register C value.
     * The value in register A is written to the memory address.
     */
    public static void executeLD_Memory_FF00_plus_C_A(byte instruction, CPU cpu) {
        byte c = cpu.getRc();
        short address = GameBoyUtil.getShortFromBytes(c, (byte) 0xFF);
        byte a = cpu.getRa();
        cpu.getMemory().setByte(a, address);
    }

    /**
     * Executes the instruction LD A, (0xFF00 + C)
     * The value in the memory address with msb = 0xFF and lsb = reg C value
     * is read to register A.
     */
    public static void executeLD_A_Memory_FF00_plus_C(byte instruction, CPU cpu) {
        byte c = cpu.getRc();
        short address = GameBoyUtil.getShortFromBytes(c, (byte) 0xFF);
        byte memoryRead = cpu.getMemory().getByte(address);
        cpu.setRa(memoryRead);
    }

    /**
     * Executes the instruction LD (u16), A
     */
    public static void executeLD_Memory_u16_A(byte instruction, CPU cpu) {
        short pc = cpu.getProgramCounter();
        pc = (short) (pc + 1);
        byte u16_lsb = cpu.getMemory().getByte(pc);
        pc = (short) (pc + 1);
        byte u16_msb = cpu.getMemory().getByte(pc);
        cpu.setProgramCounter(pc);

        short u16 = GameBoyUtil.getShortFromBytes(u16_lsb, u16_msb);
        byte a = cpu.getRa();
        cpu.getMemory().setByte(a, u16);
    }

    /**
     * Executes the instruction LD A, (u16)
     */
    public static void executeLD_A_Memory_u16(byte instruction, CPU cpu) {
        short pc = cpu.getProgramCounter();
        pc = (short) (pc + 1);
        byte u16_lsb = cpu.getMemory().getByte(pc);
        pc = (short) (pc + 1);
        byte u16_msb = cpu.getMemory().getByte(pc);
        cpu.setProgramCounter(pc);

        short u16 = GameBoyUtil.getShortFromBytes(u16_lsb, u16_msb);
        byte memoryVal = cpu.getMemory().getByte(u16);
        cpu.setRa(memoryVal);
    }

    /**
     * Executes the instruction PUSH r16.
     * The stack pointer should point to the byte at the top of the stack.
     * (NOT the first "empty" byte above the stack)
     */
    public static void executePUSH_r16(byte instruction, CPU cpu) {
        Function<CPU, Short> getR16 = INSTRUCTION_TO_GET_R16_AF_MAP.get(
                GameBoyUtil.get2BitValue(
                        GameBoyUtil.getBitFromPosInByte(instruction, 5),
                        GameBoyUtil.getBitFromPosInByte(instruction, 4)));

        short r16 = getR16.apply(cpu);
        byte r16_lsb = GameBoyUtil.getByteFromShort(true, r16);
        byte r16_msb = GameBoyUtil.getByteFromShort(false, r16);

        short sp = cpu.getStackPointer();
        sp = (short) (sp - 1);
        cpu.getMemory().setByte(r16_msb, sp);
        sp = (short) (sp - 1);
        cpu.getMemory().setByte(r16_lsb, sp);
        cpu.setStackPointer(sp);
    }

    /**
     * Executes the instruction POP r16.
     * The stack pointer should point to the byte at the top of the stack.
     * (NOT the first "empty" byte above the stack)
     */
    public static void executePOP_r16(byte instruction, CPU cpu) {
        short sp = cpu.getStackPointer();
        byte r16_lsb = cpu.getMemory().getByte(sp);
        sp = (short) (sp + 1);
        byte r16_msb = cpu.getMemory().getByte(sp);
        sp = (short) (sp + 1);
        cpu.setStackPointer(sp);
        short r16 = GameBoyUtil.getShortFromBytes(r16_lsb, r16_msb);

        BiConsumer<Short, CPU> setR16 = INSTRUCTION_TO_SET_R16_AF_MAP.get(
                GameBoyUtil.get2BitValue(
                        GameBoyUtil.getBitFromPosInByte(instruction, 5),
                        GameBoyUtil.getBitFromPosInByte(instruction, 4)));
        setR16.accept(r16, cpu);
    }
}
