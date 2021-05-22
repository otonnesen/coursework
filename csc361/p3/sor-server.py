import datetime
import sys
import time

from rdp import RDPSocket

class BadRequest(Exception):
    pass

class HTTPRequest:
    def __init__(self, keepalive=False):
        self.status_line_parsed = False
        self.headers_parsed = False
        self.body_parsed = False # Unused since we're only accepting GET requests

        self.STATUS_CODE = {200: 'OK', 404: 'Not Found', 400: 'Bad Request'}

        self.status = 200
        self.headers = {}
        self.keepalive = keepalive

    def parse_line(self, line):
        # If we haven't yet seen a status line, we expect that next
        if not self.status_line_parsed:
            self.req_status_line = line

            line = line.split()

            # Status line must be of the form '<METHOD> <URI> <HTTP-VERSION>'
            if len(line) != 3:
                self.BadRequest()
                return True
            self.method, self.resource, self.version = line

            # sws only accepts GET requests
            if self.method != 'GET':
                self.BadRequest()
                return True

            # sws only supports HTTP version 1.0
            if self.version != 'HTTP/1.0':
                self.BadRequest()
                return True


            self.status_line_parsed = True
            return False

        # If we've parsed a status line, we next expect a header
        if not self.headers_parsed:
            line = line.split()

            # End of headers
            if len(line) == 0:
                if 'connection' in self.headers:
                    if self.headers['connection'] == 'keep-alive':
                        self.keepalive = True
                    elif self.headers['connection'] == 'close':
                        self.keepalive = False
                self.headers_parsed = True
                return True

            # The only header we're accepting is Connection: Keep-alive
            if len(line) != 2:
                self.BadRequest()
                return True

            if line[0][-1] != ':':
                self.BadRequest()
                return True

            field = line[0][:-1].lower()
            value = line[1].lower()

            self.headers[field] = value

            return False

    def BadRequest(self):
        self.status = 400
        self.keepalive = False # Close connection on bad request

    def response(self):
        status = 0
        resp_headers = []
        resp_body = ''

        if self.status == 400:
            status = 400
        else:
            try:
                with open('.' + self.resource, 'r') as f:
                    resp_body = f.read()
                    status = 200
            except (FileNotFoundError, IsADirectoryError):
                status = 404

        if self.keepalive:
            resp_headers.append('Connection: Keep-Alive')
        else:
            resp_headers.append('Connection: Close')

        self.resp_status_line = f'HTTP/1.0 {status} {self.STATUS_CODE[status]}'

        resp = ''
        resp += self.resp_status_line
        resp += '\r\n'
        for header in resp_headers:
            resp += header
            resp += '\r\n'
        if resp_body:
            resp += '\r\n'
            resp += resp_body
        return resp

class Connection:
    def __init__(self, address, timeout):
        self.address = address
        self.current_request = HTTPRequest()
        self.timeout = timeout
        self.starttime = datetime.datetime.now()

    def new_request(self):
        self.current_request = HTTPRequest(self.current_request.keepalive)
        self.timer = self.timeout
        self.starttime = datetime.datetime.now()

    def timed_out(self):
        return (datetime.datetime.now() - self.starttime).seconds >= self.timeout

class Server:
    def __init__(self, ip_addr, port, buf_size, payload_len, timeout=60):
        self.connections = {}

        self.timeout = timeout

        self._socket = RDPSocket(buf_size, payload_len, logging=True)

        self._socket.bind((ip_addr, port))
        self._socket.settimeout(1)
        self._socket.listen(5)
        print(f'Server listening on port {port}...')

    def check_closes(self):
        for addr, _ in list(self._socket.connections.items()):
            conn = self._socket.connections[addr]
            if conn._state in [conn._closed, conn._close_wait]:
                del self.connections[addr]

    def check_accepts(self):
        for addr in self._socket.connections:
            if addr not in self.connections:
                self.connections[addr] = Connection(addr, self.timeout)

    def _read(self, addr):
        data = self._socket.connections[addr].recv()
        if data:
            for line in data.splitlines():
                if addr not in self.connections:
                    break
                if self.connections[addr].current_request.parse_line(line):
                    self._respond(addr)
        else:
            pass
            # self._close(addr)

    def _respond(self, addr):
        resp = self.connections[addr].current_request.response()
        # print(resp)
        self._socket.connections[addr].send(resp)
        time = datetime.datetime.now().strftime('%a %b %d %H:%M:%S %Z %Y')
        c = self.connections[addr]
        print(f'{time}: {c.address[0]}:{c.address[1]} {c.current_request.req_status_line}; {c.current_request.resp_status_line}')

        if self.connections[addr].current_request.keepalive:
            self.connections[addr].new_request()
        else:
            self._close(addr)

    def _close(self, addr):
        del self.connections[addr]
        self._socket.connections[addr].close()

    def check_timeouts(self):
        d = []
        for addr in self.connections:
            if self.connections[addr].timed_out():
                d.append(addr)

        for addr in d:
            self._close(addr)

    def read_sockets(self):
        for addr in list(self.connections):
            if self._socket.connections[addr]._state == self._socket.connections[addr]._established:
                self._read(addr)


def parse_args():
    try:
        return sys.argv[1], int(sys.argv[2]),\
               int(sys.argv[3]), int(sys.argv[4])
    except (IndexError, ValueError):
        print('Usage: python3 sor-server.py server_ip_address \
server_udp_port_number server_buffer_size \
server_payload_length')
        exit(1)


def main():

    ip_addr, port, buf_size, payload_len = parse_args()

    server = Server(ip_addr, port, buf_size, payload_len)

    while True:
        server.check_closes()

        server.check_accepts()

        server.check_timeouts()

        server.read_sockets()

        server._socket._loop_server()

if __name__ == '__main__':
    main()
