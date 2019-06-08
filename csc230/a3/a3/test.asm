#define		LCD_LIBONLY

.def STR1_OFFSET	=	r20
.def STR1_LENGTH	=	r21

.include	"lcd.asm"

.cseg
			call	lcd_init

			call	init_string

			ldi		STR1_OFFSET, 1

			ldi		ZH, high(msg)
			ldi		ZL, low(msg)
			call	str_len
			mov		STR1_LENGTH, r24

			ldi		ZH, high(msg)
			ldi		ZL, low(msg)
			ldi		XH, high(tmp)
			ldi		XL, low(tmp)
			call	cpy_str_w_offset

			call	display_string

lpb:		rjmp	lpb

lp:			ldi		ZH, high(msg)
			ldi		ZL, low(msg)
			ldi		XH, high(tmp)
			ldi		XL, low(tmp)
			call	cpy_str_w_offset

			inc		STR1_OFFSET
			cp		STR1_OFFSET, STR1_LENGTH
			brne	notlen
			ldi		STR1_OFFSET, 0
notlen:		rjmp	lp

; Takes str addresss in Z
; dest address in X
cpy_str_w_offset:
			push	r16
			push	r17
			push	r18
			push	ZH
			push	ZL

			clr		r17
			clr		r18

offsetlp:	cp		r17, STR1_OFFSET
			breq	cpylp
			ld		r16, Z+
			inc		r17
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
			st		X+, r16
			inc		r18
			cpi		r18, 16
			breq	donecpy
			rjmp	wraplp

donecpy:	ldi		r16, 0	; Null terminator
			st		X+, r16
			pop		ZL
			pop		ZH
			pop		r18
			pop		r17
			pop		r16
			ret

init_string:
			push	r16

			ldi		r16, high(msg)
			push	r16
			ldi		r16, low(msg)
			push	r16
			ldi		r16, high(msg_p << 1)
			push	r16
			ldi		r16, low(msg_p << 1)
			push	r16
			call	str_init
			pop		r16
			pop		r16
			pop		r16
			pop		r16

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

display_string:
			push	r16

			call	lcd_clr

			ldi		r16, 0
			push	r16
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

			pop		r16
			ret

msg_p:		.db "Short string. ", 0

.dseg
.org 0x200
msg:		.byte 200
tmp:		.byte 200
