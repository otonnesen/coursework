;
; CSc 230 Assignment 1
; Question 4
;
.include "m2560def.inc"
; This program should calculate the sum of the integers from 1 to (value)
; inclusive.
;
; The result should be stored in (result).
;
; Where:
;
;   (value) refers to the byte stored at memory location value in data memory
;   (result) refers to the byte stored at memory location result in data memory
;
; The sample code you've been given already contains labels named value and result
;
; In the AVR there is no way to automatically initialize data memory
; with constant values.  This is why I have supplied code that initializes data
; memory from program memory.
;

;--*1 Do not change anything between here and the line starting with *--
;
; You don't need to understand this code, we will get to it later
;
; But, if you are keen -- I am using the Z register as a pointer into
; program memory
;
.cseg
	ldi ZH,high(init<<1)		; initialize Z to point to init
	ldi ZL,low(init<<1)
	lpm r0,Z				; get the first byte and increment Z
	sts value,r0				; store into A
;*--1 Do not change anything above this line to the --*

;***
; Your code goes here:
;
lds		r0, value ; n -- constant
mov		r2, r0 ; n -- for shifting
inc		r2 ; n+1
ldi		r16, 0x00 ; counter
clr		r3

square_n:
		add		r3, r1
		clr		r1
		cp		r2, r19
		breq	divide_by_two
		lsr		r2 ; move rightmost bit in r1 to C flag
		inc		r16

		brcs	shift ; if rightmost bit was 1, left shift r0 r16 times
		jmp		square_n

shift:
		ldi		r18, 0x01 ; loop counter
		mov		r1, r0

shift_loop:
		cp		r16, r18
		breq	square_n
		inc		r18
		lsl		r1
		jmp		shift_loop

divide_by_two:
		lsr		r3

		sts		result, r3

;****

;--*2 Do not change anything between here and the line starting with *--
done:	jmp done
;*--2

;--*3 Do not change anything between here and the line starting with *--
; This is the constant to initialize value to
; Program memory must be specified in words (2 bytes) which
; is why there is a 2nd byte (0x00) at the end.
init:	.db 0x05, 0x00
;*--3
;--*4 Do not change anything between here and the line starting with *--
; This is in the data segment (ie. SRAM)
; The first real memory location in SRAM starts at location 0x200 on
; the ATMega 2560 processor.  Locations less than 0x200 are special
; and will be discussed much more later
;
.dseg
.org 0x200
value:	.byte 1
result:	.byte 1
;*--4
