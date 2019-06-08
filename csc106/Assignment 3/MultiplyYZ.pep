; Oliver Tonnesen
; V00885732

         STRO    PROMPT, d
         DECI    X, d
         STRO    PROMPT, d
         DECI    Y, d
         LDA     0, i
         LDX     Y, d
         CPX     0, i
         BRGE    POS
         BRLT    NEG


NEG:     NOP     106, i
         LDX     0, i
LOOPNEG: NOP     106,i
         SUBX    COUNTER, d
         SUBA    X, d
         CPX     Y, d
         BRGT    LOOPNEG
         BR      END

POS:     NOP     106, i
         LDX     0, i
LOOPPOS: NOP     106, i
         ADDX    COUNTER, d
         ADDA    X, d
         CPX     Y, d
         BRLT    LOOPPOS
         BR      END

END:     NOP     106, i
         STA     PRODUCT, d
         STRO    PRODSTR, d
         DECO    PRODUCT, d
         STOP



X:       .WORD 0
Y:       .WORD 0
COUNTER: .WORD 1
PRODUCT: .WORD 0
PROMPT:  .ASCII "Enter a number between -100 and 100 \x00"
PRODSTR: .ASCII "The product of your two numbers is \x00"

         .END
