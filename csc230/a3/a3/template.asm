.cseg
.org		0x000
			jmp		start
.org		0x028
			jmp		timer1_isr
start:

lp:			rjmp	lp



timer1_isr:	push	r19
			push	r24
			lds		r19, SREG
			push	r19

			;stuff goes here

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

; Initializes LEDs

led_setup:	push	r16
			ldi		r16, 0b00001010
			out		DDRB, r16
			ldi		r16, 0b10101010
			sts		DDRL, r16
			pop		r16
			ret

; Initializes LCD buttons

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