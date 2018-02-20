// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

//
// R2 = 0
// negative = false
// if (R1 < 0) {
//  R1 = -R1
//  negative = true
// }
// while (R1 > 0) {
//  R2 = R2 + R0
//  R1--    
// }
// if (negative) {
//  R2 = -R2   
// }

    @R2                 // R2 = 0
    M=0 

    @negative           // negative = 0
    M=0

    @R1                 // if (R1 < 0) {
    D=M        
    @LOOP
    D;JGT

    @negative           // nagative = true
    M=1

    @R1                 // R1 = -R1
    M=-M

(LOOP)                  // while (R1 != 0)
    @R1
    D=M        

    @END_LOOP
    D;JLE        

    @R0                 // R2 = R2 + R0
    D=M
    @R2
    M=D+M

    @R1                 // R1--
    M=M-1                            

    @LOOP
    0;JMP
(END_LOOP)    

    @negative           // if (negative)
    D=M
    @END
    D;JEQ

    @R2                 // R2 = -R2
    M=-M

(END)
    @END
    0;JMP