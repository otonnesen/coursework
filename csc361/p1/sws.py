import datetime
import selectors, socket, sys

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
        return resp.encode()

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
    def __init__(self, ip_addr, port, timeout=60):
        self.connections = {}

        self.timeout = timeout

        self._sel = selectors.DefaultSelector()
        self._socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

        self._socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self._socket.bind((ip_addr, port))
        self._socket.listen(5)
        self._socket.setblocking(False)
        self._sel.register(self._socket, selectors.EVENT_READ, self._accept)
        print(f'Server listening on port {port}...')

    def _accept(self, socket, mask):
        conn, addr = socket.accept()
        conn.setblocking(False)
        self._sel.register(conn, selectors.EVENT_READ, self._read)
        self.connections[conn] = Connection(f'{addr[0]}:{addr[1]}', self.timeout)

    def _read(self, conn, mask):
        data = conn.recv(1024)
        if data:
            data = data.decode()

            for line in data.splitlines():
                if conn not in self.connections:
                    break
                if self.connections[conn].current_request.parse_line(line):
                    self._respond(conn)
        else:
            self._close(conn)

    def _respond(self, conn):
        conn.send(self.connections[conn].current_request.response())
        time = datetime.datetime.now().strftime('%a %b %d %H:%M:%S %Z %Y')
        c = self.connections[conn]
        print(f'{time}: {c.address} {c.current_request.req_status_line}; {c.current_request.resp_status_line}')

        if self.connections[conn].current_request.keepalive:
            self.connections[conn].new_request()
        else:
            self._close(conn)

    def _close(self, conn):
        self.connections.pop(conn)
        self._sel.unregister(conn)
        conn.close()

    def check_timeouts(self):
        for conn in list(self.connections):
            if self.connections[conn].timed_out():
                self._close(conn)

    def read_sockets(self):
        # Poll all connections every 1 second
        events = self._sel.select(timeout=1)
        for key, mask in events:
            key.data(key.fileobj, mask)


def parse_args():
    ip_addr = 'localhost'
    port = 8080
    if len(sys.argv) >= 2:
        ip_addr = sys.argv[1]
    if len(sys.argv) >= 3:
        try:
            port = int(sys.argv[2])
        except ValueError:
            print(f'Port {sys.argv[2]} invalid, using 8080 instead.')
    return ip_addr, port


def main():

    server = Server(*parse_args())

    while True:
        server.check_timeouts()

        server.read_sockets()

if __name__ == '__main__':
    main()
