#include <stdint.h>

#define BLOCK_SIZE	512
#define NUM_BLOCKS	4096
#define DISK_NAME	"vdisk"

// Use in conjunction with SEEK_SET to move rw head to
// start of block n
#define BLOCK_OFFSET(n) BLOCK_SIZE * n

void open_disk(int);
void close_disk();
void zero_disk();
void write_block(int, void*);
void read_block(int, void*);
