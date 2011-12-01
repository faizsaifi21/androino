#include <SoftModem.h> // Include the library 
#include <ctype.h> 

SoftModem modem; //create an instance of SoftModem 

void setup () 
{ 
  Serial.begin (115200); //permite 315bps en emision y recepcion
  modem.begin (); // setup () call to begin with 
}  

void loop () 
{ 
  while (modem. available ())// check that data received from phone
  {
    int c = modem. read (); // 1byte Reed 
    if (isprint (c)) { 
      Serial.print((char)c);

    } 
    else { 
      Serial.print ("Character Unkown:");
      Serial.print ("("); //printable characters is displayed in Hex 
      Serial.print (c, HEX); 
      Serial.print (")"); 
    } 
  } 
  if (Serial. available ()){// check that it is receiving data from PC 
    while(Serial.available()){
      char c = Serial. read (); // 1byte Reed 
      modem. write (c); // sent to Phone 
    }
  } 
} 




