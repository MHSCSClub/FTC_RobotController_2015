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
import com.qualcomm.robotcore.hardware.GyroSensor;
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
	DcMotor frontRight;
	DcMotor frontLeft;


	DcMotor tiltArm;
	DcMotor rackBottom;
	DcMotor rackMid;
	DcMotor rackTop;

	Servo swipeLeft;
	Servo swipeRight;
	Servo flipClimbers;

	GyroSensor gyro;

	boolean xpressed = false;
	boolean slenabled = false;

	boolean bpressed = false;
	boolean srenabled = false;

	boolean apressed = false;
	boolean fcenabled = false;

	int xval [];
	int yval [];
	int zval [];
	int hval [];

	public K9TeleOp() {

	}

	@Override
	public void init() {
		motorRight = hardwareMap.dcMotor.get("motorRight");
		motorLeft = hardwareMap.dcMotor.get("motorLeft");
		frontRight = hardwareMap.dcMotor.get("frontRight");
		frontLeft = hardwareMap.dcMotor.get("frontLeft");
		motorRight.setDirection(DcMotor.Direction.REVERSE); //Mirror right motor
		frontRight.setDirection(DcMotor.Direction.REVERSE);

		tiltArm = hardwareMap.dcMotor.get("tiltArm");

		rackBottom = hardwareMap.dcMotor.get("rackBottom");
		rackMid = hardwareMap.dcMotor.get("rackMid");
		rackMid.setDirection(DcMotor.Direction.REVERSE);
		rackTop = hardwareMap.dcMotor.get("rackTop");

		swipeLeft = hardwareMap.servo.get("swipeLeft");
		swipeLeft.setPosition(.2f);
		swipeRight = hardwareMap.servo.get("swipeRight");
		swipeRight.setPosition(.5f);

		flipClimbers = hardwareMap.servo.get("flipClimbers");
		flipClimbers.setPosition(0f);

		gyro = hardwareMap.gyroSensor.get("gyro");
		gyro.calibrate();

		xval = new int[10];
		yval = new int[10];
		zval = new int[10];
		hval = new int[10];
	}

	@Override
	public void loop() {

		//GAMEPAD 1

		//Robot movement
		left_boost_movement();


		//X-> swipeLeft
		if(gamepad1.x) {
			if(!xpressed) {
				if(slenabled) {
					swipeLeft.setPosition(.2f);
					slenabled = false;
				} else {
					swipeLeft.setPosition(.7f);
					slenabled = true;
				}
			}
			xpressed = true;
		} else {
			xpressed = false;
		}

		//B-> swipeRight
		if(gamepad1.b) {
			if(!bpressed) {
				if(srenabled) {
					swipeRight.setPosition(.5f);
					srenabled = false;
				} else {
					swipeRight.setPosition(0f);
					srenabled = true;
				}
			}
			bpressed = true;
		} else {
			bpressed = false;
		}

		//A-> flipClimbers
		if(gamepad1.a) {
			if(!apressed) {
				if(fcenabled) {
					flipClimbers.setPosition(0f);
					fcenabled = false;
				} else {
					flipClimbers.setPosition(1f);
					fcenabled = true;
				}
			}
			apressed = true;
		} else {
			apressed = false;
		}

		//GAMEPAD 2
		boolean noarmmove = false;

		//Tilt arm control, left stick on gamepad 2
		float tpower = gamepad2.left_stick_y;
		tpower = (float) scaleInput(tpower);
		tiltArm.setPower(-tpower);

		//Rack control: right and left bumpers
		float boffset = .2f;

		//TODO: Add a button that disables/enables lowest rack motor.
		//Right trigger extends
		if(gamepad2.right_trigger > 0) {
			float rpow = (float) scaleInput(gamepad2.right_trigger);
			setRackPower(rpow); //Forwards direction
			float bpow = rpow + boffset;
			rackBottom.setPower(bpow > 1.0f ? 1.0f : bpow);

		//Left trigger contracts, b is the trigger button for max pull
		} else if(gamepad2.left_trigger > 0) {
			setRackPower(-1 * (float) scaleInput(gamepad2.left_trigger)); //Reverse direction
		} else {
			setRackPower(0f);
			noarmmove = true;
		}

		//Debug data
        telemetry.addData("Text", "*** Robot Data***");

		//Gyro Data
		int x = gyro.rawX();
		int y = gyro.rawY();
		int z = gyro.rawZ();
		int h = gyro.getHeading();

		addSwap(xval, x);
		addSwap(yval, y);
		addSwap(zval, z);
		addSwap(hval, h);

		telemetry.addData("1. x", getAvg(xval));
		telemetry.addData("2. y", getAvg(yval));
		telemetry.addData("3. z", getAvg(zval));
		telemetry.addData("4. h", getAvg(hval));
		String s = "";
		for(int i = 0; i < 10; ++i) {
			s += xval[i] + " ";
		}
		telemetry.addData("x test", s);

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
		frontRight.setPower(left);

		motorLeft.setPower(right);
		frontLeft.setPower(right);
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

		motorRight.setPower(left);
		frontRight.setPower(left);

		motorLeft.setPower(right);
		frontLeft.setPower(right);
	}

	private void addSwap(int arr[], int nv) {
		for(int i = 0; i < 9; ++i) {
			arr[i] = arr[i + 1];
		}
		arr[9] = nv;
	}

	private double getAvg(int arr[]) {
		int total = 0;
		for(int i = 0; i < 9; ++i)
			total += arr[i];
		return (total / 9.0);
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

		motorRight.setPower(left);
		frontRight.setPower(left);

		motorLeft.setPower(right);
		frontLeft.setPower(right);
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
