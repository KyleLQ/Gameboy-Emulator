package model.execution;

import model.CPU;
import util.GameBoyUtil;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * class containing methods that execute misc bit operations, including
 * bit shift, set, reset, test, and nibble swap operations
 */
public class BitOpExecution {

    /**
     * corresponds to the bit shift, bit rotate, and nibble swap instructions
     */
    public static void executeSHIFT_ROTATE(byte instruction, CPU cpu) {

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

        int result = r8 & ~(0b00000001 << bitPos);

        BiConsumer<Byte, CPU> setR8 = GameBoyUtil.INSTRUCTION_TO_SET_R8_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 2),
                GameBoyUtil.getBitFromPosInByte(instruction, 1),
                GameBoyUtil.getBitFromPosInByte(instruction, 0)
        ));

        setR8.accept((byte) result, cpu);
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

        int result = r8 | 0b00000001 << bitPos;

        BiConsumer<Byte, CPU> setR8 = GameBoyUtil.INSTRUCTION_TO_SET_R8_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 2),
                GameBoyUtil.getBitFromPosInByte(instruction, 1),
                GameBoyUtil.getBitFromPosInByte(instruction, 0)
        ));

        setR8.accept((byte) result, cpu);
    }
}
