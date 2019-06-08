		ldi		r16, high(0x21FF)
		out		SPH, r16
		ldi		r16, low(0x21FF)
		out		SPL, r16

		ser		r16
		out		DDRB, r16
		out		PORTB, r16
		sts		DDRL, r16
		sts		PORTL, r16

done:	jmp		done