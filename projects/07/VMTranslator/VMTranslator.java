public class VMTranslator {
    public static void main(String[] args) {
        String fileName = args[0];
        assert fileName != null;
        assert !fileName.split("\\.")[1].equals("vm");

        Parser parser = new Parser(fileName);
        String resultFile = fileName.split("\\.")[0] + ".asm";
        CodeWriter writer = new CodeWriter(resultFile);

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
                    System.out.print("Invalid command type: " + commandType);
                    break;
            }
        }

        parser.close();
        writer.close();
    }
}