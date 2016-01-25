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

import android.graphics.Color;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IrSeekerSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.ColorSensor;

/**
 * Driver Mode
 */
public class K9TeleOp extends OpMode {

	//Motors
	DcMotor motorRight;
	DcMotor motorLeft;

	DcMotor tiltArm;
	DcMotor rackBottom;
	DcMotor rackMid;
	DcMotor rackTop;

	Servo swipeLeft;
	Servo swipeRight;
	Servo flipClimbers;

	boolean rbpressed = false;
	boolean lbpressed = false;
	boolean trcheck = false;

	boolean rtpressed = false;
	boolean ltpressed = false;
	boolean renabled = false;

	//servo
	double hammerPosition = 0;

	public K9TeleOp() {

	}

	@Override
	public void init() {
		motorRight = hardwareMap.dcMotor.get("motorRight");
		motorLeft = hardwareMap.dcMotor.get("motorLeft");
		motorRight.setDirection(DcMotor.Direction.REVERSE); //Mirror right motor

		tiltArm = hardwareMap.dcMotor.get("tiltArm");

		rackBottom = hardwareMap.dcMotor.get("rackBottom");
		rackMid = hardwareMap.dcMotor.get("rackMid");
		rackMid.setDirection(DcMotor.Direction.REVERSE);
		rackTop = hardwareMap.dcMotor.get("rackTop");

		swipeLeft = hardwareMap.servo.get("swipeLeft");
		swipeRight = hardwareMap.servo.get("swipeRight");

		flipClimbers = hardwareMap.servo.get("flipClimbers");
	}

	@Override
	public void loop() {

		//GAMEPAD 1

		//Robot movement
		left_boost_movement();

		//GAMEPAD 2

		//Tilt arm control, left stick on gamepad 2
		float tpower = gamepad2.left_stick_y;
		tpower = (float) scaleInput(tpower);
		tiltArm.setPower(-tpower);

		//Rack control: right and left bumpers
		float boffset = .2f;

		if(gamepad2.right_trigger > 0) {
			float rpow = (float) scaleInput(gamepad2.right_trigger);
			setRackPower(rpow); //Forwards direction
			float bpow = rpow + boffset;
			rackBottom.setPower(bpow > 1.0f ? 1.0f : bpow);
		} else if(gamepad2.left_trigger > 0) {
			setRackPower(-1 * (float) scaleInput(gamepad2.left_trigger)); //Reverse direction
		} else {
			setRackPower(0f);
		}

		//Debug data
        telemetry.addData("Text", "*** Robot Data***");
		telemetry.addData("RT", "" + gamepad1.right_trigger);

	}

	private void setRackPower(float power) {
		rackBottom.setPower(power);
		rackMid.setPower(power);
		rackTop.setPower(power);
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

		motorRight.setPower(left);
		motorLeft.setPower(right);
	}

	private void left_boost_movement() {
		float right = gamepad1.left_stick_y - gamepad1.left_stick_x;
		float left = gamepad1.left_stick_y +  gamepad1.left_stick_x;


		//Scale inputs
		right = (float)scaleInput(right) / 2f;
		left =  (float)scaleInput(left) / 2f;

		float boost = gamepad1.left_trigger;
		boost = boost < .2f ? .2f : boost; //min value of boost is .2
		boost = (float) scaleInput(boost) * 2f;

		//add boost amount to vectors right and left
		right *= boost;
		left *= boost;

		right = Range.clip(right, -1, 1);
		left = Range.clip(left, -1, 1);

		motorRight.setPower(right);
		motorLeft.setPower(left);
	}

	private float add_magnitude(float f, float a) {
		if(f >= 0)
			return f + a;
		else
			return f - a;
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
		left = (float)scaleInput(left);

		motorRight.setPower(right);
		motorLeft.setPower(left);
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
