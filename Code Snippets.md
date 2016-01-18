# Helpful Code Snippets

### One Button Motor Toggle

Press [Button] to toggle motor on and then off to toggle motor off. Not as easy as you think!

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
