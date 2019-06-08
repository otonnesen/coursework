#include <stdlib.h>
#include <assert.h>

#include "../include/bitmap.h"

#define BITS_PER_ENTRY 32
#define OFFSET(n) (n/BITS_PER_ENTRY)
#define OFFSET_BIT(n) (n%BITS_PER_ENTRY)

void set_bit(bitmap_t *bm, int n) {
	bm[OFFSET(n)] |= (1<<OFFSET_BIT(n));
}

void clr_bit(bitmap_t *bm, int n) {
	bm[OFFSET(n)] &= ~(1<<OFFSET_BIT(n));
}

int get_bit(bitmap_t *bm, int n) {
	return (bm[OFFSET(n)] & (1<<OFFSET_BIT(n))) != 0;
}

int get_first_zero(bitmap_t *bm, int len) {
	for (int i = 0; i < len; i++) {
		if (bm[i] != 0xffffffff) {
			for (int j = 0; j < 32; j++) {
				if ((bm[i] & (1 << j)) == 0) {
					return i*BITS_PER_ENTRY+j;
				}
			}
		}
	}
	return -1;
}
