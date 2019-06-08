.cseg
.org 0x000
			jmp		start

.org 0x028
			jmp		timer1_isr

start:		call	timer1_setup
			call	led_setup

			ldi		r17, 0b10101010
			sts		PORTL, r17
			ldi		r18, 0b00001010
			out		PORTB, r18
lp:			jmp		lp


timer1_isr:	
			push	r19
			ldi		r19, 0b10101010
			eor		r17, r19
			sts		PORTL, r17
			ldi		r19, 0b00001010
			eor		r18, r19
			out		PORTB, r18
			pop		r19
			reti

timer1_setup:	
			push	r16

			ldi		r16, 1
			sts		TIMSK1, r16
			ldi		r16, 0b100
			sts		TCCR1B, r16
			sei

			pop		r16
			ret

led_setup:	push	r16
			ldi		r16, 0xFF
			out		DDRB, r16
			sts		DDRL, r16
			pop		r16
			ret