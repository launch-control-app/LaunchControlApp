#include <memory>

#include <Arduino.h>

#include <OBD2UART.h> // OBD library automatically interfaces with Serial1
                      //  pins 0 (RX) and 1 (TX)
#include "Bluetooth.hpp"

#include "debug/DebugLog.hpp" // Prints to USB Serial at 9600 baudrate

#define BT_SERIAL Serial4 // Serial4 uses pins 31 (RX) and 32 (TX)
#define BT_BAUD 9600      // Optional: change to Serial2 and 460800 \
                          //  for faster data transfer
#define BT_CMD 24         // TODO: the defines should probably be changed to
#define BT_STATE 25       //  constant variables

COBD obd;

std::shared_ptr<Bluetooth> bluetooth;

void setup()
{
  bluetooth = std::make_shared<Bluetooth>(BT_SERIAL, BT_CMD, BT_STATE, BT_BAUD);
}

void loop() // TODO: move the following series of test cases
{
  DebugLog::info("sending '1' over bluetooth");
  bluetooth->transmit("1");
  delay(3000);

  DebugLog::info("sending '999' over bluetooth");
  bluetooth->transmit("999");
  delay(3000);

  // Bug: the above test cases complete successfully but bluetooth->connected()
  //  always returns false
  if (bluetooth->connected())
  {
    DebugLog::info("sending '1337' over bluetooth");
    bluetooth->transmit("1337");
  }
  else
  {
    DebugLog::error("bluetooth not connected");
  }
  delay(3000);
}