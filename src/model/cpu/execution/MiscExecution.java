package model.cpu.execution;

import model.cpu.CPU;

public class MiscExecution {

    /**
     * Executes the NOP instruction
     */
    public static void executeNOP(byte instruction, CPU cpu) {

    }

    /**
     * Executes the DI instruction.
     * Sets IME = 0, and cancels any pending EI effect if needed.
     */
    public static void executeDI(byte instruction, CPU cpu) {
        cpu.setIME(0);
    }

    /**
     * Executes EI instruction.
     * Sets IME to 1 at the end of the instruction after EI.
     */
    public static void executeEI(byte instruction, CPU cpu) {
        cpu.setIME(1);
    }

    /**
     * Executes the HALT instruction.
     *
     */
    public static void executeHALT(byte instruction, CPU cpu) {
        cpu.setIsHalted(true);
        // todo add tests later once you have timer setup
    }

    /**
     * Executes the STOP instruction
     */
    public static void executeSTOP(byte instruction, CPU cpu) {
        // todo
        // actual behavior seems to be very complicated, and it doesn't seem to be used in any
        // commercial games. May not be worth the effort to implement.

        System.out.println("Executing STOP instruction");
    }
}
