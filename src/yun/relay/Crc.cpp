#include <util/crc16.h>

Crc::Crc() : _crc(0xFFFF) {
}

uint16_t Crc::Get() {
  return _crc;
}

void Crc::Update(uint8_t data) {
  _crc = _crc_ccitt_update(_crc, data);
}

void Crc::Reset() {
  _crc = 0xFFFF;
}

#if defined(ARDUINO_ARCH_AVR)
// AVR use an optimized implementation of CRC
#include <util/crc16.h>
#else
// Generic implementation for non-AVR architectures
uint16_t _crc_ccitt_update(uint16_t crc, uint8_t data)
{
  data ^= crc & 0xff;
  data ^= data << 4;
  return ((((uint16_t)data << 8) | ((crc >> 8) & 0xff)) ^
          (uint8_t)(data >> 4) ^
          ((uint16_t)data << 3));
}
#endif

bool Crc::Check(uint16_t crc) {
  return _crc == crc;
}
