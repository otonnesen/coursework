#include "CSC230.h"
#include <stdlib.h>

typedef struct Message {
	char* text;
	int offset;
} Message;

volatile int scroll = 1;

unsigned short poll_adc(){
	unsigned short adc_result = 0;
	ADCSRA |= 0x40;
	while((ADCSRA & 0x40) == 0x40);
	unsigned short result_low = ADCL;
	unsigned short result_high = ADCH;
	adc_result = (result_high<<8)|result_low;
	return adc_result;
}

ISR(TIMER0_OVF_vect) {
	unsigned short adc_result = poll_adc();
	if (adc_result == 0x0C3) {
		scroll = 0;
	} if (adc_result == 0x17C) {
		scroll = 1;
	}
}

int str_len(char* msg) {
	for (int i = 0;;i++) if (*(msg+i)=='\0') return i;
}

char *cpy_w_offset(Message* msg, char* cpy) {
	int len = str_len(msg->text);
	for (int i = msg->offset, j = 0; j < 16; i++, j++) {
		*(cpy+j) = *(msg->text+i%len);
	}
	cpy[16] = '\0';
    return cpy;
}

Message *new_message(char* message) {
	Message* msg = (Message*) malloc(sizeof *msg);
	msg->text = message;
	msg->offset = 0;
	return msg;
}

int main() {
	lcd_init();
	ADCSRA = 0x87;
	ADMUX = 0x40;
	TIMSK0 = 0x01;
	TCNT0 = 0x00;
	TCCR0A = 0x00;
	TCCR0B = 0x03;
	sei();
	Message* msg1 = new_message("short string ");
	Message* msg2 = new_message("this is a slightly longer string ");
    char* cpy = (char*) malloc(17 * sizeof(char));
	for(;;_delay_ms(500)) {
		if (!scroll) continue;
		lcd_command(1);
		msg1->offset++;
		msg1->offset %= str_len(msg1->text);
		cpy_w_offset(msg1, cpy);
		lcd_xy(0,0);
		lcd_puts(cpy);
		msg2->offset++;
		msg2->offset %= str_len(msg2->text);
		cpy_w_offset(msg2, cpy);
		lcd_xy(0,1);
		lcd_puts(cpy);
	}
}