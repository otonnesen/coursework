;
; CSc 230 Assignment 1 
; Question 2
;

; This program should calculate:
; R0 = R16 + R17
; if the sum of R16 and R17 is > 255 (ie. there was a carry)
; then R1 = 1, otherwise R1 = 0
;

;--*1 Do not change anything between here and the line starting with *--
.cseg
	ldi	r16, 0xF0
	ldi r17, 0x31
;*--1 Do not change anything above this line to the --*

;***
; Your code goes here:
;
add		r16, r17
mov		r0, r16
brcs	carry
jmp		done

carry:
		ldi		r16, 0x01
		mov		r1, r16
		jmp		done

;****
;--*2 Do not change anything between here and the line starting with *--
done:	jmp done
;*--2 Do not change anything above this line to the --*


