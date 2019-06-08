;
; a2q2.asm
;
;
; Turn the code you wrote in a2q1.asm into a subroutine
; and then use that subroutine with the delay subroutine
; to have the LEDs count up in binary.

		ldi r16, 0xFF
		out DDRB, r16		; PORTB all output
		sts DDRL, r16		; PORTL all output

; Your code here
; Be sure that your code is an infite loop

		ldi		r16, high(0x21FF) ; Initialize stack pointer
		out		SPH, r16
		ldi		r16, low(0x21FF)
		out		SPL, r16

		ldi		r16, 0x3F ; to cp with r0 (0x3F is 0b111111)
reset:	clr		r0
start:	cp		r0, r16
		brge	reset
		call	display
		call	delay
		inc		r0
		jmp		start

done:	jmp done	; if you get here, you're doing it wrong

;
; display
;
; display the value in r0 on the 6 bit LED strip
;
; registers used:
;	r0 - value to display
;
display:

		push	r17
		push	r18
		push	r16

		clr		r17
		clr		r18
		mov		r16, r0

		lsr		r16
		brcc	off42
		ori		r17, 0b10000000
		
off42:	lsr		r16
		brcc	off44
		ori		r17, 0b00100000

off44:	lsr		r16
		brcc	off46
		ori		r17, 0b00001000

off46:	lsr		r16
		brcc	off48
		ori		r17, 0b00000010

off48:	lsr		r16
		brcc	off50
		ori		r18, 0b00001000

off50:	lsr		r16
		brcc	off52
		ori		r18, 0b00000010
off52:	sts		PORTL, r17
		out		PORTB, r18

		pop		r16
		pop		r17
		pop		r18

		ret
;
; delay
;
; set r20 before calling this function
; r20 = 0x40 is approximately 1 second delay
;
; registers used:
;	r20
;	r21
;	r22
;
delay:	
del1:	nop
		ldi r21,0xFF
del2:	nop
		ldi r22, 0x3F
del3:	nop
		dec r22
		brne del3
		dec r21
		brne del2
		dec r20
		brne del1	
		ret
