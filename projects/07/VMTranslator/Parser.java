import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
    //constants
    public static final int C_ARITHMETIC = 0;
    public static final int C_PUSH = 1;
    public static final int C_POP = 2;
    public static final int C_LABEL = 3;
    public static final int C_GOTO = 4;
    public static final int C_IF = 5;
    public static final int C_FUNCTION = 6;
    public static final int C_RETURN = 7;
    public static final int C_CALL = 8;


    private BufferedReader bufferedReader;
    private String currentCommand;
    private String nextCommand;

    private int commandType;
    private String arg1;
    private Integer arg2;

    public Parser(String fileName) {
        FileReader fileReader;
        try {
            fileReader = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file:" + e);
            return;
        }
        bufferedReader = new BufferedReader(fileReader);
    }

    public boolean hasMoreCommands() {
        if (nextCommand == null) {
            nextCommand = readCommand();
        }

        return nextCommand != null;
    }

    public void advance() {
        if (nextCommand == null) {
            nextCommand = readCommand();
        }
        currentCommand = nextCommand;
        nextCommand = readCommand();
        parseCommand();
    }

    private void parseCommand() {
        assert currentCommand != null;
        String[] commandParts = currentCommand.toLowerCase().split(" ");

        switch (commandParts[0]) {
            case "add":
            case "sub":
            case "neg":
            case "eq":
            case "gt":
            case "lt":
            case "and":
            case "or":
            case "not":
                commandType = C_ARITHMETIC;
                arg1 = commandParts[0];
                arg2 = null;
                break;
            case "push":
                commandType = C_PUSH;
                arg1 = commandParts[1];
                arg2 = Integer.parseInt(commandParts[2]);
                break;
            case "pop":
                commandType = C_POP;
                arg1 = commandParts[1];
                arg2 = Integer.parseInt(commandParts[2]);
                break;
            default:
                System.out.print("Invalid command: " + currentCommand);
                break;
        }
    }

    private String readCommand() {
        String command = "";
        while (command.equals("")) {
            try {
                command = bufferedReader.readLine();
            } catch (IOException e) {
                System.out.println("Error reading file:" + e);
                return null;
            }
            if (command == null) {
                return null;
            }
            command = command.replaceAll("//.*", "").trim();
        }
        return command;
    }

    public int commandType() {
        return commandType;
    }

    public String arg1() {
        return arg1;
    }

    public int arg2() {
        return arg2;
    }

    public void close() {
        try {
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("Error closing file:" + e);
        }
    }
}