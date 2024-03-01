package model.memory;

import exception.MemoryException;
import util.Constants;
import util.GameBoyUtil;

import java.util.*;

/**
 * Class that represents the memory in the Game Boy.
 */
public class Memory {

    enum MBC {
        NO_MBC,
        MBC_1
    }
    private MBC mbc; // the type of mapper used on the cartridge
    private int romBanks; // the amount of ROM banks on the cartridge
    private int ramBanks; // the amount of RAM banks on the cartridge
    private byte[] fixedRom; // fixed ROM bank 0 from the cartridge (I think technically you can switch, but just ignore)
    private byte[] switchableRom; // potentially switchable ROM banks 1-N from cartridge
    private byte[] vram; // VRAM from the console
    private byte[] cartridgeRam; // potentially switchable RAM banks 0-N from cartridge
    private byte[] consoleRam; // RAM from the console (will have to split this into 2 potentially for GBC)
    private byte[] oam; // todo once you work on ppu
    private byte[] ioRegisters; // data for IO registers
    private byte[] hram; // data for high ram
    private byte ieRegister; // interrupt enable register

    // todo the 4 Cartridge registers below are probably specific to MBC1
    private boolean ramEnabled; // determines whether cartridge RAM is enabled
    private int currRomBank; // current ROM bank number in use for switchableRom section, represents either the 5, or 5 and 2 bit registers together
    private int currRamBank; // current RAM bank number for use in cartridgeRam section, represents the 2 bit register
    private boolean isRamMode; // true = RAM mode, false = ROM mode

    // todo end MBC1 specific =================

    // todo the timing related stuff probably belongs in CPU
    private short sysClock; // upper 8 bits are mapped to DIV register
    private int oldEnabled; // enable bit value at previous tick
    private boolean requestTimerInterrupt; // request timer interrupt due to TIMA overflow on next m-cycle

    public Memory() {
        byte[] cartridge = new byte[0x8000];
        getMemoryBankInfo(cartridge);
        memorySetup(cartridge);
    }

    public Memory(byte[] cartridge) {
        getMemoryBankInfo(cartridge);
        memorySetup(cartridge);
    }

    /**
     * gets MBC type, and number of ram and ram banks on cartridge
     */
    private void getMemoryBankInfo(byte[] cartridge) {
        final short MBC_ADDRESS = (short) 0x147;
        final short ROM_BANKS_ADDRESS = (short) 0x148;
        final short RAM_BANKS_ADDRESS = (short) 0x149;

        mbc = switch(cartridge[MBC_ADDRESS]) {
            case (byte) 0x1, (byte) 0x2, (byte) 0x3 ->  MBC.MBC_1;
            default -> MBC.NO_MBC; // 0x0 case
        };

        romBanks = switch(cartridge[ROM_BANKS_ADDRESS]) {
            case (byte) 0x1 -> 4;
            case (byte) 0x2 -> 8;
            case (byte) 0x3 -> 16;
            case (byte) 0x4 -> 32;
            case (byte) 0x5 -> 64;
            case (byte) 0x6 -> 128;
            case (byte) 0x7 -> 256;
            case (byte) 0x8 -> 512;
            default -> 2; // 0x0 case, no banking beyond the 2 already present in cartridge
        };

        ramBanks = switch(cartridge[RAM_BANKS_ADDRESS]) {
            case (byte) 0x2 -> 1;
            case (byte) 0x3 -> 4;
            case (byte) 0x4 -> 16;
            case (byte) 0x5 -> 8;
            default -> 0; // 0x0 case, no RAM present in cartridge
        };
    }

    private void memorySetup(byte[] cartridge) {
        sysClock = 0;
        oldEnabled = 0; // todo idk if this is right
        requestTimerInterrupt = false;

        ramEnabled = false;
        currRomBank = 1; // todo?
        currRamBank = 0; // todo?
        isRamMode = false;

        fixedRom = new byte[Constants.kb16];
        System.arraycopy(cartridge, 0, fixedRom, 0, Constants.kb16);

        switchableRom = new byte[Constants.kb16 * (romBanks - 1)];
        System.arraycopy(cartridge, Constants.kb16, switchableRom, 0, Constants.kb16 * (romBanks - 1));

        vram = new byte[Constants.kb8];
        cartridgeRam = new byte[Constants.kb8 * (ramBanks)];
        consoleRam = new byte[Constants.kb8];
        oam = new byte[0xA0];
        ioRegisters = new byte[0x80];
        hram = new byte[0x7F];
        ieRegister = (byte) 0;
    }

    public byte getByte(short address) {
        byte retVal;
        // todo zero extend short is needed, otherwise could interpret as negative!
        if (GameBoyUtil.zeroExtendShort(Constants.FIXED_ROM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.FIXED_ROM_END_ADDRESS)) {

            retVal = fixedRom[GameBoyUtil.zeroExtendShort(address)];

        } else if (GameBoyUtil.zeroExtendShort(Constants.SWITCHABLE_ROM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.SWITCHABLE_ROM_END_ADDRESS)) {

            retVal = switchableRom[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.SWITCHABLE_ROM_START_ADDRESS) +
                    (currRomBank - 1) * Constants.kb16];

        } else if (GameBoyUtil.zeroExtendShort(Constants.VRAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.VRAM_END_ADDRESS)) {

            retVal = vram[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.VRAM_START_ADDRESS)];

        } else if (GameBoyUtil.zeroExtendShort(Constants.CARTRIDGE_RAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.CARTRIDGE_RAM_END_ADDRESS)) {

            if (ramEnabled) {
                retVal = cartridgeRam[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.CARTRIDGE_RAM_START_ADDRESS) +
                        currRamBank * Constants.kb8];
            } else {
                retVal = (byte) 0xFF; // technically not guaranteed to return this
            }

        } else if (GameBoyUtil.zeroExtendShort(Constants.CONSOLE_RAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.CONSOLE_RAM_END_ADDRESS)) {

            retVal = consoleRam[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.CONSOLE_RAM_START_ADDRESS)];

        } else if (GameBoyUtil.zeroExtendShort(Constants.ECHO_RAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.ECHO_RAM_END_ADDRESS)) {

            // todo technically this is prohibited so I could return an exception
            retVal = consoleRam[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.ECHO_RAM_START_ADDRESS)];

        } else if (GameBoyUtil.zeroExtendShort(Constants.OAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.OAM_END_ADDRESS)) {

            // todo placeholder for now
            retVal = oam[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.OAM_START_ADDRESS)];

        } else if (GameBoyUtil.zeroExtendShort(Constants.IO_REGISTERS_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.IO_REGISTERS_END_ADDRESS)) {

            // todo for testing only
            if (address == (short) 0xFF44) {
                retVal =  (byte) 0x90;
            } else if (address == (short) 0xFF4D) { // todo prevent blargg test from trying to speed switch
                // todo according to https://www.reddit.com/r/EmuDev/comments/u1d1p6/gameboy_blargg_test_03_infinite_loop/
                // maybe I should make unused/unmapped IO registers read out 0xFF and be read only!
                retVal = (byte) 0xFF;
            } else {
                retVal = ioRegisters[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.IO_REGISTERS_START_ADDRESS)];
            }

        } else if (GameBoyUtil.zeroExtendShort(Constants.HRAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.HRAM_END_ADDRESS)) {

            retVal = hram[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.HRAM_START_ADDRESS)];

        } else if (Constants.IE_ADDRESS == address) {
            retVal = ieRegister;
        } else {
            // unusuable or out of bounds
            // todo I think unusable area actually returns an a value, but probably don't need to implement.
            throw new MemoryException("called getByte on not usable, or out of bounds address space: " +
                    GameBoyUtil.zeroExtendShort(address));
        }

        doMCycle();
        return retVal;
    }

    public void setByte(byte value, short address) {
        if (GameBoyUtil.zeroExtendShort(Constants.FIXED_ROM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.SWITCHABLE_ROM_END_ADDRESS)) {

            setMBC1Registers(value, address);

        } else if (GameBoyUtil.zeroExtendShort(Constants.VRAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.VRAM_END_ADDRESS)) {

            vram[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.VRAM_START_ADDRESS)] = value;

        } else if (GameBoyUtil.zeroExtendShort(Constants.CARTRIDGE_RAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.CARTRIDGE_RAM_END_ADDRESS)) {

            if (ramEnabled) {
                cartridgeRam[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.CARTRIDGE_RAM_START_ADDRESS) +
                        currRamBank * Constants.kb8] = value;
            }

        } else if (GameBoyUtil.zeroExtendShort(Constants.CONSOLE_RAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.CONSOLE_RAM_END_ADDRESS)) {

            consoleRam[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.CONSOLE_RAM_START_ADDRESS)] = value;

        } else if (GameBoyUtil.zeroExtendShort(Constants.ECHO_RAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.ECHO_RAM_END_ADDRESS)) {

            consoleRam[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.ECHO_RAM_START_ADDRESS)] = value;

        } else if (GameBoyUtil.zeroExtendShort(Constants.OAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.OAM_END_ADDRESS)) {

            // todo, placeholder for now
            oam[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.OAM_START_ADDRESS)] = value;

        } else if (GameBoyUtil.zeroExtendShort(Constants.IO_REGISTERS_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.IO_REGISTERS_END_ADDRESS)) {

            setByteIORegister(value, address);

        } else if (GameBoyUtil.zeroExtendShort(Constants.HRAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.HRAM_END_ADDRESS)) {

            hram[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.HRAM_START_ADDRESS)] = value;

        } else if (Constants.IE_ADDRESS == address) {
            ieRegister = value;
        } else {
            // unusuable or out of bounds
            throw new MemoryException("called setByte on not usable, or out of bounds address space: " +
                    GameBoyUtil.zeroExtendShort(address));
        }
        doMCycle();
        // todo for testing only
        printSerialOutput();
    }

    private void setMBC1Registers(byte value, short address) {
        if (mbc != MBC.MBC_1) {
            return;
        }

        if (GameBoyUtil.zeroExtendShort(Constants.RAM_ENABLE_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.RAM_ENABLE_END_ADDRESS)) {
            ramEnabled = GameBoyUtil.getNibble(true, value) == 0xA;
        } else if (GameBoyUtil.zeroExtendShort(Constants.ROM_BANK_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.ROM_BANK_END_ADDRESS)) {
            // todo I'm pretty sure this handles the 0x20, 0x40, and 0x60 translation, but make sure
            byte fiveBitValue = (byte) (value & 0b00011111);
            if (fiveBitValue == (byte) 0) {
                fiveBitValue = (byte) 1;
            }
            currRomBank = currRomBank & 0b1100000;
            currRomBank = currRomBank | fiveBitValue;
        } else if (GameBoyUtil.zeroExtendShort(Constants.RAM_BANK_OR_UPPER_ROM_BANK_BIT_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.RAM_BANK_OR_UPPER_ROM_BANK_BIT_END_ADDRESS)) {
            byte twoBitValue = (byte) (value & 0b00000011);
            if (isRamMode) {
                currRamBank = twoBitValue;
            } else {
                currRomBank = currRomBank & 0b0011111;
                currRomBank = currRomBank | (twoBitValue << 5);
            }
        } else {
            isRamMode = GameBoyUtil.getBitFromPosInByte(value, 0) == 1;
            if (!isRamMode) {
                currRamBank = 0;
            }
        }
    }

    private void setByteIORegister(byte value, short address) {
        if (address == Constants.DIV_ADDRESS) {
            // writing any value to DIV sets it to 0
            sysClock = (short) 0;
            ioRegisters[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.IO_REGISTERS_START_ADDRESS)] = (byte) 0;
        } else if (address == Constants.TIMA_ADDRESS) {
            // abort timer interrupt and TMA reload
            requestTimerInterrupt = false;
            ioRegisters[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.IO_REGISTERS_START_ADDRESS)] = value;
        } else {
            ioRegisters[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.IO_REGISTERS_START_ADDRESS)] = value;
        }
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
            setByteNoTick(getByteNoTick(Constants.TMA_ADDRESS), Constants.TIMA_ADDRESS); // reload TMA value
            // request timer interrupt
            byte b = getByteNoTick(Constants.IF_ADDRESS);
            b = GameBoyUtil.modifyBitOnPosInByte(b, Constants.TIMER, 1);
            setByteNoTick(b, Constants.IF_ADDRESS);

            // do this to guarantee TIMA won't be incremented this cycle
            oldEnabled = 0;
        }
        short oldSysClock = sysClock;
        sysClock = (short) (sysClock + 4); // increments once every T cycle, so 4 every M cycle
        // since tick is called in setByte, we need to use setByteNoTick
        setByteNoTick(GameBoyUtil.getByteFromShort(false, sysClock), Constants.DIV_ADDRESS);

        byte tima = getByteNoTick(Constants.TIMA_ADDRESS);
        byte tac = getByteNoTick(Constants.TAC_ADDRESS);

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
        setByteNoTick(tima, Constants.TIMA_ADDRESS);
    }


    /**
     * returns a queue containing all pending interrupts, i.e. bit positions
     * in both IE and IF that are 1. They can be removed in priority order, where
     * lowest bit position = highest priority.
     */
    public Queue<Integer> getPendingInterrupts() {
        Queue<Integer> pendingInterrupts = new PriorityQueue<>();
        byte andResult = (byte) (getByte(Constants.IE_ADDRESS) & getByte(Constants.IF_ADDRESS));
        for (int i = Constants.VBLANK; i <= Constants.JOYPAD; i++) {
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
        return ieRegister;
    }

    // todo these cause m-cycle to pass - is this OK??? - are these even being used????
    // the above IE and IF register getters and setter probably make these individual methods redundant!
    /**
     * @return bit 4 of the byte at address 0xFFFF.
     * This represents the joypad bit of the IE register.
     */
    public int getJoypadIE(){
        byte b = getByte(Constants.IE_ADDRESS);
        return GameBoyUtil.getBitFromPosInByte(b, Constants.JOYPAD);
    }

    /**
     * Sets bit 4 of the byte at address 0xFFFF to jp.
     * This represents the joypad bit of the IE register.
     */
    public void setJoypadIE(int jp) {
        byte b = getByte(Constants.IE_ADDRESS);
        b = GameBoyUtil.modifyBitOnPosInByte(b, Constants.JOYPAD, jp);
        setByte(b, Constants.IE_ADDRESS);
    }

    /**
     * @return bit 3 of the byte at address 0xFFFF.
     * This represents the serial bit of the IE register.
     */
    public int getSerialIE(){
        byte b = getByte(Constants.IE_ADDRESS);
        return GameBoyUtil.getBitFromPosInByte(b, Constants.SERIAL);
    }

    /**
     * Sets bit 3 of the byte at address 0xFFFF to serial.
     * This represents the serial bit of the IE register.
     */
    public void setSerialIE(int serial) {
        byte b = getByte(Constants.IE_ADDRESS);
        b = GameBoyUtil.modifyBitOnPosInByte(b, Constants.SERIAL, serial);
        setByte(b, Constants.IE_ADDRESS);
    }

    /**
     * @return bit 2 of the byte at address 0xFFFF.
     * This represents the timer bit of the IE register.
     */
    public int getTimerIE(){
        byte b = getByte(Constants.IE_ADDRESS);
        return GameBoyUtil.getBitFromPosInByte(b, Constants.TIMER);
    }

    /**
     * Sets bit 2 of the byte at address 0xFFFF to timer.
     * This represents the timer bit of the IE register.
     */
    public void setTimerIE(int timer) {
        byte b = getByte(Constants.IE_ADDRESS);
        b = GameBoyUtil.modifyBitOnPosInByte(b, Constants.TIMER, timer);
        setByte(b, Constants.IE_ADDRESS);
    }

    /**
     * @return bit 1 of the byte at address 0xFFFF.
     * This represents the LCD bit of the IE register.
     */
    public int getLcdIE(){
        byte b = getByte(Constants.IE_ADDRESS);
        return GameBoyUtil.getBitFromPosInByte(b, Constants.LCD);
    }

    /**
     * Sets bit 1 of the byte at address 0xFFFF to lcd.
     * This represents the LCD bit of the IE register.
     */
    public void setLcdIE(int lcd) {
        byte b = getByte(Constants.IE_ADDRESS);
        b = GameBoyUtil.modifyBitOnPosInByte(b, Constants.LCD, lcd);
        setByte(b, Constants.IE_ADDRESS);
    }

    /**
     * @return bit 0 of the byte at address 0xFFFF.
     * This represents the VBlank bit of the IE register.
     */
    public int getVBlankIE(){
        byte b = getByte(Constants.IE_ADDRESS);
        return GameBoyUtil.getBitFromPosInByte(b, Constants.VBLANK);
    }

    /**
     * Sets bit 0 of the byte at address 0xFFFF to vblank.
     * This represents the VBlank bit of the IE register.
     */
    public void setVBlankIE(int vblank) {
        byte b = getByte(Constants.IE_ADDRESS);
        b = GameBoyUtil.modifyBitOnPosInByte(b, Constants.VBLANK, vblank);
        setByte(b, Constants.IE_ADDRESS);
    }

    /**
     * @return bit 4 of the byte at address 0xFF0F.
     * This represents the joypad bit of the IF register.
     */
    public int getJoypadIF(){
        byte b = getByte(Constants.IF_ADDRESS);
        return GameBoyUtil.getBitFromPosInByte(b, Constants.JOYPAD);
    }

    /**
     * Sets bit 4 of the byte at address 0xFF0F to jp.
     * This represents the joypad bit of the IF register.
     */
    public void setJoypadIF(int jp) {
        byte b = getByte(Constants.IF_ADDRESS);
        b = GameBoyUtil.modifyBitOnPosInByte(b, Constants.JOYPAD, jp);
        setByte(b, Constants.IF_ADDRESS);
    }

    /**
     * @return bit 3 of the byte at address 0xFF0F.
     * This represents the serial bit of the IE register.
     */
    public int getSerialIF(){
        byte b = getByte(Constants.IF_ADDRESS);
        return GameBoyUtil.getBitFromPosInByte(b, Constants.SERIAL);
    }

    /**
     * Sets bit 3 of the byte at address 0xFF0F to serial.
     * This represents the serial bit of the IF register.
     */
    public void setSerialIF(int serial) {
        byte b = getByte(Constants.IF_ADDRESS);
        b = GameBoyUtil.modifyBitOnPosInByte(b, Constants.SERIAL, serial);
        setByte(b, Constants.IF_ADDRESS);
    }

    /**
     * @return bit 2 of the byte at address 0xFF0F.
     * This represents the timer bit of the IF register.
     */
    public int getTimerIF(){
        byte b = getByte(Constants.IF_ADDRESS);
        return GameBoyUtil.getBitFromPosInByte(b, Constants.TIMER);
    }

    /**
     * Sets bit 2 of the byte at address 0xFF0F to timer.
     * This represents the timer bit of the IF register.
     */
    public void setTimerIF(int timer) {
        byte b = getByte(Constants.IF_ADDRESS);
        b = GameBoyUtil.modifyBitOnPosInByte(b, Constants.TIMER, timer);
        setByte(b, Constants.IF_ADDRESS);
    }

    /**
     * @return bit 1 of the byte at address 0xFF0F.
     * This represents the LCD bit of the IF register.
     */
    public int getLcdIF(){
        byte b = getByte(Constants.IF_ADDRESS);
        return GameBoyUtil.getBitFromPosInByte(b, Constants.LCD);
    }

    /**
     * Sets bit 1 of the byte at address 0xFF0F to lcd.
     * This represents the LCD bit of the IF register.
     */
    public void setLcdIF(int lcd) {
        byte b = getByte(Constants.IF_ADDRESS);
        b = GameBoyUtil.modifyBitOnPosInByte(b, Constants.LCD, lcd);
        setByte(b, Constants.IF_ADDRESS);
    }

    /**
     * @return bit 0 of the byte at address 0xFF0F.
     * This represents the VBlank bit of the IF register.
     */
    public int getVBlankIF(){
        byte b = getByte(Constants.IF_ADDRESS);
        return GameBoyUtil.getBitFromPosInByte(b, Constants.VBLANK);
    }

    /**
     * Sets bit 0 of the byte at address 0xFF0F to vblank.
     * This represents the VBlank bit of the IF register.
     */
    public void setVBlankIF(int vblank) {
        byte b = getByte(Constants.IF_ADDRESS);
        b = GameBoyUtil.modifyBitOnPosInByte(b, Constants.VBLANK, vblank);
        setByte(b, Constants.IF_ADDRESS);
    }

    // todo for testing purposes, probably remove later
    public void printSerialOutput() {
        if (getByteNoTick((short) 0xff02) == (byte) 0x81) {
            byte b = getByteNoTick((short) 0xff01);
            char c = (char) GameBoyUtil.zeroExtendByte(b);
            System.out.println(c);
            setByteNoTick((byte) 0, (short) 0xff02);
        }
    }

    // todo for testing purposes, probably remove later (some of my actual methods use this, so need to come up with better way to do this)
    public byte getByteNoTick(short address) {
        byte retVal;
        if (GameBoyUtil.zeroExtendShort(Constants.FIXED_ROM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.FIXED_ROM_END_ADDRESS)) {

            retVal = fixedRom[GameBoyUtil.zeroExtendShort(address)];

        } else if (GameBoyUtil.zeroExtendShort(Constants.SWITCHABLE_ROM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.SWITCHABLE_ROM_END_ADDRESS)) {

            retVal = switchableRom[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.SWITCHABLE_ROM_START_ADDRESS) +
                    (currRomBank - 1) * Constants.kb16];

        } else if (GameBoyUtil.zeroExtendShort(Constants.VRAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.VRAM_END_ADDRESS)) {

            retVal = vram[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.VRAM_START_ADDRESS)];

        } else if (GameBoyUtil.zeroExtendShort(Constants.CARTRIDGE_RAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.CARTRIDGE_RAM_END_ADDRESS)) {

            if (ramEnabled) {
                retVal = cartridgeRam[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.CARTRIDGE_RAM_START_ADDRESS) +
                        currRamBank * Constants.kb8];
            } else {
                retVal = (byte) 0xFF; // technically not guaranteed to return this
            }

        } else if (GameBoyUtil.zeroExtendShort(Constants.CONSOLE_RAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.CONSOLE_RAM_END_ADDRESS)) {

            retVal = consoleRam[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.CONSOLE_RAM_START_ADDRESS)];

        } else if (GameBoyUtil.zeroExtendShort(Constants.ECHO_RAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.ECHO_RAM_END_ADDRESS)) {

            // todo technically this is prohibited so I could return an exception
            retVal = consoleRam[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.ECHO_RAM_START_ADDRESS)];

        } else if (GameBoyUtil.zeroExtendShort(Constants.OAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.OAM_END_ADDRESS)) {

            // todo placeholder for now
            retVal = oam[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.OAM_START_ADDRESS)];

        } else if (GameBoyUtil.zeroExtendShort(Constants.IO_REGISTERS_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.IO_REGISTERS_END_ADDRESS)) {

            // todo for testing only
            if (address == (short) 0xFF44) {
                retVal =  (byte) 0x90;
            } else if (address == (short) 0xFF4D) { // todo prevent blargg test from trying to speed switch
                // todo according to https://www.reddit.com/r/EmuDev/comments/u1d1p6/gameboy_blargg_test_03_infinite_loop/
                // maybe I should make unused/unmapped IO registers read out 0xFF and be read only!
                retVal = (byte) 0xFF;
            } else {
                retVal = ioRegisters[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.IO_REGISTERS_START_ADDRESS)];
            }

        } else if (GameBoyUtil.zeroExtendShort(Constants.HRAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.HRAM_END_ADDRESS)) {

            retVal = hram[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.HRAM_START_ADDRESS)];

        } else if (Constants.IE_ADDRESS == address) {
            retVal = ieRegister;
        } else {
            // unusuable or out of bounds
            // todo I think unusable area actually returns an a value, but probably don't need to implement.
            throw new MemoryException("called getByte on not usable, or out of bounds address space: " +
                    GameBoyUtil.zeroExtendShort(address));
        }

        return retVal;
    }

    // todo for testing purposes, probably remove later, need to come up with better way to do this
    public void setByteNoTick(byte value, short address) {
        if (GameBoyUtil.zeroExtendShort(Constants.FIXED_ROM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.SWITCHABLE_ROM_END_ADDRESS)) {

            setMBC1Registers(value, address);

        } else if (GameBoyUtil.zeroExtendShort(Constants.VRAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.VRAM_END_ADDRESS)) {

            vram[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.VRAM_START_ADDRESS)] = value;

        } else if (GameBoyUtil.zeroExtendShort(Constants.CARTRIDGE_RAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.CARTRIDGE_RAM_END_ADDRESS)) {

            if (ramEnabled) {
                cartridgeRam[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.CARTRIDGE_RAM_START_ADDRESS) +
                        currRamBank * Constants.kb8] = value;
            }

        } else if (GameBoyUtil.zeroExtendShort(Constants.CONSOLE_RAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.CONSOLE_RAM_END_ADDRESS)) {

            consoleRam[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.CONSOLE_RAM_START_ADDRESS)] = value;

        } else if (GameBoyUtil.zeroExtendShort(Constants.ECHO_RAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.ECHO_RAM_END_ADDRESS)) {

            consoleRam[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.ECHO_RAM_START_ADDRESS)] = value;

        } else if (GameBoyUtil.zeroExtendShort(Constants.OAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.OAM_END_ADDRESS)) {

            // todo, placeholder for now
            oam[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.OAM_START_ADDRESS)] = value;

        } else if (GameBoyUtil.zeroExtendShort(Constants.IO_REGISTERS_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.IO_REGISTERS_END_ADDRESS)) {

            // todo setByteNoTick method is only used by doMCycle().
            // Since setByteIORegister has special behavior for stuff like DIV and TIMA, which we don't want,
            // we can't use that and just use this instead.
            ioRegisters[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.IO_REGISTERS_START_ADDRESS)] = value;

        } else if (GameBoyUtil.zeroExtendShort(Constants.HRAM_START_ADDRESS) <= GameBoyUtil.zeroExtendShort(address) &&
                GameBoyUtil.zeroExtendShort(address) <= GameBoyUtil.zeroExtendShort(Constants.HRAM_END_ADDRESS)) {

            hram[GameBoyUtil.zeroExtendShort(address) - GameBoyUtil.zeroExtendShort(Constants.HRAM_START_ADDRESS)] = value;

        } else if (Constants.IE_ADDRESS == address) {
            ieRegister = value;
        } else {
            // unusuable or out of bounds
            throw new MemoryException("called setByte on not usable, or out of bounds address space: " +
                    GameBoyUtil.zeroExtendShort(address));
        }
        // todo for testing only
        printSerialOutput();
    }
}
