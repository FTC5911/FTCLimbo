package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import org.firstinspires.ftc.teamcode.subsystems.LEDs;
import org.firstinspires.ftc.teamcode.subsystems.drive;
import org.firstinspires.ftc.teamcode.subsystems.delivery;
import org.firstinspires.ftc.teamcode.subsystems.collection;

import java.sql.Timestamp;

@TeleOp

public class teleopBlue extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    private ColorSensor colorSensor;
    private ColorSensor colorSensor2;



    String stage = "GROUND";
    String hang_stage = "Ground";

    public void runOpMode() throws InterruptedException {

        drive drive = new drive(hardwareMap);
        collection intake = new collection(hardwareMap);
        delivery slides = new delivery(hardwareMap);
        LEDs lusp = new LEDs(hardwareMap);

        colorSensor = hardwareMap.colorSensor.get("colorSensor");
        colorSensor2 = hardwareMap.colorSensor.get("colorSensor2");

        boolean intakeToggle = false;

        int i = 0;
        int s = 0;

        int ki = 0;
        int ks = 0;

        Gamepad currentGamepad1 = new Gamepad();
        Gamepad currentGamepad2 = new Gamepad();

        Gamepad previousGamepad1 = new Gamepad();
        Gamepad previousGamepad2 = new Gamepad();

        slides.back_to_start();

        waitForStart();
        if (isStopRequested()) return;
        while (opModeIsActive()) {


            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x * 1.1;
            double rx = gamepad1.right_stick_x;

            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);

            double pFrontRight = (y + x + rx) / denominator;
            double pBackRight = (y - x + rx) / denominator;
            double pFrontLeft = (y - x - rx) / denominator;
            double pBackLeft = (y + x - rx) / denominator;

            final double fallSpeed = 0.8;
            final double liftSpeed = 1;

          previousGamepad1.copy(currentGamepad1);
          previousGamepad2.copy(currentGamepad2);



          currentGamepad1.copy(gamepad1);
          currentGamepad2.copy(gamepad2);

            if (gamepad1.square) {
                drive.drive(pFrontRight + 0.5, pBackRight - 0.5,
                        pFrontLeft - 0.5, pBackLeft + 0.5);
            } else if (gamepad1.circle) {
                drive.drive(pFrontRight - 0.5, pBackRight + 0.5,
                        pFrontLeft + 0.5, pBackLeft - 0.5);
            } else {
                drive.drive(pFrontRight, pBackRight, pFrontLeft, pBackLeft);
            }


            if (gamepad2.left_trigger > 0.1) {
                slides.back_to_start();
            } else if (gamepad2.right_trigger > 0.1) {
                slides.dump();
            } else if (gamepad2.left_bumper){
                slides.gyat();
            }

            if (currentGamepad2.right_bumper && !previousGamepad2.right_bumper){
                intakeToggle = !intakeToggle;
            }

            if (intakeToggle) {
                slides.ready_to_go();
            } else {
                slides.back_to_sleep();
            }


            if (gamepad2.dpad_left){
                slides.ready_to_go();
            } else if (gamepad2.dpad_right){
                slides.back_to_sleep();
            }





            if (gamepad2.x) {
                hang_stage = "GROUND";
                ks = 1;

            } else if (gamepad2.triangle) {
                hang_stage = "LOW";
                ks = 1;
            } else if (ks == 1){
                slides.stage_hang(hang_stage);
            }

            if (gamepad2.dpad_up) {
                slides.extend(liftSpeed);
                s = 0;

            } else if (gamepad2.dpad_down) {
                slides.retract(fallSpeed);
                s = 0;

            } else if (s == 1) {
                slides.moveToStage(stage);
            } else {
                slides.stall();
            }
            if (gamepad1.right_trigger > 0.1) {
                intake.grab_pixel();

            } else if (gamepad1.left_trigger > 0.1) {
                intake.reverse_intake();

            } else {
                intake.no_feed();
            }
            if (gamepad1.right_bumper) {
                intake.spin_up();
            } else if (gamepad1.left_bumper) {
                intake.spin_down();

            } else {
                intake.dont_move();
            } if (gamepad2.left_bumper) {
                    slides.launch();
            } else if (gamepad2.right_bumper) {
                    slides.reload();
            }
                resetRuntime();


                if (runtime.seconds() > 80 && runtime.seconds() < 90) {

                    lusp.flash("yellow", "purple", runtime);

                } else {


                    if (((DistanceSensor) colorSensor).getDistance(DistanceUnit.MM) < 35 ^
                            ((DistanceSensor) colorSensor2).getDistance(DistanceUnit.MM) < 52) {

                        lusp.setColor("yellow");

                        if (i == 0) {

                            gamepad1.rumble(500);
                            gamepad2.rumble(500);
                            ++i;

                        }


                    } else if (((DistanceSensor) colorSensor).getDistance(DistanceUnit.MM) < 35 &&
                            ((DistanceSensor) colorSensor2).getDistance(DistanceUnit.MM) < 52) {

                        lusp.setColor("green");
                        if (i == 1) {

                            gamepad1.rumble(500);
                            gamepad2.rumble(500);
                            ++i;

                        }
                    } else {

                        lusp.setColor("dark blue");
                        i = 0;

                    }
                }

            }
        }
    }
