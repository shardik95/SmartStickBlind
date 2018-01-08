/*
  HC-SR04 Ping distance sensors:

  sensor 1 - pothole
  sensor 2 - front obstacle
  sensor 3 - right obstacle
  sensor 4 - left obstacle

  Values sent to android phone
  D -> Calibration complete
  P -> pothole detected
  O -> obstacle ahead
  N -> obstacle ahead and near
  R -> obstacle on right
  L -> obstacle on left
  B -> blocked ahead
  A -> both sides clear

*/

#define echoPin1 2 // Echo Pin Sensor 1
#define trigPin1 3 // Trigger Pin Sensor 1

#define echoPin2 4 // Echo Pin Sensor 2
#define trigPin2 5 // Trigger Pin Sensor 2

#define echoPin3 6 // Echo Pin Sensor 3
#define trigPin3 7 // Trigger Pin Sensor 3

#define echoPin4 8 // Echo Pin Sensor 4
#define trigPin4 9 // Trigger Pin Sensor 4

#define LEDPin 13 // Onboard LED

int maximumRange = 400; // Maximum range needed
int minimumRange = 0; // Minimum range needed

long duration1, duration2, duration3, duration4;
long distance1, distance2, distance3, distance4;
long threshold = 0;

void setup() {
  Serial.begin (9600);
  pinMode(trigPin1, OUTPUT);
  pinMode(echoPin1, INPUT);
  pinMode(trigPin2, OUTPUT);
  pinMode(echoPin2, INPUT);
  pinMode(trigPin3, OUTPUT);
  pinMode(echoPin3, INPUT);
  pinMode(trigPin4, OUTPUT);
  pinMode(echoPin4, INPUT);
  pinMode(LEDPin, OUTPUT); // Use LED indicator (if required)

  threshold = calcThresh(); // Calculate threshold for pothole detection

  Serial.println("D"); // D indicates the android app that calibaration done
  Serial.println("Thresh:");
  Serial.println(threshold);
}

// Reading ten values in pothole sensor for threshold calculation
long calcThresh() {
  int i = 0;
  long duration, distance;
  long sum = 0, maxim = 0;
  long average, diff;
  long thresh = 0;

  while (i < 10) {
    digitalWrite(trigPin1, LOW);
    delayMicroseconds(2);

    digitalWrite(trigPin1, HIGH);
    delayMicroseconds(10);

    digitalWrite(trigPin1, LOW);
    duration = pulseIn(echoPin1, HIGH);

    distance = duration / 58.2;

    Serial.println(distance);
    // distance between 12 to 45 considered valid for threshold calculation
    // 12: sensor's distance from the ground
    // 45: cannot lift the stick more than this distance
    if (distance >= 12 && distance <= 45) {
      sum = sum + distance;
      if (distance > maxim) {
        maxim = distance;
      }
      i++;
    }
    delay(1000);
  }
  
  // Difference between average and max value gives the maximum deviation from the mean
  // This mean is tripled and added to average to calculate a threshold
  // Value greater than threshold will be a pothole

  // If average and maximum value equal, maximimum considered 20 based on observations
  // As same mixim and average value will make difference zero setting threshold to average value

  average = sum / 10;
  if(maxim == average){
    maxim = 20;
  }
  diff = maxim - average;
  thresh = average + (3 * diff);

  return thresh;
}

void loop() {
  // Loop starts after the calculation of threshold done successfully
  if (threshold != 0) {
    digitalWrite(trigPin1, LOW);
    delayMicroseconds(2);

    digitalWrite(trigPin2, LOW);
    delayMicroseconds(2);

    //====================================//
    digitalWrite(trigPin1, HIGH);
    delayMicroseconds(10);

    digitalWrite(trigPin2, HIGH);
    delayMicroseconds(10);


    //====================================//

    digitalWrite(trigPin1, LOW);
    duration1 = pulseIn(echoPin1, HIGH);

    digitalWrite(trigPin2, LOW);
    duration2 = pulseIn(echoPin2, HIGH);
    //====================================//

    //Calculate the distance (in cm) based on the speed of sound.
    distance1 = duration1 / 58.2;
    distance2 = duration2 / 58.2;

    //==========Pothole Detection==========================//
    // Sensor1
    if (distance1 >= maximumRange || distance1 <= minimumRange) {
      Serial.println("-1");
    } else {
      if (distance1 > threshold) {
        Serial.println("P"); // Alert for pothole
        delay(500);
      }
      digitalWrite(LEDPin, LOW);
    }

    //==========Obstacle Detection==========================//
    // Sensor2
    if (distance2 >= maximumRange || distance2 <= minimumRange) {
      Serial.println("-1");
      digitalWrite(LEDPin, HIGH);
    } else {
      if (distance2 <= 200 && distance2 > 100) {
        Serial.println("O"); // Obstacle ahead but not close
        delay(500);
      } else if (distance2 <= 100) {
        //Obstacle ahead and close
        //Check right and left to suggest user
        Serial.println("N");
        obsDetect();
      }
      digitalWrite(LEDPin, LOW);
    }
  }
  delay(500);
}

void obsDetect() {

  boolean left = false;
  boolean right = false;

  // Checking for obstacles on left and right
  digitalWrite(trigPin3, LOW);
  delayMicroseconds(2);

  digitalWrite(trigPin4, LOW);
  delayMicroseconds(2);

  //=============================//
  digitalWrite(trigPin3, HIGH);
  delayMicroseconds(10);

  digitalWrite(trigPin4, HIGH);
  delayMicroseconds(10);

  //=============================//
  digitalWrite(trigPin3, LOW);
  duration3 = pulseIn(echoPin3, HIGH);

  digitalWrite(trigPin4, LOW);
  duration4 = pulseIn(echoPin4, HIGH);

  distance3 = duration3 / 58.2;
  distance4 = duration4 / 58.2;

  //==============================================
  // Sensor3 obstacle on right
  if (distance3 >= maximumRange || distance3 <= minimumRange) {
    Serial.println("-1");
  } else {
    if (distance3 <= 100) {
      right = true;
    }
  }
  // Sensor4 obstacle on left
  if (distance4 >= maximumRange || distance4 <= minimumRange) {
    Serial.println("-1");
    digitalWrite(LEDPin, HIGH);
  } else {
    if (distance4 <= 100) {
      left = true;
    }
  }
  //==============================================
  if (left && right) {
    // Obstacle on left and right
    Serial.print("B");
  } else if (right) {
    // Obstacle on right, left clear
    Serial.print("R");
  } else if (left) {
    // Obstacle on left, right clear
    Serial.print("L");
  } else {
    // Obstacle on left, right clear
    Serial.print("A");
  }
  delay(1000);
}


