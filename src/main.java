import model.cpu.CPU;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class main {

    static int i = 1;
    static StringBuilder stringBuilder = new StringBuilder();
    public static void main(String[] args) {
        String testFile = "roms/gb-test-roms-master/cpu_instrs/cpu_instrs.gb";
        CPU cpu = new CPU(readFile(testFile));
        while (true) {
            i++;
            //writeToFile(cpu);
            cpu.doInstructionCycle();
        }
    }

    public static byte[] readFile(String filename) {
        byte[] bytes = {};
        try (FileInputStream fis = new FileInputStream(filename)) {
            bytes = fis.readAllBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }


    public static void writeToFile(CPU cpu) {
        String str = "A:" + String.format("%02X",cpu.getRa()) +
                " F:" + String.format("%02X",cpu.getRf()) +
                " B:" + String.format("%02X",cpu.getRb()) +
                " C:" + String.format("%02X",cpu.getRc()) +
                " D:" + String.format("%02X",cpu.getRd()) +
                " E:" + String.format("%02X",cpu.getRe()) +
                " H:" + String.format("%02X",cpu.getRh()) +
                " L:" + String.format("%02X",cpu.getRl()) +
                " SP:" + String.format("%04X",cpu.getStackPointer()) +
                " PC:" + String.format("%04X",cpu.getProgramCounter()) +
                " PCMEM:" + String.format("%02X",cpu.getMemory().getByteNoTick((short) (cpu.getProgramCounter() + 0))) +
                "," + String.format("%02X",cpu.getMemory().getByteNoTick((short) (cpu.getProgramCounter() + 1))) +
                "," + String.format("%02X",cpu.getMemory().getByteNoTick((short) (cpu.getProgramCounter() + 2))) +
                "," + String.format("%02X",cpu.getMemory().getByteNoTick((short) (cpu.getProgramCounter() + 3))) +
                "\n";
        stringBuilder.append(str);

        if (i % 4096 == 0) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("logs/results.log", true))) {
                writer.write(stringBuilder.toString());
                stringBuilder.setLength(0);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
