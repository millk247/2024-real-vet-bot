// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//Added imports
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;  //used for follow() method

import javax.lang.model.util.ElementScanner14;

import com.ctre.phoenix.motorcontrol.ControlMode;  //black VEX pro motor controler
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;  //black VEX pro motor controler
import edu.wpi.first.wpilibj.Timer;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultDriveBack = "Default Auto_drive back";
  private static final String kShootBack = "lean_shoot_driveback";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //added
 

  //below are constants.  Benifit: the constant may show up in more than one place, you only have to change one.
  //                               your code has words and not just numbers
  private static final int leftfrontID = 2;
  private static final int leftbackID = 1;
  private static final int rightfrontID = 11;
  private static final int rightbackID = 12;
  private static final int feedID = 5;
  private static final int lauchTopID = 6;
  private static final int launchBottomID = 7;
  private static final int rotateLeftID = 8;
  private static final int rotateRightID = 9;

  //motor controlers
  private final WPI_VictorSPX m_leftfront = new WPI_VictorSPX(leftfrontID);
  private final WPI_VictorSPX m_leftback = new WPI_VictorSPX(leftbackID);
  private final WPI_VictorSPX m_righfront = new WPI_VictorSPX(rightfrontID);
  private final WPI_VictorSPX m_rightback = new WPI_VictorSPX(rightbackID);
  private final CANSparkMax m_feed = new CANSparkMax(feedID, MotorType.kBrushed);
  private final CANSparkMax m_launchTop = new CANSparkMax(lauchTopID, MotorType.kBrushless);
  private final CANSparkMax m_launchBottom = new CANSparkMax(launchBottomID, MotorType.kBrushless);
  private final CANSparkMax m_rotateLeft = new CANSparkMax(rotateLeftID, MotorType.kBrushless);
  private final CANSparkMax m_rotateRight = new CANSparkMax(rotateRightID, MotorType.kBrushless);


  private final DifferentialDrive robotDrive = new DifferentialDrive(m_leftfront, m_righfront);
  private final Joystick driver = new Joystick(0);
  private final XboxController operator = new XboxController(1);
  
  private final Timer timerY = new Timer();
  private final Timer timerA = new Timer();

  double rotateSpeed = 0; //constants
  double launchpower = 0;
  double feedpower = 0;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto_drive back", kDefaultDriveBack);
    m_chooser.addOption("lean_shoot_driveback", kShootBack);
    SmartDashboard.putData("Auto choices", m_chooser);

     //invert motor
     m_leftfront.setInverted(true);
     m_rotateRight.setInverted(false);
     m_leftback.setInverted(true);

     //CANREV follow method

      m_leftback.follow(m_leftfront);
     m_rightback.follow(m_righfront);
     m_launchBottom.follow(m_launchTop);
     //m_rotateRight.follow(m_rotateLeft);
 
    //CAN CTRE (VICTOR SPX) follower method
    // m_leftback.set(ControlMode.Follower, leftfrontID);
    // m_rightback.set(ControlMode.Follower, rightbackID);
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    timerY.reset();
    timerY.start();

    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {

    
    switch (m_autoSelected) {
      case kDefaultDriveBack:
      default:
      if (timerY.get() < 2.25){
       robotDrive.arcadeDrive(-0.35, 0,false);
      }
        break;

      case kShootBack:
   // speaker shoot and drive backwards
      if (timerY.get() < 1){
        m_rotateLeft.set(-0.2);
        m_feed.set(0);
        m_launchTop.set(-0.2);
        robotDrive.arcadeDrive(0, 0,false);

      } else if (timerY.get() < 2){
        m_rotateLeft.set(0);
        m_feed.set(0.5);
        m_launchTop.set(-0.2);
        robotDrive.arcadeDrive(0, 0,false);
      
      }else if (timerY.get() < 4.25){
        m_rotateLeft.set(0);
        m_feed.set(0);
        m_launchTop.set(0);
        robotDrive.arcadeDrive(-0.35, 0,false);
      }else{
        m_rotateLeft.set(0);
        m_feed.set(0);
        m_launchTop.set(0);
        robotDrive.arcadeDrive(0, 0,false);
      }
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}


  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {

        

    robotDrive.arcadeDrive(driver.getY(), driver.getX());

    
     
     if(operator.getYButton()){
      launchpower = 0.7;
    }  
    else if(operator.getAButton()){
      launchpower = -0.3;
    }

    else{
      launchpower = 0;
    }


    double intakepower = operator.getRightTriggerAxis();
    double outputpower = operator.getLeftTriggerAxis();
    if(intakepower > 0){
      intakepower *= -1;
      outputpower = 0;
      feedpower = intakepower + outputpower;

    } else if(outputpower > 0){
       intakepower = 0;
       outputpower *= 1;
       feedpower = intakepower + outputpower;

    } else {
      intakepower = 0;
      outputpower = 0; 
      feedpower = 0;
    }  
    
      //rotate arm
    double rotateSpeed = operator.getLeftY(); //Get the rotate speed, forward is down
      if(rotateSpeed > 0) { //stick pulled down(+) -> rotate up
        rotateSpeed *= 0.4; //apply a scale factor of to the rotateSpeed variable
      }  else if(rotateSpeed < 0) {  // stick pushed forward (-)  -> rotate down
        rotateSpeed *= 0.4;
      }  else{
        rotateSpeed *= 0;  // I would like to use a motor stop here
      }
      
     
    
    
      




    m_feed.set(feedpower);

     m_rotateLeft.set(rotateSpeed); // motor values are set based on logic above
     m_rotateRight.set(rotateSpeed);

     m_launchTop.set(launchpower);


     
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
  
} // end of class
