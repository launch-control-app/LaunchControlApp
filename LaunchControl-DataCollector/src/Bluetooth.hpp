/*
  Creation Date: Tuesday, 29th January 2019 
  Original Author: Christian Francisco 
  Contents of File: Encapsulates bluetooth functions
*/
#ifndef BLUETOOTH_H
#define BLUETOOTH_H

#include <Arduino.h>

#include "Hardware.hpp"

class Bluetooth
{
  public:
    Bluetooth(HardwareSerial &UART, PIN CMD_PIN,
              PIN STATE_PIN, uint32_t baud);
    ~Bluetooth();
    void transmit(const String msg);
    bool connected();

  private:
    HardwareSerial &UART_;
    PIN CMD_PIN_;
    PIN STATE_PIN_;
    uint32_t baud_;
};

#endif