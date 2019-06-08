;
; a2q4.asm
;
; Fix the button subroutine program so that it returns
; a different value for each button
;
		; initialize the Analog to Digital conversion

		ldi r16, 0x87
		sts ADCSRA, r16
		ldi r16, 0x40
		sts ADMUX, r16

		; initialize PORTB and PORTL for ouput
		ldi	r16, 0xFF
		out DDRB,r16
		sts DDRL,r16

		ldi		r16, high(0x21FF) ; Initialize stack pointer
		out		SPH, r16
		ldi		r16, low(0x21FF)
		out		SPL, r16

;;;;; testing code ;;;;;
;;;
;;;		call	check_button

;;;done:	jmp		done

;;;;; testing code ;;;;;

		clr r0
		call display
lp:
		call check_button
		tst r24
		breq lp
		mov	r0, r24

		call display
		ldi r20, 99
		call delay
		ldi r20, 0
		mov r0, r20
		call display
		rjmp lp

;
; An improved version of the button test subroutine
;
; Returns in r24:
;	0 - no button pressed
;	1 - right button pressed
;	2 - up button pressed
;	4 - down button pressed
;	8 - left button pressed
;	16- select button pressed
;
; this function uses registers:
;	r24
;
; if you consider the word:
;	 value = (ADCH << 8) +  ADCL
; then:
;
; value > 0x3E8 - no button pressed
;
; Otherwise:
; value < 0x032 - right button pressed
; value < 0x0C3 - up button pressed
; value < 0x17C - down button pressed
; value < 0x22B - left button pressed
; value < 0x316 - select button pressed
;
check_button:
		push	r16
		push	r17
		; start a2d
		lds	r16, ADCSRA
		ori r16, 0x40
		sts	ADCSRA, r16
;;;;;
;;;		jmp		test
;;;;;
		; wait for it to complete
wait:	lds r16, ADCSRA
		andi r16, 0x40
		brne wait

		; read the value
		lds r16, ADCL
		lds r17, ADCH
;;;;;
;;;test:	ldi		r16, 0x00
;;;		ldi		r17, 0x0
;;;;;
		; put your new logic here:
		;clr		r24
		ldi		r24, 0b10000

		cpi		r17, 0x3
		brlo	le2
		cpi		r16, 0x16
		brlo	skip
		clr		r24
		jmp		skip

le2:	cpi		r17, 0x2
		brlo	le1
		cpi		r16, 0x2B
		brlo	sh1			; Turns out brge
		jmp		skip		; is signed :|
sh1:	lsr		r24			; y i k e s
		jmp		skip

le1:	lsr		r24
		cpi		r17, 0x1
		brlo	le0
		cpi		r16, 0x7C
		brlo	sh2
		jmp		skip
sh2:	lsr		r24
		jmp		skip

le0:	lsr		r24
		cpi		r16, 0xC3
		brlo	sh3
		jmp		skip
sh3:	lsr		r24
		cpi		r16, 0x32
		brlo	sh4
		jmp		skip
sh4:	lsr		r24

skip:	pop		r17
		pop		r16
		ret

;
; delay
;
; set r20 before calling this function
; r20 = 0x40 is approximately 1 second delay
;
; this function uses registers:
;
;	r20
;	r21
;	r22
;
delay:
del1:		nop
		ldi r21,0xFF
del2:		nop
		ldi r22, 0xFF
del3:		nop
		dec r22
		brne del3
		dec r21
		brne del2
		dec r20
		brne del1
		ret

;
; display
;
; display the value in r0 on the 6 bit LED strip
;
; registers used:
;	r0 - value to display
;
display:
		; copy your code from a2q2.asm here

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

