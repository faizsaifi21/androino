/*
  RunCorre
 
 RunCorre game. 
 
 */


#define S_READY        0
#define S_GAME_STARTS  1
#define S_CHALLENGE    2
#define S_RESPONSE     3
#define S_GAME_ENDS    4

#define CHALLENGE_TIMEOUT  1000

int STATE = S_READY;
unsigned long time;

// the setup routine runs once when you press reset:
void setup() {
  // initialize serial communication at 9600 bits per second:
  Serial.begin(9600);
}

// the loop routine runs over and over again forever:
void loop() {

  stateMachine();
/*
  // read the input on analog pin 0:
  int sensorValue = analogRead(A0);
  // Convert the analog reading (which goes from 0 - 1023) to a voltage (0 - 5V):
  float voltage = sensorValue * (5.0 / 1023.0);
  // print out the value you read:
  Serial.println(voltage);
*/
}

// main game state machine 
void stateMachine(){

  int response = 0;
  switch (STATE) {
  case S_READY:
    //initialization checks
    STATE = S_GAME_STARTS;
    break;
  case S_GAME_STARTS:
    // game start tune
    playStartGameTune();
    STATE = S_CHALLENGE;
    break;
  case S_CHALLENGE:
    // launch challenge
    launchChallenge();
    time = millis(); //start timeout counter
    STATE = S_RESPONSE;
    break;
  case S_RESPONSE:
    // wait for response or timeout
    response = checkResponse();
    if (response == 1) { // solved, go for next challenge
      STATE = S_CHALLENGE;
    };
    if (response == -1) { // timeout or failure, game ends
      STATE = S_GAME_ENDS;
    };
    break;
  case S_GAME_ENDS:
    // you loose tune and back to the beginning
    playEndGameTune();
    STATE = S_READY;
    break;
  }
}

void playStartGameTune(){
  dummyTune();
}
void playEndGameTune(){
  dummyTune();
}
void dummyTune(){
  delay(2*1000);
}

void launchChallenge(){
}

int checkResponse(){
  // wait for user response, returns
  // 0: no response
  // 1: ok
  // -1: timeout or failure
  int response = readUserInput();
  if (response>0) return 1;
  if (isTimeOut) return -1;
  return 0;
}
boolean isTimeOut(){
  unsigned long t = millis();
  long counter =  t-time; 
  if (counter > CHALLENGE_TIMEOUT) return true;
  else false;
}

int readUserInput(){
  // read analog inputs 
  return 1;
}

