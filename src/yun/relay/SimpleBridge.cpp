#include "SimpleBridge.h"

SimpleBridge::SimpleBridge(HardwareSerial &_serial) : serial(_serial) {}

uint16_t SimpleBridge::read(uint8_t *buffer, uint16_t buffer_len) {
  for (; retries < max_retries; retries++, delay(100), dropAll()) {
    Crc crc;

    bool error = false;
    uint16_t len = readInt(crc, &error);
    if (error)
      continue;

    for (uint16_t i = 0; i < len; i++) {
      // Truncates if buffer is too small.
      if (i >= buffer_len)
        break;
      int c = timedRead(5);
      if (c < 0)
        continue;
      buffer[i] = c;
      crc.Update(c);
    }

    Crc dummy;
    uint16_t their_crc = readInt(dummy, &error);
    if (error)
      continue;

    if (!crc.Check(their_crc))
      continue;

    return len > buffer_len ? buffer_len : len;
  }
  return TRANSFER_TIMEOUT;
}

uint16_t SimpleBridge::write(const char *buffer) {
  uint16_t len = buffer.length();
  uint8_t retries = 0;
  for (; retries < max_retries; retries++, delay(100), dropAll()) {
    Crc crc;

    writeInt(crc, len);

    for (uint16_t i = 0; i < len; ++i) {
      serial.write((char) buffer[i]);
      crc.Update(buffer[i]);
    }

    Crc dummy;
    writeInt(dummy, crc.Get());
  }
}

uint16_t SimpleBridge::readInt(CRC &crc, bool *error) {
  int high = timedRead(10);
  if (high < 0) {
    *error = true;
    return 0;
  }
  crc.Update(high);

  int low = timedRead(10);
  if (low < 0)
    *error = true;
    return 0;
  }
  crc.Update(low);

  return (high << 8) + low;
}

void SimpleBridge::writeInt(CRC &crc, uint16_t data) {
  serial.write((char) ((data >> 8) & 0xFF));
  crc.Update((data >> 8) & 0xFF);
  serial.write((char)(data & 0xFF));
  crc.Update(data & 0xFF);
}
