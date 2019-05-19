package frc.common.wrappers;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.SpeedController;

/**
 * A GearBox is a wrapper for any pair of WPI_TalonSRX motor controllers where the first controller has an encoder attached.
 */
public class MixedGearBox extends GearBox{
    public WPI_TalonSRX front;
    public WPI_VictorSPX rear;

    public SpeedControllerGroup speedcontroller;

    private boolean is_inverse_motion = false;

    /**
     * GearBox Constructor
     * 
     * @param front The front or master talon in the gearbox
     * @param rear The rear or slave talon in the gearbox
     */
    public MixedGearBox(WPI_TalonSRX front, WPI_VictorSPX rear) {
        /* Store both Talons */
        this.front = front;
        this.rear = rear;

        /* Configure the Talons */
        this.front.configFactoryDefault();
        this.rear.follow(this.front);

        /* Group the Talons together */
        // Currently broken. The internet is of no help.
        // this.speedcontroller = SpeedControllerGroup(front, rear);
    }

    /**
     * Wrapper method around the WPI_TalonSRX current limiting functionality
     * 
     * @param peakCurrent The current threshold that must be passed before the limiter kicks in
     * @param holdCurrent The current to hold the motors at once the threshold has been passed
     * @param peakDuration The duration of the corrent limit
     */
    public void limitCurrent(int peakCurrent, int holdCurrent, int peakDuration){
        int timeout = 0;
        this.front.configPeakCurrentLimit(peakCurrent, timeout);
        this.front.configPeakCurrentDuration(peakDuration, timeout);
        this.front.configContinuousCurrentLimit(holdCurrent, timeout);

        // My opinion on victors has changed after seeing that they can not current limit
        // this.rear.configPeakCurrentLimit(peakCurrent, timeout);
        // this.rear.configPeakCurrentDuration(peakDuration, timeout);
        // this.rear.configContinuousCurrentLimit(holdCurrent, timeout);
    }

    /**
     * Wrapper around the encoder for the front or master talon
     * 
     * @return Number of ticks reported by the front or master talon
     */
    public int getTicks() {
        return this.front.getSelectedSensorPosition();
    }
    
    /**
     * Flips the sensor phase of the GearBox's encoder
     * 
     * This is mainly used for inverse motion profiling
     * 
     * @param is_inverted Should the phase be inverted?
     */
    public void setInverseMotion(boolean is_inverted) {
        this.front.setSensorPhase(is_inverted);
    }

    /**
     * Is the gearbox currently in inverse motion mode?
     * 
     * @return Is inverse motion?
     */
    public boolean getInverseMotion() {
        return this.is_inverse_motion;
    }
}