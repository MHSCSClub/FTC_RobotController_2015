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

        //Drive to the end zone
        case 1:
            run_using_encoders();
            set_drive_power (.5f, .5f);
            if(have_drive_encoders_reached(20000, 20000)) {
                set_drive_power(0.0f, 0.0f);
                reset_drive_encoders();
                ++v_state;
            }
            break;

        case 2:
            reset_drive_encoders();
             ++v_state;
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
