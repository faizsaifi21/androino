# Androino Terminal #

This application provides a basic example of the software that allows to communicate an android mobile phone with an arduino board. You can modify it at your convenience.

https://lh5.googleusercontent.com/-NmJrOtfby0E/T0of14vDI1I/AAAAAAAAAMM/X9vIHBPKTtg/s539/Dibujo.PNG


## Required parts ##
  * [Arduino board](http://arduino.cc/en/Main/Hardware). Tested with Pro Mini and Duemilanove boards.
  * Android mobile phone (minimum version 1.6)
  * Sparkfun's [Audio jack modem for android](http://www.sparkfun.com/products/10331)
  * Audio cable connector (for instance [TRRS audio cable](http://www.amazon.com/Stereo-Circuit-ThinLine-Audio-Cable/dp/B004QOUGV4))

## Hardware setup ##
```
Pin mappings ( Modem board / Arduino pins)

FSK OUT = D5
FSK IN  = D6
VREF    = D7
```
.
## Software setup ##

### Arduino sketch ###
Download and install into the arduino board the [Androino Terminal sketch](http://androino.googlecode.com/svn-history/r85/trunk/arduino/SoftTerm/SoftTerm.pde).

### Android APP ###
Install the Ardroino Terminal app into the mobile phone.

http://chart.apis.google.com/chart?cht=qr&chs=230x230&chld=L&choe=UTF-8&chl=http%3A%2F%2Fandroino.googlecode.com%2Ffiles%2FAndroinoTerm.beta.b20120313.apk&ext=.PNG

## Putting all together ##

Once you have everything installed, to run the application:

  1. Connect all the wires
  1. Power on the arduino board
  1. Open the Arduino IDE Serial Monitor
  1. Start the the android App and push START/STOP button

Then, you can send messages  from android to arduino and viceversa.

![https://lh6.googleusercontent.com/-R4N2ry6loLc/T0ox3BQniXI/AAAAAAAAAMc/wbdeFM_8rxc/s569/ard_serial_monitor.png](https://lh6.googleusercontent.com/-R4N2ry6loLc/T0ox3BQniXI/AAAAAAAAAMc/wbdeFM_8rxc/s569/ard_serial_monitor.png)

![https://lh3.googleusercontent.com/--UDgopg2tYQ/T0ox7WJXAoI/AAAAAAAAAMk/G0pIGi1VMRc/s560/and_terminal.jpg](https://lh3.googleusercontent.com/--UDgopg2tYQ/T0ox7WJXAoI/AAAAAAAAAMk/G0pIGi1VMRc/s560/and_terminal.jpg)

## Understanding the source code ##

### Arduino ###
Go to source code.

### Android ###

The Android app starts a thread object that continuously records sound ArduinoService. The sound is processed by a secondary thread FSKDecoder. The decoded information is delivered to the user interface MainActivity as messages using an Android Handler object.

The main classes and methods are summarized below:

  * ArduinoService: Main library thread object (sound acquisition)
    * AudioRecordingRun(): loop that records sound
    * write(int): encodes the integer and plays the sound signal.
  * FSKDecoder: Thread object that demodulates the sound signal.
  * FSKModule: Utility static methods related to FSK modulation/demodulation.
    * encode(int): encodes the number into a sound signal
    * decodeSound(double[.md](.md)): decodes the sound signal into an integer


### Error correction ###
The application implements a simple error correction mechanism. Before transmission the number(between 0 and 31) is added with a checksum. Then at the reception, the message is decoded and if checksum check:

  * fails: an ARQ (Automatic Request Query) code is sent, then the sender repeats the last message sent.
  * agrees: an ACK (Acknowledgment) code is sent (if not received the sender tries to the send the last message again).

See some code extract

```
SofTerm.pde sketch

#define CODE_REPEAT_LAST_MESSAGE    20
#define CODE_ACK_MESSAGE            21
#define MESSAGE_CHECKSUM_ERROR      -2
#define PARITY_EVEN                 64
#define PARITY_ODD                  32

int encodeMessage(int number){
  // adds the checksum
  // Example: 3 (000.00011) => (101.00011)
  int cSum = checkSum(number);
  int msg = number + cSum;
  return msg;
}

int checkSum(int number){
  // calculates the checkSum for error correction
  // simple implementation even => 010, odd =>001
  ...
  if (sign>0)
    return PARITY_EVEN;
  else 
    return PARITY_ODD; 
}

int decodeMessage(int message){
  // Message format: 111.11111 (3bits=checksum 5bits=information)
  int number = B00011111 & message; //extract number from message 
  int chk =    B11100000 & message;  //extract checksum from message
  int cSum = checkSum(number);
  if ( chk != cSum) {
    return MESSAGE_CHECKSUM_ERROR; // erroneus message received
  } else
    return number;
}

```