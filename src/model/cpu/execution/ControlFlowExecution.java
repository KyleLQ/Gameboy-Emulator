package model.cpu.execution;

import model.cpu.CPU;
import util.GBUtil;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ControlFlowExecution {

    private static final List<Function<CPU, Integer>> INSTRUCTION_TO_CONDITION_MAP = Arrays.asList(
            (CPU cpu) -> (cpu.getZeroFlag() == 1) ? 0 : 1,
            (CPU cpu) -> cpu.getZeroFlag(),
            (CPU cpu) -> (cpu.getCarryFlag() == 1) ? 0 : 1,
            (CPU cpu) -> cpu.getCarryFlag()
    );

    private static final List<Consumer<CPU>> INSTRUCTION_TO_RET_HL_MAP = Arrays.asList(
            (CPU cpu) -> {
                short sp = cpu.getStackPointer();
                byte pc_lsb = cpu.getMemory().getByte(sp);
                sp = (short) (sp + 1);
                byte pc_msb = cpu.getMemory().getByte(sp);
                sp = (short) (sp + 1);
                cpu.setStackPointer(sp);
                short pc = GBUtil.getShortFromBytes(pc_lsb, pc_msb);
                cpu.getMemory().doMCycle(); // this instruction takes an extra M-cycle
                cpu.setProgramCounter((short) (pc - 1)); // account for pc++ at end of cycle
            },
            (CPU cpu) -> {
                // RETI instruction. Sets IME to 1 without having to wait another instruction.

                short sp = cpu.getStackPointer();
                byte pc_lsb = cpu.getMemory().getByte(sp);
                sp = (short) (sp + 1);
                byte pc_msb = cpu.getMemory().getByte(sp);
                sp = (short) (sp + 1);
                cpu.setStackPointer(sp);
                short pc = GBUtil.getShortFromBytes(pc_lsb, pc_msb);
                cpu.getMemory().doMCycle(); // this instruction takes an extra M-cycle
                cpu.setProgramCounter((short) (pc - 1)); // account for pc++ at end of cycle

                cpu.setIMEImmediately();
            },
            (CPU cpu) -> {
                short hl = cpu.getRegisterHL();
                cpu.setProgramCounter((short) (hl-1)); // account for pc++ at end of cycle
            },
            (CPU cpu) -> {
                short hl = cpu.getRegisterHL();
                cpu.setStackPointer(hl);
                // this takes an extra M-cycle
                cpu.getMemory().doMCycle();
            }
    );

    /**
     * corresponds to unconditional relative jump instruction.
     * the signed 8 bit offset is calculated based on the address
     * immediately following the JR instruction.
     * This takes 3 M-cycles, so add an extra M-cycle call.
     */
    public static void executeJR_UNCONDITIONAL(byte instruction, CPU cpu) {
        short pc = cpu.getProgramCounter();
        byte offset = cpu.getMemory().getByte((short) (pc + 1));

        // offset is from address of next instruction
        pc = (short) (pc + 2);
        // offset should be treated as signed, so no zero extension
        pc = (short) (pc + offset);
        // account for pc will increment by one at the end of every fetch decode execute cycle
        pc = (short) (pc - 1);
        cpu.setProgramCounter(pc);
        cpu.getMemory().doMCycle();
    }

    /**
     * corresponds to conditional relative jump instruction.
     * the signed 8 bit offset is calculated based on the address
     * immediately following the JR instruction.
     * Conditions are based on the C and Z flags.
     * The 8 bit immediate is still read no matter what.
     */
    public static void executeJR_CONDITIONAL(byte instruction, CPU cpu) {
        Function<CPU, Integer> conditionFunction = INSTRUCTION_TO_CONDITION_MAP.get(
                GBUtil.get2BitValue(
                        GBUtil.getBit(instruction, 4),
                        GBUtil.getBit(instruction, 3)));

        if (conditionFunction.apply(cpu) == 1) {
            executeJR_UNCONDITIONAL(instruction, cpu);
        } else {
            short pc = cpu.getProgramCounter();
            pc = (short) (pc + 1);
            cpu.getMemory().getByte(pc);
            cpu.setProgramCounter(pc);
        }
    }

    /**
     * corresponds to unconditional absolute jump instruction. The address to set PC
     * to is specified by the 2 bytes after instruction, in little Endian.
     * This takes 4 M-cycles, so add an extra M-cycle call.
     */
    public static void executeJP_UNCONDITIONAL(byte instruction, CPU cpu) {
        short pc = cpu.getProgramCounter();
        byte lsb = cpu.getMemory().getByte((short) (pc + 1));
        byte msb = cpu.getMemory().getByte((short) (pc + 2));
        short nextPc = GBUtil.getShortFromBytes(lsb, msb);
        // account for pc incrementing by one at end of every fetch decode execute cycle
        nextPc = (short) (nextPc - 1);
        cpu.setProgramCounter(nextPc);
        cpu.getMemory().doMCycle();
    }

    /**
     * corresponds to conditional absolute jump instruction. The address to set PC
     * to is specified by the 2 bytes after instruction, in little Endian.
     * Conditions are based on the C and Z flags.
     * The 16 bit immediate is read no matter what.
     */
    public static void executeJP_CONDITIONAL(byte instruction, CPU cpu) {
        Function<CPU, Integer> conditionFunction = INSTRUCTION_TO_CONDITION_MAP.get(
                GBUtil.get2BitValue(
                        GBUtil.getBit(instruction, 4),
                        GBUtil.getBit(instruction, 3)));

        if (conditionFunction.apply(cpu) == 1) {
            executeJP_UNCONDITIONAL(instruction, cpu);
        } else {
            short pc = cpu.getProgramCounter();
            pc = (short) (pc + 1);
            cpu.getMemory().getByte(pc);
            pc = (short) (pc + 1);
            cpu.getMemory().getByte(pc);
            cpu.setProgramCounter(pc);
        }
    }

    /**
     * Executes the unconditional CALL instruction.
     * Jumps to the 16 bit immediate and pushes the address of the instruction after the
     * CALL on the stack.
     * This takes 6 M-cycles, so need to add M-Cycle calls.
     */
    public static void executeCALL_UNCONDITIONAL(byte instruction, CPU cpu) {
        short pc = cpu.getProgramCounter();
        pc = (short) (pc + 1);
        byte u16_lsb = cpu.getMemory().getByte(pc);
        pc = (short) (pc + 1);
        byte u16_msb = cpu.getMemory().getByte(pc);
        pc = (short) (pc + 1);

        byte pc_lsb = GBUtil.getByteFromShort(true, pc);
        byte pc_msb = GBUtil.getByteFromShort(false, pc);

        short sp = cpu.getStackPointer();
        sp = (short) (sp - 1);
        cpu.getMemory().doMCycle();
        cpu.getMemory().setByte(pc_msb, sp);
        sp = (short) (sp - 1);
        cpu.getMemory().setByte(pc_lsb, sp);
        cpu.setStackPointer(sp);

        short u16 = GBUtil.getShortFromBytes(u16_lsb, u16_msb);
        cpu.setProgramCounter(u16);
        cpu.setProgramCounter((short) (cpu.getProgramCounter() - 1)); // account for pc++ at the end of the cycle
    }

    /**
     * Executes the conditional CALL instruction.
     * Jumps to the 16 bit immediate and pushes the address of the instruction after the
     * CALL on the stack, if condition is met. Either way, the 16 bit immediate is read.
     */
    public static void executeCALL_CONDITIONAL(byte instruction, CPU cpu) {
        Function<CPU, Integer> conditionFunction = INSTRUCTION_TO_CONDITION_MAP.get(
                GBUtil.get2BitValue(
                        GBUtil.getBit(instruction, 4),
                        GBUtil.getBit(instruction, 3)));

        if (conditionFunction.apply(cpu) == 1) {
            executeCALL_UNCONDITIONAL(instruction, cpu);
        } else {
            short pc = cpu.getProgramCounter();
            pc = (short) (pc + 1);
            cpu.getMemory().getByte(pc);
            pc = (short) (pc + 1);
            cpu.getMemory().getByte(pc);
            cpu.setProgramCounter(pc);
        }
    }

    /**
     * Executes the instruction RST.
     * Equivalent to CALL 00EXP000, where EXP is represented by
     * bits 5,4,3 in the instruction.
     * This takes 4 M-cycles, so add an extra M-cycle call.
     */
    public static void executeRST(byte instruction, CPU cpu) {
        int bit5 = GBUtil.getBit(instruction, 5);
        int bit4 = GBUtil.getBit(instruction, 4);
        int bit3 = GBUtil.getBit(instruction, 3);

        byte exp = (byte) 0b00000000;
        exp = GBUtil.modifyBit(exp, 5, bit5);
        exp = GBUtil.modifyBit(exp, 4, bit4);
        exp = GBUtil.modifyBit(exp, 3, bit3);

        short pc = cpu.getProgramCounter();
        pc = (short) (pc + 1);

        byte pc_lsb = GBUtil.getByteFromShort(true, pc);
        byte pc_msb = GBUtil.getByteFromShort(false, pc);

        short sp = cpu.getStackPointer();
        sp = (short) (sp - 1);
        cpu.getMemory().doMCycle();
        cpu.getMemory().setByte(pc_msb, sp);
        sp = (short) (sp - 1);
        cpu.getMemory().setByte(pc_lsb, sp);
        cpu.setStackPointer(sp);

        short address = GBUtil.getShortFromBytes(exp, (byte) 0);
        cpu.setProgramCounter(address);
        cpu.setProgramCounter((short) (cpu.getProgramCounter() - 1)); // account for pc++ at the end of the cycle
    }

    /**
     * Executes the instructions RET, RETI, JP HL, and LD SP, HL
     */
    public static void executeRET_HL_OPS(byte instruction, CPU cpu) {
        Consumer<CPU> ret_hl_op = INSTRUCTION_TO_RET_HL_MAP.get(
                GBUtil.get2BitValue(
                        GBUtil.getBit(instruction, 5),
                        GBUtil.getBit(instruction, 4)));
        ret_hl_op.accept(cpu);
    }

    /**
     * Executes the instruction RET conditional
     * This instruction takes 5 M-cycles in True case
     * and 2 M-cycles in false case, so add an extra
     * M-cycle call.
     */
    public static void executeRET_CONDITIONAL(byte instruction, CPU cpu) {
        Function<CPU, Integer> conditionFunction = INSTRUCTION_TO_CONDITION_MAP.get(
                GBUtil.get2BitValue(
                        GBUtil.getBit(instruction, 4),
                        GBUtil.getBit(instruction, 3)));

        cpu.getMemory().doMCycle();
        if (conditionFunction.apply(cpu) == 1) {
            INSTRUCTION_TO_RET_HL_MAP.get(0).accept(cpu);
        }
    }
}
