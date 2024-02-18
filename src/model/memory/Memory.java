package model.memory;

import util.GameBoyUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that represents the memory in the Game Boy.
 */
public class Memory {
    private byte[] memory;
    public static final short IE_ADDRESS = (short) 0xFFFF;
    public static final short IF_ADDRESS = (short) 0xFF0F;
    public static final int JOYPAD = 4;
    public static final int SERIAL = 3;
    public static final int TIMER = 2;
    public static final int LCD = 1;
    public static final int VBLANK = 0;

    // todo technically part of the memory is unusable or echoed. probably not bother with that for now.
    public Memory() {
        memory = new byte[0x10000];
        Arrays.fill(memory, (byte) 0);
        // todo what to init IE and IF registers as???
    }

    public byte getByte(short address) {
        return memory[GameBoyUtil.zeroExtendShort(address)];
    }

    public void setByte(byte value, short address) {
        memory[GameBoyUtil.zeroExtendShort(address)] = value;
    }

    /**
     * returns a set containing all pending interrupts, i.e. bit positions
     * in both IE and IF that are 1.
     */
    public Set<Integer> getPendingInterrupts() {
        Set<Integer> pendingInterrupts = new HashSet<>();
        byte andResult = (byte) (getByte(IE_ADDRESS) & getByte(IF_ADDRESS));
        for (int i = VBLANK; i <= JOYPAD; i++) {
            if (GameBoyUtil.getBitFromPosInByte(andResult, i) == 1) {
                pendingInterrupts.add(i);
            }
        }
        return pendingInterrupts;
    }

    /**
     * @return bit 4 of the byte at address 0xFFFF.
     * This represents the joypad bit of the IE register.
     */
    public int getJoypadIE(){
        byte b = getByte(IE_ADDRESS);
        return GameBoyUtil.getBitFromPosInByte(b, JOYPAD);
    }

    /**
     * Sets bit 4 of the byte at address 0xFFFF to jp.
     * This represents the joypad bit of the IE register.
     */
    public void setJoypadIE(int jp) {
        byte b = getByte(IE_ADDRESS);
        b = GameBoyUtil.modifyBitOnPosInByte(b,JOYPAD, jp);
        setByte(b, IE_ADDRESS);
    }

    /**
     * @return bit 3 of the byte at address 0xFFFF.
     * This represents the serial bit of the IE register.
     */
    public int getSerialIE(){
        byte b = getByte(IE_ADDRESS);
        return GameBoyUtil.getBitFromPosInByte(b, SERIAL);
    }

    /**
     * Sets bit 3 of the byte at address 0xFFFF to serial.
     * This represents the serial bit of the IE register.
     */
    public void setSerialIE(int serial) {
        byte b = getByte(IE_ADDRESS);
        b = GameBoyUtil.modifyBitOnPosInByte(b,SERIAL, serial);
        setByte(b, IE_ADDRESS);
    }

    /**
     * @return bit 2 of the byte at address 0xFFFF.
     * This represents the timer bit of the IE register.
     */
    public int getTimerIE(){
        byte b = getByte(IE_ADDRESS);
        return GameBoyUtil.getBitFromPosInByte(b, TIMER);
    }

    /**
     * Sets bit 2 of the byte at address 0xFFFF to timer.
     * This represents the timer bit of the IE register.
     */
    public void setTimerIE(int timer) {
        byte b = getByte(IE_ADDRESS);
        b = GameBoyUtil.modifyBitOnPosInByte(b,TIMER, timer);
        setByte(b, IE_ADDRESS);
    }

    /**
     * @return bit 1 of the byte at address 0xFFFF.
     * This represents the LCD bit of the IE register.
     */
    public int getLcdIE(){
        byte b = getByte(IE_ADDRESS);
        return GameBoyUtil.getBitFromPosInByte(b, LCD);
    }

    /**
     * Sets bit 1 of the byte at address 0xFFFF to lcd.
     * This represents the LCD bit of the IE register.
     */
    public void setLcdIE(int lcd) {
        byte b = getByte(IE_ADDRESS);
        b = GameBoyUtil.modifyBitOnPosInByte(b,LCD, lcd);
        setByte(b, IE_ADDRESS);
    }

    /**
     * @return bit 0 of the byte at address 0xFFFF.
     * This represents the VBlank bit of the IE register.
     */
    public int getVBlankIE(){
        byte b = getByte(IE_ADDRESS);
        return GameBoyUtil.getBitFromPosInByte(b, VBLANK);
    }

    /**
     * Sets bit 0 of the byte at address 0xFFFF to vblank.
     * This represents the VBlank bit of the IE register.
     */
    public void setVBlankIE(int vblank) {
        byte b = getByte(IE_ADDRESS);
        b = GameBoyUtil.modifyBitOnPosInByte(b,VBLANK, vblank);
        setByte(b, IE_ADDRESS);
    }

    /**
     * @return bit 4 of the byte at address 0xFF0F.
     * This represents the joypad bit of the IF register.
     */
    public int getJoypadIF(){
        byte b = getByte(IF_ADDRESS);
        return GameBoyUtil.getBitFromPosInByte(b, JOYPAD);
    }

    /**
     * Sets bit 4 of the byte at address 0xFF0F to jp.
     * This represents the joypad bit of the IF register.
     */
    public void setJoypadIF(int jp) {
        byte b = getByte(IF_ADDRESS);
        b = GameBoyUtil.modifyBitOnPosInByte(b,JOYPAD, jp);
        setByte(b, IF_ADDRESS);
    }

    /**
     * @return bit 3 of the byte at address 0xFF0F.
     * This represents the serial bit of the IE register.
     */
    public int getSerialIF(){
        byte b = getByte(IF_ADDRESS);
        return GameBoyUtil.getBitFromPosInByte(b, SERIAL);
    }

    /**
     * Sets bit 3 of the byte at address 0xFF0F to serial.
     * This represents the serial bit of the IF register.
     */
    public void setSerialIF(int serial) {
        byte b = getByte(IF_ADDRESS);
        b = GameBoyUtil.modifyBitOnPosInByte(b,SERIAL, serial);
        setByte(b, IF_ADDRESS);
    }

    /**
     * @return bit 2 of the byte at address 0xFF0F.
     * This represents the timer bit of the IF register.
     */
    public int getTimerIF(){
        byte b = getByte(IF_ADDRESS);
        return GameBoyUtil.getBitFromPosInByte(b, TIMER);
    }

    /**
     * Sets bit 2 of the byte at address 0xFF0F to timer.
     * This represents the timer bit of the IF register.
     */
    public void setTimerIF(int timer) {
        byte b = getByte(IF_ADDRESS);
        b = GameBoyUtil.modifyBitOnPosInByte(b,TIMER, timer);
        setByte(b, IF_ADDRESS);
    }

    /**
     * @return bit 1 of the byte at address 0xFF0F.
     * This represents the LCD bit of the IF register.
     */
    public int getLcdIF(){
        byte b = getByte(IF_ADDRESS);
        return GameBoyUtil.getBitFromPosInByte(b, LCD);
    }

    /**
     * Sets bit 1 of the byte at address 0xFF0F to lcd.
     * This represents the LCD bit of the IF register.
     */
    public void setLcdIF(int lcd) {
        byte b = getByte(IF_ADDRESS);
        b = GameBoyUtil.modifyBitOnPosInByte(b,LCD, lcd);
        setByte(b, IF_ADDRESS);
    }

    /**
     * @return bit 0 of the byte at address 0xFF0F.
     * This represents the VBlank bit of the IF register.
     */
    public int getVBlankIF(){
        byte b = getByte(IF_ADDRESS);
        return GameBoyUtil.getBitFromPosInByte(b, VBLANK);
    }

    /**
     * Sets bit 0 of the byte at address 0xFF0F to vblank.
     * This represents the VBlank bit of the IF register.
     */
    public void setVBlankIF(int vblank) {
        byte b = getByte(IF_ADDRESS);
        b = GameBoyUtil.modifyBitOnPosInByte(b,VBLANK, vblank);
        setByte(b, IF_ADDRESS);
    }
}
