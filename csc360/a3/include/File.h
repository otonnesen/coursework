#include <stdint.h>

#include "bitmap.h"
#include "disk.h"

#define FILE_FLAG 1
#define DIR_FLAG 2

struct superblock {
	uint32_t	magic_number;
	uint32_t	num_blocks;
	uint32_t	num_inodes;
	uint8_t		root_inode;
};
typedef struct superblock superblock_t;

struct inode {
	uint32_t	size;
	uint32_t	flags;
	uint16_t	block_pointers[10];
	uint16_t	single_indirect;
	uint16_t	double_indirect;
};
typedef struct inode inode_t;

struct dir_entry {
	uint8_t		inode;
	char		fname[31];
};
typedef struct dir_entry dir_entry_t;

union block {
	uint32_t		free_inode_vector[4];
	superblock_t	superblock;
	uint32_t		free_block_vector[128];
	inode_t			inodes[BLOCK_SIZE/sizeof(inode_t)];
	dir_entry_t		dir_entries[16];
	uint16_t		indirect_pointers[256];
};
typedef union block block_t;

void init_disk(void);
void format_disk(void);
void load_metadata(void);
void test(void);
int llfs_mkdir(const char*);
int llfs_create(const char*);
int llfs_open(const char*);
int llfs_close(int);
int llfs_read(int, void*, uint32_t);
int llfs_write(int, void*, uint32_t);
int llfs_seek(int, uint64_t);
int llfs_rename(const char*, const char*);
int llfs_delete(const char*);

void dump_file_metadata(const char*);
void dump_files();
void test();
