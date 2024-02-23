package model.memory;

import util.GameBoyUtil;

import java.util.*;

/**
 * Class that represents the memory in the Game Boy.
 */
public class Memory {
    private byte[] memory;

    // todo the timing related stuff probably belongs in CPU
    private short sysClock; // upper 8 bits are mapped to DIV register
    private int oldEnabled; // enable bit value at previous tick
    private boolean requestTimerInterrupt; // request timer interrupt due to TIMA overflow on next m-cycle
    public static final short DIV_ADDRESS = (short) 0xFF04;
    public static final short TIMA_ADDRESS = (short) 0xFF05;
    public static final short TMA_ADDRESS = (short) 0xFF06;
    public static final short TAC_ADDRESS = (short) 0xFF07;
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
        sysClock = 0;
        oldEnabled = 0; // todo idk if this is right
        requestTimerInterrupt = false;
    }

    public byte getByte(short address) {
        // todo for testing only
        if (address == (short) 0xFF44) {
            return (byte) 0x90;
        }
        doMCycle();
        return memory[GameBoyUtil.zeroExtendShort(address)];
    }

    // todo there are other sections of memory that have special behavior too
    public void setByte(byte value, short address) {
        if (address == DIV_ADDRESS) {
            // writing any value to DIV sets it to 0
            sysClock = (short) 0;
            memory[GameBoyUtil.zeroExtendShort(address)] = (byte) 0;
        } else if (address == TIMA_ADDRESS) {
            // abort timer interrupt and TMA reload
            requestTimerInterrupt = false;
            memory[GameBoyUtil.zeroExtendShort(address)] = value;
        } else {
            memory[GameBoyUtil.zeroExtendShort(address)] = value;
        }
        doMCycle();
        // todo for testing only
        printSerialOutput();
    }

    // todo this really belongs in CPU, but that may require refactoring. actually maybe not, idk
    // todo I'm assuming this is the only way to progress "time", aka progress m-cycles
    // todo at the last paragraph of "TIMA overflow behavior" there are specific T cycle timings. Ignoring those.

    // TODO THIS NEEDS TO BE SPLIT INTO SEPARATE METHODS!!!
    /**
     * Causes 1 M-cycle worth of time to pass. This should be called
     * during every memory operation. This also increments timers as needed.
     */
    public void doMCycle() {
        if (requestTimerInterrupt) {
            requestTimerInterrupt = false;
            memory[GameBoyUtil.zeroExtendShort(TIMA_ADDRESS)] = memory[GameBoyUtil.zeroExtendShort(TMA_ADDRESS)]; // reload TMA value
            // request timer interrupt
            byte b = getByte(IF_ADDRESS);
            b = GameBoyUtil.modifyBitOnPosInByte(b,TIMER, 1);
            setByte(b, IF_ADDRESS);

            // do this to guarantee TIMA won't be incremented this cycle
            oldEnabled = 0;
        }
        short oldSysClock = sysClock;
        sysClock = (short) (sysClock + 4); // increments once every T cycle, so 4 every M cycle
        // since tick is called in getByte, we need to use memory array directly instead.
        memory[GameBoyUtil.zeroExtendShort(DIV_ADDRESS)] = GameBoyUtil.getByteFromShort(false, sysClock);

        byte tima =  memory[GameBoyUtil.zeroExtendShort(TIMA_ADDRESS)];
        byte tac =  memory[GameBoyUtil.zeroExtendShort(TAC_ADDRESS)];

        int enable = GameBoyUtil.getBitFromPosInByte(tac, 2);
        int clockSelect = GameBoyUtil.get2BitValue(
                GameBoyUtil.getBitFromPosInByte(tac, 1),
                GameBoyUtil.getBitFromPosInByte(tac, 0));
        int bitPos = switch (clockSelect) {
            case 0 -> 9; // every 256 m-cycles
            case 1 -> 3; // every 4 m-cycles
            case 2 -> 5; // every 16 m-cycles
            default -> 7; // evert 64 m-cycles
        };

        int oldAndResult = GameBoyUtil.getBitFromPosInShort(oldSysClock, bitPos) & oldEnabled;
        int newAndResult = GameBoyUtil.getBitFromPosInShort(sysClock, bitPos) & enable;

        // only increment if falling edge is detected
        if (oldAndResult == 1 && newAndResult == 0) {
            if (tima == (byte) 0xFF) {
                requestTimerInterrupt = true;
            }
            tima = (byte) (tima + 1);
        }

        oldEnabled = enable;
        memory[GameBoyUtil.zeroExtendShort(TIMA_ADDRESS)] = tima;
    }


    /**
     * returns a queue containing all pending interrupts, i.e. bit positions
     * in both IE and IF that are 1. They can be removed in priority order, where
     * lowest bit position = highest priority.
     */
    public Queue<Integer> getPendingInterrupts() {
        Queue<Integer> pendingInterrupts = new PriorityQueue<>();
        byte andResult = (byte) (getByte(IE_ADDRESS) & getByte(IF_ADDRESS));
        for (int i = VBLANK; i <= JOYPAD; i++) {
            if (GameBoyUtil.getBitFromPosInByte(andResult, i) == 1) {
                pendingInterrupts.add(i);
            }
        }
        return pendingInterrupts;
    }

    /**
     * @return the IE register. This does NOT consume an m-cycle.
     */
    public byte getIERegister() {
        return memory[GameBoyUtil.zeroExtendShort(IE_ADDRESS)];
    }

    // todo these cause m-cycle to pass - is this OK??? - are these even being used????
    // the above IE and IF register getters and setter probably make these individual methods redundant!
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

    // todo for testing purposes, replace with a better boot rom loader later
    public void loadBootRom(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            memory[i] = bytes[i];
        }
    }

    // todo for testing purposes, replace with something better later
    public void loadGameRom(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            memory[i] = bytes[i];
        }
    }

    // todo for testing purposes, probably remove later
    public void printSerialOutput() {
        if (memory[GameBoyUtil.zeroExtendShort((short) 0xff02)] == (byte) 0x81) {
            byte b = memory[GameBoyUtil.zeroExtendShort((short) 0xff01)];
            char c = (char) GameBoyUtil.zeroExtendByte(b);
            System.out.println(c);
            memory[GameBoyUtil.zeroExtendShort((short) 0xff02)] = (byte) 0;
        }
    }

    // todo for testing purposes, probably remove later
    public byte getByteNoTick(short address) {
        // todo for testing purposes only
        if (address == (short) 0xFF44) {
            return (byte) 0x90;
        }
        return memory[GameBoyUtil.zeroExtendShort(address)];
    }
}
