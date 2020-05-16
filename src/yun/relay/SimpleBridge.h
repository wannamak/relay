#include "Crc.h"

#ifndef BRIDGE_BAUDRATE
#define BRIDGE_BAUDRATE 250000
#endif

class SimpleBridge {
  public:
    SimpleBridge(HardwareSerial &_serial);
    uint16_t read(uint8_t *buffer, uint16_t len);
    uint16_t write(const char *buffer);

  private:
    uint16_t readInt(Crc &crc, bool *error);
    HardwareSerial &serial;
};
