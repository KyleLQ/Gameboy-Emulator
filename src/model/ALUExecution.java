package model;

import Util.GameBoyUtil;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class ALUExecution {

    private static final List<BiConsumer<Byte, CPU>> INSTRUCTION_TO_ALU_A_R8_MAP = Arrays.asList(
            (Byte b, CPU cpu) -> {
                byte regA = cpu.getRa();
                cpu.setZeroFlag(((regA + b) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                updateCarryFlagAdditionR8(cpu.getRa(), b, (byte) 0, cpu);
                updateHalfCarryFlagAdditionR8(cpu.getRa(), b, (byte) 0, cpu);
                cpu.setRa((byte) (cpu.getRa() + b));
            },
            (Byte b, CPU cpu) -> {
                byte regA = cpu.getRa();
                cpu.setZeroFlag(((regA + b + cpu.getCarryFlag()) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                updateHalfCarryFlagAdditionR8(regA, b, (byte) cpu.getCarryFlag(), cpu);
                cpu.setRa((byte) (regA + b + cpu.getCarryFlag()));
                updateCarryFlagAdditionR8(regA, b, (byte) cpu.getCarryFlag(), cpu);
            },
            (Byte b, CPU cpu) -> {
                byte regA = cpu.getRa();
                cpu.setZeroFlag(((regA - b) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(1);
                updateCarryFlagSubtractionR8(regA, b, (byte) 0, cpu);
                updateHalfCarryFlagSubtractionR8(regA, b, (byte) 0, cpu);
                cpu.setRa((byte) (regA - b));
            },
            (Byte b, CPU cpu) -> {
                byte regA = cpu.getRa();
                cpu.setZeroFlag(((regA - b - cpu.getCarryFlag()) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(1);
                updateHalfCarryFlagSubtractionR8(regA, b, (byte) cpu.getCarryFlag(), cpu);
                cpu.setRa((byte) (regA - b - cpu.getCarryFlag()));
                updateCarryFlagSubtractionR8(regA, b, (byte) cpu.getCarryFlag(), cpu);
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
                updateCarryFlagSubtractionR8(regA, b, (byte) 0, cpu);
                updateHalfCarryFlagSubtractionR8(regA, b, (byte) 0, cpu);
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
        BiConsumer<Byte, CPU> ALUFunction = INSTRUCTION_TO_ALU_A_R8_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction,5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4),
                GameBoyUtil.getBitFromPosInByte(instruction, 3)));

        ALUFunction.accept(r8, cpu);
    }

    /**
     * corresponds to the ADD HL, r16 instruction
     */
    public static void executeADD_HL_r16(byte instruction, CPU cpu) {
        final short[] INSTRUCTION_TO_R16_MAP = {cpu.getRegisterBC(), cpu.getRegisterDE(),
                cpu.getRegisterHL(), cpu.getStackPointer()};

        short r16 = INSTRUCTION_TO_R16_MAP[GameBoyUtil.get2BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4)
        )];

        cpu.setSubtractionFlag(0);
        updateCarryFlagAdditionR16(cpu.getRegisterHL(), r16, cpu);
        updateHalfCarryFlagAdditionR16(cpu.getRegisterHL(), r16, cpu);
        cpu.setRegisterHL((short) (cpu.getRegisterHL() + r16));
    }

    /**
     * updates carry flag based on the result of operand1 + operand2 + operand3
     */
    private static void updateCarryFlagAdditionR8(byte operand1, byte operand2, byte operand3, CPU cpu) {
        int result = GameBoyUtil.zeroExtendByte(operand1) +
                GameBoyUtil.zeroExtendByte(operand2) +
                GameBoyUtil.zeroExtendByte(operand3);
        cpu.setCarryFlag((result > GameBoyUtil.UNSIGNED_BYTE_MAX) ? 1 : 0);
    }

    /**
     * updates carry flag based on the result of operand1 - operand2 - operand3
     */
    private static void updateCarryFlagSubtractionR8(byte operand1, byte operand2, byte operand3, CPU cpu) {
        int result = GameBoyUtil.zeroExtendByte(operand1) -
                GameBoyUtil.zeroExtendByte(operand2) -
                GameBoyUtil.zeroExtendByte(operand3);
        cpu.setCarryFlag((result < 0) ? 1 : 0);
    }

    /**
     * updates half carry flag based on the result of operand1 + operand2 + operand3
     */
    private static void updateHalfCarryFlagAdditionR8(byte operand1, byte operand2, byte operand3, CPU cpu) {
        int nibbleAdditionResult = GameBoyUtil.getNibble(true, operand1) +
                GameBoyUtil.getNibble(true, operand2) +
                GameBoyUtil.getNibble(true, operand3);
        cpu.setHalfCarryFlag((nibbleAdditionResult > GameBoyUtil.UNSIGNED_NIBBLE_MAX) ? 1 : 0);
    }

    /**
     * updates half carry flag based on result of operand1 - operand2 - operand3
     */
    private static void updateHalfCarryFlagSubtractionR8(byte operand1, byte operand2, byte operand3, CPU cpu) {
        int nibbleSubtractionResult = GameBoyUtil.getNibble(true, operand1) -
                GameBoyUtil.getNibble(true, operand2) -
                GameBoyUtil.getNibble(true, operand3);
        cpu.setHalfCarryFlag((nibbleSubtractionResult < 0) ? 1 : 0);
    }

    /**
     * updates carry flag based on the result of operand1 + operand2
     */
    private static void updateCarryFlagAdditionR16(short operand1, short operand2, CPU cpu) {
        int result = GameBoyUtil.zeroExtendShort(operand1) + GameBoyUtil.zeroExtendShort(operand2);
        cpu.setCarryFlag((result > GameBoyUtil.UNSIGNED_SHORT_MAX) ? 1 : 0);
    }

    /**
     * updates half carry flag based on the result of operand1 + operand2
     */
    private static void updateHalfCarryFlagAdditionR16(short operand1, short operand2, CPU cpu) {
        int mask = 0x0FFF;
        int result = (GameBoyUtil.zeroExtendShort(operand1) & mask) + (GameBoyUtil.zeroExtendShort(operand2) & mask);
        cpu.setHalfCarryFlag((result > GameBoyUtil.UNSIGNED_12_BIT_MAX) ? 1 : 0);
    }
}
