import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
    private BufferedWriter bufferedWriter;

    public CodeWriter(String fileName) {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(fileName);
        } catch (IOException e) {
            System.out.println("Error reading file:" + e);
            return;
        }
        bufferedWriter = new BufferedWriter(fileWriter);
    }

    public void writeArithmetic(String command) {
        try {
            bufferedWriter.write("// " + command);
            bufferedWriter.newLine();

            //write command in assembler
        } catch (IOException e) {
            System.out.println("Error write to file:" + e);
        }
    }

    public void writePushPop(int commandType, String segment, int index) {
        try {
            bufferedWriter.write("// ");
            String command = null;
            switch (commandType) {
                case Parser.C_POP:
                    command = "pop";
                    break;
                case Parser.C_PUSH:
                    command = "push";
                    break;
            }
            bufferedWriter.write(command + " " + segment + " " + index);
            bufferedWriter.newLine();

            //write command in assembler
        } catch (IOException e) {
            System.out.println("Error write to file:" + e);
        }
    }

    public void close() {
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println("Error closing file:" + e);
        }
    }
}