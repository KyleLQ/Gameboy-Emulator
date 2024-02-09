package model.cpu.execution;

import model.cpu.CPU;
import util.GameBoyUtil;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ControlFlowExecution {

    private static final List<Function<CPU, Integer>> INSTRUCTION_TO_CONDITION_MAP = Arrays.asList(
            (CPU cpu) -> (cpu.getZeroFlag() == 1) ? 0 : 1,
            (CPU cpu) -> cpu.getZeroFlag(),
            (CPU cpu) -> (cpu.getCarryFlag() == 1) ? 0 : 1,
            (CPU cpu) -> cpu.getCarryFlag()
    );

    /**
     * corresponds to unconditional relative jump instruction.
     * the signed 8 bit offset is calculated based on the address
     * immediately following the JR instruction.
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
    }

    /**
     * corresponds to conditional relative jump instruction.
     * the signed 8 bit offset is calculated based on the address
     * immediately following the JR instruction.
     *
     * Conditions are based on the C and Z flags.
     */
    public static void executeJR_CONDITIONAL(byte instruction, CPU cpu) {
        Function<CPU, Integer> conditionFunction = INSTRUCTION_TO_CONDITION_MAP.get(
                GameBoyUtil.get2BitValue(
                        GameBoyUtil.getBitFromPosInByte(instruction, 4),
                        GameBoyUtil.getBitFromPosInByte(instruction, 3)));

        if (conditionFunction.apply(cpu) == 1) {
            executeJR_UNCONDITIONAL(instruction, cpu);
        } else {
            // instruction is two bytes, while pc only increments by one at end of every fetch decode execute cycle
            cpu.setProgramCounter((short) (cpu.getProgramCounter() + 1));
        }
    }
}
