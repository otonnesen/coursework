#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "../include/File.h"

void main() {
	printf("Beginning test2.c...\n");
	init_disk();
	format_disk();
	load_metadata();

	printf("Creating file /old...\n");
	int fd = llfs_create("/old");
	printf("Renaming /old to /new...\n");
	llfs_rename("/old", "new");
	dump_files("/");
	printf("Closing file /new...\n");

	printf("\n");
	llfs_close(fd);
	printf("Deleting file /new...\n");
	llfs_delete("/new");
	dump_files("/");

	printf("\n");
	printf("Creating file /test...\n");
	fd = llfs_create("/test");
	void *buf = malloc(11);
	memset(buf, 'a', 10);
	printf("Writing 'aaaaaaaaaa' to /test...\n");
	llfs_write(fd, buf, 10);
	printf("Seeking to the beginning of /test...\n");
	llfs_seek(fd, 0);
	printf("Writing 'bbbbb' to /test...\n");
	memset(buf, 'b', 5);
	llfs_write(fd, buf, 5);
	printf("Seeking to the beginning of /test...\n");
	llfs_seek(fd, 0);
	printf("Reading first 10 bytes from /test...\n");
	llfs_read(fd, buf, 10);
	((char*)buf)[10] = 0;
	printf("First 10 bytes: %s\n", (char*)buf);
	free(buf);
}
