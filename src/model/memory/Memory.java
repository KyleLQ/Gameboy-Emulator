package model.memory;

import java.util.Arrays;

/**
 * Class the represents the memory in the Game Boy. Right now it is just a
 * simple array, but I'm expecting it to be more complicated later.
 */
public class Memory {
    private byte[] memory;

    Memory() {
        memory = new byte[0xFFFF];
        Arrays.fill(memory, (byte) 0);
    }

    public byte[] getMemory() {
        return memory;
    }
}
