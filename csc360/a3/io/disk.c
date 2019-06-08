#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include <string.h>

#include "../include/disk.h"

int		*disk_fd;

void open_disk(int fd) {
	disk_fd = malloc(sizeof *disk_fd);
	*disk_fd = fd;
	if (-1 == *disk_fd) {
		printf("Error opening vdisk. Aborting...\n");
		exit(-1);
	}
}

void close_disk() {
	free(disk_fd);
}

void zero_disk() {
	assert (-1 != *disk_fd);
	char *data = calloc(512, 8);
	lseek(*disk_fd, 0, SEEK_SET);
	for (int i = 0; i < 4096/8; i++) {
		if (-1 == write(*disk_fd, data, 512*8)) {
			printf("Error zeroing vdisk. Aborting...\n");
			exit(-1);
		}
	}
	free(data);
}

void write_block(int block, void *data) {
	assert (-1 != *disk_fd);
	assert (block < 4096);
	lseek(*disk_fd, BLOCK_OFFSET(block), SEEK_SET);
	if (-1 == write(*disk_fd, data, BLOCK_SIZE)) {
		printf("Error writing to vdisk. Aborting...\n");
		exit(-1);
	}
}

void read_block(int block, void *data) {
	assert (-1 != *disk_fd);
	assert (block < 4096);
	lseek(*disk_fd, BLOCK_OFFSET(block), SEEK_SET);
	if (-1 == read(*disk_fd, data, BLOCK_SIZE)) {
		printf("Error reading vdisk. Aborting...\n");
		exit(-1);
	}
}
