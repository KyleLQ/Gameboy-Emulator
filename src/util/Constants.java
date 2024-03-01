package util;

public class Constants {
    public static final short FIXED_ROM_START = (short) 0x0000; // the first address in fixed rom address space
    public static final short FIXED_ROM_END = (short) 0x3FFF; // the last address in fixed rom address space
    public static final short SWITCHABLE_ROM_START = (short) 0x4000;
    public static final short SWITCHABLE_ROM_END = (short) 0x7FFF;
    public static final short VRAM_START = (short) 0x8000;
    public static final short VRAM_END = (short) 0x9FFF;
    public static final short CARTRIDGE_RAM_START = (short) 0xA000;
    public static final short CARTRIDGE_RAM_END = (short) 0xBFFF;
    public static final short CONSOLE_RAM_START = (short) 0xC000;
    public static final short CONSOLE_RAM_END = (short) 0xDFFF;
    public static final short ECHO_RAM_START = (short) 0xE000;
    public static final short ECHO_RAM_END = (short) 0xFDFF;
    public static final short OAM_START = (short) 0xFE00;
    public static final short OAM_END = (short) 0xFE9F;
    public static final short UNUSABLE_START = (short) 0xFEA0;
    public static final short UNUSUABLE_END = (short) 0xFEFF;
    public static final short IO_REGISTERS_START = (short) 0xFF00;
    public static final short IO_REGISTERS_END = (short) 0xFF7F;
    public static final short HRAM_START = (short) 0xFF80;
    public static final short HRAM_END = (short) 0xFFFE;
    // TODO THESE CONSTANTS ARE WRITTEN ASSUMING MBC1/MBC0 ONLY!
    public static final short RAM_ENABLE_START = (short) 0x0000; // write here to enable or disable external ram
    public static final short RAM_ENABLE_END = (short) 0x1FFF;
    public static final short ROM_BANK_START = (short) 0x2000; // write here to select lower 5 bits of rom bank number
    public static final short ROM_BANK_END = (short) 0x3FFF;
    public static final short ROM_RAM_MODE_SELECT_START = (short) 0x6000; // write here to select extended ROM or RAM mode
    public static final short ROM_RAM_MODE_SELECT_END = (short) 0x7FFF;
    public static final short RAM_BANK_OR_UPPER_ROM_BANK_BIT_START_ADDRESS = (short) 0x4000;
    // write here to select upper 2 bits of ROM bank number or RAM bank number
    public static final short RAM_BANK_OR_UPPER_ROM_BANK_BIT_END_ADDRESS = (short) 0x5FFF;
    // todo end MBC1/MBC 0
    public static final short DIV_ADDRESS = (short) 0xFF04;
    public static final short TIMA_ADDRESS = (short) 0xFF05;
    public static final short TMA_ADDRESS = (short) 0xFF06;
    public static final short TAC_ADDRESS = (short) 0xFF07;
    public static final short IE_ADDRESS = (short) 0xFFFF;
    public static final short IF_ADDRESS = (short) 0xFF0F;
    public static final int JOYPAD = 4; // bit position of joypad interrupt on IE and IF register
    public static final int SERIAL = 3; // bit position of serial interrupt on IE and IF register
    public static final int TIMER = 2; // bit position of timer interrupt on IE and IF register
    public static final int LCD = 1; // bit position of lcd interrupt on IE and IF register
    public static final int VBLANK = 0; // bit position of vblank interrupt on IE and IF register
    public static final short JOYPAD_HANDLER_ADDRESS = (short) 0x60;
    public static final short SERIAL_HANDLER_ADDRESS = (short) 0x58;
    public static final short TIMER_HANDLER_ADDRESS = (short) 0x50;
    public static final short LCD_HANDLER_ADDRESS = (short) 0x48;
    public static final short VBLANK_HANDLER_ADDRESS = (short) 0x40;

    public static final int kb16 = 0x4000;
    public static final int kb8 = 0x2000;
}
