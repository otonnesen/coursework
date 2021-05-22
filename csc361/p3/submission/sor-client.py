import datetime
import socket
import sys
import time


ACK = 'Acknowledgment'
LEN = 'Length'
SEQ = 'Sequence'
WIN = 'Window'


class Packet:
    def __init__(self, commands, headers, payload=''):
        if isinstance(commands, list):
            self.commands = commands
        else:
            self.commands = [commands]
        self.headers = headers
        self.payload = payload

    def __str__(self):
        '''
        Turns packet object into a string of the form
        ```
        (COMMAND )*
        Header: Value
        ...
        Header: Value

        PAYLOAD
        ```
        '''
        s = f'{self.commands[0]}'
        for c in self.commands[1:]:
            s += f' {c}'
        s += '\r\n'
        for h in self.headers:
            s += f'{h}: {self.headers[h]}'
            s += '\r\n'

        if self.payload:
            s += '\r\n'
            s += self.payload

        return s

    def __repr__(self):
        return f'Packet<{self.commands}, {self.headers}>'

    def from_str(packets):
        '''
        Please look away.
        '''
        p = []
        command, packets = packets.split('\r\n', 1)
        commands = command.split(' ')

        while commands[0] != '':
            if packets == '':
                break
            headers = {}
            payload = ''
            if 'DAT' in commands:  # Packet has a payload
                h, packets = packets.split('\r\n\r\n', 1)
                for header in h.split('\r\n'):
                    header = header.split(': ')
                    headers[header[0]] = int(header[1])
                assert LEN in headers
                payload = packets[:headers[LEN]]
                packets = packets[headers[LEN]:]
                p.append(Packet(commands, headers, payload))
                if len(packets.split('\r\n', 1)) == 1:
                    command, packets = packets, ''
                else:
                    command, packets = packets.split('\r\n', 1)
                commands = command.split(' ')
            else:  # Packet has no payload
                header, packets = packets.split('\r\n', 1)
                header = header.split(': ')
                while len(header) == 2:
                    headers[header[0]] = int(header[1])
                    if packets == '':
                        header[0] = ''
                        break
                    if len(packets.split('\r\n', 1)) == 1:
                        header, packets = packets, ''
                    else:
                        header, packets = packets.split('\r\n', 1)
                    header = header.split(': ')
                p.append(Packet(commands, headers, payload))
                commands = header[0].split(' ')

        return p

    def log(self, method):
        timezone = datetime.datetime.now().strftime('%a %b %d %H:%M:%S PDT %Y')
        s = f'{timezone}: {method}; {"|".join(self.commands)}'

        for i in self.headers:
            s += f'; {i}: {self.headers[i]}'

        return s

    def ack(self):
        if ACK in self.headers:
            return self.headers[ACK]
        return 0

    def seq(self):
        if SEQ in self.headers:
            return self.headers[SEQ]
        return 0

    def len(self):
        if LEN in self.headers:
            return self.headers[LEN]
        return 0

    def win(self):
        if WIN in self.headers:
            return self.headers[WIN]
        return 0


class RDPSocket:
    def __init__(self, buffer_size, payload_length, logging=False):
        # Socket variables
        self.laddr = None
        self.raddr = None
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self._timeout = 0
        self.connections = {}
        self._prev_state = None
        self._state = self._closed

        self.buffer_size = buffer_size
        self.payload_length = payload_length

        # TCP variables
        self.seq = 0
        self.rnxt = 0
        self.snxt = 0
        self.suna = 0
        self.dack = 0
        self.swin = 1024
        self.rwin = buffer_size
        self.iss = 0
        self.irs = 0
        self.rbuf = ''
        self.buf_ready = False
        self.sbuf = []
        self.rexmt = []

        self._close_queued = False
        self.logging = logging
        self.max_connections = 0

    def bind(self, laddr):
        self.laddr = laddr
        self.sock.bind(self.laddr)

    def settimeout(self, timeout):
        self._timeout = timeout
        self.sock.settimeout(timeout)

    def listen(self, n):
        self.max_connections = n

    def _send(self, packet, addr=None):
        if addr is None:
            addr = self.raddr
        if self.logging:
            print(packet.log('Send'))
        self.sock.sendto(bytes(packet.__str__(), 'utf-8'), addr)

    def _recv(self):
        try:
            data, addr = self.sock.recvfrom(16384)
            packets = Packet.from_str(data.decode('utf-8'))
            for p in packets:
                if self.logging:
                    print(p.log('Receive'))
            return packets, addr
        except socket.timeout:
            return [], None

    def _loop_server(self):
        for addr in self.connections:
            conn = self.connections[addr]
            if conn._close_queued and len(conn.sbuf) == 0:
                conn.close()
                if conn._state == _closed:
                    del self.connections[addr]
        packets, addr = self._recv()

        if addr not in self.connections:
            self.accept(packets, addr)

        if packets:
            for p in packets:
                resp = self.connections[addr]._state(p)
                for r in resp:
                    self.connections[addr]._send(r)
        else:
            for addr in self.connections:
                if len(self.connections[addr].sbuf) > 0:
                    resp = self.connections[addr]._retransmit()
                    for r in resp:
                        self.connections[addr]._send(r)

    def _loop_client(self):
        packets, _ = self._recv()
        if packets:
            for p in packets:
                resp = self._state(p)
                for r in resp:
                    self._send(r)
        else:
            resp = self._retransmit()
            for r in resp:
                self._send(r)

    def connect(self, raddr):
        assert self._state == self._closed

        if self.raddr:
            print('Endpoint already connected.')
            exit(1)

        self.raddr = raddr

        self.iss = 0  # Can pick random seqno here
        self.snxt = self.iss + 1

        p = Packet('SYN', {SEQ: self.iss, WIN: self.rwin})
        self._send(p)

        self.rexmt.insert(0, p)

        self._prev_state = self._state
        self._state = self._syn_sent

        while self._state != self._established:
            self._loop_client()

    def _close_client(self):
        pass

    def _close_server(self, addr):
        fin_sent = False
        ack_rcvd = False
        timeout_counter = 0

    def close(self, addr=None):
        assert self._state in [self._listen, self._syn_sent, self._established]

        if len(self.sbuf) > 0:
            self._close_queued = True

        if self._state in [self._listen, self._syn_sent]:
            self._prev_state = self._state
            self._state = self._closed
        elif self._state == self._established:
            self._send(Packet('FIN', {SEQ: self.snxt}))
            self.snxt += 1
            self._prev_state = self._state
            self._state = self._fin_wait_1

        # if addr:
        #     self._close_server(addr)
        # else:
        #     self._close_client()


    def _retransmit(self):
        if self.rexmt:
            return [self.rexmt[-1]]
        else:
            return []

    def _next_ack(self):
        if self.snxt >= (self.suna + self.swin) or len(self.sbuf) == 0:
            return Packet('ACK', {SEQ: self.snxt, ACK: self.rnxt, WIN: self.rwin})
        d_seg = self.sbuf.pop()

        p =  Packet(['DAT', 'ACK'], {SEQ: self.snxt, ACK: self.rnxt,
                    LEN: len(d_seg), WIN: self.rwin}, d_seg)
        self.snxt += p.len()
        self.rexmt.insert(0, p)
        return p

    def send(self, data):
        assert self._state == self._established

        for i in range((len(data) // self.payload_length) + 1):
            d_seg = data[i * self.payload_length:(i+1) * self.payload_length]
            self.sbuf.insert(0, d_seg)

        self.sbuf.insert(0, '')

        p = self._next_ack()
        self.rexmt.insert(0, p)
        self._send(p)


    def recv(self, size=0):
        assert self._state in [self._established, self._fin_wait_1, self._fin_wait_2]

        if size == 0:
            r = self.rbuf
            self.rbuf = ''
            self.buf_ready = False
            return r
        elif len(self.rbuf) >= size:
            r = self.rbuf[:size]
            self.rbuf = self.rbuf[size:]
            return r
        return None

    def accept(self, packets=None, addr=None):
        assert self._state == self._closed
        # assert self.max_connections == 0 or len(self.connections) + 1 <= self.max_connections

        try:
            if packets is None and addr is None:
                packets, addr = self._recv()
            if addr:
                if addr in self.connections:
                    # Endpoint already connected.
                    self._send(Packet('RST', {SEQ: 0, LEN: 0}), addr)
                    return None
                conn = RDPSocket(self.buffer_size, self.payload_length, logging=self.logging)
                conn.settimeout(self._timeout)
                conn.laddr = self.laddr
                conn.raddr = addr

                conn._state = conn._listen

                for p in packets:
                    resp = conn._state(p)
                    for r in resp:
                        conn._send(r)

                self.connections[addr] = conn

                return conn

            return None

        except socket.timeout:
            return None

    def _seq_acceptable(self, packet):
        rwin = self.rwin
        rnxt = self.rnxt
        length = packet.len()
        seq = packet.seq()
        if rwin == 0:
            if length == 0:
                return seq == rnxt
            return False
        if length == 0:
            return rnxt <= seq and seq < rnxt + rwin
        return (rnxt <= seq and seq < rnxt + rwin) or\
                (rnxt <= seq + length - 1 and seq + length - 1 < rnxt + rwin)

    def _acked(self, packet):
        self.rexmt = list(filter(lambda p: p.seq() + p.len() < packet.ack(), self.rexmt))

    def _closed(self, packet):
        return [Packet('RST', {SEQ: 0, LEN: 0})]

    def _listen(self, packet):
        packets = []
        if 'RST' in packet.commands:
            return packets
        elif 'ACK' in packet.commands:
            return [Packet('RST', {SEQ: 0, LEN: 0})]
        elif 'SYN' in packet.commands:
            self.rnxt = packet.seq() + 1
            self.irs = packet.seq()
            self.iss = 0  # Can pick random initial seqno here
            packets.append(Packet(['SYN', 'ACK'], {SEQ: self.iss, ACK: self.rnxt, WIN: self.rwin}))
            self.snxt = self.iss + 1
            self.suna = self.iss
            self._prev_state = self._state
            self._state = self._syn_rcvd

        return packets

    def _syn_sent(self, packet):
        if 'ACK' in packet.commands:
            if packet.ack() <= self.iss or packet.ack() > self.snxt\
                    or packet.ack() < self.suna or packet.ack() > self.snxt:
                return [Packet('RST', {SEQ: packet.ack()})]
            if 'RST' in packet.commands:
                self._prev_state = self._state
                self._state = self._closed
                return []
            if 'SYN' in packet.commands:
                self.rnxt = packet.seq() + 1
                self.irs = packet.seq()
                self.suna = packet.ack()
                if self.suna > self.iss:
                    self.rexmt.pop()
                    self._prev_state = self._state
                    self._state = self._established
                    return [Packet('ACK', {SEQ: self.suna, ACK: self.rnxt, WIN: self.rwin})]
                self._prev_state = self._state
                self._state = self._syn_rcvd
                return [Packet(['SYN', 'ACK'], {SEQ: self.iss, ACK: self.rnxt, WIN: self.rwin})]

            return []
        if 'RST' in packet.commands:
            return []
        if 'SYN' in packet.commands:
            self.rnxt = packet.seq() + 1
            self.irs = packet.seq()
            self._prev_state = self._state
            self._state = self._syn_rcvd
            return [Packet(['SYN', 'ACK'], {SEQ: self.iss, ACK: self.rnxt, WIN: self.rwin})]
        return []

    def _syn_rcvd(self, packet):
        if not self._seq_acceptable(packet):
            if 'RST' in packet.commands:
                return []
            elif 'SYN' in packet.commands:
                return [Packet(['SYN', 'ACK'], {SEQ: self.iss, ACK: self.rnxt, WIN: self.rwin})]

            return [Packet('ACK', {SEQ: self.snxt, ACK: self.rnxt, WIN: self.rwin})]

        if 'RST' in packet.commands:
            if self._prev_state == self._listen:
                self._prev_state = self._state
                self._state = self._listen
            else:
                self._prev_state = self._state
                self._state = self._closed
            return []

        if 'SYN' in packet.commands:
            self._prev_state = self._state
            self._state = self._closed
            return [Packet('RST', {SEQ: self.snxt})]

        if 'ACK' in packet.commands:
            if self.suna <= packet.ack() and packet.ack() <= self.snxt:
                self._prev_state = self._state
                self._state = self._established
            else:
                if 'FIN' in packet.commands:
                    self.rnxt = packet.seq() + 1
                    packets.append(Packet('ACK', {SEQ: self.snxt, ACK: self.rnxt}))
                    self._prev_state = self._state
                    self._state = self._close_wait
                    return []

                self._prev_state = self._state
                self._state = self._closed
                return [Packet('RST', {SEQ: self.snxt})]

        return []

    def _established(self, packet):
        packets = []
        if not self._seq_acceptable(packet):
            if 'RST' in packet.commands:
                return []
            packets.append(Packet('ACK', {SEQ: self.snxt, ACK: self.rnxt, WIN: self.rwin}))
            return packets

        if 'RST' in packet.commands:
            self._prev_state = self._state
            self._state = self._closed
            return packets

        if 'SYN' in packet.commands:
            self._prev_state = self._state
            self._state = self._closed
            packets.append(Packet('RST', {SEQ: self.snxt}))
            return packets

        if 'FIN' in packet.commands:
            self.rnxt = packet.seq() + 1
            packets.append(Packet('ACK', {SEQ: self.snxt, ACK: self.rnxt}))
            self._prev_state = self._state
            self._state = self._close_wait

        if 'ACK' not in packet.commands:
            return packets

        if self.suna < packet.ack() and packet.ack() <= self.snxt:
            self.swin = packet.win()
            self._acked(packet)

            self.suna = packet.ack()
        else:
            if packet.ack() != self.suna:
                return packets
            self.dack += 1
            if self.dack == 3:
                if len(self.sbuf) > 0:
                    p = self._next_ack()
                    packets.append(p)
                    self.rexmt.insert(0, p)
                    self.snxt += packet.len()

        if packet.seq() == self.rnxt:
            if packet.len() == 1 and packet.payload == '':
                self.buf_ready = True
            else:
                self.rbuf += packet.payload
            self.rnxt = packet.seq() + packet.len()

        # self.rwin += packet.len()
        if 'DAT' in packet.commands or\
                (self.snxt < (self.suna + self.swin) and len(self.sbuf) != 0):
            packets.append(self._next_ack())
        return packets

    def _fin_wait_1(self, packet):
        packets = []

        if not self._seq_acceptable(packet):
            if 'RST' in packet.commands:
                return []
            return [Packet('ACK', {SEQ: self.snxt, ACK: self.rnxt})]

        if 'RST' in packet.commands:
            self._prev_state = self._state
            self._state = self._closed
            return []

        if 'SYN' in packet.commands:
            self._prev_state = self._state
            self._state = self._closed
            return [Packet('RST', {SEQ: self.snxt})]

        if 'ACK' not in packet.commands:
            return []

        # Maybe I can skip the rest here?
        self._prev_state = self._state
        self._state = self._closed
        return packets

        if self.suna < packet.ack() and packet.ack() <= self.snxt:
            self.suna = packet.ack()
            self._acked(packet)

        if packet.seq() == self.rnxt:
            if packet.len() == 1 and packet.payload == '':
                self.buf_ready = True
            else:
                self.rbuf += packet.payload
            self.rnxt = packet.seq() + packet.len()

        # packets.append(Packet('ACK', {SEQ: self.snxt, ACK: self.rnxt}))

        if packet.ack() == self.snxt:
            if 'FIN' in packet.commands:
                self._prev_state = self._state
                self._state = self._time_wait
                return packets
            else:
                self._prev_state = self._state
                self._state = self._fin_wait_2
                return packets
        else:
            if 'FIN' in packet.commands:
                self.rnxt = packet.seq() + 1
                packets.append(Packet('ACK', {SEQ: self.snxt, ACK: self.rnxt}))
                self._prev_state = self._state
                self._state = self._closing
            else:
                return packets

    def _fin_wait_2(self, packet):
        packets = []

        if not self._seq_acceptable(packet):
            if 'RST' in packet.commands:
                return []

            return [Packet('ACK', {SEQ: self.snxt, ACK: self.rnxt, WIN: self.rwin})]
        if 'RST' in packet.commands:
            self._prev_state = self._state
            self._state = self._closed
            return []

        if 'SYN' in packet.commands:
            packets.append(Packet('RST', {SEQ: self.snxt}))
            self._prev_state = self._state
            self._state = self._closed
            return packets

        if 'ACK' not in packet.commands:
            return []

        if self.suna < packet.ack() and packet.ack() <= self.snxt:
            self.suna = packet.ack()
            self._acked(packet)

        if packet.seq() != self.rnxt:
            return packets
        else:
            if packet.len() == 1 and packet.payload == '':
                self.buf_ready = True
            else:
                self.rbuf += packet.payload
            self.rnxt = packet.seq() + packet.len()
            packets.append(Packet('ACK', {SEQ: self.snxt, ACK: self.rnxt}))

            # self.rwin += packet.len()

        if 'FIN' in packet.commands:
            self.rnxt = packet.seq() + 1
            packets.append(Packet('ACK', {SEQ: self.snxt, ACK: self.rnxt}))
            self._prev_state = self._state
            self._state = self._time_wait
            return packets
        return []

    def _closing(self, packet):
        packets = []

        if not self._seq_acceptable(packet):
            if 'RST' in packet.commands:
                return []

            return [Packet('ACK', {SEQ: self.snxt, ACK: self.rnxt, WIN: self.rwin})]

        if 'RST' in packet.commands:
            self._prev_state = self._state
            self._state = self._closed
            return []

        if 'SYN' in packet.commands:
            packets.append(Packet('RST', {SEQ: self.snxt}))
            self._prev_state = self._state
            self._state = self._closed
            return packets

        if 'ACK' in packet.commands and\
                (self.suna < packet.ack() and packet.ack() <= self.snxt) and\
                packet.ack() > self.snxt:
            self._prev_state = self._state
            self._state = self._time_wait
            return packet


    def _time_wait(self, packet):
        packets = []

        if not self._seq_acceptable(packet):
            if 'RST' in packet.commands:
                return []

            return [Packet('ACK', {SEQ: self.snxt, ACK: self.rnxt, WIN: self.rwin})]

        if 'RST' in packet.commands:
            self._prev_state = self._state
            self._state = self._closed
            return []

        if 'SYN' in packet.commands:
            if 'ACK' in packet.commands and\
                    (self.suna < packet.ack() and packet.ack() <= self.snxt) and\
                    packet.ack() > self.snxt:

                self.rnxt = packet.seq() + 1
                packets.append(Packet('ACK', {SEQ: self.snxt, ACK: self.rnxt}))
            return packets

        packets.append(Packet('RST', {SEQ: self.snxt}))
        self._prev_state = self._state
        self._state = self._closed
        return packets

    def _close_wait(self, packet):
        packets = []

        if not self._seq_acceptable(packet):
            if 'RST' in packet.commands:
                return []

            return [Packet('ACK', {SEQ: self.snxt, ACK: self.rnxt, WIN: self.rwin})]

        if 'RST' in packet.commands:
            self._prev_state = self._state
            self._state = self._closed
            return []

        if 'SYN' in packet.commands:
            packets.append(Packet('RST', {SEQ: self.snxt}))
            self._prev_state = self._state
            self._state = self._closed
            return packets

        if 'ACK' not in packet.commands:
            return []

        if self.suna < packet.ack() and packet.ack() <= self.snxt:
            self.swin = packet.win()
            self._acked(packet)
            self.suna = packet.ack()
            if 'FIN' in packet.commands:
                self.rnxt = packet.seq() + 1
                packets.append(Packet('ACK', {SEQ: self.snxt, ACK: self.rnxt}))

        return packets


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
