package com.qualcomm.ftcrobotcontroller.opmodes;

//------------------------------------------------------------------------------
//
// PushBotAutoRed
//

/**
 * Provide a basic autonomous operational mode that uses the left and right
 * drive motors and associated encoders implemented using a state machine for
 * the Push Bot.
 *
 * @author SSI Robotics
 * @version 2015-08-01-06-01
 */
public class PushBotAutoBlue extends PushBotTelemetry

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
    public PushBotAutoBlue()

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
            if(have_drive_encoders_reached(10000, 10000)) {
                set_drive_power(0.0f, 0.0f);
                reset_drive_encoders();
                ++v_state;
            }
            break;
        case 2:
            reset_drive_encoders();
            ++v_state;
            break;

        case 3:
            reset_drive_encoders();
            ++v_state;
            break;

        case 4:
            run_using_encoders();
            set_drive_power (-.3f, 1f);
            if(has_right_drive_encoder_reached(8000)){
                set_drive_power(0.0f, 0.0f);
                reset_drive_encoders();
                ++v_state;
            }
             break;

        case 5:
            reset_drive_encoders();
            ++v_state;
            break;

        case 6:
            reset_drive_encoders();
            ++v_state;
            break;

        case 7:
            run_using_encoders();
            set_drive_power (.3f, .3f);
            if(have_drive_encoders_reached(3000, 3000)){
                flip();
                set_drive_power(0.0f, 0.0f);
                reset_drive_encoders();
                ++v_state;
            }
            break;
        default:
            break;
        }

        //
        // Send telemetry data to the driver station.
        //
        update_telemetry (); // Update common telemetry
        telemetry.addData("Text", "*** Robot Data***");
        telemetry.addData("vstate", "" + v_state);

        //Gyro Data
        int xVal = gyro.rawX();
        int yVal = gyro.rawY();
        int zVal = gyro.rawZ();
        int heading = gyro.getHeading();

        telemetry.addData("1. x", String.format("%03d", xVal));
        telemetry.addData("2. y", String.format("%03d", yVal));
        telemetry.addData("3. z", String.format("%03d", zVal));
        telemetry.addData("4. h", String.format("%03d", heading));
        telemetry.addData("Whiteness", getWhiteness());


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


} // PushBotAutoRed
