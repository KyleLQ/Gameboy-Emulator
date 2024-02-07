package model.cpu;

import model.cpu.execution.ALUExecution;
import model.cpu.execution.BitOpExecution;
import model.memory.Memory;
import util.GameBoyUtil;
import exception.CPUException;

import java.nio.ByteBuffer;
import java.util.AbstractMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

public class CPU {

    // registers should be unsigned! but the underlying bits are the same anyways,
    // can just interpret them as signed or unsigned
    private byte ra,rb,rc,rd,re,rf,rh,rl;
    private short sp;
    private Memory memory;


    // opcodes are always one byte long. If you need a constant, then you look at the byte(s) after
    // the opcode.
    // CB prefix are one byte that tells you to switch to a different instruction table.

    // handle the alu instructions only first
    private final Map<Pattern, BiConsumer<Byte, CPU>> REGEX_TO_EXECUTION_MAP = Map.ofEntries(
            new AbstractMap.SimpleEntry<Pattern, BiConsumer<Byte, CPU>>(Pattern.compile("^10[01]{6}$"),
                    ALUExecution::executeALU_A_r8),
            new AbstractMap.SimpleEntry<Pattern, BiConsumer<Byte, CPU>>(Pattern.compile("^00[01]{2}1001$"),
                    ALUExecution::executeADD_HL_r16),
            new AbstractMap.SimpleEntry<Pattern, BiConsumer<Byte, CPU>>(Pattern.compile("^00[01]{3}100$"),
                    ALUExecution::executeINC_r8),
            new AbstractMap.SimpleEntry<Pattern, BiConsumer<Byte, CPU>>(Pattern.compile("^00[01]{3}101$"),
                    ALUExecution::executeDEC_r8),
            new AbstractMap.SimpleEntry<Pattern, BiConsumer<Byte, CPU>>(Pattern.compile("^00[01]{2}0011$"),
                    ALUExecution::executeINC_r16),
            new AbstractMap.SimpleEntry<Pattern, BiConsumer<Byte, CPU>>(Pattern.compile("^00[01]{2}1011$"),
                    ALUExecution::executeDEC_r16),
            new AbstractMap.SimpleEntry<Pattern, BiConsumer<Byte, CPU>>(Pattern.compile("^00[01]{3}111$"),
                    BitOpExecution::executeACCUMULATOR_FLAG_OPS)
    );

    private final Map<Pattern, BiConsumer<Byte, CPU>> REGEX_TO_CB_EXECUTION_MAP = Map.ofEntries(
            new AbstractMap.SimpleEntry<Pattern, BiConsumer<Byte, CPU>>(Pattern.compile("^00[01]{6}$"),
                    BitOpExecution::executeSHIFT_ROTATE),
            new AbstractMap.SimpleEntry<Pattern, BiConsumer<Byte, CPU>>(Pattern.compile("^01[01]{6}$"),
                    BitOpExecution::executeBIT_bit_r8),
            new AbstractMap.SimpleEntry<Pattern, BiConsumer<Byte, CPU>>(Pattern.compile("^10[01]{6}$"),
                    BitOpExecution::executeRES_bit_r8),
            new AbstractMap.SimpleEntry<Pattern, BiConsumer<Byte, CPU>>(Pattern.compile("^11[01]{6}$"),
                    BitOpExecution::executeSET_bit_r8)
    );

    public CPU () {
    }

    public void decodeInstruction(byte instruction) {
        String binaryString = GameBoyUtil.convertByteToBinaryString(instruction);
        for (Map.Entry<Pattern, BiConsumer<Byte, CPU>> mapEntry : REGEX_TO_EXECUTION_MAP.entrySet()) {
            if (mapEntry.getKey().matcher(binaryString).find()) {
               mapEntry.getValue().accept(instruction, this);
               return;
            }
        }
        throw new CPUException("Unknown instruction: " + binaryString);
    }

    // todo should be private later if not too much work, public right now for testing purposes
    // this might not even be the right location for this method (BitOpExecution instead?)
    public void decodeCBInstruction(byte instruction) {
        String binaryString = GameBoyUtil.convertByteToBinaryString(instruction);
        for (Map.Entry<Pattern, BiConsumer<Byte, CPU>> mapEntry : REGEX_TO_CB_EXECUTION_MAP.entrySet()) {
            if (mapEntry.getKey().matcher(binaryString).find()) {
                mapEntry.getValue().accept(instruction, this);
                return;
            }
        }
        throw new CPUException("Unknown instruction: " + binaryString);
    }

    public int getZeroFlag() {
        return GameBoyUtil.getBitFromPosInByte(rf, 7);
    }

    public void setZeroFlag(int z) {
        if (z == 1) {
            rf = (byte) (rf | 0b10000000);
        } else if (z == 0) {
            rf = (byte) (rf & 0b01111111);
        } else {
            throw new CPUException("zero flag out of bounds");
        }
    }

    public int getSubtractionFlag() {
        return GameBoyUtil.getBitFromPosInByte(rf, 6);
    }

    public void setSubtractionFlag(int n) {
        if (n == 1) {
            rf = (byte) (rf | 0b01000000);
        } else if (n == 0) {
            rf = (byte) (rf & 0b10111111);
        } else {
            throw new CPUException("subtraction flag out of bounds");
        }
    }

    public int getHalfCarryFlag() {
        return GameBoyUtil.getBitFromPosInByte(rf, 5);
    }

    public void setHalfCarryFlag(int h) {
        if (h == 1) {
            rf = (byte) (rf | 0b00100000);
        } else if (h == 0) {
            rf = (byte) (rf & 0b11011111);
        } else {
            throw new CPUException("half carry flag out of bounds");
        }
    }

    public int getCarryFlag() {
        return GameBoyUtil.getBitFromPosInByte(rf, 4);
    }

    public void setCarryFlag(int c) {
        if (c == 1) {
            rf = (byte) (rf | 0b00010000);
        } else if (c == 0) {
            rf = (byte) (rf & 0b11101111);
        } else {
            throw new CPUException("carry flag out of bounds");
        }
    }

    public short getRegisterAF() {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.put(ra);
        bb.put(rf);
        return bb.getShort(0);
    }

    public void setRegisterAF(short value) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.putShort(value);
        ra = bb.get(0);
        rf = bb.get(1);
    }

    public short getRegisterBC() {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.put(rb);
        bb.put(rc);
        return bb.getShort(0);
    }

    public void setRegisterBC(short value) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.putShort(value);
        rb = bb.get(0);
        rc = bb.get(1);
    }

    public short getRegisterDE() {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.put(rd);
        bb.put(re);
        return bb.getShort(0);
    }

    public void setRegisterDE(short value) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.putShort(value);
        rd = bb.get(0);
        re = bb.get(1);
    }

    public short getRegisterHL() {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.put(rh);
        bb.put(rl);
        return bb.getShort(0);
    }

    public void setRegisterHL(short value) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.putShort(value);
        rh = bb.get(0);
        rl = bb.get(1);
    }

    public short getStackPointer() {
        return sp;
    }

    public void setStackPointer(short value) {
        sp = value;
    }

    public byte getRa() {
        return ra;
    }

    public void setRa(byte ra) {
        this.ra = ra;
    }

    public byte getRb() {
        return rb;
    }

    public void setRb(byte rb) {
        this.rb = rb;
    }

    public byte getRc() {
        return rc;
    }

    public void setRc(byte rc) {
        this.rc = rc;
    }

    public byte getRd() {
        return rd;
    }

    public void setRd(byte rd) {
        this.rd = rd;
    }

    public byte getRe() {
        return re;
    }

    public void setRe(byte re) {
        this.re = re;
    }

    public byte getRf() {
        return rf;
    }

    public void setRf(byte rf) {
        this.rf = rf;
    }

    public byte getRh() {
        return rh;
    }

    public void setRh(byte rh) {
        this.rh = rh;
    }

    public byte getRl() {
        return rl;
    }

    public void setRl(byte rl) {
        this.rl = rl;
    }
}
