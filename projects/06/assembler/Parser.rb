class Parser
    def initialize(file)
        @current = "";
        @file = file
    end

    # wrong workin if last lines is not instructions
    def hasMoreCommands
        !@file.eof?
    end

    def advance
        command = ''
        while command == '' and hasMoreCommands do
            command = clean(@file.gets)                
        end
        @current = command
    end

    def commandType
        case @current
            when /^\@.*$/
                'A_COMMAND'
            when /^\(.*\)$/
                'L_COMMAND'
            else 'C_COMMAND'
        end                                    
    end

    def symbol
        case commandType
            when 'A_COMMAND'
                @current[1..-1]
            when 'L_COMMAND'
                @current[1..(@current.length - 2)]    
        end
    end

    def dest
        dest = @current[/^.*=/]        
        dest[0, dest.length - 1] if dest
    end

    def comp
        comp = 
            if @current =~ /\=/
                @current[/\=.*$/][1..-1]
            else
               @current   
            end  
        comp.split(';')[0]                
    end

    def jump
        jmp = @current[/;.*$/]        
        jmp[1..-1] if jmp
    end

    def clean(raw_command)
        raw_command.sub(/\/\/.*$/, '').gsub(/\s+/, '')
    end
end