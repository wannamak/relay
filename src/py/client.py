#!/usr/bin/env python2

import socket
import sys
import time
import errno

def Subscribe(sock):
  sock.sendall(
    'SUBSCRIBE\nId:123\n\n'
  )

def HandleMessages(sock):
  while True:
    start = time.time()
    try:
      bytes = sock.recv(4096)
    except socket.error, e:
      err = e.args[0]
      if err == errno.EAGAIN or err == errno.EWOULDBLOCK:
        sleep(1)
        print 'No data available'
        continue
      else:
        print e
        sys.exit(1)
    else:
      end = time.time()
      if len(bytes) == 0:
        print 'Server closed socket after %d seconds' % (end - start)
        return
      else:
        for message in bytes.decode('UTF-8').splitlines():
          print 'message [%s]' % message
          if message == 'ON':
            print 'Turn on'
          elif message == 'OFF':
            print 'Turn off'
          else:
            print 'Unrecognized message: %s' % message

while True:
  print 'Client connecting to localhost:1228 ...'
  sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
  try:
    sock.connect(('localhost', 1228))
  except socket.error, e:
    print 'Unable to connect'
    time.sleep(5)
    continue
  print '... connected!'
  Subscribe(sock)
  HandleMessages(sock)
