package model;

import Util.GameBoyUtil;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class ALUExecution {

    private static final List<BiConsumer<Byte, CPU>> INSTRUCTION_TO_ALU_MAP = Arrays.asList(
            (Byte b, CPU cpu) -> {
                byte regA = cpu.getRa();
                cpu.setZeroFlag(((regA + b) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                updateCarryFlagAddition(cpu.getRa(), b, (byte) 0, cpu);
                updateHalfCarryFlagAddition(cpu.getRa(), b, (byte) 0, cpu);
                cpu.setRa((byte) (cpu.getRa() + b));
            },
            (Byte b, CPU cpu) -> {
                byte regA = cpu.getRa();
                cpu.setZeroFlag(((regA + b + cpu.getCarryFlag()) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                updateHalfCarryFlagAddition(regA, b, (byte) cpu.getCarryFlag(), cpu);
                cpu.setRa((byte) (regA + b + cpu.getCarryFlag()));
                updateCarryFlagAddition(regA, b, (byte) cpu.getCarryFlag(), cpu);
            },
            (Byte b, CPU cpu) -> {
                byte regA = cpu.getRa();
                cpu.setZeroFlag(((regA - b) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(1);
                updateCarryFlagSubtraction(regA, b, (byte) 0, cpu);
                updateHalfCarryFlagSubtraction(regA, b, (byte) 0, cpu);
                cpu.setRa((byte) (regA - b));
            },
            (Byte b, CPU cpu) -> {
                byte regA = cpu.getRa();
                cpu.setZeroFlag(((regA - b - cpu.getCarryFlag()) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(1);
                updateHalfCarryFlagSubtraction(regA, b, (byte) cpu.getCarryFlag(), cpu);
                cpu.setRa((byte) (regA - b - cpu.getCarryFlag()));
                updateCarryFlagSubtraction(regA, b, (byte) cpu.getCarryFlag(), cpu);
            },
            (Byte b, CPU cpu) -> {
                byte regA = cpu.getRa();
                cpu.setZeroFlag(((regA & b) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setCarryFlag(0);
                cpu.setHalfCarryFlag(1);
                cpu.setRa((byte) (regA & b));
            },
            (Byte b, CPU cpu) -> {
                byte regA = cpu.getRa();
                cpu.setZeroFlag(((regA ^ b) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setCarryFlag(0);
                cpu.setHalfCarryFlag(0);
                cpu.setRa((byte) (regA ^ b));
            },
            (Byte b, CPU cpu) -> {
                byte regA = cpu.getRa();
                cpu.setZeroFlag(((regA | b) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setCarryFlag(0);
                cpu.setHalfCarryFlag(0);
                cpu.setRa((byte) (regA | b));
            },
            (Byte b, CPU cpu) -> {
                byte regA = cpu.getRa();
                cpu.setZeroFlag(((regA - b) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(1);
                updateCarryFlagSubtraction(regA, b, (byte) 0, cpu);
                updateHalfCarryFlagSubtraction(regA, b, (byte) 0, cpu);
            }
    );

    /**
     * corresponds to ALU A,r8 instruction.
     */
    public static void executeALU_A_r8(byte instruction, CPU cpu) {
        // 0x0 should be (HL) todo
        // need a better place to put this?
        // can't make it an instance variable, since its values only get set once as 0/initial values
        final byte[] INSTRUCTION_TO_R8_MAP = {cpu.getRb(), cpu.getRc(), cpu.getRd(), cpu.getRe(),
                cpu.getRh(), cpu.getRl(), 0x0, cpu.getRa()};

        byte r8 = INSTRUCTION_TO_R8_MAP[GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction,2),
                GameBoyUtil.getBitFromPosInByte(instruction, 1),
                GameBoyUtil.getBitFromPosInByte(instruction, 0))];
        BiConsumer<Byte, CPU> ALUFunction = INSTRUCTION_TO_ALU_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction,5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4),
                GameBoyUtil.getBitFromPosInByte(instruction, 3)));

        ALUFunction.accept(r8, cpu);
    }

    /**
     * updates carry flag based on the result of operand1 + operand2 + operand3
     */
    private static void updateCarryFlagAddition(byte operand1, byte operand2, byte operand3, CPU cpu) {
        int result = GameBoyUtil.zeroExtendByte(operand1) +
                GameBoyUtil.zeroExtendByte(operand2) +
                GameBoyUtil.zeroExtendByte(operand3);
        cpu.setCarryFlag((result > GameBoyUtil.UNSIGNED_BYTE_MAX) ? 1 : 0);
    }

    /**
     * updates carry flag based on the result of operand1 - operand2 - operand3
     */
    private static void updateCarryFlagSubtraction(byte operand1, byte operand2, byte operand3, CPU cpu) {
        int result = GameBoyUtil.zeroExtendByte(operand1) -
                GameBoyUtil.zeroExtendByte(operand2) -
                GameBoyUtil.zeroExtendByte(operand3);
        cpu.setCarryFlag((result < 0) ? 1 : 0);
    }

    /**
     * updates half carry flag based on the result of operand1 + operand2 + operand3
     */
    private static void updateHalfCarryFlagAddition(byte operand1, byte operand2, byte operand3, CPU cpu) {
        int nibbleAdditionResult = GameBoyUtil.getNibble(true, operand1) +
                GameBoyUtil.getNibble(true, operand2) +
                GameBoyUtil.getNibble(true, operand3);
        cpu.setHalfCarryFlag((nibbleAdditionResult > GameBoyUtil.UNSIGNED_NIBBLE_MAX) ? 1 : 0);
    }

    /**
     * updates half carry flag based on result of operand1 - operand2 - operand3
     */
    private static void updateHalfCarryFlagSubtraction(byte operand1, byte operand2, byte operand3, CPU cpu) {
        int nibbleSubtractionResult = GameBoyUtil.getNibble(true, operand1) -
                GameBoyUtil.getNibble(true, operand2) -
                GameBoyUtil.getNibble(true, operand3);
        cpu.setHalfCarryFlag((nibbleSubtractionResult < 0) ? 1 : 0);
    }
}
