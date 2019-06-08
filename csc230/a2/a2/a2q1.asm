;
; a2q1.asm
;
; Write a program that displays the binary value in r16
; on the LEDs.
;
; See the assignment PDF for details on the pin numbers and ports.
;


		ldi		r16, 0xFF
		out		DDRB, r16		; PORTB all output
		sts		DDRL, r16		; PORTL all output

		ldi		r16, 0x25		; display the value
		mov		r0, r16			; in r0 on the LEDs

; Your code here

		clr		r17				; goes to PORTL
		clr		r18				; goes to PORTB

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
;
; Don't change anything below here
;
done:	jmp done
