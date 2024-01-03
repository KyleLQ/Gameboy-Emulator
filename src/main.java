import model.CPU;

public class main {

    public static void main(String[] args) {
        CPU cpu = new CPU();
        //cpu.ra = 0x58;
        //cpu.rf = (byte) 0xea; // casting it to byte causes it to be interpreted as negative instead of positive, which screws the stuff up
        // and we have to cast it to byte cause its not in the range -128 to 127 for signed byte

        System.out.println(String.format("0x%08X", cpu.getRegisterAF()));
        System.out.println(cpu.getRegisterAF());
    }
}
