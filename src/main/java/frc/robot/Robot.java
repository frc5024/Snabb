package frc.robot;

import edu.wpi.first.networktables.NetworkTable;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.common.wrappers.Camera;
import frc.common.utils.FileUtils;
import frc.common.field.FieldStatusThread;
import frc.common.utils.RobotLogger;
import frc.common.utils.RobotLogger.Level;
import frc.common.network.NetworkTables;
import frc.common.network.VisionInterface;
import frc.common.network.ConnectionMonitor;

import frc.robot.commands.TriggerDrive;
import frc.robot.subsystems.DriveTrain;

public class Robot extends TimedRobot {
  public double last_timestamp = Timer.getFPGATimestamp();

  /* Telemetry */
  NetworkTables nt_inst = NetworkTables.getInstance();

  ShuffleboardTab driver_view = Shuffleboard.getTab("Driver View");
  FieldStatusThread field_status = new FieldStatusThread();

  /* Vision */
  Camera main_camera;
  VisionInterface vision_interface;

  /* Networking and Logging */
  ConnectionMonitor net_monitor = ConnectionMonitor.getInstance();
  RobotLogger logger = RobotLogger.getInstance();

  /* Sybsystems */
  public static DriveTrain mDriveTrain;
  public static OI mOI;

  /* Commands */
  public TriggerDrive mTriggerDrive;

  @Override
  public void robotInit() {
    System.out.println("Robot starting...\nWelcome 6856!");
    logTimestamp();
    this.m_period = Constants.PeriodicTiming.robot_period;

    /* Start the CameraServer for the default USBCamera */
    logger.log("Starting CameraServer", Level.kRobot);
    this.main_camera = new Camera(Constants.MainCamera.name, Constants.MainCamera.http_port);
    main_camera.loadJsonConfig(FileUtils.constructDeployPath("maincamera.json"));
    main_camera.keepCameraAwake(true);

    // Push camera to shuffleboard
    nt_inst.getEntry("vision", "camera_fov").setNumber(Constants.MainCamera.fov);
    driver_view.add(main_camera.getCameraSever()).withSize(4, 4);

    /* Construct all Subsystems */
    logger.log("Constructing Subsystems", Level.kRobot);
    mOI = new OI();
    mDriveTrain = new DriveTrain();

    /* Initalize Subsystems if required */
    logger.log("Initializing Subsystems", Level.kRobot);
    mDriveTrain.setBrakes(true);

    /* Construct Commands */
    logger.log("Constructing Commands", Level.kRobot);
    this.mTriggerDrive = new TriggerDrive();

    /* Set up notifiers */
    logger.log("Setting up Notifiers", Level.kRobot);
    this.field_status.start(Constants.PeriodicTiming.field_period);
    this.logger.start(Constants.PeriodicTiming.logging_period);
    this.net_monitor.start(Constants.PeriodicTiming.net_monitor_period);

    /* Enable vision data thread */
    logger.log("Starting vision thread", Level.kRobot);
    this.vision_interface = VisionInterface.getInstance();
    this.vision_interface.start(Constants.PeriodicTiming.vision_thread);
  }


  @Override
  public void robotPeriodic() {
  }


  @Override
  public void disabledInit() {
    SmartDashboard.putString("Robot Mode", "DISABLED");
    logger.log("Robot Disabled", Level.kRobot);
    logTimestamp();

    // Stop all recordings
    Shuffleboard.stopRecording();
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();

    /* Run all updaters */
    updateSmartdashboard();
    updateTimestamp();
  }

  @Override
  public void autonomousInit() {
    SmartDashboard.putString("Robot Mode", "AUTO");
    logger.log("Autonomous Started with GSM: " + this.field_status.getCurrentMatch().getGSM(), Level.kRobot);
    logTimestamp();
    
    // Start recording video on driverstation computer
    Shuffleboard.startRecording();
  }

  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();

    /* Run all updaters */
    updateSmartdashboard();
    updateTimestamp();
  }

  @Override
  public void teleopInit() {
    SmartDashboard.putString("Robot Mode", "TELEOP");
    logger.log("Teleop started", Level.kRobot);
    logTimestamp();

    // Start recording video on driverstation computer
    Shuffleboard.startRecording();

    /* Start commands */
    this.mTriggerDrive.start();
    
  }

  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();

    /* Run all updaters */
    updateSmartdashboard();
    updateTimestamp();
  }

  @Override
  public void testPeriodic() {
  }

  /**
   * Asks all Subsystems to push their telemetry data to SmartDashboard
   */
  private void updateSmartdashboard() {
    mDriveTrain.outputTelemetry();
  }
  
  /**
   * Asks the FPGA how long it has been powered on, then stores the value in last_timestamp
   */
  private void updateTimestamp() {
    this.last_timestamp = Timer.getFPGATimestamp();
  }

  /**
   * Prints the current timestamp to the log
   */
  private void logTimestamp() {
    System.out.println("Current time: " + this.last_timestamp);
  }
}
