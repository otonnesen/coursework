#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "../include/File.h"

char *sample = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer ut lorem sed ligula ornare gravida id vitae metus. In feugiat finibus finibus. Nulla pharetra tortor ut velit porta euismod. Cras luctus nulla quis fringilla tempor. Donec congue, elit ac euismod sagittis, purus odio sollicitudin ante, vitae tincidunt turpis orci eu ipsum. Vestibulum felis elit, congue a purus eget, scelerisque ullamcorper ipsum. Sed quis pretium tellus, vel tempor lorem. Maecenas pharetra non ex a congue. Pellentesque dapibus mauris vel magna lobortis, id mattis quam aliquet. Aliquam erat volutpat.\n\nCras convallis lobortis mi consectetur congue. Donec semper, ex congue mattis porta, justo dolor malesuada dolor, a auctor libero tellus sit amet ipsum. Nunc purus urna, gravida et dui in, feugiat dictum magna. Vestibulum id nulla et quam egestas vehicula eu sed lectus. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Quisque viverra suscipit purus quis facilisis. Nam ut lacus luctus, pretium metus nec, volutpat dolor. Etiam imperdiet nisl eu turpis eleifend sollicitudin. Quisque vel dignissim tellus. In euismod convallis nibh et congue. Donec sit amet ipsum lectus. Praesent malesuada a dui eu fermentum. Maecenas euismod mi id velit scelerisque blandit nec a est.\n\nAenean tortor eros, rutrum non velit non, tempor porttitor augue. Aenean malesuada nunc et finibus ornare. Mauris quis lobortis tortor. Fusce eget elit id arcu elementum fermentum. Nullam vel viverra mi. Sed eu magna a tortor auctor tristique. Donec rhoncus et nulla pretium vulputate. Vivamus sagittis orci tortor. Proin nec mauris commodo lectus varius convallis.\n\nMauris sed velit at libero mattis molestie vitae sed ante. Nunc in ex a mauris blandit ullamcorper ac eget lectus. Proin consectetur, erat id euismod consequat, odio mi hendrerit nibh, in aliquam lacus ligula sit amet enim. Donec ut nisi a nisl elementum condimentum a ut tellus. Etiam varius ipsum eu eleifend posuere. Morbi at mi sed felis suscipit elementum suscipit pulvinar nisi. Donec porttitor vel massa vel ultrices. Nam volutpat quis leo ac pulvinar. Mauris tincidunt diam ligula, at placerat velit feugiat in. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur vestibulum sapien ligula, faucibus rutrum ex consequat non. Etiam nunc lectus, euismod et risus ut, placerat suscipit eros. Ut faucibus dapibus nisi, eget placerat velit tristique sit amet. Aenean vel ullamcorper felis, et elementum felis.\n\nNulla vitae enim non lectus condimentum euismod a ut velit. Pellentesque condimentum a arcu ac sollicitudin. Nulla ac luctus risus. Proin tempus at nibh eget dictum. Cras nisl ante, fringilla a lorem vitae, porta porta risus. Etiam porta eros et odio tempus fringilla. Quisque magna nunc, tincidunt varius facilisis et, fringilla non odio. Duis facilisis massa sed vestibulum tempor. Phasellus vestibulum bibendum sollicitudin. Mauris ligula diam, ornare a odio et, tincidunt ullamcorper lacus.";

/* This file tests the filesystem's ability to create and delete files and directories,
 * and write large amounts of data to and from them.
 */
void main() {
	printf("Beginning test1.c...\n");
	init_disk();
	printf("Formatting disk...\n");
	printf("\n");
	format_disk();
	load_metadata(); // Do NOT move this.

	printf("Creating directory /home...\n");
	llfs_mkdir("/home");
	printf("Creating directory /home/...\n");
	llfs_mkdir("/home/");
	printf("Creating directory /home/Documents...\n");
	llfs_mkdir("/home/Documents");
	printf("Creating file /home/Documents/loremipsum.txt...\n");
	int fd = llfs_create("/home/Documents/loremipsum.txt");

	printf("\n");
	printf("Writing sample to /home/Documents/loremipsum.txt...\n");
	int len = strlen(sample);
	llfs_write(fd, sample, len);

	printf("Bytes in sample text: %d\n", len);
	printf("ceil(%d/512) = %d\n", len, len/512 + (len%512==0?0:1));

	printf("\n");
	dump_file_metadata("/home/Documents/loremipsum.txt");

	printf("\n");
	printf("Writing sample to /home/Documents/loremipsum.txt 25 more times...\n");
	for (int i = 0; i < 25; i++) {
		llfs_write(fd, sample, len);
	}

	printf("Bytes in sample text (times 26): %d\n", len*26);
	printf("ceil(%d/512) = %d\n", len*26, (len*26)/512 + ((len*26)%512==0?0:1));

	printf("\n");
	dump_file_metadata("/home/Documents/loremipsum.txt");

	printf("Reading /home/Documents/lorempipsum.txt (on the vdisk) into ./lorempipsum.txt (on the physical disk)\n");

	void *buf = malloc(2000);
	FILE *f = fopen("./loremipsum.txt", "w");
	int i;
	llfs_seek(fd, 0);
	for (i = llfs_read(fd, buf, 2000); i == 2000; i = llfs_read(fd, buf, 2000)) {
		fwrite(buf, 1, 2000, f);
	}
	fwrite(buf, 1, i, f);
	fclose(f);

	printf("\n");
	printf("Closing /home/Documents/loremipsum.txt...\n");
	llfs_close(fd);

	printf("Creating directory /home/Downloads...\n");
	llfs_mkdir("/home/Downloads");
	printf("Creating directory /home/Downloads again...\n");
	llfs_mkdir("/home/Downloads");

	printf("\n");
	printf("Creating directory /csc360...\n");
	llfs_mkdir("/csc360");
	printf("Creating directory /csc360/a3...\n");
	llfs_mkdir("/csc360/a3");
	printf("Creating directory /csc360/a3/io...\n");
	llfs_mkdir("/csc360/a3/io");
	printf("Creating directory /csc360/a3/apps...\n");
	llfs_mkdir("/csc360/a3/apps");
	printf("Creating directory /csc360/a3/include...\n");
	llfs_mkdir("/csc360/a3/include");
	printf("Creating directory /csc360/a3/disk...\n");
	llfs_mkdir("/csc360/a3/disk");
	printf("Creating file /csc360/a3/io/File.c...\n");
	fd = llfs_create("/csc360/a3/io/File.c");

	printf("\n");
	f = fopen("./io/File.c", "r");
	printf("Writing from ./io/File.c (on physical disk) to /csc360/a3/io/File.c (on vdisk)\n");
	for (i = fread(buf, 1, 2000, f); i == 2000; i = fread(buf, 1, 2000, f)) {
		llfs_write(fd, buf, 2000);
	}
	llfs_write(fd, buf, i);

	printf("\n");
	dump_file_metadata("/csc360/a3/io/File.c");

	printf("\n");
	printf("Closing /csc360/a3/io/File.c...\n");
	llfs_close(fd);
	fclose(f);

	printf("Creating file /csc360/a3/disk/vdisk...\n");
	printf("This seems like a bad idea...\n");
	fd = llfs_create("/csc360/a3/disk/vdisk");
	f = fopen("/disk/vdisk", "r");
	printf("Writing ??? bytes from ./disk/vdisk (on physical disk) to /csc360/a3/disk/vdisk (on vdisk)\n");
	for (i = 0; i < 992; i++) {
		fread(buf, 1, 2000, f);
		llfs_write(fd, buf, 2000);
	}

	printf("\n");
	dump_file_metadata("/csc360/a3/disk/vdisk");

	printf("\n");
	printf("Couldn't quite fit the whole vdisk on our vdisk vdisk...\n");
	printf("Closing /csc360/a3/disk/vdisk...\n");
	llfs_close(fd);
	fclose(f);
	free(buf);
}
