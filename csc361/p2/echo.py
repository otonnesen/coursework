import socket
import sys
import time
import random

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.bind(('localhost', int(sys.argv[1])))

while True:
        data, addr = sock.recvfrom(4096)
        # time.sleep(0.1)
        # if random.randint(0,3) == 0:
        #     print('packet dropped')
        #     continue
        sock.sendto(data, addr)
