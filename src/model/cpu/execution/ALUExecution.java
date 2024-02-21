package model.cpu.execution;

import model.cpu.CPU;
import util.GameBoyUtil;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static util.GameBoyUtil.INSTRUCTION_TO_GET_R16_SP_MAP;
import static util.GameBoyUtil.INSTRUCTION_TO_SET_R16_SP_MAP;

/**
 * class containing methods that execute ALU operations
 */
public class ALUExecution {

    private static final List<BiConsumer<Byte, CPU>> INSTRUCTION_TO_ALU_A_R8_MAP = Arrays.asList(
            (Byte b, CPU cpu) -> {
                byte regA = cpu.getRa();
                cpu.setZeroFlag(((byte) (regA + b) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                updateCarryFlagAdditionR8(cpu.getRa(), b, (byte) 0, cpu);
                updateHalfCarryFlagAdditionR8(cpu.getRa(), b, (byte) 0, cpu);
                cpu.setRa((byte) (cpu.getRa() + b));
            },
            (Byte b, CPU cpu) -> {
                byte regA = cpu.getRa();
                cpu.setZeroFlag(((byte) (regA + b + cpu.getCarryFlag()) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                updateHalfCarryFlagAdditionR8(regA, b, (byte) cpu.getCarryFlag(), cpu);
                cpu.setRa((byte) (regA + b + cpu.getCarryFlag()));
                updateCarryFlagAdditionR8(regA, b, (byte) cpu.getCarryFlag(), cpu);
            },
            (Byte b, CPU cpu) -> {
                byte regA = cpu.getRa();
                cpu.setZeroFlag(((byte) (regA - b) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(1);
                updateCarryFlagSubtractionR8(regA, b, (byte) 0, cpu);
                updateHalfCarryFlagSubtractionR8(regA, b, (byte) 0, cpu);
                cpu.setRa((byte) (regA - b));
            },
            (Byte b, CPU cpu) -> {
                byte regA = cpu.getRa();
                cpu.setZeroFlag(((byte) (regA - b - cpu.getCarryFlag()) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(1);
                updateHalfCarryFlagSubtractionR8(regA, b, (byte) cpu.getCarryFlag(), cpu);
                cpu.setRa((byte) (regA - b - cpu.getCarryFlag()));
                updateCarryFlagSubtractionR8(regA, b, (byte) cpu.getCarryFlag(), cpu);
            },
            (Byte b, CPU cpu) -> {
                byte regA = cpu.getRa();
                cpu.setZeroFlag(((byte) (regA & b) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setCarryFlag(0);
                cpu.setHalfCarryFlag(1);
                cpu.setRa((byte) (regA & b));
            },
            (Byte b, CPU cpu) -> {
                byte regA = cpu.getRa();
                cpu.setZeroFlag(((byte) (regA ^ b) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setCarryFlag(0);
                cpu.setHalfCarryFlag(0);
                cpu.setRa((byte) (regA ^ b));
            },
            (Byte b, CPU cpu) -> {
                byte regA = cpu.getRa();
                cpu.setZeroFlag(((byte) (regA | b) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(0);
                cpu.setCarryFlag(0);
                cpu.setHalfCarryFlag(0);
                cpu.setRa((byte) (regA | b));
            },
            (Byte b, CPU cpu) -> {
                byte regA = cpu.getRa();
                cpu.setZeroFlag(((byte) (regA - b) == 0) ? 1 : 0);
                cpu.setSubtractionFlag(1);
                updateCarryFlagSubtractionR8(regA, b, (byte) 0, cpu);
                updateHalfCarryFlagSubtractionR8(regA, b, (byte) 0, cpu);
            }
    );

    /**
     * corresponds to ALU A,r8 instruction.
     */
    public static void executeALU_A_r8(byte instruction, CPU cpu) {
        Function<CPU, Byte> getR8 = GameBoyUtil.INSTRUCTION_TO_GET_R8_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 2),
                GameBoyUtil.getBitFromPosInByte(instruction, 1),
                GameBoyUtil.getBitFromPosInByte(instruction, 0)));

        byte r8 = getR8.apply(cpu);

        BiConsumer<Byte, CPU> ALUFunction = INSTRUCTION_TO_ALU_A_R8_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4),
                GameBoyUtil.getBitFromPosInByte(instruction, 3)));

        ALUFunction.accept(r8, cpu);
    }

    /**
     * corresponds to ALU A,u8 instruction.
     */
    public static void executeALU_A_u8(byte instruction, CPU cpu) {

        short pc = cpu.getProgramCounter();
        pc = (short) (pc + 1);
        byte u8 = cpu.getMemory().getByte(pc);
        cpu.setProgramCounter(pc);

        BiConsumer<Byte, CPU> ALUFunction = INSTRUCTION_TO_ALU_A_R8_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4),
                GameBoyUtil.getBitFromPosInByte(instruction, 3)));

        ALUFunction.accept(u8, cpu);
    }

    /**
     * corresponds to the ADD HL, r16 instruction
     */
    public static void executeADD_HL_r16(byte instruction, CPU cpu) {
       Function<CPU, Short> getR16 = INSTRUCTION_TO_GET_R16_SP_MAP.get(GameBoyUtil.get2BitValue(
               GameBoyUtil.getBitFromPosInByte(instruction, 5),
               GameBoyUtil.getBitFromPosInByte(instruction, 4)
       ));

       short r16 = getR16.apply(cpu);

        cpu.setSubtractionFlag(0);
        updateCarryFlagAdditionR16(cpu.getRegisterHL(), r16, cpu);
        updateHalfCarryFlagAdditionR16(cpu.getRegisterHL(), r16, cpu);
        cpu.setRegisterHL((short) (cpu.getRegisterHL() + r16));
    }

    /**
     * Executes the instruction ADD SP, i8.
     * Adds the signed byte i8 to SP. Updates C and H only if there is a carry
     * from bit 7 and 3 respectively. (applies to negative i8 as well, using twos complement addition)
     */
    public static void executeADD_SP_i8(byte instruction, CPU cpu) {
        short pc = cpu.getProgramCounter();
        pc = (short) (pc + 1);
        byte i8 = cpu.getMemory().getByte(pc);
        cpu.setProgramCounter(pc);

        short sp = cpu.getStackPointer();
        cpu.setStackPointer((short) (sp + i8));
        cpu.setZeroFlag(0);
        cpu.setSubtractionFlag(0);

        byte sp_lsb = GameBoyUtil.getByteFromShort(true, sp);
        updateCarryFlagAdditionR8(sp_lsb, i8, (byte) 0, cpu);
        updateHalfCarryFlagAdditionR8(sp_lsb, i8, (byte) 0, cpu);
    }

    /**
     * Executes the instruction LD HL, SP + i8.
     * The signed byte i8 is added to SP. The result is loaded into HL.
     * Updates C and H only if there is a carry from bit 7 and 3 respectively.
     * (applies to negative i8 as well, using twos complement addition)
     */
    public static void executeLD_HL_SP_plus_i8(byte instruction, CPU cpu) {
        short pc = cpu.getProgramCounter();
        pc = (short) (pc + 1);
        byte i8 = cpu.getMemory().getByte(pc);
        cpu.setProgramCounter(pc);

        short sp = cpu.getStackPointer();
        cpu.setRegisterHL((short) (sp + i8));
        cpu.setZeroFlag(0);
        cpu.setSubtractionFlag(0);

        byte sp_lsb = GameBoyUtil.getByteFromShort(true, sp);
        updateCarryFlagAdditionR8(sp_lsb, i8, (byte) 0, cpu);
        updateHalfCarryFlagAdditionR8(sp_lsb, i8, (byte) 0, cpu);
    }

    /**
     * corresponds to INC r8 instruction
     */
    public static void executeINC_r8(byte instruction, CPU cpu) {

        Function<CPU, Byte> getR8 = GameBoyUtil.INSTRUCTION_TO_GET_R8_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4),
                GameBoyUtil.getBitFromPosInByte(instruction, 3)));

        byte r8 = getR8.apply(cpu);
        int result = GameBoyUtil.zeroExtendByte(r8) + 1;
        cpu.setZeroFlag(( (byte) result == 0) ? 1 : 0);
        cpu.setSubtractionFlag(0);
        updateHalfCarryFlagAdditionR8(r8, (byte) 1, (byte) 0, cpu);

        BiConsumer<Byte, CPU> setR8 = GameBoyUtil.INSTRUCTION_TO_SET_R8_MAP.get(GameBoyUtil.get3BitValue(
           GameBoyUtil.getBitFromPosInByte(instruction, 5),
           GameBoyUtil.getBitFromPosInByte(instruction, 4),
           GameBoyUtil.getBitFromPosInByte(instruction, 3)
        ));

        setR8.accept((byte) result, cpu);
    }


    /**
     * corresponds to DEC r8 instruction
     */
    public static void executeDEC_r8(byte instruction, CPU cpu) {
        Function<CPU, Byte> getR8 = GameBoyUtil.INSTRUCTION_TO_GET_R8_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4),
                GameBoyUtil.getBitFromPosInByte(instruction, 3)));

        byte r8 = getR8.apply(cpu);
        int result = GameBoyUtil.zeroExtendByte(r8) - 1;
        cpu.setZeroFlag(((byte) result == 0) ? 1 : 0);
        cpu.setSubtractionFlag(1);
        updateHalfCarryFlagSubtractionR8(r8, (byte) 1, (byte) 0, cpu);

        BiConsumer<Byte, CPU> setR8 = GameBoyUtil.INSTRUCTION_TO_SET_R8_MAP.get(GameBoyUtil.get3BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4),
                GameBoyUtil.getBitFromPosInByte(instruction, 3)
        ));

        setR8.accept((byte) result, cpu);
    }

    /**
     * corresponds to INC r16 instruction
     */
    public static void executeINC_r16(byte instruction, CPU cpu) {
        Function<CPU, Short> getR16 = INSTRUCTION_TO_GET_R16_SP_MAP.get(GameBoyUtil.get2BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4)
        ));

        short r16 = getR16.apply(cpu);
        int result = GameBoyUtil.zeroExtendShort(r16) + 1;

        BiConsumer<Short, CPU> setR16 = INSTRUCTION_TO_SET_R16_SP_MAP.get(GameBoyUtil.get2BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4)
        ));
        setR16.accept((short) result, cpu);
    }

    /**
     * corresponds to DEC r16 instruction
     */
    public static void executeDEC_r16(byte instruction, CPU cpu) {
        Function<CPU, Short> getR16 = INSTRUCTION_TO_GET_R16_SP_MAP.get(GameBoyUtil.get2BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4)
        ));

        short r16 = getR16.apply(cpu);
        int result = GameBoyUtil.zeroExtendShort(r16) - 1;

        BiConsumer<Short, CPU> setR16 = INSTRUCTION_TO_SET_R16_SP_MAP.get(GameBoyUtil.get2BitValue(
                GameBoyUtil.getBitFromPosInByte(instruction, 5),
                GameBoyUtil.getBitFromPosInByte(instruction, 4)
        ));
        setR16.accept((short) result, cpu);
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
