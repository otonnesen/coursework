.cseg
.org 0x000
			jmp		start

.org 0x028
			jmp		timer1_isr

start:		ldi		r16, high(0x21FF)
			out		SPH, r16
			ldi		r16, low(0x21FF)
			out		SPL, r16

			call	adc_setup
			call	led_setup
			ldi		r16, 0b11
			push	r16
			call	timer1_setup
			pop		r16

;			clr		r0
			ldi		r16, 0b1
			mov		r0, r16
lp:			jmp		lp


timer1_isr:	push	r19
			push	r24
			lds		r19, SREG
			push	r19

			call	check_button
			cpi		r24, 0
			breq	done_isr
			cpi		r24, 1
			brne	not1
			ldi		r24, 0b100000
			cp		r0, r24
			breq	oflowl
			lsl		r0
			rjmp	done_isr
oflowl:		ldi		r24, 0b1
			mov		r0, r24
			rjmp	done_isr
not1:		cpi		r24, 8
			brne	done_isr
			ldi		r24, 1
			cp		r0, r24
			breq	oflowr
			lsr		r0
			rjmp	done_isr
oflowr:		ldi		r24, 0b100000
			mov		r0, r24

done_isr:	call display
			pop		r19
			sts		SREG, r19
			pop		r24
			pop		r19
			reti

; Prepares timer1 for use. Takes r16 as a parameter
; and sets the prescaler to its value.

timer1_setup:	
			push	r16
			push	ZH
			push	ZL

			in		ZH, SPH
			in		ZL, SPL

			ldi		r16, 1
			sts		TIMSK1, r16
			ldd		r16, Z+7
			sts		TCCR1B, r16
			sei

			pop		ZL
			pop		ZH
			pop		r16
			ret

led_setup:	push	r16
			ldi		r16, 0xFF
			out		DDRB, r16
			sts		DDRL, r16
			pop		r16
			ret

adc_setup:	push	r16
			ldi		r16, 0x87
			sts		ADCSRA, r16
			ldi		r16, 0x40
			sts		ADMUX, r16
			pop		r16
			ret

display:	push    r17
			push    r18
			push    r16

			clr     r17
			clr     r18
			mov     r16, r0

			lsr     r16
			brcc    off42
			ori     r17, 0b10000000

off42:		lsr     r16
			brcc    off44
			ori     r17, 0b00100000

off44:		lsr     r16
			brcc    off46
			ori     r17, 0b00001000

off46:		lsr     r16
			brcc    off48
			ori     r17, 0b00000010

off48:		lsr     r16
			brcc    off50
			ori     r18, 0b00001000

off50:		lsr     r16
			brcc    off52
			ori     r18, 0b00000010
off52:		sts     PORTL, r17
			out     PORTB, r18

			pop     r16
			pop     r17
			pop     r18

			ret

; Returns in r24:
;   0 - no button pressed
;   1 - right button pressed
;   2 - up button pressed
;   4 - down button pressed
;   8 - left button pressed
;   16- select button pressed
check_button:
			push    r16
			push    r17
			; start a2d
			lds r16, ADCSRA
			ori r16, 0x40
			sts ADCSRA, r16
			; wait for it to complete
wait:		lds r16, ADCSRA
			andi r16, 0x40
			brne wait

			; read the value
			lds r16, ADCL
			lds r17, ADCH
			ldi     r24, 0b10000

			cpi     r17, 0x3
			brlo    le2
			cpi     r16, 0x16
			brlo    skip
			clr     r24
			jmp     skip

le2:		cpi     r17, 0x2
			brlo    le1
			cpi     r16, 0x2B
			brlo    sh1
			jmp     skip
sh1:		lsr     r24
			jmp     skip

le1:		lsr     r24
			cpi     r17, 0x1
			brlo    le0
			cpi     r16, 0x7C
			brlo    sh2
			jmp     skip
sh2:		lsr     r24
			jmp     skip

le0:		lsr     r24
			cpi     r16, 0xC3
			brlo    sh3
			jmp     skip
sh3:		lsr     r24
			cpi     r16, 0x32
			brlo    sh4
			jmp     skip
sh4:		lsr     r24

skip:		pop     r17
			pop     r16
			ret