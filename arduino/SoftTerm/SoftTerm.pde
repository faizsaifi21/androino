#include <SoftModem.h> // Include the library 
#include <ctype.h> 

//bueno
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
    Serial.print(c, DEC);
    Serial.print(":");
    Serial.println(c, BIN);
/*
    if (isprint (c)) { 
      Serial.println((char)c);

    } 
    else { 
      Serial.print ("Character Unkown:");
      Serial.print ("("); //printable characters is displayed in Hex 
      Serial.print (c, HEX); 
      Serial.println (")"); 
    } 
 */
  }
 if (Serial.available()) { // data received from the PC
   while(Serial.available() ){
     // send data character by character
     int c = Serial.read();
     Serial.print("Sending character:");
    Serial.print(c, DEC);
    Serial.print(":");
    Serial.println(c, BIN);
    modem.write(c);
   }
     
   }
/*
  if (Serial. available ()){// check that it is receiving data from PC 
    while(Serial.available()){
      char c = Serial. read (); // 1byte Reed 
      modem. write (c); // sent to Phone 
    }
  } 
*/
} 

