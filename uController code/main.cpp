#include <Arduino.h>
#include <SoftwareSerial.h>
#include <stdio.h>
#include <string.h> 


#define TX 18
#define RX 19
#define VERT 2
#define HORIZ 3
#define SEL 9
#define PHOTORESISTOR A0

SoftwareSerial mySerial(RX,TX);
int value = 0;
char out_string [100];

void setup() {
  // put your setup code here, to run once:
  pinMode(RX, INPUT);
  pinMode(TX, OUTPUT);
  pinMode(SEL, INPUT);
  digitalWrite(SEL, HIGH);

  Serial.begin(9600);
  mySerial.begin(9600);
  pinMode(PHOTORESISTOR, INPUT);

  while(!Serial)
  {
    //wait
  }
  Serial.println("Hello World\n");
}

void loop() {
  // put your main code here, to run repeatedly:
  int vertical, horizontal;

  vertical = analogRead(VERT);
  horizontal = analogRead(HORIZ);

  value = analogRead(PHOTORESISTOR);
  sprintf(out_string, "%d %d %d", value, vertical, horizontal);

  Serial.println(out_string);
  mySerial.println(out_string);
  delay(1000);

}