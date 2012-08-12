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


int STATE = S_READY; //initial state

// internal variables
unsigned long time;
int challenge = 1;

// pin mapping
int inputPin = A0;
int ALedPin = 8;
int ABuzzerPin = 9;
int BLedPin = 10;
int BBuzzerPin = 11;

int led = 13;


// the setup routine runs once when you press reset:
void setup() {
  // initialize serial communication at 9600 bits per second:
  Serial.begin(9600);
  
  // pin intialization
  pinMode(ALedPin, OUTPUT);  
  pinMode(ABuzzerPin, OUTPUT);  
  pinMode(BLedPin, OUTPUT);  
  pinMode(BBuzzerPin, OUTPUT);  
}

// the loop routine runs over and over again forever:
void loop() {
  stateMachine();
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
void playChallenge(int id){
  dummyTune();
  debugMessage(String( "playChallenge:" + id));
}
void dummyTune(){
  digitalWrite(led, HIGH);
  delay(1000);
  digitalWrite(led, LOW);
  delay(1000);    
}


void launchChallenge(){
  long n = random(1,3);
  challenge = n;
  playChallenge(challenge);
}

int checkResponse(){
  // wait for user response, returns
  // 0: no response
  // 1: ok
  // -1: timeout or failure
  if (isTimeOut) return -1;
  int response = readUserInput();
  if (response == challenge) {
    debugMessage(String( "checkResponse:" + response));
    return 1;
  } else 
    return 0;
}
boolean isTimeOut(){
  unsigned long t = millis();
  long counter =  t-time; 
  if (counter > CHALLENGE_TIMEOUT) return true;
  else false;
}

int readUserInput(){
  // returns 0(no input), 1(A button), 2(B button)
  int userInput = 0;
  // read analog inputs 
  float meanValue = 0;
  int nSamples = 10;
  for (int i = 0; i < nSamples; i++)  {  
    meanValue += analogRead(inputPin); // 0-1023
  }
  meanValue = meanValue/nSamples;
  
  if ( 100 < meanValue && meanValue < 500 ) {
    userInput = 1;
    debugMessage(String( "userInput:" + userInput));
 }
  if ( 500 < meanValue && meanValue < 900 ) {
    userInput = 2;
    debugMessage(String( "userInput:" + userInput));
  }
  return userInput;  
}

void debugMessage(String message){
  Serial.println(message);
}
