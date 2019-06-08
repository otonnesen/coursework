#include <fcntl.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <assert.h>
#include <sys/stat.h>

#include "../include/File.h"

uint8_t mkroot();
uint8_t create_file(const char*, int);
void sync_metadata();
void load_metadata();
void llfs_shutdown();

uint8_t search_dir(uint8_t, const char*);
int parse_path(const char*, char***);
uint8_t get_parent_inode(const char*);
uint8_t get_file_inode(const char*);
char *get_file_name(const char*);
int get_block_list(uint8_t, uint16_t**);
void update_block_list(uint8_t, uint16_t*, int);

struct openfile {
	uint8_t		inode;
	uint64_t	offset;
};
typedef struct openfile openfile_t;

superblock_t	*superblock;
inode_t			*inode_vector; // Has size 128 * 32B
bitmap_t		*free_block_vector; // Has size 512B
bitmap_t		*free_inode_vector; // Has size 16B
block_t			*data;
openfile_t		(*open_files)[10]; // Only 10 files can be open at once.
int 			*vdisk_fd;

// Opens vdisk file and sets up persistent storage for metadata.
void init_disk() {
	char* d = "./disk/vdisk";
	printf("Opening \"%s\"...\n", d);
	struct stat st = {0};
	if (stat("./disk", &st) == -1) {
		printf("./disk not found, please create it with rwx permissions for group others (xx7).\n");
		exit(-1);
	}
	vdisk_fd = malloc(sizeof *vdisk_fd);
	*vdisk_fd = open(d, O_CREAT|O_RDWR, 0666);
	open_disk(*vdisk_fd);

	// Persistent (between function calls) metadata
	data = malloc(sizeof *data);
	superblock = calloc(sizeof *superblock, 1);
	inode_vector = calloc(128 * sizeof(inode_t), 1);

	// If you change the order of these mallocs
	// the program will segfault.
	free_block_vector = calloc(512, 1);
	free_inode_vector = calloc(16, 1);
	open_files = malloc(sizeof *open_files);
	for (int i = 0; i < 10; i++) (*open_files)[i].inode = 0;

	atexit(llfs_shutdown);
}

// Creates a a root directory
uint8_t mkroot() {
	// Allocate an inode slot
	uint8_t inode_num = get_first_zero(free_inode_vector, 4);
	set_bit(free_inode_vector, inode_num);

	// Initialize inode
	inode_vector[inode_num].size = 0;
	inode_vector[inode_num].flags = DIR_FLAG;
	memset(inode_vector[inode_num].block_pointers, 0, 10 * sizeof(uint16_t));
	inode_vector[inode_num].single_indirect = 0;
	inode_vector[inode_num].double_indirect = 0;


	// Allocate a block and create the . and .. directories.
	uint16_t block = get_first_zero(free_block_vector, 128);
	set_bit(free_block_vector, block);

	inode_vector[inode_num].block_pointers[0] = block;

	memset(data, 0, BLOCK_SIZE);

	data->dir_entries[0].inode = inode_num;
	strcpy(data->dir_entries[0].fname, ".");
	inode_vector[inode_num].size += sizeof data->dir_entries[0];

	data->dir_entries[1].inode = inode_num;
	strcpy(data->dir_entries[1].fname, "..");
	inode_vector[inode_num].size += sizeof data->dir_entries[1];

	write_block(block, data); // TODO: Write to cache instead of direct to disk

	read_block(block, data);

	return inode_num;
}

uint8_t create_file(const char *pathname, int flags) {

	uint8_t inode = get_file_inode(pathname);
	if (inode != 0) {
		printf("File already exists.\n");
		return 0;
	}
	char *fname = get_file_name(pathname);
	uint8_t parent_inode = get_parent_inode(pathname);
	// Parent doesn't exist
	assert (parent_inode != 0);

	inode = get_first_zero(free_inode_vector, 4);
	// No inode avaialbe
	assert (inode != 0);
	set_bit(free_inode_vector, inode);

	inode_vector[inode].size = 0;
	inode_vector[inode].flags = flags;
	memset(inode_vector[inode].block_pointers, 0, 10 * sizeof(uint16_t));
	inode_vector[inode].single_indirect = 0;
	inode_vector[inode].double_indirect = 0;

	uint16_t *block_list;
	int num_blocks = get_block_list(parent_inode, &block_list);
	if (!block_list) {
		block_list = malloc(sizeof(uint16_t));
		block_list[0] = get_first_zero(free_block_vector, 128);
		num_blocks = 1;
	}

	int last_block = num_blocks - 1;
	read_block(block_list[last_block], data);
	int i;
	for (i = 0; i < 16; i++) {
		if (data->dir_entries[i].inode == 0) break;
	}
	if (i == 16) {
		block_list = realloc(block_list, (num_blocks+1) * sizeof(uint16_t));
		block_list[++last_block] = get_first_zero(free_block_vector, 128);
		set_bit(free_block_vector, block_list[last_block]);
		i = 0;
		num_blocks++;
	}

	data->dir_entries[i].inode = inode;
	strcpy(data->dir_entries[i].fname, fname);
	free(fname);
	inode_vector[parent_inode].size += sizeof data->dir_entries[i];
	write_block(block_list[last_block], data);
	update_block_list(parent_inode, block_list, num_blocks);

	free(block_list);
	return inode;
}

// Takes pathname, returns 0 on success
int llfs_mkdir(const char *pathname) {
	uint8_t parent_inode = get_parent_inode(pathname);
	uint8_t inode = create_file(pathname, DIR_FLAG);

	memset(data, 0, BLOCK_SIZE);
	data->dir_entries[0].inode = inode;
	strcpy(data->dir_entries[0].fname, ".");
	inode_vector[inode].size += sizeof data->dir_entries[0];

	data->dir_entries[1].inode = parent_inode;
	strcpy(data->dir_entries[1].fname, "..");
	inode_vector[inode].size += sizeof data->dir_entries[1];

	uint16_t block = get_first_zero(free_block_vector, 128);
	set_bit(free_block_vector, block);
	inode_vector[inode].block_pointers[0] = block;
	write_block(block, data);

	return 0;
}

// Formats disk with LLFS
void format_disk() {
	zero_disk();

	for (int i = 0; i < 11; i++) {
		set_bit(free_block_vector, i);
	}

	set_bit(free_inode_vector, 0); // 0th inode is not used.

	superblock->magic_number = 15;
	superblock->num_blocks = NUM_BLOCKS;
	superblock->num_inodes = 128;
	superblock->root_inode = mkroot();

	sync_metadata();
}

// Writes all memory-stored metadata to its spot on disk
void sync_metadata() {
	memset(data, 0, BLOCK_SIZE);
	data->superblock = *superblock;
	write_block(0, data);

	memset(data, 0, BLOCK_SIZE);
	memcpy(data->free_block_vector, free_block_vector, BLOCK_SIZE);
	write_block(1, data);

	memset(data, 0, BLOCK_SIZE);
	memcpy(data->free_inode_vector, free_inode_vector, 16);
	write_block(2, data);

	for (int i = 0; i < 8; i++) {
		memset(data, 0, BLOCK_SIZE);
		memcpy(data->inodes, inode_vector+(16*i), BLOCK_SIZE);
		write_block(i+3, data);
	}
}

// Loads metadata from disk into its spot in memory
void load_metadata() {
	read_block(0, data);
	*superblock = data->superblock;

	read_block(1, data);
	memcpy(free_block_vector, data->free_block_vector, BLOCK_SIZE);

	read_block(2, data);
	memcpy(free_inode_vector, data->free_inode_vector, 16);

	for (int i = 0; i < 8; i++) {
		read_block(i+3, data);
		memcpy(inode_vector+(16*i), data->inodes, BLOCK_SIZE);
	}
}

// Destroys all allocated memory and closes all open files.
void llfs_shutdown() {
	printf("Shutting down...\n");
	sync_metadata();
	free(data);
	free(superblock);
	free(inode_vector);
	free(free_block_vector);
	free(free_inode_vector);
	free(open_files);
	if (close(*vdisk_fd) == -1) printf("Error closing file.\n");
	free(vdisk_fd);
	close_disk();
}


// Taking a directory's inode number and a file name, returns  the inode
// number of the child if there is one, and 0 otherwise.
uint8_t search_dir(uint8_t dir_inode, const char *fname) {
	assert (strlen(fname) < 31);
	assert (get_bit(free_inode_vector, dir_inode));

	inode_t dir = inode_vector[dir_inode];

	uint16_t *block_list;
	int num_blocks = get_block_list(dir_inode, &block_list);
	if (!block_list) {
		printf("Cannot search empty directory.\n");
		return -1;
	}

	for (int i = 0; i < num_blocks; i++) {
		read_block(block_list[i], data);
		for (int j = 0; j < 16; j++) {
			if (data->dir_entries[j].inode == 0) {
				free(block_list);
				return 0;
			}

			if (!strcmp(data->dir_entries[j].fname, fname)) {
				free(block_list);
				return data->dir_entries[j].inode;
			}
		}
	}
	free(block_list);

	return 0;
}

// Parses pathname into path and returns the depth
// Must free after use.
int parse_path(const char *pathname, char ***path) {
	// Max pathname length is 4 filenames of length at most 30 delimited by 4 /'s
	assert (strlen(pathname) < 4*30+4);

	*path = malloc(4 * sizeof(char*));
	for (int i = 0; i < 4; i++) (*path)[i] = malloc(31 * sizeof(char));
	int depth = 0;
	int j = 0;
	// Start at 1 to skip the leading /
	for (int i = 1; pathname[i]; i++) {
		if (pathname[i] == '/') {
			(*path)[depth][j++] = 0;
			depth++;
			assert(depth < 4);
			j = 0;
			continue;
		}
		(*path)[depth][j++] = pathname[i];
	}
	(*path)[depth][j++] = 0;
	return depth;
}

// Used to free a path allocated by parse_path.
void free_path(char ***path) {
	for (int i = 0; i < 4; i++) free((*path)[i]);
	free(*path);
}

// Takes a pathname of the form '/a/b/c/d' and a char*.
// Returns the inode number corresponding '/a/b/c' or 0 if
// it does not exist.
uint8_t get_parent_inode(const char *pathname) {
	// Locate the parent directory
	char **path;
	int depth = parse_path(pathname, &path);
	char *fname = path[depth];

	uint8_t parent_inode_num = superblock->root_inode; // Start at /

	for (int i = 0; i < depth; i++) {
		parent_inode_num = search_dir(parent_inode_num, path[i]);
		if (parent_inode_num == 0) {
			return 0; // Directory not found
		}
	}

	free_path(&path);
	// If we got here, the directory exists and was found.
	return parent_inode_num;
}

uint8_t get_file_inode(const char *pathname) {
	if (!strcmp(pathname, "/")) return superblock->root_inode;
	uint8_t parent_inode = get_parent_inode(pathname);
	char *fname = get_file_name(pathname);
	uint8_t inode = search_dir(parent_inode, fname);
	free(fname);
	return inode;
}

// Given a pathname of the form '/a/b/c/d', return 'd'.
// Must free after use.
char *get_file_name(const char *pathname) {
	assert (strlen(pathname) < 4*30+4);
	char *fname = malloc(31 * sizeof(char));
	char **path;
	int depth = parse_path(pathname, &path);
	strncpy(fname, path[depth], strlen(path[depth]));
	fname[strlen(path[depth])] = 0;
	free_path(&path);
	return fname;
}

// Takes pathname, creates and opens file, returns fd
int llfs_create(const char *pathname) {
	int fd = -1;
	for (int i = 0; i < 10; i++) {
		if ((*open_files)[i].inode == 0) {
			fd = i;
			break;
		}
	}
	// No slot available
	assert (fd != -1);

	uint8_t inode = create_file(pathname, FILE_FLAG);
	assert (inode != 0);

	(*open_files)[fd].inode = inode;
	(*open_files)[fd].offset = 0;
	return fd;
}

// Deallocates blocks from file's list, deallocates the file's inode, and
// removes it from its parent directory's list of files.
int llfs_delete(const char *pathname) {

	uint8_t inode = get_file_inode(pathname);
	for (int i = 0; i < 10; i++) {
		if ((*open_files)[i].inode == inode) {
			printf("Cannot delete an open file.\n");
			return -1;
		}
	}

	char *fname = get_file_name(pathname);
	uint8_t parent_inode = get_parent_inode(pathname);

	uint16_t *block_list;
	int num_blocks = get_block_list(inode, &block_list);

	// Deallocate blocks
	for (int i = 0; i < num_blocks; i++) {
		clr_bit(free_block_vector, block_list[i]);
	}
	if (inode_vector[inode].single_indirect != 0) {
		clr_bit(free_block_vector, inode_vector[inode].single_indirect);
	}
	if (inode_vector[inode].double_indirect != 0) {
		clr_bit(free_block_vector, inode_vector[inode].double_indirect);
	}
	free(block_list);

	// Deallocate inode
	clr_bit(free_inode_vector, inode);
	inode_vector[inode].size = 0;
	inode_vector[inode].flags = 0;
	memset(inode_vector[inode].block_pointers, 0, 10 * sizeof(uint16_t));
	inode_vector[inode].single_indirect = 0;
	inode_vector[inode].double_indirect = 0;

	// Remove from parent dir's file list
	num_blocks = get_block_list(parent_inode, &block_list);
	for (int i = 0; i < num_blocks; i++) {
		read_block(block_list[i], data);
		for (int j = 0; j < 16; j++) {
			if (!strcmp(data->dir_entries[j].fname, fname)) {
				data->dir_entries[j].inode = 0;
				memset(data->dir_entries[j].fname, 0, 31);
				write_block(block_list[i], data);
				free(fname);
				free(block_list);
				return 0;
			}
		}
	}
	free(fname);
	printf("File not found.\n");
	return -1;
}

// If pathname == '/a/b/c/d' and filename == 'e',
// then '/a/b/c/d' becomes '/a/b/c/e'.
int llfs_rename(const char *pathname, const char *filename) {
	char *fname = get_file_name(pathname);
	uint8_t parent_inode = get_parent_inode(pathname);
	uint8_t inode = get_file_inode(pathname);

	uint16_t *block_list;
	int num_blocks = get_block_list(parent_inode, &block_list);
	for (int i = 0; i < num_blocks; i++) {
		read_block(block_list[i], data);
		for (int j = 0; j < 16; j++) {
			if (!strcmp(data->dir_entries[j].fname, fname)) {
				memset(data->dir_entries[j].fname, 0, 31);
				strcpy(data->dir_entries[j].fname, filename);
				write_block(block_list[i], data);
				free(fname);
				free(block_list);
				return 0;
			}
		}
	}
	free(fname);
	free(block_list);
	printf("File not found.\n");
	return -1;
}

// Returns file descriptor for pathname on success, -1 on failure.
int llfs_open(const char *pathname) {
	int fd = -1;
	for (int i = 0; i < 10; i++) {
		if ((*open_files)[i].inode == 0) {
			fd = i;
			break;
		}
	}
	// No slot available
	assert (fd != -1);

	uint8_t file_inode = get_file_inode(pathname);

	(*open_files)[fd].inode = file_inode;
	(*open_files)[fd].offset = 0;

	return fd;
}

int llfs_close(int fd) {
	(*open_files)[fd].inode = 0;
	(*open_files)[fd].offset = 0;
	return 0;
}

int get_block_list(uint8_t inode_num, uint16_t **block_list) {
	inode_t *inode = &inode_vector[inode_num];

	int num_blocks = inode->size % 512 == 0 ? inode->size / 512 :
		((inode->size / 512) + 1);
	int blocks_left = num_blocks;
	int block = 0;

	if (blocks_left == 0) {
		*block_list = NULL;
		return 0;
	}
	*block_list = malloc(num_blocks * sizeof(uint16_t));

	for (int i = 0; i < 10; i++) {
		(*block_list)[block++] = inode->block_pointers[i];
		if (--blocks_left == 0) return num_blocks;
	}

	read_block(inode->single_indirect, data);
	for (int i = 0; i < 256; i++) {
		(*block_list)[block++] = data->indirect_pointers[i];
		if (--blocks_left == 0) return num_blocks;
	}

	uint16_t double_indirect[256];
	read_block(inode->double_indirect, data);
	memcpy(double_indirect, data, BLOCK_SIZE);

	for (int i = 0; i < 256; i++) {
		read_block(double_indirect[i], data);
		for (int j = 0; j < 256; j++) {
			(*block_list)[block++] = data->indirect_pointers[i];
			if (--blocks_left == 0) return num_blocks;
		}
	}

	return num_blocks;
}


// Updates inode block pointers/indirection blocks with new block list.
void update_block_list(uint8_t inode_num, uint16_t *blocks, int total_blocks) {
	inode_t *inode = &inode_vector[inode_num];

	int block = 0;

	for (int i = 0; i < 10; i++) {
		inode_vector[inode_num].block_pointers[i] = blocks[block++];
		if (--total_blocks == 0) return;
	}

	memset(data, 0, BLOCK_SIZE);
	if (inode->single_indirect == 0) {
		int i = get_first_zero(free_block_vector, 128);
		set_bit(free_block_vector, i);
		inode->single_indirect = i;
	}
	for (int i = 0; i < 256; i++) {
		data->indirect_pointers[i] = blocks[block++];
		if (--total_blocks == 0) {
			write_block(inode->single_indirect, data);
			return;
		}
	}

	uint16_t double_indirect[256];
	if (inode->double_indirect == 0) {
		int i = get_first_zero(free_block_vector, 128);
		set_bit(free_block_vector, i);
		inode->double_indirect = i;
	}
	read_block(inode->double_indirect, data);
	memcpy(double_indirect, data, BLOCK_SIZE);
	for (int i = 0; i < 256; i++) {
		memset(data, 0, BLOCK_SIZE);
		for (int j = 0; j < 256; j++) {
			data->indirect_pointers[i] = blocks[block++];
			if (--total_blocks == 0) {
				write_block(double_indirect[i], data);
				return;
			}
		}
		/* printf("%d\n", double_indirect[i]); */
		write_block(double_indirect[i], data);
	}
}

// Takes fd, offset, moves the offset of the open
// file referenced by fd to offset, returns 0 on success.
int llfs_seek(int fd, uint64_t offset) {
	assert ((*open_files)[fd].inode != 0);
	assert (offset >= 0);

	openfile_t file = (*open_files)[fd];
	inode_t *inode = &inode_vector[file.inode];

	if (offset >= inode->size) {
		// Need to zero bytes from inode->size to offset
		uint64_t bytes_to_write = offset-inode->size;
		(*open_files)[fd].offset = inode->size;
		void *buf = calloc(bytes_to_write, 1);
		/* memset(buf, 'a', bytes_to_write); // Test */
		llfs_write(fd, buf, bytes_to_write);
		free(buf);
		assert ((*open_files)[fd].offset == offset);
	} else {
		(*open_files)[fd].offset = offset;
	}
	return 0;
}

// Takes fd, buf, count, returns 0 on success
int llfs_write(int fd, void *b, uint32_t count) {
	assert (fd != -1);
	assert ((*open_files)[fd].inode != 0);

	openfile_t file = (*open_files)[fd];
	inode_t *inode = &inode_vector[file.inode];

	uint64_t block_offset = file.offset / 512;
	int byte_offset = file.offset % 512;

	int total_blocks = (file.offset+count)%512 ? (file.offset+count)/512 + 1 :
		(file.offset+count)/512;

	uint16_t *block_list;
	int num_blocks = get_block_list(file.inode, &block_list);

	if (total_blocks > num_blocks) {
		if (!block_list) {
			block_list = malloc(total_blocks * sizeof(uint16_t));
		} else {
			block_list = realloc(block_list, total_blocks * sizeof(uint16_t));
		}
		uint16_t block;
		for (int i = num_blocks; i < total_blocks; i++) {
			block = get_first_zero(free_block_vector, 128);
			set_bit(free_block_vector, block);
			block_list[i] = block;
		}
		update_block_list(file.inode, block_list, total_blocks);
	}

	uint32_t bytes_written = 0;

	memset(data, 0, BLOCK_SIZE);
	read_block(block_list[block_offset], data);

	if (count - bytes_written < 512 - byte_offset) {
		// We cast to void* because we want to add the literal value
		// of byte_offset, not byte_offset * sizeof *data
		memcpy(((void*)data)+byte_offset, b, count);
		write_block(block_list[block_offset], data);
		bytes_written += count;
		(*open_files)[fd].offset += bytes_written;
		free(block_list);
		inode->size += count;
		return 0;
	}

	memcpy(((void*)data)+byte_offset, b+bytes_written, 512-byte_offset);
	write_block(block_list[block_offset], data);
	bytes_written += 512-byte_offset;

	int i;
	for (i = block_offset + 1; i < total_blocks; i++) {
		if (count - bytes_written <= 512) break;
		memcpy(data, b+bytes_written, 512);
		bytes_written += 512;
		write_block(block_list[i], data);
	}

	memset(data, 0, 512);
	memcpy(data, b+bytes_written, count-bytes_written);
	bytes_written += count-bytes_written;
	write_block(block_list[i], data);

	assert (count == bytes_written);

	(*open_files)[fd].offset += bytes_written;
	free(block_list);
	inode->size += count;
	return 0;

}

// Takes fd, buf, count, returns 0 on success.
int llfs_read(int fd, void *b, uint32_t count) {
	assert ((*open_files)[fd].inode != 0);

	if ((*open_files)[fd].offset + count >= inode_vector[(*open_files)[fd].inode].size) {
		count -= inode_vector[(*open_files)[fd].inode].size - (*open_files)[fd].offset;
	}

	openfile_t file = (*open_files)[fd];
	inode_t *inode = &inode_vector[file.inode];

	uint16_t block_offset = file.offset / 512;
	int byte_offset = file.offset % 512;

	uint16_t *block_list;
	int num_blocks = get_block_list(file.inode, &block_list);
	if (!block_list) {
		printf("Cannot read from an empty file.\n");
		return -1;
	}
	uint32_t bytes_read = 0;

	read_block(block_list[block_offset], data);

	if (count - bytes_read < 512 - byte_offset) {
		memcpy(b+bytes_read, data, count);
		bytes_read += count;
		(*open_files)[fd].offset += bytes_read;
		free(block_list);
		return count;
	}

	memcpy(b+bytes_read, data, 512-byte_offset);
	bytes_read += 512-byte_offset;

	int i;
	for (i = block_offset + 1; i < num_blocks; i++) {
		if (count - bytes_read <= 512) break;
		read_block(block_list[i], data);
		memcpy(b+bytes_read, data, 512);
		bytes_read += 512;
	}

	read_block(i, data);
	memcpy(b+bytes_read, data, count-bytes_read);
	bytes_read += count-bytes_read;

	assert(count == bytes_read);

	(*open_files)[fd].offset += bytes_read;
	free(block_list);
	return count;
}

// Prints file metadata.
void dump_file_metadata(const char *pathname) {
	char *fname = get_file_name(pathname);
	printf("Dumping metadata: %s\n", fname);
	free(fname);
	uint8_t inode_num = get_file_inode(pathname);
	assert (inode_num != 0);
	inode_t *inode = &inode_vector[inode_num];
	printf("Size: %d\n", inode->size);
	printf("Flags: %d\n", inode->flags);
	uint16_t *block_list;
	int num_blocks = get_block_list(inode_num, &block_list);
	printf("Number of blocks: %d\n", num_blocks);
	free(block_list);
}

void dump_dir(uint8_t inode, int depth) {
	if (inode_vector[inode].flags == FILE_FLAG) return;
	uint16_t *block_list;
	int num_blocks = get_block_list(inode, &block_list);
	if (!block_list) return;
	for (int i = 0; i < num_blocks; i++) {
		read_block(block_list[i], data);
		// Start at 2 to skip . and ..
		for (int j = 2; j < 16; j++) {
			if (data->dir_entries[j].inode == 0) {
				free(block_list);
				return;
			}
			printf("%d: %s\n", depth, data->dir_entries[j].fname);
			dump_dir(data->dir_entries[j].inode, depth+1);
		}
	}
	free(block_list);
}

void dump_files(const char *pathname) {
	printf("Recursively listing files in %s:\n", pathname);
	printf("<depth>: <filename>\n");
	uint8_t inode = get_file_inode(pathname);
	dump_dir(inode, 0);
}
