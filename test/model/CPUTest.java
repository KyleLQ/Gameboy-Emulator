package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CPUTest {
    CPU cpu;

    @BeforeEach
    public void setup() {
        cpu = new CPU();
    }


    @Test
    public void test16BitRegister() {
        cpu.setRb((byte) 0x58);
        cpu.setRc((byte) 0xea);
        assertEquals((short) 0x58ea,cpu.getRegisterBC());
        System.out.println("register BC value: " + String.format("0x%08X", cpu.getRegisterBC()));

        cpu.setRegisterBC((short) 0x78fa);
        assertEquals((byte) 0x78, cpu.getRb());
        assertEquals((byte) 0xfa, cpu.getRc());
        System.out.println("register B value: " + String.format("0x%08X", cpu.getRb()));
        System.out.println("register C value: " + String.format("0x%08X", cpu.getRc()));
    }

    @Test
    public void testFlags() {
        cpu.setRf((byte) 0b10100000);
        assertEquals(1, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(1, cpu.getHalfCarryFlag());
        assertEquals(0, cpu.getCarryFlag());

        cpu.setZeroFlag(0);
        assertEquals(0, cpu.getZeroFlag());
        cpu.setZeroFlag(1);
        assertEquals(1, cpu.getZeroFlag());

        cpu.setSubtractionFlag(1);
        assertEquals(1, cpu.getSubtractionFlag());
        cpu.setHalfCarryFlag(0);
        assertEquals(0, cpu.getHalfCarryFlag());
        cpu.setCarryFlag(1);
        assertEquals(1, cpu.getCarryFlag());
    }

    // todo doesn't test (HL) register
    @Test
    public void testALU_A_r8() {
        cpu.setRa((byte) 2);
        cpu.setRb((byte) 4);
        byte instruction = (byte) 0b10000000;
        System.out.println(cpu.getRa() + " + " + cpu.getRb() + " = ");
        cpu.decodeInstruction(instruction);
        System.out.println(cpu.getRa());
        assertEquals((byte) 6, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getCarryFlag());
        assertEquals(0, cpu.getHalfCarryFlag());

        cpu.setRa((byte) 0b11111111);
        cpu.setRb((byte) 0b1);
        instruction = (byte) 0b10000000;
        System.out.println("Test overflow");
        cpu.decodeInstruction(instruction);
        System.out.println(cpu.getRa());
        assertEquals((byte) 0, cpu.getRa());
        assertEquals(1, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(1, cpu.getCarryFlag());
        assertEquals(1, cpu.getHalfCarryFlag());

        cpu.setRa((byte) 4);
        cpu.setRc((byte) 13);
        cpu.setCarryFlag(1);
        instruction = (byte) 0b10001001;
        System.out.println(cpu.getRa() + " ADC " + cpu.getRc() + " = ");
        cpu.decodeInstruction(instruction);
        System.out.println(cpu.getRa());
        assertEquals((byte) 18, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getCarryFlag());
        assertEquals(1, cpu.getHalfCarryFlag());

        cpu.setRa((byte) 4);
        cpu.setRd((byte) 3);
        instruction = (byte) 0b10010010;
        System.out.println(cpu.getRa() + " - " + cpu.getRd() + " = ");
        cpu.decodeInstruction(instruction);
        System.out.println(cpu.getRa());
        assertEquals((byte) 1, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(1, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getCarryFlag());
        assertEquals(0, cpu.getHalfCarryFlag());

        cpu.setRa((byte) 10);
        cpu.setRd((byte) 11);
        instruction = (byte) 0b10010010;
        System.out.println("Test underflow");
        cpu.decodeInstruction(instruction);
        System.out.println(cpu.getRa());
        assertEquals((byte) 255, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(1, cpu.getSubtractionFlag());
        assertEquals(1, cpu.getCarryFlag());
        assertEquals(1, cpu.getHalfCarryFlag());

        cpu.setRa((byte) 130);
        cpu.setRe((byte) 78);
        cpu.setCarryFlag(1);
        instruction = (byte) 0b10011011;
        System.out.println(cpu.getRa() + " SUBC " + cpu.getRe() + " = ");
        cpu.decodeInstruction(instruction);
        System.out.println(cpu.getRa());
        assertEquals((byte) 51, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(1, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getCarryFlag());
        assertEquals(1, cpu.getHalfCarryFlag());

        cpu.setRa((byte) 0b11001010);
        cpu.setRh((byte) 0b01100000);
        instruction = (byte) 0b10100100;
        System.out.println(cpu.getRa() + " AND " + cpu.getRh() + " = ");
        cpu.decodeInstruction(instruction);
        System.out.println(cpu.getRa());
        assertEquals((byte) 0b01000000, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getCarryFlag());
        assertEquals(1, cpu.getHalfCarryFlag());

        cpu.setRa((byte) 0b11001111);
        cpu.setRl((byte) 0b01100000);
        instruction = (byte) 0b10101101;
        System.out.println(cpu.getRa() + " XOR " + cpu.getRl() + " = ");
        cpu.decodeInstruction(instruction);
        System.out.println(cpu.getRa());
        assertEquals((byte) 0b10101111, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getCarryFlag());
        assertEquals(0, cpu.getHalfCarryFlag());

        cpu.setRa((byte) 0b10101010);
        cpu.setRl((byte) 0b01010000);
        instruction = (byte) 0b10110101;
        System.out.println(cpu.getRa() + " OR " + cpu.getRl() + " = ");
        cpu.decodeInstruction(instruction);
        System.out.println(cpu.getRa());
        assertEquals((byte) 0b11111010, cpu.getRa());
        assertEquals(0, cpu.getZeroFlag());
        assertEquals(0, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getCarryFlag());
        assertEquals(0, cpu.getHalfCarryFlag());

        cpu.setRa((byte) 0b10101010);
        instruction = (byte) 0b10111111;
        System.out.println(cpu.getRa() + " CP " + cpu.getRa() + " = ");
        int originalRa = cpu.getRa();
        cpu.decodeInstruction(instruction);
        System.out.println(cpu.getRa());
        assertEquals(originalRa, cpu.getRa());
        assertEquals(1, cpu.getZeroFlag());
        assertEquals(1, cpu.getSubtractionFlag());
        assertEquals(0, cpu.getCarryFlag());
        assertEquals(0, cpu.getHalfCarryFlag());
    }
}
