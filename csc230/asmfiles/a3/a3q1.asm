#define		LCD_LIBONLY

.def STR1_OFFSET	=	r20
.def STR1_LENGTH	=	r21
.def STR2_OFFSET	=	r22
.def STR2_LENGTH	=	r23
.def TIMER_BOOL		=	r1

.cseg
.org		0x000
			jmp		start
.org		0x028
			jmp		timer1_isr

start:
.include	"lcd.asm"

.cseg
			call	lcd_init

			call	init_string

			ldi		STR1_OFFSET, 0
			ldi		STR2_OFFSET, 0

			ldi		ZH, high(msg1)
			ldi		ZL, low(msg1)
			call	str_len
			mov		STR1_LENGTH, r24
			
			ldi		ZH, high(msg2)
			ldi		ZL, low(msg2)
			call	str_len
			mov		STR2_LENGTH, r24


			clr		TIMER_BOOL
			ldi		r16, 0b11
			push	r16
			call	timer1_setup
			pop		r16

lp:			rjmp	lp

timer1_isr:	push	r19
			lds		r19, SREG
			push	r19
			com		TIMER_BOOL
			brmi	skip_t1_isr

			call	lcd_clr
			call	update_strings

skip_t1_isr:
			pop		r19
			sts		SREG, r19
			pop		r19
			reti

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

; Takes str addresss in Z,
; dest address in X.
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

msg1_p:		.db "Test message please ignore. ", 0
msg2_p:		.db "--- buy --- more --- hats ", 0

.dseg
msg1:		.byte 200
msg2:		.byte 200
tmp:		.byte 200