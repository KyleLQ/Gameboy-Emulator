package model.cpu.execution;

import model.cpu.CPU;
import util.GBUtil;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * class containing methods that execute misc bit operations, including
 * bit shift, set, reset, test, nibble swap, DAA, and flag operations
 */
public class BitOpExecution {

    private static final List<BiFunction<Byte, CPU, Byte>> INSTRUCTION_TO_SHIFT_ROTATE_MAP = Arrays.asList(
            (Byte b, CPU cpu) -> {
                int bit7Value = GBUtil.getBit(b, 7);
                int result = b << 1;
                byte byteResult = (byte) result;
                byteResult = GBUtil.modifyBit(byteResult, 0, bit7Value);
                cpu.setZeroFlag((GBUtil.zeroExtend(byteResult) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
                cpu.setCarryFlag(bit7Value);
                return byteResult;
            },
            (Byte b, CPU cpu) -> {
                int bit0Value = GBUtil.getBit(b, 0);
                int result = GBUtil.zeroExtend(b) >>> 1;
                byte byteResult = (byte) result;
                byteResult = GBUtil.modifyBit(byteResult, 7, bit0Value);
                cpu.setZeroFlag((GBUtil.zeroExtend(byteResult) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
                cpu.setCarryFlag(bit0Value);
                return byteResult;
            },
            (Byte b, CPU cpu) -> {
                int carryValue = cpu.getCarryFlag();
                cpu.setCarryFlag(GBUtil.getBit(b, 7));
                int result = b << 1;
                byte byteResult = (byte) result;
                byteResult = GBUtil.modifyBit(byteResult, 0, carryValue);
                cpu.setZeroFlag((GBUtil.zeroExtend(byteResult) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
                return byteResult;
            },
            (Byte b, CPU cpu) -> {
                int carryValue = cpu.getCarryFlag();
                cpu.setCarryFlag(GBUtil.getBit(b, 0));
                int result = GBUtil.zeroExtend(b) >>> 1;
                byte byteResult = (byte) result;
                byteResult = GBUtil.modifyBit(byteResult, 7, carryValue);
                cpu.setZeroFlag((GBUtil.zeroExtend(byteResult) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
                return byteResult;
            },
            (Byte b, CPU cpu) -> {
                cpu.setCarryFlag(GBUtil.getBit(b, 7));
                int result = b << 1;
                byte byteResult = (byte) result;
                cpu.setZeroFlag((GBUtil.zeroExtend(byteResult) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
                return byteResult;
            },
            (Byte b, CPU cpu) -> {
                cpu.setCarryFlag(GBUtil.getBit(b, 0));
                int result = b >> 1;
                byte byteResult = (byte) result;
                cpu.setZeroFlag((GBUtil.zeroExtend(byteResult) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
                return byteResult;
            },
            (Byte b, CPU cpu) -> {
                int lowerNibble = GBUtil.getNibble(true, b);
                int upperNibble = GBUtil.getNibble(false, b);
                int result = lowerNibble * 16  + upperNibble;
                byte byteResult = (byte) result;
                cpu.setZeroFlag((GBUtil.zeroExtend(byteResult) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
                cpu.setCarryFlag(0);
                return byteResult;
            },
            (Byte b, CPU cpu) -> {
                cpu.setCarryFlag(GBUtil.getBit(b, 0));
                int result = GBUtil.zeroExtend(b) >>> 1;
                byte byteResult = (byte) result;
                cpu.setZeroFlag((GBUtil.zeroExtend(byteResult) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
                return byteResult;
            }
    );

    private static final List<Consumer<CPU>> INSTRUCTION_TO_ACCUMULATOR_FLAG_MAP = Arrays.asList(
            (CPU cpu) -> {
                byte accumulatorValue = cpu.getRa();
                int bit7Value = GBUtil.getBit(accumulatorValue, 7);
                int result = accumulatorValue << 1;
                byte byteResult = (byte) result;
                byteResult = GBUtil.modifyBit(byteResult, 0, bit7Value);
                cpu.setRa(byteResult);
                cpu.setZeroFlag(0);
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
                cpu.setCarryFlag(bit7Value);
            },
            (CPU cpu) -> {
                byte accumulatorValue = cpu.getRa();
                int bit0Value = GBUtil.getBit(accumulatorValue, 0);
                int result = GBUtil.zeroExtend(accumulatorValue) >>> 1;
                byte byteResult = (byte) result;
                byteResult = GBUtil.modifyBit(byteResult, 7, bit0Value);
                cpu.setRa(byteResult);
                cpu.setZeroFlag(0);
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
                cpu.setCarryFlag(bit0Value);
            },
            (CPU cpu) -> {
                byte accumulatorValue = cpu.getRa();
                int carryValue = cpu.getCarryFlag();
                cpu.setCarryFlag(GBUtil.getBit(accumulatorValue, 7));
                int result = accumulatorValue << 1;
                byte byteResult = (byte) result;
                byteResult = GBUtil.modifyBit(byteResult, 0, carryValue);
                cpu.setRa(byteResult);
                cpu.setZeroFlag(0);
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
            },
            (CPU cpu) -> {
                byte accumulatorValue = cpu.getRa();
                int carryValue = cpu.getCarryFlag();
                cpu.setCarryFlag(GBUtil.getBit(accumulatorValue, 0));
                int result = GBUtil.zeroExtend(accumulatorValue) >>> 1;
                byte byteResult = (byte) result;
                byteResult = GBUtil.modifyBit(byteResult, 7, carryValue);
                cpu.setRa(byteResult);
                cpu.setZeroFlag(0);
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
            },
            (CPU cpu) -> {
                byte accumulatorValue = cpu.getRa();
                int lowerNibble = GBUtil.getNibble(true, accumulatorValue);
                int upperNibble = GBUtil.getNibble(false, accumulatorValue);
                int newCarryValue = cpu.getCarryFlag(); // if not modified, keep as previous value
                if (cpu.getSubtractionFlag() == 0) {
                    if (cpu.getHalfCarryFlag() == 1 || lowerNibble > 9) {
                        lowerNibble += 6;
                    }
                    if (cpu.getCarryFlag() == 1 || GBUtil.zeroExtend(accumulatorValue) > 0x99) {
                        upperNibble += 6;
                        newCarryValue = 1;
                    }
                } else {
                    if (cpu.getHalfCarryFlag() == 1) {
                        lowerNibble -= 6;
                    }
                    if (cpu.getCarryFlag() == 1) {
                        upperNibble -= 6;
                    }
                }
                cpu.setRa((byte) (upperNibble * 16 + lowerNibble));
                cpu.setZeroFlag((GBUtil.zeroExtend(cpu.getRa()) == 0) ? 1 : 0);
                cpu.setHalfCarryFlag(0);
                cpu.setCarryFlag(newCarryValue);
            },
            (CPU cpu) -> {
                cpu.setRa((byte) (~cpu.getRa()));
                cpu.setSubtractionFlag(1);
                cpu.setHalfCarryFlag(1);
            },
            (CPU cpu) -> {
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
                cpu.setCarryFlag(1);
            },
            (CPU cpu) -> {
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
                cpu.setCarryFlag((cpu.getCarryFlag() == 0) ? 1 : 0);
            }
    );

    /**
     * corresponds to the bit shift, bit rotate, and nibble swap instructions
     */
    public static void executeSHIFT_ROTATE(byte instruction, CPU cpu) {
        Function<CPU, Byte> getR8 = GBUtil.INSTRUCTION_TO_GET_R8_MAP.get(GBUtil.get3BitValue(
                GBUtil.getBit(instruction, 2),
                GBUtil.getBit(instruction, 1),
                GBUtil.getBit(instruction, 0)));
        byte r8 = getR8.apply(cpu);

        BiFunction<Byte, CPU, Byte> shiftRotateFunction = INSTRUCTION_TO_SHIFT_ROTATE_MAP.get(GBUtil.get3BitValue(
                GBUtil.getBit(instruction, 5),
                GBUtil.getBit(instruction, 4),
                GBUtil.getBit(instruction, 3)));
        byte result = shiftRotateFunction.apply(r8, cpu);

        BiConsumer<Byte, CPU> setR8 = GBUtil.INSTRUCTION_TO_SET_R8_MAP.get(GBUtil.get3BitValue(
                GBUtil.getBit(instruction, 2),
                GBUtil.getBit(instruction, 1),
                GBUtil.getBit(instruction, 0)
        ));

        setR8.accept(result, cpu);
    }

    /**
     * corresponds to the instruction BIT bit, r8
     */
    public static void executeBIT_bit_r8(byte instruction, CPU cpu) {
        Function<CPU, Byte> getR8 = GBUtil.INSTRUCTION_TO_GET_R8_MAP.get(GBUtil.get3BitValue(
                GBUtil.getBit(instruction, 2),
                GBUtil.getBit(instruction, 1),
                GBUtil.getBit(instruction, 0)));
        byte r8 = getR8.apply(cpu);

        int bitPos = GBUtil.get3BitValue(
                GBUtil.getBit(instruction, 5),
                GBUtil.getBit(instruction, 4),
                GBUtil.getBit(instruction, 3));

        cpu.setZeroFlag((GBUtil.getBit(r8, bitPos) == 0) ? 1 : 0);
        cpu.setSubtractionFlag(0);
        cpu.setHalfCarryFlag(1);
    }

    /**
     * corresponds to the instruction RES bit, r8
     */
    public static void executeRES_bit_r8(byte instruction, CPU cpu) {
        Function<CPU, Byte> getR8 = GBUtil.INSTRUCTION_TO_GET_R8_MAP.get(GBUtil.get3BitValue(
                GBUtil.getBit(instruction, 2),
                GBUtil.getBit(instruction, 1),
                GBUtil.getBit(instruction, 0)));
        byte r8 = getR8.apply(cpu);

        int bitPos = GBUtil.get3BitValue(
                GBUtil.getBit(instruction, 5),
                GBUtil.getBit(instruction, 4),
                GBUtil.getBit(instruction, 3));

        byte result = GBUtil.modifyBit(r8, bitPos, 0);

        BiConsumer<Byte, CPU> setR8 = GBUtil.INSTRUCTION_TO_SET_R8_MAP.get(GBUtil.get3BitValue(
                GBUtil.getBit(instruction, 2),
                GBUtil.getBit(instruction, 1),
                GBUtil.getBit(instruction, 0)
        ));

        setR8.accept( result, cpu);
    }

    /**
     * corresponds to the instruction SET bit, r8
     */
    public static void executeSET_bit_r8(byte instruction, CPU cpu) {
        Function<CPU, Byte> getR8 = GBUtil.INSTRUCTION_TO_GET_R8_MAP.get(GBUtil.get3BitValue(
                GBUtil.getBit(instruction, 2),
                GBUtil.getBit(instruction, 1),
                GBUtil.getBit(instruction, 0)));
        byte r8 = getR8.apply(cpu);

        int bitPos = GBUtil.get3BitValue(
                GBUtil.getBit(instruction, 5),
                GBUtil.getBit(instruction, 4),
                GBUtil.getBit(instruction, 3));

        byte result = GBUtil.modifyBit(r8, bitPos, 1);

        BiConsumer<Byte, CPU> setR8 = GBUtil.INSTRUCTION_TO_SET_R8_MAP.get(GBUtil.get3BitValue(
                GBUtil.getBit(instruction, 2),
                GBUtil.getBit(instruction, 1),
                GBUtil.getBit(instruction, 0)
        ));

        setR8.accept(result, cpu);
    }

    /**
     * corresponds to instructions related to accumulators and flags:
     * RLCA, RRCA, RLA, RRA, DAA, CPL, SCF, CCF
     */
    public static void executeACCUMULATOR_FLAG_OPS(byte instruction, CPU cpu) {
        Consumer<CPU> accumulatorFlagFunction = INSTRUCTION_TO_ACCUMULATOR_FLAG_MAP.get(GBUtil.get3BitValue(
                GBUtil.getBit(instruction, 5),
                GBUtil.getBit(instruction, 4),
                GBUtil.getBit(instruction, 3)));
        accumulatorFlagFunction.accept(cpu);
    }

    /**
     * This is the CB prefix instruction. This indicates that the next byte is
     * an instruction that should be decoded from the CB prefix table instead.
     */
    public static void executeCB_PREFIX(byte instruction, CPU cpu) {
        // todo this instruction is atomic, no interrupts can happen here
        short pc = cpu.getProgramCounter();
        pc = (short) (pc + 1);
        byte nextInstruction = cpu.getMemory().getByte(pc);
        cpu.setProgramCounter(pc);
        cpu.decodeExecuteCBInstruction(nextInstruction);
    }
}
