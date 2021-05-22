import sys
import time

from rdp import RDPSocket


def parse_args():
    usage = 'Usage: python3 sor-client.py server_ip_address \
server_udp_port_number client_buffer_size client_payload_length \
read_file_name write_file_name [read_file_name write_file_name]*'
    try:
        args = sys.argv[1], int(sys.argv[2]),\
                int(sys.argv[3]), int(sys.argv[4])
    except (IndexError, ValueError):
        print(usage)
        exit(1)

    file_pairs = []
    if (len(sys.argv) - 5) % 2 != 0:
        print(usage)
        exit(1)
    num_pairs = (len(sys.argv) - 5) // 2
    if num_pairs < 1:
        print(usage)
        exit(1)

    for i in range(num_pairs):
        file_pairs.append((sys.argv[5 + (2 * i)], sys.argv[6 + (2 * i)]))

    return args + (file_pairs,)


def main():
    ip_addr, port, buf_size, payload_len, file_pairs = parse_args()

    sock = RDPSocket(buf_size, payload_len, logging=True)

    sock.connect((ip_addr, port))
    sock.settimeout(1)

    for r, w in file_pairs:
        req = f'\
GET /{r} HTTP/1.0\n\
Connection: Keep-alive\n\n\
'
        sock.send(req)
        start = time.time()
        buf_len = len(sock.rbuf)
        i = 0
        while not sock.buf_ready:
            if buf_len == len(sock.rbuf):
                i += 1
            else:
                buf_len = len(sock.rbuf)
            sock._loop_client()
        r = sock.recv()
        if r:
            if '404' not in r.splitlines()[0]:
                r = r.split('\r\n\r\n', 1)[1]
        else:
            r = ''
        with open(w, 'w') as f:
            f.truncate(0)
            f.seek(0)
            f.write(r)

    sock.close()

    while sock._state != sock._closed:
        sock._loop_client()

if __name__ == '__main__':
    main()
