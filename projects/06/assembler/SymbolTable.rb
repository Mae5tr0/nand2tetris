class SymbolTable
    def initialize
        @store = {}
    end

    def addEntry(symbol, address)
        @store[symbol] = address
    end

    def contains(symbol)
        @store[symbol] != nil
    end

    def getAddress(symbol)
        @store[symbol]
    end
end