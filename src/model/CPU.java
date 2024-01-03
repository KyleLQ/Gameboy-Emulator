package model;

import java.nio.ByteBuffer;

public class CPU {
    private byte ra,rb,rc,rd,re,rf,rh,rl;

    public CPU () {

    }

    public boolean getZeroFlag() {
        return ((rf & 0b10000000) >> 7) == 1;
    }

    public void setZeroFlag(boolean z) {
        if (z) {
            rf = (byte) (rf | 0b10000000);
        } else {
            rf = (byte) (rf & 0b01111111);
        }
    }

    public boolean getSubtractionFlag() {
        return ((rf & 0b01000000) >> 6) == 1;
    }

    public void setSubtractionFlag(boolean n) {
        if (n) {
            rf = (byte) (rf | 0b01000000);
        } else {
            rf = (byte) (rf & 0b10111111);
        }
    }

    public boolean getHalfCarryFlag() {
        return ((rf & 0b00100000) >> 5) == 1;
    }

    public void setHalfCarryFlag(boolean h) {
        if (h) {
            rf = (byte) (rf | 0b00100000);
        } else {
            rf = (byte) (rf & 0b11011111);
        }
    }

    public boolean getCarryFlag() {
        return ((rf & 0b00010000) >> 4) == 1;
    }

    public void setCarryFlag(boolean c) {
        if (c) {
            rf = (byte) (rf | 0b00010000);
        } else {
            rf = (byte) (rf & 0b11101111);
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
