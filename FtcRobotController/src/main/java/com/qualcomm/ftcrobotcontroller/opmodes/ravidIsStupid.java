/* Copyright (c) 2014 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/**
 * Driver Mode
 */
public class ravidIsStupid extends OpMode {

	//Motors
	DcMotor motorRight;
	DcMotor motorLeft;
	DcMotor motorRight2;
	DcMotor motorLeft2;
	DcMotor jackisidiota;
	/*DcMotor linearSlide;

	Servo flipServo;
	Servo swipeServo;
	Servo wipeServo;

	boolean lscheck = false; //checks if linearslide is in motion, false because it is not in motion
	boolean rbpressed = false;
	boolean lbpressed = false;
	boolean bpressed = false;
	boolean fpcheck = false;
	boolean swcheck = false;
	int swcount = 0;
	boolean apressed = false;
	boolean xpressed = false; */

	//servo
	Servo hammer;
	double hammerPosition = 0;

	public ravidIsStupid() {

	}

	@Override
	public void init() {
		motorRight = hardwareMap.dcMotor.get("motor_2");
		motorLeft = hardwareMap.dcMotor.get("motor_1");
		motorRight.setDirection(DcMotor.Direction.REVERSE); //Mirror right motor

		motorRight2 = hardwareMap.dcMotor.get("motorRight2");
		motorLeft2 = hardwareMap.dcMotor.get("motorLeft2");
		motorLeft2.setDirection(DcMotor.Direction.REVERSE);
		hammer=hardwareMap.servo.get("hammer");


		jackisidiota = hardwareMap.dcMotor.get("curry_spice_powder");

		/*linearSlide = hardwareMap.dcMotor.get("linearSlide");
		flipServo = hardwareMap.servo.get("flipServo");
		flipServo.setDirection(Servo.Direction.FORWARD);
		swipeServo = hardwareMap.servo.get("swipeServo");
		wipeServo = hardwareMap.servo.get("wipeServo"); */

	}

	@Override
	public void loop() {

		//left_joystick_movement();
		dual_joystick_movement();

		if(gamepad1.right_bumper){
			hammerPosition += .1;
		}
		if(gamepad1.left_bumper){
			hammerPosition -= .1;
		}
		hammerPosition = Range.clip(hammerPosition, 0, 1);
		hammer.setPosition(hammerPosition);
		/*

		//Linear slide control, left and right bumpers
		float lsPower = .2f;

		if(gamepad1.right_bumper) {
			if(!rbpressed) {
				if (lscheck) {
					linearSlide.setPower(0);
					lscheck = false;
				} else {
					linearSlide.setPower(lsPower);
					lscheck = true;
				}
				rbpressed = false;
			}
			rbpressed = true;
			lbpressed = false;
		} else if(gamepad1.left_bumper) {
			if(!lbpressed) {
				if (lscheck) {
					linearSlide.setPower(0);
					lscheck = false;
				} else {
					linearSlide.setPower(-lsPower);
					lscheck = true;
				}
				lbpressed = false;
			}
			rbpressed = false;
			lbpressed = true;
		} else {
			rbpressed = false;
			lbpressed = false;
		}

		if(gamepad1.a) {
			if(!apressed) {
				if(fpcheck) {
					flipServo.setPosition(.5);
					fpcheck = false;
				} else {
					//flipServo.setPosition(0);
					fpcheck = true;
				}
			}
			apressed = true;
		} else {
			apressed = false;
		}

		if(gamepad1.b) {
			if(!bpressed) {
				swcheck = true;
			} else {
				swcheck = false;
			}
			bpressed = true;
		} else {
			bpressed = false;
		}

		if(gamepad1.x) {
			if(!xpressed) {
				wipeServo.setPosition(1);
			} else {
				wipeServo.setPosition(0);
			}
			xpressed = true;
		} else {
			xpressed = false;
		}

		//swipe
		if(swcheck) {
			if(swcount % 8 == 0) {
				swipeServo.setPosition(0);
			} else {
				swipeServo.setPosition(1);
			}
			++swcount;
		} */

		//Debug data
        telemetry.addData("Text", "*** Robot Data***");

	}

	//Control movement with only left joystick
	private void left_joystick_movement() {
		float right = gamepad1.left_stick_y - gamepad1.left_stick_x;
		float left = gamepad1.left_stick_y +  gamepad1.left_stick_x;

		//clip inputs between +/- 1
		right = Range.clip(right, -1, 1);
		left = Range.clip(left, -1, 1);

		//Scale inputs
		right = (float)scaleInput(right);
		left =  (float)scaleInput(left);

		motorRight.setPower(right);
		motorRight2.setPower(right);
		motorLeft.setPower(left);
		motorLeft2.setPower(left);
	}

	//Dual joystick movement
	private void dual_joystick_movement() {
		float right = gamepad1.left_stick_y;
		float left = gamepad1.right_stick_y;

		//clip inputs between +/- 1
		right = Range.clip(right, -1, 1);
		left = Range.clip(left, -1, 1);

		//Scale inputs
		right = (float)scaleInput(right);
		left =  (float)scaleInput(left);

		motorRight.setPower(right);
		motorRight2.setPower(right);
		motorLeft.setPower(left);
		motorLeft2.setPower(left);
	}

	@Override
	public void stop() {

	}
    	
	/*
	 * This method scales the joystick input so for low joystick values, the 
	 * scaled value is less than linear.  This is to make it easier to drive
	 * the robot more precisely at slower speeds.
	 */
	double scaleInput(double dVal)  {
		double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
				0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };
		
		// get the corresponding index for the scaleInput array.
		int index = (int) (dVal * 16.0);
		
		// index should be positive.
		if (index < 0) {
			index = -index;
		}

		// index cannot exceed size of array minus 1.
		if (index > 16) {
			index = 16;
		}

		// get value from the array.
		double dScale = 0.0;
		if (dVal < 0) {
			dScale = -scaleArray[index];
		} else {
			dScale = scaleArray[index];
		}

		// return scaled value.
		return dScale;
	}


}
