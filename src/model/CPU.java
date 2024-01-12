package model;

import Util.GameBoyUtil;
import exception.CPUException;

import java.nio.ByteBuffer;

public class CPU {

    // registers should be unsigned! but the underlying bits are the same anyways,
    // can just interpret them as signed or unsigned
    private byte ra,rb,rc,rd,re,rf,rh,rl;

    public CPU () {
    }

    // opcodes are always one byte long. If you need a constant, then you look at the byte(s) after
    // the opcode.


    // handle the alu instructions only first
    public void decodeInstruction(byte instruction) {
        if (GameBoyUtil.getBitFromPosInByte(instruction, 7) == 1) {
            if (GameBoyUtil.getBitFromPosInByte(instruction, 6) == 1) {

            } else {
                ALUExecution.executeALU_A_r8(instruction, this);
            }
        } else {

        }
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
