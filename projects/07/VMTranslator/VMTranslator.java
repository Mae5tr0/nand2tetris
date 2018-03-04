public class VMTranslator {
    public static void main(String[] args) {
        assert args[0] != null;

        String filePath = args[0];
        Parser parser = new Parser(filePath);
        String resultPath = filePath.split("\\.")[0] + ".asm";
        CodeWriter writer = new CodeWriter(resultPath);

        while (parser.hasMoreCommands()) {
            parser.advance();
            int commandType = parser.commandType();
            switch (commandType) {
                case Parser.C_ARITHMETIC:
                    writer.writeArithmetic(parser.arg1());
                    break;
                case Parser.C_PUSH: case Parser.C_POP:
                    writer.writePushPop(commandType, parser.arg1(), parser.arg2());
                    break;
                default:
                    throw new IllegalArgumentException("Invalid command type: " + commandType);
            }
        }

        parser.close();
        writer.close();
    }
}