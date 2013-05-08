#include <SoftwareSerial.h>

SoftwareSerial bluetooth(2, 3); // RX, TX

char incomingByte;  
int  LED_GREEN = 10;      
int  LED_YELLOW = 9;    
int  LED_RED = 8;
int  lightLed = 1;
long interval = 30000;
long i=0; 


void setup() {
  bluetooth.begin(19200);
  Serial.begin(9600);
  pinMode(LED_GREEN, OUTPUT);
  pinMode(LED_RED, OUTPUT);
  pinMode(LED_YELLOW, OUTPUT);
  bluetooth.write("Starting Application");
}
 
void loop() {
  if(Serial.available()>0){
    bluetooth.write(Serial.read());
  }  
  if (bluetooth.available() > 0) { 
    incomingByte = bluetooth.read();
    if(incomingByte == '3') {
       lightLed = 2;
    }
    if(incomingByte == '4') {
       lightLed = 3;
    }
  }
  if(lightLed>1){
    if(i<=interval){
      digitalWrite(LED_YELLOW,LOW);
      i++;
      switch(lightLed){
        case 2:
          digitalWrite(LED_GREEN,HIGH);
          break;
        case 3:
          digitalWrite(LED_RED,HIGH);
          break;
      }
    }
    else lightLed = 1;
  }
  else{
    i=0;
    digitalWrite(LED_RED,LOW);
    digitalWrite(LED_GREEN,LOW);
    digitalWrite(LED_YELLOW,HIGH);
    lightLed = 1;
  }
}
      
