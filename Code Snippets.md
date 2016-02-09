# Helpful Code Snippets

### One Button Motor Toggle

Press [Button] to toggle motor on and then off to toggle motor off. Not as easy as you think!

Example below uses the `a` button on `gamepad1`.

```
//Globals
DCMotor motor;
boolean apressed = false; //Is the button pressed?
boolean enabled = false;

//Button pressed!
if(gamepad1.a) {

  //Has the button been pressed before? 
  //Only change state if first time pressing button
  if(!apressed) {
	
    //Motor enabled, disable
    if(enabled) {
      motor.setPower(0f);
      enabled = false;
      
    //Motor disabled, enable
    } else {
      motor.setPower(0.5f);
      enabled = true;
    }
  }
  apressed = true;

//Button not pressed
} else {
  apressed = false;
}
```

### Two Button Motor Toggle

Press [Button1] to got forwards, again to stop. Press [Button2] to go backwards, again to stop. Much more complex!

In the example below, right bumper toggles going forwards and left bumper toggles going backwards

```
//Globals
DCMotor motor;
boolean rbpressed = false; //Right bumper pressed?
boolean lbpressed = false; //Left bumper pressed?
boolean enabled = false;

float mPower = .5f;

//Right bumper pressed
if(gamepad1.right_bumper) {

  if(!rbpressed) {
    if (enabled) {
      motor.setPower(0);
      enabled = false;
    } else {
      motor.setPower(mPower); //Forwards direction
      enabled = true;
    }
    rbpressed = false;
  
  rbpressed = true;
  lbpressed = false;

//Left bumper pressed
} else if(gamepad1.left_bumper) {

  if(!lbpressed) {
    if (enabled) {
      motor.setPower(0);
      enabled = false;
    } else {
      motor.setPower(-mPower); //Reverse direction
      enabled = true;
    }
    lbpressed = false;
  }

  rbpressed = false;
  lbpressed = true;

//None pressed
} else {
  rbpressed = false;
  lbpressed = false;
}
```
