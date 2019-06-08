;
; a2q3.asm
;
; Write a main program that increments a counter when the buttons are pressed
;
; Use the subroutine you wrote in a2q2.asm to solve this problem.
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

; Your code here
; make sure your code is an infinite loop

		ldi		r16, high(0x21FF) ; Initialize stack pointer
		out		SPH, r16
		ldi		r16, low(0x21FF)
		out		SPL, r16

		ldi		r16, 0x40 ; to cp with r0 (0x40 is 0b111111 + 1)
reset:	clr		r0
start:	call	check_button
		cpi		r24, 0
		breq	start
		cp		r0, r16
		brge	reset
		call	display
		call	delay
		inc		r0
		jmp		start

done:	jmp		done ; if you get here, you're doing it wrong

;
; the function tests to see if the button
; UP or SELECT has been pressed
;
; on return, r24 is set to be: 0 if not pressed, 1 if pressed
;
; this function uses registers:
;	r16
;	r17
;	r24
;
; This function could be made much better.  Notice that the a2d
; returns a 2 byte value (actually 12 bits).
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
; This function 'cheats' because I observed
; that ADCH is 0 when the right or up button is
; pressed, and non-zero otherwise.
; 
check_button:
		push		r16
		push		r17
		; start a2d
		lds	r16, ADCSRA	
		ori r16, 0x40
		sts	ADCSRA, r16

		; wait for it to complete
wait:	lds r16, ADCSRA
		andi r16, 0x40
		brne wait

		; read the value
		lds r16, ADCL
		lds r17, ADCH

		clr r24
		cpi r17, 0
		brne skip		
		ldi r24,1
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
		ldi r22, 0x0F
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
; copy your display subroutine from a2q2.asm here
 
; display the value in r0 on the 6 bit LED strip
;
; registers used:
;	r0 - value to display
;	r17 - value to write to PORTL
;	r18 - value to write to PORTB
;
;   r16 - scratch

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


