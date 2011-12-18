// LED multiplexing

#include <SoftModem.h>
#include <ctype.h> 
SoftModem modem; 

#define LEDTime 1


word num=0;
word ButtonPressed=0;
int RedLEDPins[] = {
  5,4,2};
int GreenLEDPins[] = {
  11,12,13};
int CathodePins[] = {
  10,9,8};
int AIN[]={
  A0,A1,A2};

void setup()
{
  Serial.begin(115200);
  //  modem.begin();
  for (int i=0;i<3;i++)
  {
    pinMode(AIN[i],INPUT);
    //digitalWrite(AIN[i],LOW);
  }
  modem.begin ();
}

word readButton(){

  int voltage;
  int SelectRow = 0;
  int SelectCol = 0;

  for (int j=0;j<3;j++)
  {

    voltage = 0;

    for (int i=0;i<=10;i++)
    {
      voltage += analogRead(AIN[j]);
      if (analogRead(AIN[j])>0)
      {
        //        Serial.print("AIN");
        //        Serial.println(j);
        //        Serial.println(analogRead(AIN[j]));
      }
    }

    if (voltage>=671*11 && voltage<=677*11)
    {
      SelectCol=j+1;
      SelectRow=1; 
      break;      
    }

    else if (voltage>=324*11 && voltage<=328*11)
    {
      SelectCol=j+1;
      SelectRow=2;
      break;         
    }

    else if ( voltage<=164*11 && voltage>=160*11)
    {
      SelectCol=j+1;
      SelectRow=3;
      break;        
    }
  }




  if ((SelectRow > 0) && (SelectCol > 0))
  {
    //    Serial.println("Aqui estoy!!");
    num= (1 << (SelectRow+(SelectCol-1)*3)-1);
    //    Serial.println(num,BIN);  
    //    Serial.println(SelectCol);
    //    Serial.println(SelectRow);

    //Serial.println(num);


    return num;

  } 
  else
  {
    return (0);
  } 

}


void lightLED(word LEDOnOff, word LEDColour)
{
  // shift the bits to the right, turning on the LEDs whenever
  // there is a 1, turning off whenever there is a 0 in LEDOnOff
  // If the LED is lit, LEDColour determines which LED is lit
  // 1 is red, 0 is green

  for (int j=0;j<3;j++)
  {
    pinMode(RedLEDPins[j], INPUT);
    digitalWrite(RedLEDPins[j], LOW);
    pinMode(GreenLEDPins[j], INPUT);
    digitalWrite(GreenLEDPins[j], LOW);
    pinMode(CathodePins[j], INPUT);
    digitalWrite(CathodePins[j], LOW);
  }

  for (int i=0;i<9;i++)
  {
    if (LEDOnOff & 1)
    {     
      if (LEDColour & 1)
      {
        pinMode(RedLEDPins[i/3], OUTPUT);
        pinMode(CathodePins[i%3], OUTPUT);
        digitalWrite(RedLEDPins[i/3], HIGH);
        digitalWrite(CathodePins[i%3], LOW);

        delay(LEDTime);

        digitalWrite(RedLEDPins[i/3], LOW);
        pinMode(RedLEDPins[i/3], INPUT);
        pinMode(CathodePins[i%3], INPUT);
      } 
      else 
      {
        pinMode(GreenLEDPins[i/3], OUTPUT);
        pinMode(CathodePins[i%3], OUTPUT);
        digitalWrite(GreenLEDPins[i/3], HIGH);
        digitalWrite(CathodePins[i%3], LOW);

        delay(LEDTime);

        digitalWrite(GreenLEDPins[i/3], LOW);
        pinMode(GreenLEDPins[i/3], INPUT);
        pinMode(CathodePins[i%3], INPUT);        
      }

    }
    LEDOnOff = LEDOnOff >> 1;
    LEDColour = LEDColour >> 1;
  }

}

word checkWinner(word GridOnOff, word GridColour, boolean Turn)
{


  word winArray[] = {
    7, 56, 73, 84, 146, 273, 292, 448                                                };  

  if (Turn)        // red's turn, check for green
  {
    for (int i=0;i<8;i++)
    {
      if ( ((GridOnOff & ~GridColour) & winArray[i]) == winArray[i])
      {
        return winArray[i];
      }
    }
    return 0;
  } 
  else        // green's turn, check for red
  {
    for (int i=0;i<8;i++)
    {
      if ( ((GridOnOff & GridColour) & winArray[i]) == winArray[i])
      {      
        return winArray[i];
      }
    }
    return 0;
  }
}

void displayWin(word winCondition, boolean Turn)
{
  word winColour;

  if (Turn)
  {
    winColour = 0;
  } 
  else
  {
    winColour = 511;
  }  
  for (int i=256;i>=0;i--)
  {
    lightLED(winCondition, winColour);  // light up the winning combo
    //    lightLEDPWM(~winCondition, ~winColour, i);  // fade out the other colour
  }

  for (int i=0;i<10;i++)      // blink winning combo a few times
  {
    lightLED(winCondition, winColour);
    delay(100);
  }    
}
void sending(word ButtonPressed)
{
  //void sending(uint8_t (ButtonPressed))
  //{

  //  int move=uint8_t (ButtonPressed);
  //  modem.write(ButtonPressed); -> 64=@

  Serial.println(ButtonPressed);
 if(ButtonPressed==1)
  {
    modem.write(49);
  }
  else if(ButtonPressed==2)
  {
    modem.write(52);
  }
    else if(ButtonPressed==4)
  {
    modem.write(55);
  }
  else if(ButtonPressed==8)
  {
    modem.write(50);
  }
    else if(ButtonPressed==16)
  {
    modem.write(53);
  }
  else if(ButtonPressed==32)
  {
    modem.write(56);
  }
    else if(ButtonPressed==64)
  {
    modem.write(51);
  }
  else if(ButtonPressed==128)
  {
    modem.write(54);
  }
    else if(ButtonPressed==256)
  {
    modem.write(57);
  }
//  int b100;
//  int m1;
//  int b1;
//  int m01;
//  int b10;
//  int u;
//  u=ButtonPressed;
//  b100=u/100;
//  m1=u%100;
//  b10=m1/10;
//  m01=m1%10;
//  b1=m01;
//  modem.write(b100+48);
//  modem.write(b10+48);
//  modem.write(b1+48);
//  modem.write(32);
//  modem.write(32);



  delay(100);
}




void loop()
{

byte NoOfTurns = 0;
word LedOnOff = 0;
word LedColour = 0;
boolean Turn = 1;  
word WinCondition=0;


  while (1)
  {

    if ((WinCondition == 0) && (NoOfTurns < 9))
    {
      switch(Turn){
      case 0:
        ButtonPressed=readButton();   // take a reading

        if(ButtonPressed>0)
        {
          sending(ButtonPressed);
        }


        if( ButtonPressed & ~LedOnOff)   // if an empty space is selected
        { 
          LedOnOff = LedOnOff | ButtonPressed;
          Turn = !Turn;  //-> Escribir evento del turno y envio al server
          NoOfTurns+=1;
          WinCondition=checkWinner(LedOnOff, LedColour, Turn);  
        }
        // Recepcion del evento de que ha hecho su movimiento. 
        lightLED(LedOnOff,LedColour);    // light up the LED    
        break;


      case 1:
        word array[2];

        word c=0;
        word a=0;
        while (modem. available ())// check that data received from phone
        {
          Serial.println("modem available");
          c = modem. read (); 
          switch(c){
          case 'c': 
            a=1; 
            break;
          case 'f': 
            a=2; 
            break;
          case 'i': 
            a=4; 
            break;
          case 'b': 
            a=8; 
            break;
          case 'e': 
            a=16; 
            break;
          case 'h': 
            a=32; 
            break;
          case 'a': 
            a=64; 
            break;
          case 'd': 
            a=128; 
            break;
          case 'g': 
            a=256; 
            break;


          }
        }
        if( a & ~LedOnOff)   // if an empty space is selected
        {

          LedOnOff = LedOnOff | a;
          LedColour = LedColour | a;



          Turn = !Turn;  //-> Escribir evento del turno y envio al server
          NoOfTurns+=1;

          WinCondition=checkWinner(LedOnOff, LedColour, Turn);   
        }   


        //  <- Recepcion del evento de que ha hecho su movimiento. 

        lightLED(LedOnOff,LedColour);    // light up the LED    


      }
    }  

  else {break;}
  }



  //  do
  //  {
  //    ButtonPressed=readButton();   // take a reading
  //    // Serial.println(play);
  //
  //    if(ButtonPressed>0)
  //    {
  //      sending(ButtonPressed);
  //    }
  //
  //    if( ButtonPressed & ~LedOnOff)   // if an empty space is selected
  //    { 
  //      LedOnOff = LedOnOff | ButtonPressed;  // light up the space
  //      //-> Envio del movimiento realizado.
  //      //
  //      if (Turn)                         // set colour according to whose turn it is
  //      {
  //        do
  //        {
  //
  //          Serial.println("turno Rojo");
  //          word c=0;
  //          word a=0;
  //          while (modem. available ())// check that data received from phone
  //          {
  //            Serial.println("modem available");
  //            c = modem. read (); 
  //            a = c-48;// 1byte Reed 
  //            Serial.println(a);
  //            if (isprint (c)) { 
  //              Serial.print("a");
  //              Serial.println(a);
  //
  //
  //            } 
  //            else { 
  //              Serial.print ("Character Unkown:");
  //              Serial.print ("("); //printable characters is displayed in Hex 
  //              Serial.print (c, HEX); 
  //              Serial.print (")"); 
  //            } 
  //          } 
  //          LedColour = LedColour | a;
  //          Serial.println("LedColour");
  //          Serial.println(LedColour);
  //        }
  //        while(modem. available ()==0);
  //
  //
  //        Turn = !Turn;  //-> Escribir evento del turno y envio al server
  //        NoOfTurns+=1;
  //
  //        WinCondition=checkWinner(LedOnOff, LedColour, Turn);      
  //      }
  //
  //      //  <- Recepcion del evento de que ha hecho su movimiento. 
  //
  //      lightLED(LedOnOff,LedColour);    // light up the LED    
  //
  //    }
  //  }  
  //  while ((WinCondition == 0) && (NoOfTurns < 9));


  if (WinCondition > 0)              // did anybody win?
  {
    displayWin(WinCondition, Turn);  

  } 
  else if(NoOfTurns= 9)                            // it was a draw, fade out all lights
  {
    for (int i=0;i<150;i++)      // blink winning combo a few times
    {

      lightLED(LedOnOff,LedColour);

    }    


    for (int i=0;i<10;i++)      // blink winning combo a few times
    {

      lightLED(LedOnOff,LedColour);
      delay(100);
    }    



  }

}












