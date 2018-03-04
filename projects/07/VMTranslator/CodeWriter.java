import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class CodeWriter {
    private static final int TMP_BASE_ADDRESS = 5;

    private BufferedWriter bufferedWriter;
    private HashMap<String, String> segmentPointers;
    private String fileName;
    private int counter;

    public CodeWriter(String filePath) {
        String[] filePathParts = filePath.split("[/.]");
        fileName = filePathParts[filePathParts.length - 2];

        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(filePath);
        } catch (IOException e) {
            System.out.println("Error reading file:" + e);
            return;
        }
        bufferedWriter = new BufferedWriter(fileWriter);
        counter = 0;
        initializeSegmentPointers();
    }

    private void initializeSegmentPointers() {
        segmentPointers = new HashMap<>();

        segmentPointers.put("local", "LCL");
        segmentPointers.put("argument", "ARG");
        segmentPointers.put("this", "THIS");
        segmentPointers.put("that", "THAT");
    }

    private void w(String command) {
        try {
            bufferedWriter.write(command);
            bufferedWriter.newLine();
        } catch (IOException e) {
            System.out.println("Error writing command: " + command);
        }
    }

    public void writeArithmetic(String command) {
        w("// " + command);

        switch (command) {
            case "not":
            case "neg":
                arithmeticOneArguments(command);
                break;
            case "add":
            case "sub":
            case "and":
            case "or":
                arithmeticTwoArguments(command);
                break;
            case "eq":
            case "gt":
            case "lt":
                arithmeticCompare(command);
                break;
            default:
                throw new IllegalArgumentException("Invalid arithmetic command: " + command);
        }
    }

    private void arithmeticOneArguments(String command) {
        decrementSP();
        w("A=M");      // M=y
        w("M=" + arithmeticCommand(command)); // M=f(M)
        incrementSP();
    }

    private void arithmeticTwoArguments(String command) {
        decrementSP();
        w("A=M");  // D=y
        w("D=M");
        w("A=A-1"); // M=x
        w("M=" + arithmeticCommand(command)); //M=f(M,M+1)
    }

    private void arithmeticCompare(String command) {
        decrementSP();
        w("A=M");
        w("D=M");       // D = *SP
        w("A=A-1");
        w("D=M-D");
        w("@" + uniqLabel("TRUE"));
        w("D;" + jumpCommand(command));
        w("D=0");
        w("@" + uniqLabel("END"));
        w("0;JMP");
        w("(" + uniqLabel("TRUE") + ")");
        w("D=-1");
        w("(" + uniqLabel("END") + ")");
        decrementSP();
        w("A=M");
        w("M=D");
        incrementSP();
        counter++;
    }

    private String uniqLabel(String label) {
        return label + "_" + fileName + "." + counter;
    }

    private String jumpCommand(String command) {
        String result;
        switch (command) {
            case "eq":
                result = "JEQ";
                break;
            case "gt":
                result = "JGT";
                break;
            case "lt":
                result = "JLT";
                break;
            default:
                throw new IllegalArgumentException("Invalid jump command: " + command);
        }

        return result;
    }

    private String arithmeticCommand(String command) {
        String result;
        switch (command) {
            case "add":
                result = "D+M";
                break;
            case "sub":
                result = "M-D";
                break;
            case "and":
                result = "D&M";
                break;
            case "or":
                result = "D|M";
                break;
            case "not":
                result = "!M";
                break;
            case "neg":
                result = "-M";
                break;
            default:
                throw new IllegalArgumentException("Invalid arithmetic command: " + command);
        }

        return result;
    }

    public void writePushPop(int commandType, String segment, int index) {
        switch (commandType) {
            case Parser.C_POP:
                writePopCommand(segment, index);
                break;
            case Parser.C_PUSH:
                writePushCommand(segment, index);
                break;
            default:
                throw new IllegalArgumentException("Invalid command type: " + commandType);
        }
    }

    private void writePushCommand(String segment, int index) {
        w("// push " + segment + " " + index);

        switch (segment) {
            case "local":
            case "argument":
            case "this":
            case "that":
                setIndexToD(index);
                w("@" + segmentPointers.get(segment));
                w("A=D+M");
                w("D=M");      // D=*addr
                putDtoSP();
                incrementSP();
                break;
            case "constant":
                setIndexToD(index);
                putDtoSP();
                incrementSP();
                break;
            case "static":
                staticSegment(index);
                w("D=M");
                putDtoSP();
                incrementSP();
                break;
            case "pointer":
                if (index != 0 && index != 1) { break; }
                w("@" + segmentPointers.get(pointerSegment(index)));
                w("D=M");
                putDtoSP();
                incrementSP();
                break;
            case "temp":
                setIndexToD(TMP_BASE_ADDRESS + index);
                w("A=D");
                w("D=M");      // D=*addr
                putDtoSP();
                incrementSP();
                break;
            default:
                throw new IllegalArgumentException("Invalid segment: " + segment);
        }
    }

    private void writePopCommand(String segment, int index) {
        w("// pop " + segment + " " + index);

        switch (segment) {
            case "local":
            case "argument":
            case "this":
            case "that":
                setIndexToD(index);
                w("@" + segmentPointers.get(segment));
                w("D=D+M");
                w("@addr");
                w("M=D");

                decrementSP();
                w("A=M");
                w("D=M");  // D -> SP*

                w("@addr");
                w("A=M");
                w("M=D");
                break;
            case "constant":
                // nothing
                break;
            case "static":
                decrementSP();
                w("A=M");
                w("D=M");

                staticSegment(index);
                w("M=D");
                break;
            case "pointer":
                decrementSP();
                if (index != 0 && index != 1) { break; }
                w("A=M");
                w("D=M");

                w("@" + segmentPointers.get(pointerSegment(index)));
                w("M=D");
                break;
            case "temp":
                setIndexToD(TMP_BASE_ADDRESS + index);
                w("@addr");
                w("M=D");

                decrementSP();
                w("A=M");
                w("D=M");  // D -> SP*

                w("@addr");
                w("A=M");
                w("M=D");
                break;
            default:
                throw new IllegalArgumentException("Invalid segment: " + segment);
        }
    }

    private String pointerSegment(int index) {
        return index == 0 ? "this" : "that";
    }

    private void staticSegment(int index) {
        w("@" + fileName + "." + index);
    }

    // SP--
    private void decrementSP() {
        w("@SP");
        w("M=M-1");
    }

    // SP++
    private void incrementSP() {
        w("@SP");
        w("M=M+1");
    }

    // D=i
    private void setIndexToD(int index) {
        w("@" + index);
        w("D=A");
    }

    // *SP=D
    private void putDtoSP() {
        w("@SP");
        w("A=M");
        w("M=D");
    }

    public void close() {
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println("Error closing file:" + e);
        }
    }
}