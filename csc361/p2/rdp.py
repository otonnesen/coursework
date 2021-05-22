import datetime
import socket
import sys


MAX_PAYLOAD_SIZE = 1024
# MAX_WINDOW_SIZE = 2048
MAX_WINDOW_SIZE = 5 * 1024

ACKNOWLEDGE = 'Acknowledgment'
LENGTH = 'Length'
SEQUENCE = 'Sequence'
WINDOW = 'Window'


class Packet:
    def __init__(self, command, headers, payload):
        self.command = command
        self.headers = headers
        self.payload = payload

    def __str__(self):
        '''
        Turns packet object into a string of the form
        ```
        COMMAND
        Header: Value
        ...
        Header: Value

        PAYLOAD
        ```
        '''
        s = f'{self.command}\r\n'
        for h in self.headers:
            s += f'{h}: {self.headers[h]}'
            s += '\r\n'

        if self.payload:
            s += '\r\n'
            s += self.payload

        return s

    def __repr__(self):
        return f'Packet<{self.command}, {self.headers}>'

    def from_str(packets):
        '''
        Please look away.
        '''
        p = []
        command, packets = packets.split(None, 1)

        while command != '':
            if packets == '':
                break
            headers = {}
            payload = ''
            if command == 'DAT':  # Packet has a payload
                h, packets = packets.split('\r\n\r\n', 1)
                for header in h.split('\r\n'):
                    header = header.replace(' ', '').split(':')
                    headers[header[0]] = int(header[1])
                assert LENGTH in headers
                payload = packets[:headers[LENGTH]]
                packets = packets[headers[LENGTH]:]
                p.append(Packet(command, headers, payload))
                if len(packets.split('\r\n', 1)) == 1:
                    command, packets = packets, ''
                else:
                    command, packets = packets.split(None, 1)
            else:  # Packet has no payload
                header, packets = packets.split('\r\n', 1)
                header = header.replace(' ', '').split(':')
                while len(header) == 2:
                    headers[header[0]] = int(header[1])
                    if packets == '':
                        header[0] = ''
                        break
                    if len(packets.split('\r\n', 1)) == 1:
                        header, packets = packets, ''
                    else:
                        header, packets = packets.split('\r\n', 1)
                    header = header.replace(' ', '').split(':')
                p.append(Packet(command, headers, payload))
                command = header[0]

        return p

    def log(self, method):
        timezone = datetime.datetime.now().strftime('%a %b %d %H:%M:%S PDT %Y')
        s = f'{timezone}: {method}; {self.command}'

        for i in self.headers:
            s += f'; {i}: {self.headers[i]}'

        return s


class RDPSocket:
    def __init__(self):
        self.ip_addr = None
        self.port = None
        self.sock = None
        self.timeout = 0

    def bind(self, ip_addr, port):
        self.ip_addr = ip_addr
        self.port = port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

    def settimeout(self, timeout):
        self.sock.settimeout(timeout)

    def send(self, packet):
        print(packet.log('Send'))
        self.sock.sendto(bytes(packet.__str__(), 'utf-8'),
                         (self.ip_addr, self.port))

    def receive(self):
        try:
            data, _ = self.sock.recvfrom(4096)
            p = Packet.from_str(data.decode('utf-8'))
            return p
        except socket.timeout:  # socket timed out
            return []


class RDPConnection:
    '''
    An RDP connection.
    Instantiate a server connection by passing 'server' for `conn_type`.
    Instantiate a client connection by passing 'client' for `conn_type`.
    A server will read from `fname`, and a client will write to `fname`.


    self.seq:
        Server Mode: last SEQ sent to receiver
        Client Mode: unused

    self.ack:
        Server Mode: last ACK received from receiver
        Client Mode: last ACK sent to sender

    self.win:
        Server Mode: receiver's current window size
        Client Mode: current window size

    self.buf:
        Server Mode: unused
        Client Mode: list of packets to be processed

    self.file_len:
        Server Mode: length of file to be sent;
                        only written once eof is reached
        Client Mode: unused



    RDPConnection.receive(packet) will read a packet and update its internal
    state to reflect the packet contents.
    '''
    def __init__(self, conn_type, fname):
        self.seq = 0
        self.ack = 0
        self.win = 0
        self.buf = []  # list of packets to process
        self.file_len = -1

        self.established = False
        self.terminated = False

        self.syn_timeout_counter = 0
        self.dat_timeout_counter = 0
        self.buf_timeout_counter = 0
        self.fin_timeout_counter = 0
        self.ack_dups = 0

        if conn_type == 'server':
            self.syn_sent = False
            self.fin_sent = False
            self.server = True
        elif conn_type == 'client':
            self.syn_rcvd = False
            self.fin_rcvd = False
            self.server = False
        else:
            raise

        if self.server:
            # Open fname for reading
            self.file = open(fname, 'r')
            self.file.seek(0)
        else:
            # Open fname for writing
            self.file = open(fname, 'w')
            self.file.truncate(0)
            self.file.seek(0)

            self.win = MAX_WINDOW_SIZE

    def establish(self):
        '''
        Checks if connection is established and attempts to establish one
        if not already connected.

        returns packet if not connected, none otherwise
        '''
        if not self.established:
            if self.server:  # Server mode
                if not self.syn_sent:  # SYN not sent
                    self.syn_sent = True
                    return Packet('SYN', {SEQUENCE: 0, LENGTH: 0}, '')
                else:  # SYN sent
                    self.syn_timeout_counter += 1
                    if self.syn_timeout_counter == 3:
                        self.syn_timeout_counter = 0
                        return Packet('SYN', {SEQUENCE: 0, LENGTH: 0}, '')
                    else:
                        return None
            else:  # Client mode
                if not self.syn_rcvd:  # SYN not received
                    return None
                else:   # SYN received
                    self.established = True
                    return Packet('ACK', {ACKNOWLEDGE: self.ack,
                                  WINDOW: self.win}, '')
        else:
            return None

    def transfer_data(self):
        assert self.established
        if self.server:  # Server mode
            if self.win > 0:  # Still room in buffer
                self.file.seek(self.seq - 1)
                dat = self.file.read(1024)
                if len(dat) == 0:  # EOF
                    if self.file_len == -1:
                        # Set file_len if unset
                        self.file_len = self.file.tell()
                    if self.file_len <= self.ack:
                        # Once last packet is received, ack
                        # should be file_len + 1.
                        self.fin_sent = True
                        return Packet('FIN', {SEQUENCE: self.seq,
                                      LENGTH: 0}, '')
                    else:  # DAT lost
                        # Wait for timeout, then reset
                        # SEQ to the last received ACK.
                        self.dat_timeout_counter += 1
                        if self.dat_timeout_counter == 3:
                            self.seq = self.ack
                            self.file.seek(self.seq - 1)
                        else:
                            return None
                else:  # Still more data to process
                    p = Packet('DAT', {SEQUENCE: self.seq,
                               LENGTH: len(dat)}, dat)
                    self.win -= len(dat)
                    self.seq += len(dat)
                    return p

            else:  # No more room in buffer, wait for receiver to process
                return None
        else:  # Client mode
            if len(self.buf) == 0:  # No packets to process
                self.dat_timeout_counter += 1
                if self.dat_timeout_counter == 3:
                    self.dat_timeout_counter = 0
                    return Packet('ACK', {ACKNOWLEDGE: self.ack,
                                          WINDOW: self.win}, '')
                else:
                    return None
            else:  # Still packets in buf to process
                p = self.buf.pop(0)
                self.win += p.headers[LENGTH]
                if p.headers[SEQUENCE] != self.ack:
                    return None
                self.ack += p.headers[LENGTH]
                self.file.write(p.payload)
                return Packet('ACK', {ACKNOWLEDGE: self.ack,
                              WINDOW: self.win}, '')

    def terminating(self):
        if self.server:
            return self.fin_sent
        else:
            return self.fin_rcvd

    def terminate(self):
        if self.server:  # Server mode
            self.fin_timeout_counter += 1
            if self.fin_timeout_counter == 3:
                self.fin_timeout_counter = 0
                return Packet('FIN', {SEQUENCE: self.seq, LENGTH: 0}, '')
            else:
                return None
        else:  # Client mode
            self.terminated = True
            return Packet('ACK', {ACKNOWLEDGE: self.ack,
                          WINDOW: self.win}, '')

    def loop(self):
        if self.terminated or self.terminating():
            return self.terminate()

        if not self.established:
            return self.establish()

        p = self.transfer_data()

        if p is not None:
            return p

    def receive(self, packet):
        print(packet.log('Receive'))
        if packet.command == 'ACK':
            assert self.server
            assert ACKNOWLEDGE in packet.headers
            assert WINDOW in packet.headers
            if self.fin_sent:
                if packet.headers[ACKNOWLEDGE] > self.file_len:
                    self.terminated = True
                return
            if not self.established:
                if not self.syn_sent:
                    return
                else:
                    self.seq = 1
                    self.established = True
            if self.ack == packet.headers[ACKNOWLEDGE]:
                self.ack_dups += 1
                if self.ack_dups == 3:
                    self.ack_dups = 0
                    self.seq = packet.headers[ACKNOWLEDGE]
            else:
                self.ack_dups = 0
            self.ack = packet.headers[ACKNOWLEDGE]
            self.win = packet.headers[WINDOW]

        elif packet.command == 'DAT':
            assert not self.server
            if self.win - packet.headers[LENGTH] >= 0:
                self.buf.append(packet)
                self.win -= packet.headers[LENGTH]

        elif packet.command == 'FIN':
            assert not self.server
            self.fin_rcvd = True
            self.ack = packet.headers[SEQUENCE] + 1

        elif packet.command == 'RST':
            assert not self.server
            exit(-1)

        elif packet.command == 'SYN':
            assert not self.server
            self.established = False
            self.syn_rcvd = True
            self.ack = 1


def parse_args():
    try:
        return sys.argv[1], int(sys.argv[2]), sys.argv[3], sys.argv[4]
    except (IndexError, ValueError):
        print('Usage: python3 rdb.py ip_addr port src_file dst_file')
        exit(1)


def main():

    ip_addr, port, r_file, w_file = parse_args()

    sock = RDPSocket()
    sock.bind(ip_addr, port)
    sock.settimeout(1)

    sender = RDPConnection('server', r_file)
    receiver = RDPConnection('client', w_file)

    while True:
        if sender.terminated and receiver.terminated:
            break
        p = sender.loop()
        if p is not None:
            sock.send(p)
        p = receiver.loop()
        if p is not None:
            sock.send(p)
        packets = sock.receive()
        # Set short timeout to see if multiple packets in queue
        sock.settimeout(0.05)
        while len(packets) > 0:
            for p in packets:
                if p.command == 'ACK':
                    sender.receive(p)
                else:
                    receiver.receive(p)
            packets = sock.receive()
        # Reset timeout to 1
        sock.settimeout(1)


if __name__ == '__main__':
    main()
