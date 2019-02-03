/*
  Creation Date: Tuesday, 29th January 2019 
  Original Author: Christian Francisco 
  Contents of File: Implements bluetooth functions
*/
#include "Bluetooth.hpp"

Bluetooth::Bluetooth(HardwareSerial &UART, PIN CMD_PIN,
                     PIN STATE_PIN_, uint32_t baud)
    : UART_(UART), CMD_PIN_(CMD_PIN), STATE_PIN_(STATE_PIN_), baud_(baud)
{
  UART_.begin(baud_);
}

Bluetooth::~Bluetooth()
{
  UART_.end();
}

void Bluetooth::transmit(const String msg)
{
  UART_.println(msg);
}

bool Bluetooth::connected()
{
  return digitalRead(STATE_PIN_);
}
