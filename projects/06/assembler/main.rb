require_relative 'Code'
require_relative 'Parser'
require_relative 'SymbolTable'

filename = ARGV[0]
file = File.new(filename, 'r')
parser = Parser.new(file)

code = Code.new
$st = SymbolTable.new

# fill symbol table with predefined values
$st.addEntry('SP', 0)
$st.addEntry('LCL', 1)
$st.addEntry('ARG', 2)
$st.addEntry('THIS', 3)
$st.addEntry('THAT', 4)
$st.addEntry('SCREEN', 16384)
$st.addEntry('KBD', 24576)
(0..15).each do |n|
    $st.addEntry("R#{n}", n)
end

commands = []
counter = 0

# first pass, convert c command to binary code, insert labels to symbol table
while parser.hasMoreCommands do 
    parser.advance

    command = 
        case parser.commandType
            when 'A_COMMAND'
                commands << "@#{parser.symbol}"                    
            when 'L_COMMAND'
                $st.addEntry(parser.symbol, counter)                    
            when 'C_COMMAND'
                commands << '111' + code.comp(parser.comp) + code.dest(parser.dest) + code.jump(parser.jump)
        end    
    counter = counter + 1 unless parser.commandType == 'L_COMMAND'                        
end
file.close

# second pass, replace symbols by address, write result to file
$last_user_register = 15

def binary_code_for_a_command(value) 
    binary_value = 
        if value =~ /^\d+$/
            value.to_i.to_s(2)
        elsif $st.contains(value)
            $st.getAddress(value).to_s(2)            
        else
            $last_user_register = $last_user_register + 1
            $st.addEntry(value, $last_user_register)
            $last_user_register.to_s(2)
        end

    # append 0 to left for archive correct size     
    binary_value.rjust(16,'0')    
end

result_file = File.new(filename.split('.')[0] + '.hack', 'w')
commands.each do |command|
    binary_code = 
        if command[0] == '@'
            binary_code_for_a_command(command[1..-1])
        else
            command
        end

    result_file.puts(binary_code)        
end
result_file.close

puts 'Translation complete'