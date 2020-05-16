class Crc {
  public:
    void Update(uint8_t c);
    void Reset();
    void Write();
    bool Check(uint16_t _CRC);

  private:
    uint16_t crc_;
};
