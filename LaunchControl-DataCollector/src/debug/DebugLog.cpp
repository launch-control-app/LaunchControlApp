#include "DebugLog.hpp"

bool DebugLog::started = false;

void DebugLog::info(const String msg)
{
    log("INFO", msg);
}

void DebugLog::error(const String msg)
{
    log("ERROR", msg);
}

void DebugLog::log(const String tag, const String msg)
{
    if (!started) {
        Serial.begin(BAUD);
        started = true;
    }
    Serial.println(tag + ": " + msg);
}
