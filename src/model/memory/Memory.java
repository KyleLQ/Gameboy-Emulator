package model.memory;

import exception.MemoryException;
import util.Constants;
import util.GBUtil;

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
        if (GBUtil.zeroExtend(Constants.FIXED_ROM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.FIXED_ROM_END)) {

            retVal = fixedRom[GBUtil.zeroExtend(address)];

        } else if (GBUtil.zeroExtend(Constants.SWITCHABLE_ROM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.SWITCHABLE_ROM_END)) {

            retVal = switchableRom[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.SWITCHABLE_ROM_START) +
                    (currRomBank - 1) * Constants.kb16];

        } else if (GBUtil.zeroExtend(Constants.VRAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.VRAM_END)) {

            retVal = vram[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.VRAM_START)];

        } else if (GBUtil.zeroExtend(Constants.CARTRIDGE_RAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.CARTRIDGE_RAM_END)) {

            if (ramEnabled) {
                retVal = cartridgeRam[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.CARTRIDGE_RAM_START) +
                        currRamBank * Constants.kb8];
            } else {
                retVal = (byte) 0xFF; // technically not guaranteed to return this
            }

        } else if (GBUtil.zeroExtend(Constants.CONSOLE_RAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.CONSOLE_RAM_END)) {

            retVal = consoleRam[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.CONSOLE_RAM_START)];

        } else if (GBUtil.zeroExtend(Constants.ECHO_RAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.ECHO_RAM_END)) {

            // todo technically this is prohibited so I could return an exception
            retVal = consoleRam[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.ECHO_RAM_START)];

        } else if (GBUtil.zeroExtend(Constants.OAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.OAM_END)) {

            // todo placeholder for now
            retVal = oam[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.OAM_START)];

        } else if (GBUtil.zeroExtend(Constants.IO_REGISTERS_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.IO_REGISTERS_END)) {

            // todo for testing only
            if (address == (short) 0xFF44) {
                retVal =  (byte) 0x90;
            } else if (address == (short) 0xFF4D) { // todo prevent blargg test from trying to speed switch
                // todo according to https://www.reddit.com/r/EmuDev/comments/u1d1p6/gameboy_blargg_test_03_infinite_loop/
                // maybe I should make unused/unmapped IO registers read out 0xFF and be read only!
                retVal = (byte) 0xFF;
            } else {
                retVal = ioRegisters[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.IO_REGISTERS_START)];
            }

        } else if (GBUtil.zeroExtend(Constants.HRAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.HRAM_END)) {

            retVal = hram[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.HRAM_START)];

        } else if (Constants.IE_ADDRESS == address) {
            retVal = ieRegister;
        } else {
            // unusuable or out of bounds
            // todo I think unusable area actually returns an a value, but probably don't need to implement.
            throw new MemoryException("called getByte on not usable, or out of bounds address space: " +
                    GBUtil.zeroExtend(address));
        }

        doMCycle();
        return retVal;
    }

    public void setByte(byte value, short address) {
        if (GBUtil.zeroExtend(Constants.FIXED_ROM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.SWITCHABLE_ROM_END)) {

            setMBC1Registers(value, address);

        } else if (GBUtil.zeroExtend(Constants.VRAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.VRAM_END)) {

            vram[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.VRAM_START)] = value;

        } else if (GBUtil.zeroExtend(Constants.CARTRIDGE_RAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.CARTRIDGE_RAM_END)) {

            if (ramEnabled) {
                cartridgeRam[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.CARTRIDGE_RAM_START) +
                        currRamBank * Constants.kb8] = value;
            }

        } else if (GBUtil.zeroExtend(Constants.CONSOLE_RAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.CONSOLE_RAM_END)) {

            consoleRam[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.CONSOLE_RAM_START)] = value;

        } else if (GBUtil.zeroExtend(Constants.ECHO_RAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.ECHO_RAM_END)) {

            consoleRam[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.ECHO_RAM_START)] = value;

        } else if (GBUtil.zeroExtend(Constants.OAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.OAM_END)) {

            // todo, placeholder for now
            oam[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.OAM_START)] = value;

        } else if (GBUtil.zeroExtend(Constants.IO_REGISTERS_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.IO_REGISTERS_END)) {

            setByteIORegister(value, address);

        } else if (GBUtil.zeroExtend(Constants.HRAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.HRAM_END)) {

            hram[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.HRAM_START)] = value;

        } else if (Constants.IE_ADDRESS == address) {
            ieRegister = value;
        } else {
            // unusuable or out of bounds
            throw new MemoryException("called setByte on not usable, or out of bounds address space: " +
                    GBUtil.zeroExtend(address));
        }
        doMCycle();
        // todo for testing only
        printSerialOutput();
    }

    private void setMBC1Registers(byte value, short address) {
        if (mbc != MBC.MBC_1) {
            return;
        }

        if (GBUtil.zeroExtend(Constants.RAM_ENABLE_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.RAM_ENABLE_END)) {
            ramEnabled = GBUtil.getNibble(true, value) == 0xA;
        } else if (GBUtil.zeroExtend(Constants.ROM_BANK_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.ROM_BANK_END)) {
            // todo I'm pretty sure this handles the 0x20, 0x40, and 0x60 translation, but make sure
            byte fiveBitValue = (byte) (value & 0b00011111);
            if (fiveBitValue == (byte) 0) {
                fiveBitValue = (byte) 1;
            }
            currRomBank = currRomBank & 0b1100000;
            currRomBank = currRomBank | fiveBitValue;
        } else if (GBUtil.zeroExtend(Constants.RAM_BANK_OR_UPPER_ROM_BANK_BIT_START_ADDRESS) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.RAM_BANK_OR_UPPER_ROM_BANK_BIT_END_ADDRESS)) {
            byte twoBitValue = (byte) (value & 0b00000011);
            if (isRamMode) {
                currRamBank = twoBitValue;
            } else {
                currRomBank = currRomBank & 0b0011111;
                currRomBank = currRomBank | (twoBitValue << 5);
            }
        } else {
            isRamMode = GBUtil.getBit(value, 0) == 1;
            if (!isRamMode) {
                currRamBank = 0;
            }
        }
    }

    private void setByteIORegister(byte value, short address) {
        if (address == Constants.DIV_ADDRESS) {
            // writing any value to DIV sets it to 0
            sysClock = (short) 0;
            ioRegisters[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.IO_REGISTERS_START)] = (byte) 0;
        } else if (address == Constants.TIMA_ADDRESS) {
            // abort timer interrupt and TMA reload
            requestTimerInterrupt = false;
            ioRegisters[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.IO_REGISTERS_START)] = value;
        } else {
            ioRegisters[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.IO_REGISTERS_START)] = value;
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
            b = GBUtil.modifyBit(b, Constants.TIMER, 1);
            setByteNoTick(b, Constants.IF_ADDRESS);

            // do this to guarantee TIMA won't be incremented this cycle
            oldEnabled = 0;
        }
        short oldSysClock = sysClock;
        sysClock = (short) (sysClock + 4); // increments once every T cycle, so 4 every M cycle
        // since tick is called in setByte, we need to use setByteNoTick
        setByteNoTick(GBUtil.getByteFromShort(false, sysClock), Constants.DIV_ADDRESS);

        byte tima = getByteNoTick(Constants.TIMA_ADDRESS);
        byte tac = getByteNoTick(Constants.TAC_ADDRESS);

        int enable = GBUtil.getBit(tac, 2);
        int clockSelect = GBUtil.get2BitValue(
                GBUtil.getBit(tac, 1),
                GBUtil.getBit(tac, 0));
        int bitPos = switch (clockSelect) {
            case 0 -> 9; // every 256 m-cycles
            case 1 -> 3; // every 4 m-cycles
            case 2 -> 5; // every 16 m-cycles
            default -> 7; // evert 64 m-cycles
        };

        int oldAndResult = GBUtil.getBit(oldSysClock, bitPos) & oldEnabled;
        int newAndResult = GBUtil.getBit(sysClock, bitPos) & enable;

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
        byte andResult = (byte) (getByteNoTick(Constants.IE_ADDRESS) & getByteNoTick(Constants.IF_ADDRESS));
        for (int i = Constants.VBLANK; i <= Constants.JOYPAD; i++) {
            if (GBUtil.getBit(andResult, i) == 1) {
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
        return GBUtil.getBit(b, Constants.JOYPAD);
    }

    /**
     * Sets bit 4 of the byte at address 0xFFFF to jp.
     * This represents the joypad bit of the IE register.
     */
    public void setJoypadIE(int jp) {
        byte b = getByte(Constants.IE_ADDRESS);
        b = GBUtil.modifyBit(b, Constants.JOYPAD, jp);
        setByte(b, Constants.IE_ADDRESS);
    }

    /**
     * @return bit 3 of the byte at address 0xFFFF.
     * This represents the serial bit of the IE register.
     */
    public int getSerialIE(){
        byte b = getByte(Constants.IE_ADDRESS);
        return GBUtil.getBit(b, Constants.SERIAL);
    }

    /**
     * Sets bit 3 of the byte at address 0xFFFF to serial.
     * This represents the serial bit of the IE register.
     */
    public void setSerialIE(int serial) {
        byte b = getByte(Constants.IE_ADDRESS);
        b = GBUtil.modifyBit(b, Constants.SERIAL, serial);
        setByte(b, Constants.IE_ADDRESS);
    }

    /**
     * @return bit 2 of the byte at address 0xFFFF.
     * This represents the timer bit of the IE register.
     */
    public int getTimerIE(){
        byte b = getByte(Constants.IE_ADDRESS);
        return GBUtil.getBit(b, Constants.TIMER);
    }

    /**
     * Sets bit 2 of the byte at address 0xFFFF to timer.
     * This represents the timer bit of the IE register.
     */
    public void setTimerIE(int timer) {
        byte b = getByte(Constants.IE_ADDRESS);
        b = GBUtil.modifyBit(b, Constants.TIMER, timer);
        setByte(b, Constants.IE_ADDRESS);
    }

    /**
     * @return bit 1 of the byte at address 0xFFFF.
     * This represents the LCD bit of the IE register.
     */
    public int getLcdIE(){
        byte b = getByte(Constants.IE_ADDRESS);
        return GBUtil.getBit(b, Constants.LCD);
    }

    /**
     * Sets bit 1 of the byte at address 0xFFFF to lcd.
     * This represents the LCD bit of the IE register.
     */
    public void setLcdIE(int lcd) {
        byte b = getByte(Constants.IE_ADDRESS);
        b = GBUtil.modifyBit(b, Constants.LCD, lcd);
        setByte(b, Constants.IE_ADDRESS);
    }

    /**
     * @return bit 0 of the byte at address 0xFFFF.
     * This represents the VBlank bit of the IE register.
     */
    public int getVBlankIE(){
        byte b = getByte(Constants.IE_ADDRESS);
        return GBUtil.getBit(b, Constants.VBLANK);
    }

    /**
     * Sets bit 0 of the byte at address 0xFFFF to vblank.
     * This represents the VBlank bit of the IE register.
     */
    public void setVBlankIE(int vblank) {
        byte b = getByte(Constants.IE_ADDRESS);
        b = GBUtil.modifyBit(b, Constants.VBLANK, vblank);
        setByte(b, Constants.IE_ADDRESS);
    }

    /**
     * @return bit 4 of the byte at address 0xFF0F.
     * This represents the joypad bit of the IF register.
     */
    public int getJoypadIF(){
        byte b = getByte(Constants.IF_ADDRESS);
        return GBUtil.getBit(b, Constants.JOYPAD);
    }

    /**
     * Sets bit 4 of the byte at address 0xFF0F to jp.
     * This represents the joypad bit of the IF register.
     */
    public void setJoypadIF(int jp) {
        byte b = getByte(Constants.IF_ADDRESS);
        b = GBUtil.modifyBit(b, Constants.JOYPAD, jp);
        setByte(b, Constants.IF_ADDRESS);
    }

    /**
     * @return bit 3 of the byte at address 0xFF0F.
     * This represents the serial bit of the IE register.
     */
    public int getSerialIF(){
        byte b = getByte(Constants.IF_ADDRESS);
        return GBUtil.getBit(b, Constants.SERIAL);
    }

    /**
     * Sets bit 3 of the byte at address 0xFF0F to serial.
     * This represents the serial bit of the IF register.
     */
    public void setSerialIF(int serial) {
        byte b = getByte(Constants.IF_ADDRESS);
        b = GBUtil.modifyBit(b, Constants.SERIAL, serial);
        setByte(b, Constants.IF_ADDRESS);
    }

    /**
     * @return bit 2 of the byte at address 0xFF0F.
     * This represents the timer bit of the IF register.
     */
    public int getTimerIF(){
        byte b = getByte(Constants.IF_ADDRESS);
        return GBUtil.getBit(b, Constants.TIMER);
    }

    /**
     * Sets bit 2 of the byte at address 0xFF0F to timer.
     * This represents the timer bit of the IF register.
     */
    public void setTimerIF(int timer) {
        byte b = getByte(Constants.IF_ADDRESS);
        b = GBUtil.modifyBit(b, Constants.TIMER, timer);
        setByte(b, Constants.IF_ADDRESS);
    }

    /**
     * @return bit 1 of the byte at address 0xFF0F.
     * This represents the LCD bit of the IF register.
     */
    public int getLcdIF(){
        byte b = getByte(Constants.IF_ADDRESS);
        return GBUtil.getBit(b, Constants.LCD);
    }

    /**
     * Sets bit 1 of the byte at address 0xFF0F to lcd.
     * This represents the LCD bit of the IF register.
     */
    public void setLcdIF(int lcd) {
        byte b = getByte(Constants.IF_ADDRESS);
        b = GBUtil.modifyBit(b, Constants.LCD, lcd);
        setByte(b, Constants.IF_ADDRESS);
    }

    /**
     * @return bit 0 of the byte at address 0xFF0F.
     * This represents the VBlank bit of the IF register.
     */
    public int getVBlankIF(){
        byte b = getByte(Constants.IF_ADDRESS);
        return GBUtil.getBit(b, Constants.VBLANK);
    }

    /**
     * Sets bit 0 of the byte at address 0xFF0F to vblank.
     * This represents the VBlank bit of the IF register.
     */
    public void setVBlankIF(int vblank) {
        byte b = getByte(Constants.IF_ADDRESS);
        b = GBUtil.modifyBit(b, Constants.VBLANK, vblank);
        setByte(b, Constants.IF_ADDRESS);
    }

    // todo for testing purposes, probably remove later
    public void printSerialOutput() {
        if (getByteNoTick((short) 0xff02) == (byte) 0x81) {
            byte b = getByteNoTick((short) 0xff01);
            char c = (char) GBUtil.zeroExtend(b);
            System.out.println(c);
            setByteNoTick((byte) 0, (short) 0xff02);
        }
    }

    // todo for testing blargg mem_timing 2, OAM_bug, dmg_sound, and cgb_sound,
    // since these test roms write results to memory instead of via the serial port. (see respective readme)
    // Call this where you call printSerialOutput(), or call it in the loop in main.java
    // every 4096 cycles or something. This will be unnecessary once I get display working.
    public void printMemoryOutput() {
        if ((getByteNoTick((short) 0xA001) == (byte) 0xDE) &&
                (getByteNoTick((short) 0xA002) == (byte) 0xB0) &&
                (getByteNoTick((short) 0xA003) == (byte) 0x61)) {
            byte character = getByteNoTick((short) 0xA004);
            byte resultCode = getByteNoTick((short) 0xA000);
            char c = (char) GBUtil.zeroExtend(character);
            int resultCodeInt = GBUtil.zeroExtend(resultCode);
            System.out.println("ResultCode: " + resultCodeInt);
            System.out.println(c);
        }
    }

    // todo for testing purposes, probably remove later (some of my actual methods use this, so need to come up with better way to do this)
    public byte getByteNoTick(short address) {
        byte retVal;
        if (GBUtil.zeroExtend(Constants.FIXED_ROM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.FIXED_ROM_END)) {

            retVal = fixedRom[GBUtil.zeroExtend(address)];

        } else if (GBUtil.zeroExtend(Constants.SWITCHABLE_ROM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.SWITCHABLE_ROM_END)) {

            retVal = switchableRom[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.SWITCHABLE_ROM_START) +
                    (currRomBank - 1) * Constants.kb16];

        } else if (GBUtil.zeroExtend(Constants.VRAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.VRAM_END)) {

            retVal = vram[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.VRAM_START)];

        } else if (GBUtil.zeroExtend(Constants.CARTRIDGE_RAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.CARTRIDGE_RAM_END)) {

            if (ramEnabled) {
                retVal = cartridgeRam[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.CARTRIDGE_RAM_START) +
                        currRamBank * Constants.kb8];
            } else {
                retVal = (byte) 0xFF; // technically not guaranteed to return this
            }

        } else if (GBUtil.zeroExtend(Constants.CONSOLE_RAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.CONSOLE_RAM_END)) {

            retVal = consoleRam[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.CONSOLE_RAM_START)];

        } else if (GBUtil.zeroExtend(Constants.ECHO_RAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.ECHO_RAM_END)) {

            // todo technically this is prohibited so I could return an exception
            retVal = consoleRam[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.ECHO_RAM_START)];

        } else if (GBUtil.zeroExtend(Constants.OAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.OAM_END)) {

            // todo placeholder for now
            retVal = oam[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.OAM_START)];

        } else if (GBUtil.zeroExtend(Constants.IO_REGISTERS_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.IO_REGISTERS_END)) {

            // todo for testing only
            if (address == (short) 0xFF44) {
                retVal =  (byte) 0x90;
            } else if (address == (short) 0xFF4D) { // todo prevent blargg test from trying to speed switch
                // todo according to https://www.reddit.com/r/EmuDev/comments/u1d1p6/gameboy_blargg_test_03_infinite_loop/
                // maybe I should make unused/unmapped IO registers read out 0xFF and be read only!
                retVal = (byte) 0xFF;
            } else {
                retVal = ioRegisters[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.IO_REGISTERS_START)];
            }

        } else if (GBUtil.zeroExtend(Constants.HRAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.HRAM_END)) {

            retVal = hram[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.HRAM_START)];

        } else if (Constants.IE_ADDRESS == address) {
            retVal = ieRegister;
        } else {
            // unusuable or out of bounds
            // todo I think unusable area actually returns an a value, but probably don't need to implement.
            throw new MemoryException("called getByte on not usable, or out of bounds address space: " +
                    GBUtil.zeroExtend(address));
        }

        return retVal;
    }

    // todo for testing purposes, probably remove later, need to come up with better way to do this
    public void setByteNoTick(byte value, short address) {
        if (GBUtil.zeroExtend(Constants.FIXED_ROM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.SWITCHABLE_ROM_END)) {

            setMBC1Registers(value, address);

        } else if (GBUtil.zeroExtend(Constants.VRAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.VRAM_END)) {

            vram[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.VRAM_START)] = value;

        } else if (GBUtil.zeroExtend(Constants.CARTRIDGE_RAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.CARTRIDGE_RAM_END)) {

            if (ramEnabled) {
                cartridgeRam[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.CARTRIDGE_RAM_START) +
                        currRamBank * Constants.kb8] = value;
            }

        } else if (GBUtil.zeroExtend(Constants.CONSOLE_RAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.CONSOLE_RAM_END)) {

            consoleRam[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.CONSOLE_RAM_START)] = value;

        } else if (GBUtil.zeroExtend(Constants.ECHO_RAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.ECHO_RAM_END)) {

            consoleRam[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.ECHO_RAM_START)] = value;

        } else if (GBUtil.zeroExtend(Constants.OAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.OAM_END)) {

            // todo, placeholder for now
            oam[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.OAM_START)] = value;

        } else if (GBUtil.zeroExtend(Constants.IO_REGISTERS_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.IO_REGISTERS_END)) {

            // todo setByteNoTick method is only used by doMCycle().
            // Since setByteIORegister has special behavior for stuff like DIV and TIMA, which we don't want,
            // we can't use that and just use this instead.
            ioRegisters[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.IO_REGISTERS_START)] = value;

        } else if (GBUtil.zeroExtend(Constants.HRAM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.HRAM_END)) {

            hram[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.HRAM_START)] = value;

        } else if (Constants.IE_ADDRESS == address) {
            ieRegister = value;
        } else {
            // unusuable or out of bounds
            throw new MemoryException("called setByte on not usable, or out of bounds address space: " +
                    GBUtil.zeroExtend(address));
        }
        // todo for testing only
        printSerialOutput();
    }

    // todo needed for unit tests to set interrupt handler methods for now.
    // maybe in the future just have unit tests directly read in a test ROM?
    // This method allows you to directly write to the ROM address space, no side effects otherwise.
    public void setByteRom(byte value, short address) {
        if (GBUtil.zeroExtend(Constants.FIXED_ROM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.FIXED_ROM_END)) {

            fixedRom[GBUtil.zeroExtend(address)] = value;

        } else if (GBUtil.zeroExtend(Constants.SWITCHABLE_ROM_START) <= GBUtil.zeroExtend(address) &&
                GBUtil.zeroExtend(address) <= GBUtil.zeroExtend(Constants.SWITCHABLE_ROM_END)) {

            switchableRom[GBUtil.zeroExtend(address) - GBUtil.zeroExtend(Constants.SWITCHABLE_ROM_START) +
                    (currRomBank - 1) * Constants.kb16] = value;

        } else {
            throw new MemoryException("Address: " + address + " is not in the ROM address space");
        }
    }
}
