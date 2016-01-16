package com.qualcomm.ftcrobotcontroller.opmodes;

//------------------------------------------------------------------------------
//
// PushBotAutoRed
//

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * Provide a basic autonomous operational mode that uses the left and right
 * drive motors and associated encoders implemented using a state machine for
 * the Push Bot.
 *
 * @author SSI Robotics
 * @version 2015-08-01-06-01
 */
public class PushBotAutoRed extends PushBotTelemetry

{
    //--------------------------------------------------------------------------
    //
    // PushBotAutoRed
    //
    /**
     * Construct the class.
     *
     * The system calls this member when the class is instantiated.
     */
    public PushBotAutoRed()

    {
        //
        // Initialize base classes.
        //
        // All via self-construction.

        //
        // Initialize class members.
        //
        // All via self-construction.

    } // PushBotAutoRed

    //--------------------------------------------------------------------------
    //
    // start
    //

    /**
     * Perform any actions that are necessary when the OpMode is enabled.
     *
     * The system calls this member once when the OpMode is enabled.
     */
    @Override public void start ()

    {
        //
        // Call the PushBotHardware (super/base class) start method.
        //
        super.start ();

        //
        // Reset the motor encoders on the drive wheels.
        //
        reset_drive_encoders ();

    } // start


    @Override public void loop ()
    {
        switch (v_state)
        {

        // Init
        case 0:
            reset_drive_encoders ();
            ++v_state;

            break;

        //Drive to the middle
        case 1:
            run_using_encoders();
            set_drive_power (.5f, .5f);
            boolean lcheck = false, rcheck = false;
            if(has_left_drive_encoder_reached(13000)) {
                set_left_drive_power(0.0f);
                lcheck = true;
            }
            if(has_right_drive_encoder_reached(13000)) {
                set_right_drive_power(0.0f);
                rcheck = true;
            }

            if (lcheck && rcheck) {
                set_drive_power(0.0f, 0.0f);
                reset_drive_encoders();
                ++v_state;
            }
            break;

        case 2:
            reset_drive_encoders();
             ++v_state;
             break;

        //Turn
        case 3:
            run_using_encoders();
            set_drive_power (.5f, -0.5f);
            if(has_left_drive_encoder_reached(2100)) {
                set_drive_power(0.0f, 0.0f);
                reset_drive_encoders();
                ++v_state;
            }
            break;

        case 4:
            reset_drive_encoders();
            ++v_state;
            break;

        // Drive forward and press the button
        case 5:
            run_without_drive_encoders();

            set_drive_power (.5f, .5f);

            if (touch_button_pressed())
            {
                reset_drive_encoders ();
                set_drive_power(0.0f, 0.0f);
                is_red = color_is_red();
                v_state = -1;
            }
            break;

        // Reverse
        case 6:
            run_without_drive_encoders();
            set_drive_power(-.5f, -.5f);
            ++reverseSteps;
            if (reverseSteps == maxReverse)
            {
                set_drive_power(0.0f, 0.0f);
                ++v_state;
            }
            break;

        // Move pressers into position
        case 7:
            if(is_red) {
                engage_left();
            } else {
                engage_right();
            }
            ++v_state;
            forwardSteps = 0;
            break;

        // Go forwards
        case 8:
            forwardSteps++;
            run_without_drive_encoders();
            set_drive_power(.5f, .5f);
            if(forwardSteps == 30000) {
                set_drive_power(0.0f, 0.0f);
                v_state = -1;
                forwardSteps = 0;
            }
            break;

        case 9:
            run_without_drive_encoders();
            set_drive_power(-.5f, -.5f);
            ++forwardSteps;
            if(forwardSteps == maxReverse) {
                set_drive_power(0.0f, 0.0f);
                reset_drive_encoders();
            }
            break;

        default:
            break;
        }

        //
        // Send telemetry data to the driver station.
        //
        update_telemetry (); // Update common telemetry
        telemetry.addData ("18", "State: " + v_state);
        telemetry.addData("Color?", color_is_red() ? "Red" : "Blue" );
        telemetry.addData("Touch?", touch_button_pressed() ? "Yes" : "No");
        telemetry.addData("FINAL RESULTS RIGHT", rightVal);
        telemetry.addData("FINAL RESULTS LEFT", leftVal);


    } // loop

    //--------------------------------------------------------------------------
    //
    // v_state
    //
    /**
     * This class member remembers which state is currently active.  When the
     * start method is called, the state will be initialized (0).  When the loop
     * starts, the state will change from initialize to state_1.  When state_1
     * actions are complete, the state will change to state_2.  This implements
     * a state machine for the loop method.
     */
    private int v_state = 0;
    private boolean is_red = false;
    private int reverseSteps = 0;
    private int maxReverse = 100;
    private int forwardSteps = 0;
    private int leftVal = 0;
    private int rightVal = 0;


} // PushBotAutoRed
