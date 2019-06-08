#Specs:

Block 0 - Superblock\
	- Magic Number\
	- Number of data blocks\
	- Number of inodes\
	- Free inode list\
Block 1 - Free block vector\
Block 2 - Free inode vector\
Block 3 - Inodes\
...\
Block 10 - Inodes\
Block 11 - Directories\
...\
Block 14 - Directories\
Block 15 - Data\
...\
Block 4095 - Data

I decided to use blocks 2-9 to store inodes, allowing for a total of 512\*8/32=128
total files in the system.

Each inode has 10 direct pointers, one single indirect and one double indirect.
Each indirect block holds 256 pointers, allowing for a maximum file size of
512B \* 10 + 512B \* 256 + 512B \* 256 \* 256 = ~33.7MB (not that the disk file
is actually that big).

My filesystem supports the following actions:
	- Format disk with the filesystem
	- Create directory
	- Create file
	- Delete file/directory
	- Rename file
	- Open file
	- Close file
	- Read from file
	- Write to file
	- Seek in file

As can be (sort of) seen in the diagram thing above, I ended up making a Unix style filesystem
instead of log structured. I originally figured I'd do this just to get a feel for how everything
would fit together, but I ended up so deep into it that I couldn't really bring myself to start
again from scratch. I'm not sure what else to put here, but I would like to mention that at one
point I had 65819 valgrind errors and I now have 0, which is pretty cool. If I had more time to
work on this, I'd probably try to implement some form of caching, but seeing as it's currently
8:26 PM and this thing is due at 11:55, I think I might have to leave it be.

#Testing:

Pretty much what I did to test was just read and write a bunch of files and directories onto the
vdisk. If you look closely you can see that I copied my filesystem source code onto the disk.
Pretty cool. It worked a lot better than expected, based on my prior experience testing as I was
working on it, and it seems like I've squashed many of the bugs that were present (although
many are definitely still around).

#Usage:

`make`
Builds all tests, runnable with `./test1`, `./test2`, etc.

`make 1`, `make 2`, etc.
Builds individual tests.

`make clean`
Cleans up.
