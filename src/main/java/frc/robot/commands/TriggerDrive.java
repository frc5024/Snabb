package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import java.lang.Math;

import frc.common.control.CubicDeadband;

import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.OI;

public class TriggerDrive extends Command {
  OI oi;

  CubicDeadband rotation_deadband;
  CubicDeadband speed_deadband;

  public TriggerDrive() {
    requires(Robot.mDriveTrain);

    this.oi = OI.getInstance();
    this.rotation_deadband = new CubicDeadband(Constants.Deadbands.rotation_deadband, Constants.Deadbands.roataion_percision);
    this.speed_deadband = new CubicDeadband(0.0, Constants.Deadbands.speed_percision);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
  }  

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {

    /* Get Speed from triggers */
    double speed = oi.getThrottle();

    /* Get rotation from joystick */
    double rotation = oi.getTurn();
    // Deadzone the turning
    rotation = rotation_deadband.feed(rotation);

    // Robot.mDriveTrain.arcadeDrive(speed, rotation);
    Robot.mDriveTrain.raiderDrive(speed, rotation);
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
