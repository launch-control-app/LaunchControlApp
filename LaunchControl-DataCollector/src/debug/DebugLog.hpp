#ifndef DEBUGLOG_H
#define DEBUGLOG_H

#include <Arduino.h>

class DebugLog
{
  public:
    void static info(const String msg);
    void static error(const String msg);
    void static log(const String tag, const String msg);
    
  private:
    const static uint32_t BAUD = 9600;  // PlatformIO serial monitor default 
    static bool started;
};

#endif