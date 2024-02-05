package model.execution;

import model.CPU;
import util.GameBoyUtil;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * class containing methods that execute misc bit operations, including
 * bit shift, set, reset, test, and nibble swap operations
 */
public class BitOpExecution {

    private static final List<BiFunction<Byte, CPU, Byte>> INSTRUCTION_TO_SHIFT_ROTATE_MAP = Arrays.asList(
            (Byte b, CPU cpu) -> {
                int bit7Value = GameBoyUtil.getBitFromPosInByte(b, 7);
                int result = b << 1;
                byte byteResult = (byte) result;
                byteResult = GameBoyUtil.modifyBitOnPosInByte(byteResult, 0, bit7Value);
                cpu.setZeroFlag((GameBoyUtil.zeroExtendByte(byteResult) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
                cpu.setCarryFlag(bit7Value);
                return byteResult;
            },
            (Byte b, CPU cpu) -> {
                int bit0Value = GameBoyUtil.getBitFromPosInByte(b, 0);
                int result = b >>> 1;
                byte byteResult = (byte) result;
                byteResult = GameBoyUtil.modifyBitOnPosInByte(byteResult, 7, bit0Value);
                cpu.setZeroFlag((GameBoyUtil.zeroExtendByte(byteResult) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
                cpu.setCarryFlag(bit0Value);
                return byteResult;
            },
            (Byte b, CPU cpu) -> {
                int carryValue = cpu.getCarryFlag();
                cpu.setCarryFlag(GameBoyUtil.getBitFromPosInByte(b, 7));
                int result = b << 1;
                byte byteResult = (byte) result;
                byteResult = GameBoyUtil.modifyBitOnPosInByte(byteResult, 0, carryValue);
                cpu.setZeroFlag((GameBoyUtil.zeroExtendByte(byteResult) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
                return byteResult;
            },
            (Byte b, CPU cpu) -> {
                int carryValue = cpu.getCarryFlag();
                cpu.setCarryFlag(GameBoyUtil.getBitFromPosInByte(b, 0));
                int result = b >>> 1;
                byte byteResult = (byte) result;
                byteResult = GameBoyUtil.modifyBitOnPosInByte(byteResult, 7, carryValue);
                cpu.setZeroFlag((GameBoyUtil.zeroExtendByte(byteResult) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
                return byteResult;
            },
            (Byte b, CPU cpu) -> {
                cpu.setCarryFlag(GameBoyUtil.getBitFromPosInByte(b, 7));
                int result = b << 1;
                byte byteResult = (byte) result;
                cpu.setZeroFlag((GameBoyUtil.zeroExtendByte(byteResult) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
                return byteResult;
            },
            (Byte b, CPU cpu) -> {
                cpu.setCarryFlag(GameBoyUtil.getBitFromPosInByte(b, 0));
                int result = b >> 1;
                byte byteResult = (byte) result;
                cpu.setZeroFlag((GameBoyUtil.zeroExtendByte(byteResult) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
                return byteResult;
            },
            (Byte b, CPU cpu) -> {
                int lowerNibble = GameBoyUtil.getNibble(true, b);
                int upperNibble = GameBoyUtil.getNibble(false, b);
                int result = lowerNibble * 16  + upperNibble;
                byte byteResult = (byte) result;
                cpu.setZeroFlag((GameBoyUtil.zeroExtendByte(byteResult) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
                cpu.setCarryFlag(0);
                return byteResult;
            },
            (Byte b, CPU cpu) -> {
                cpu.setCarryFlag(GameBoyUtil.getBitFromPosInByte(b, 0));
                int result = b >>> 1;
                byte byteResult = (byte) result;
                cpu.setZeroFlag((GameBoyUtil.zeroExtendByte(byteResult) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setHalfCarryFlag(0);
                return byteResult;
            }
    );

    /**
     * corresponds to the bit shift, bit rotate, and nibble swap instructions
     */
    public static void executeSHIFT_ROTATE(byte instruction, CPU cpu) {
        Function<CPU, Byte> getR8 = GameBoyUtil.INSTRUCTION_TO_GET_R8_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 2),
                GameBoyUtil.getBitFromPosInByte(instruction, 1),
                GameBoyUtil.getBitFromPosInByte(instruction, 0)));
        byte r8 = getR8.apply(cpu);

        BiFunction<Byte, CPU, Byte> shiftRotateFunction = INSTRUCTION_TO_SHIFT_ROTATE_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4),
                GameBoyUtil.getBitFromPosInByte(instruction, 3)));
        byte result = shiftRotateFunction.apply(r8, cpu);

        BiConsumer<Byte, CPU> setR8 = GameBoyUtil.INSTRUCTION_TO_SET_R8_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 2),
                GameBoyUtil.getBitFromPosInByte(instruction, 1),
                GameBoyUtil.getBitFromPosInByte(instruction, 0)
        ));

        setR8.accept(result, cpu);
    }

    /**
     * corresponds to the instruction BIT bit, r8
     */
    public static void executeBIT_bit_r8(byte instruction, CPU cpu) {
        Function<CPU, Byte> getR8 = GameBoyUtil.INSTRUCTION_TO_GET_R8_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 2),
                GameBoyUtil.getBitFromPosInByte(instruction, 1),
                GameBoyUtil.getBitFromPosInByte(instruction, 0)));
        byte r8 = getR8.apply(cpu);

        int bitPos = GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4),
                GameBoyUtil.getBitFromPosInByte(instruction, 3));

        cpu.setZeroFlag((GameBoyUtil.getBitFromPosInByte(r8, bitPos) == 0) ? 1 : 0);
        cpu.setSubtractionFlag(0);
        cpu.setHalfCarryFlag(1);
    }

    /**
     * corresponds to the instruction RES bit, r8
     */
    public static void executeRES_bit_r8(byte instruction, CPU cpu) {
        Function<CPU, Byte> getR8 = GameBoyUtil.INSTRUCTION_TO_GET_R8_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 2),
                GameBoyUtil.getBitFromPosInByte(instruction, 1),
                GameBoyUtil.getBitFromPosInByte(instruction, 0)));
        byte r8 = getR8.apply(cpu);

        int bitPos = GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4),
                GameBoyUtil.getBitFromPosInByte(instruction, 3));

        byte result = GameBoyUtil.modifyBitOnPosInByte(r8, bitPos, 0);

        BiConsumer<Byte, CPU> setR8 = GameBoyUtil.INSTRUCTION_TO_SET_R8_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 2),
                GameBoyUtil.getBitFromPosInByte(instruction, 1),
                GameBoyUtil.getBitFromPosInByte(instruction, 0)
        ));

        setR8.accept( result, cpu);
    }

    /**
     * corresponds to the instruction SET bit, r8
     */
    public static void executeSET_bit_r8(byte instruction, CPU cpu) {
        Function<CPU, Byte> getR8 = GameBoyUtil.INSTRUCTION_TO_GET_R8_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 2),
                GameBoyUtil.getBitFromPosInByte(instruction, 1),
                GameBoyUtil.getBitFromPosInByte(instruction, 0)));
        byte r8 = getR8.apply(cpu);

        int bitPos = GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4),
                GameBoyUtil.getBitFromPosInByte(instruction, 3));

        byte result = GameBoyUtil.modifyBitOnPosInByte(r8, bitPos, 1);

        BiConsumer<Byte, CPU> setR8 = GameBoyUtil.INSTRUCTION_TO_SET_R8_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 2),
                GameBoyUtil.getBitFromPosInByte(instruction, 1),
                GameBoyUtil.getBitFromPosInByte(instruction, 0)
        ));

        setR8.accept(result, cpu);
    }
}
