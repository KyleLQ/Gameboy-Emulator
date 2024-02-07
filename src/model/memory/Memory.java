package model.memory;

import util.GameBoyUtil;

import java.util.Arrays;

/**
 * Class the represents the memory in the Game Boy. Right now it is just a
 * simple array, but I'm expecting it to be more complicated later.
 */
public class Memory {
    private byte[] memory;

    public Memory() {
        memory = new byte[0xFFFF];
        Arrays.fill(memory, (byte) 0);
    }

    public byte getByte(short address) {
        return memory[GameBoyUtil.zeroExtendShort(address)];
    }

    public void setByte(byte value, short address) {
        memory[GameBoyUtil.zeroExtendShort(address)] = value;
    }
}
