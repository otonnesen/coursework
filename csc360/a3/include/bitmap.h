#include <stdint.h>

typedef uint32_t bitmap_t;

void set_bit(bitmap_t*, int);
void clr_bit(bitmap_t*, int);
int get_bit(bitmap_t*, int);
int get_first_zero(bitmap_t*, int);
