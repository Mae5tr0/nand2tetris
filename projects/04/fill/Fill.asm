// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.


// while (true) {
//   keyboard_char = read_keyboard()
//   if (keyboard_char != 0) {
//      fill_screen()
//      next
//   }
//  clear_screen()
// }

    // screen size
    @8192    
    D=A
    @n
    M=D

(LOOP)                  // while (true)
    @KBD           // keyboard_char = read_keyboard()
    D=M

    @CLEAN_SCREEN         // if (keyboard_char != 0) {
    D;JEQ

    // fill_screen
    // for (i=0; i<8192; i++) {
    //   M[SCREEN + i] = -1   
    // }
    @i
    M=0

 (FILL_SCREEN_LOOP) 
    @i
    D=M
    @n
    D=D-M
    @END_FILL_SCREEN_LOOP
    D;JEQ

    @SCREEN
    D=A
    @i    
    A=D+M
    M=-1

    @i
    M=M+1

    @FILL_SCREEN_LOOP
    0;JMP
 (END_FILL_SCREEN_LOOP)


    @LOOP 
    0;JMP                     

(CLEAN_SCREEN)
    // clear_screen
    // for (i=0; i<8192; i++) {
    //   M = 0   
    // }

    @i
    M=0

 (CLEAR_SCREEN_LOOP) 
    @i
    D=M
    @n
    D=D-M
    @END_CLEAR_SCREEN_LOOP
    D;JEQ

    @SCREEN
    D=A
    @i    
    A=D+M
    M=0

    @i
    M=M+1

    @CLEAR_SCREEN_LOOP
    0;JMP    
 (END_CLEAR_SCREEN_LOOP)

    @LOOP
    0;JMP 