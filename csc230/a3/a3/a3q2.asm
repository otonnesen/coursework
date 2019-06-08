#define		LCD_LIBONLY

.def STR1_OFFSET		=	r20
.def STR1_LENGTH		=	r21
.def STR2_OFFSET		=	r22
.def STR2_LENGTH		=	r23
.def SCROLLING_BOOL		=	r2
.def TIMER_COUNTER		=	r3
.def SCROLLING_SPEED	=	r4

.cseg
.org		0x000
			jmp		start
.org		0x028
			jmp		timer1_isr

start:
.include	"lcd.asm"

.cseg
			call	lcd_init
			call	led_setup
			call	adc_setup

			call	init_string

			ldi		STR1_OFFSET, 0
			ldi		STR2_OFFSET, 0
			ldi		r16, 0xFF
			mov		SCROLLING_BOOL, r16
			ldi		r16, 0b01111111
			mov		SCROLLING_SPEED, r16

			ldi		ZH, high(msg1)
			ldi		ZL, low(msg1)
			call	str_len
			mov		STR1_LENGTH, r24

			ldi		ZH, high(msg2)
			ldi		ZL, low(msg2)
			call	str_len
			mov		STR2_LENGTH, r24


			clr		TIMER_COUNTER
			ldi		r16, 0b1
			push	r16
			call	timer1_setup
			pop		r16

lp:			rjmp	lp

; Meat of the program
timer1_isr:	push	r19
			lds		r19, SREG
			push	r19

			inc		TIMER_COUNTER

			call	check_scrolling

			mov		r19, TIMER_COUNTER
			push	r19
			andi	r19, 0b01111111
			cpi		r19, 0b01111111
			breq	cmd_chk
cmd_chk_ret:
			pop		r19
			and		r19, SCROLLING_SPEED
			cp		r19, SCROLLING_SPEED
			brne	skip_t1_isr

			tst		SCROLLING_BOOL
			breq	skip_t1_isr

			call	lcd_clr
			call	update_strings


skip_t1_isr:
			pop		r19
			sts		SREG, r19
			pop		r19
			reti
cmd_chk:
			call	update_scrolling_speed
			mov		r19, SCROLLING_SPEED
			lsr		r19
			lsr		r19
			com		r19
			push	r19
			call	display
			pop		r19

			rjmp	cmd_chk_ret

; Checks if up or down buttons have been pressed and updates
; SCROLLING_BOOL accordingly.
check_scrolling:
			push	r24

			call	check_button
			cpi		r24, 0b10
			breq	scroll_off
			cpi		r24, 0b100
			breq	scroll_on
			rjmp	done_scroll_check

scroll_on:	ser		r24
			mov		SCROLLING_BOOL, r24
			rjmp	done_scroll_check

scroll_off:	clr		SCROLLING_BOOL
			rjmp	done_scroll_check

done_scroll_check:
			pop		r24
			ret

; Checks if RIGHT or LEFT buttons have been pressed
; and updates SCROLLING SPEED accordingly.
update_scrolling_speed:
			push	r24

			call	check_button

			cpi		r24, 0b1
			breq	scroll_increase
			cpi		r24, 0b1000
			breq	scroll_decrease
			rjmp	done_scroll_update

scroll_decrease:
			lsl		SCROLLING_SPEED
			ldi		r24, 1
			add		SCROLLING_SPEED, r24
			rjmp	done_scroll_update

scroll_increase:
			ldi		r24, 0b00000111
			cp		SCROLLING_SPEED, r24
			brlo	done_scroll_update
			lsr		SCROLLING_SPEED
			rjmp	done_scroll_update

done_scroll_update:
			pop		r24
			ret

; Updates msg1 and msg2 and displays them on the LCD
update_strings:
			push	ZH
			push	ZL
			push	XH
			push	XL

			ldi		ZH, high(msg1)
			ldi		ZL, low(msg1)
			ldi		XH, high(tmp)
			ldi		XL, low(tmp)
			push	STR1_OFFSET
			call	cpy_str_w_offset
			pop		STR1_OFFSET

			ldi		r16, 0
			push	r16
			call	display_string
			pop		r16

			inc		STR1_OFFSET
			cp		STR1_OFFSET, STR1_LENGTH
			brne	update_msg2
			ldi		STR1_OFFSET, 0

update_msg2:
			ldi		ZH, high(msg2)
			ldi		ZL, low(msg2)
			ldi		XH, high(tmp)
			ldi		XL, low(tmp)
			push	STR2_OFFSET
			call	cpy_str_w_offset
			pop		STR2_OFFSET

			ldi		r16, 1
			push	r16
			call	display_string
			pop		r16

			inc		STR2_OFFSET
			cp		STR2_OFFSET, STR2_LENGTH
			brne	done_update
			ldi		STR2_OFFSET, 0

done_update:
			pop		XL
			pop		XH
			pop		ZL
			pop		ZH
			ret

; Copies both strings from program memory to data memory
init_string:
			push	r16

			ldi		r16, high(msg1)
			push	r16
			ldi		r16, low(msg1)
			push	r16
			ldi		r16, high(msg1_p << 1)
			push	r16
			ldi		r16, low(msg1_p << 1)
			push	r16
			call	str_init
			pop		r16
			pop		r16
			pop		r16
			pop		r16

			ldi		r16, high(msg2)
			push	r16
			ldi		r16, low(msg2)
			push	r16
			ldi		r16, high(msg2_p << 1)
			push	r16
			ldi		r16, low(msg2_p << 1)
			push	r16
			call	str_init
			pop		r16
			pop		r16
			pop		r16
			pop		r16

			pop		r16
			ret


; Displays c-string located at 'tmp' on the LCD screen
; Parameters:
;		SP+1:		Column on which to display string (0 or 1)
display_string:
			.set	PARAM_OFFSET = 6
			push	r16
			push	ZH
			push	ZL

			in		ZH, SPH
			in		ZL, SPL

			ldd		r16, Z+1+PARAM_OFFSET
			push	r16
			ldi		r16, 0
			push	r16
			call	lcd_gotoxy
			pop		r16
			pop		r16

			ldi		r16, high(tmp)
			push	r16
			ldi		r16, low(tmp)
			push	r16
			call	lcd_puts
			pop		r16
			pop		r16

			pop		ZL
			pop		ZH
			pop		r16
			ret

; Takes src addresss in Z
; dest address in X
; Stack:	Input		-	1 byte
;				SP+1:	-	Desired offset
; Copies 16 bytes from Z to X
cpy_str_w_offset:
			push	r16
			push	r17
			push	r18
			push	r19
			push	ZH
			push	ZL

			in		ZH, SPH
			in		ZL, SPL
			ldd		r19, Z+10

			pop		ZL
			pop		ZH
			push	ZH
			push	ZL

			clr		r17
			clr		r18

offsetlp:	cp		r17, r19
			breq	cpylp
			ld		r16, Z+
			cpi		r16, 0
			brne	ptrok
			pop		ZL
			pop		ZH
			push	ZH
			push	ZL
ptrok:		inc		r17
			rjmp	offsetlp

cpylp:		ld		r16, Z+
			cpi		r16, 0
			breq	wrap
			st		X+, r16
			inc		r18
			cpi		r18, 16
			breq	donecpy
			rjmp	cpylp

wrap:		pop		ZL
			pop		ZH
			push	ZL
			push	ZH
wraplp:		ld		r16, Z+
			cpi		r16, 0
			breq	wrap
			st		X+, r16
			inc		r18
			cpi		r18, 16
			breq	donecpy
			rjmp	wraplp

donecpy:	ldi		r16, 0		; Null terminator
			st		X+, r16
			pop		ZL
			pop		ZH
			pop		r19
			pop		r18
			pop		r17
			pop		r16
			ret

; returns len of string at Z in r24
str_len:	push	r16
			push	ZH
			push	ZL
			ldi		r24, 0
strlenlp:	ld		r16, Z+
			cpi		r16, 0
			breq	strlenfound
			inc		r24
			rjmp	strlenlp

strlenfound:
			pop		ZL
			pop		ZH
			pop		r16
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

; Stack:	Input		-	1 byte
;				SP+1	-	Number to display
display:	push	r16
			push    r17
			push    r18
			push	r0
			push	ZH
			push	ZL

			in		ZH, SPH
			in		ZL, SPL

			ldd		r0, Z+10

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

			pop		ZL
			pop		ZH
			pop		r0
			pop     r18
			pop     r17
			pop     r16

			ret

; Stack:	Input		-	1 byte
;				SP+1	-	Desired prescaler value
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

msg1_p:		.db "small ", 0
msg2_p:		.db "string ", 0

.dseg
msg1:		.byte 200
msg2:		.byte 200
tmp:		.byte 17
