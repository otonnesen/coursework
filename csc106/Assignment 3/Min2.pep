; Oliver Tonnesen
; V00885732

         STRO     PROMPT, d
         DECI     NUM1, d
         STRO     PROMPT, d 
         DECI     NUM2, d

         LDA     NUM1, d
         CPA     NUM2, d
         BRLT    ONE
         BRGT    TWO

;NUM1 lesser
ONE:     NOP     106, i
         STRO    END, d
         DECO    NUM1, d
         STOP

;NUM2 lesser
TWO:     NOP     106, i
         STRO    END, d
         DECO    NUM2, d
         STOP


; Data
NUM1:    .WORD 0
NUM2:    .WORD 0
PROMPT:  .ASCII "Enter a number: \x00"
END:     .ASCII "The minimum value is \x00"
         .END
