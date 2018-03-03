import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class CodeWriter {
    private BufferedWriter bufferedWriter;
    HashMap<String, String> segmentPointers;

    private static final int TMP_BASE_ADDRESS = 5;

    public CodeWriter(String fileName) {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(fileName);
        } catch (IOException e) {
            System.out.println("Error reading file:" + e);
            return;
        }
        bufferedWriter = new BufferedWriter(fileWriter);

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
            case "eg":
            case "gt":
            case "lt":
                arithmeticCompareArguments(command);
                break;
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

    private void arithmeticCompareArguments(String command) {
        throw new NotImplementedException();
    }

    private String arithmeticCommand(String command) {
        String result = "";
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
            case "net":
                result = "-M";
                break;
        }

        assert !result.equals("");
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
                throw new NotImplementedException();
            case "pointer":
                throw new NotImplementedException();
            case "temp":
                setIndexToD(TMP_BASE_ADDRESS + index);
                w("A=D");
                w("D=M");      // D=*addr
                putDtoSP();
                incrementSP();
                break;
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
                throw new NotImplementedException();
            case "pointer":
                throw new NotImplementedException();
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
        }
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