/*
  Arduino Yun program to control relay.
*/

const int DIGITAL_OUTPUT_RELAY_PIN = 2;

void setup() {
  pinMode(DIGITAL_OUTPUT_RELAY_PIN, OUTPUT);
}

void loop() {
  digitalWrite(RELAY_PIN, HIGH);   // turn the LED on (HIGH is the voltage level)
  digitalWrite(LED_BUILTIN, HIGH);
  delay(1000 * TIME_SECONDS);                       // wait for a second
  digitalWrite(RELAY_PIN, LOW);    // turn the LED off by making the voltage LOW
  digitalWrite(LED_BUILTIN, LOW);
  delay(1000 * TIME_SECONDS);                       // wait for a second
}
