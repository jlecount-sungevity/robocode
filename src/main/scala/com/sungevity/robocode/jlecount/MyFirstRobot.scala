/**
 * Copyright (c) 2001-2016 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 */
package com.sungevity.robocode.jlecount


import java.awt.Color
import robocode._
import scala.util.Random
import java.io.File

/**
  *  jlecount -- basic robot with a few tweaks:
  *
  * debugging output with logToFile() -- log file will be target/scala-2.11/classes/com/sungevity/robocode/jlecount/MyFirstRobot.data/robot.log
  * better random movement to be less predicable
  * better targeting 
  * bounce off walls by reversing direction
  * fire continually until we miss
  */
class MyFirstRobot extends AdvancedRobot {

  val rnd = Random
  val MAX_SHORT_WALK = 400
  var moveDirection = 1

  var outputFile:File = _
  var logger:RobocodeFileOutputStream = _

  /**
   * MyFirstRobot's run method - Seesaw
   */
  override def run() {
    outputFile = getDataFile("robot.log")
    setGunColor(Color.RED)
    setBodyColor(Color.YELLOW)
    setRadarColor(Color.CYAN)

    while (true) {
      moveRandomly
    }
  }

  /**
   * Fire when we see a robot
    */
  override def onScannedRobot(e: ScannedRobotEvent) {
    val absBearing = e.getBearingRadians + getHeadingRadians //enemies absolute bearing
    val latVel = e.getVelocity * Math.sin(e.getHeadingRadians -absBearing ) //enemies later velocity
    setTurnRadarLeftRadians(getRadarTurnRemainingRadians());//lock on the radar

    if (Math.random() > .9) setMaxVelocity((12 * Math.random) + 12) //randomly change speed

    pointGunAtEnemy(e.getBearing)
    setAhead((e.getDistance - 140) * moveDirection)
    fireAtRobot(Rules.MAX_BULLET_POWER, e.getDistance)
  }

  def fireUntilMiss(power:Double, distanceToEnemy:Double):Unit = {
    Option(fireAtRobot(power, distanceToEnemy)) match {
      case Some(robot) => {
        fireUntilMiss(power, distanceToEnemy)
      }
      case _ => {
        moveRandomly
        scan
      }
    }
  }

  // normalizes a bearing recursively, to between +180 and -180
  def normalizeBearing(angle:Double):Double = {
    angle match {
      case angle if angle > 180 => normalizeBearing(angle - 360)
      case angle if angle < -180 => normalizeBearing(angle + 360)
      case _ => return angle
    }
  }

  def moveRandomly():Unit = {
    if (rnd.nextBoolean) turnLeft(rnd.nextInt(MAX_SHORT_WALK)) else turnRight(rnd.nextInt(MAX_SHORT_WALK))
    ahead(rnd.nextInt(MAX_SHORT_WALK))
  }

  def logToFile(msg:String) = {
    logger = new RobocodeFileOutputStream(outputFile)
    logger.write((msg + "\n").getBytes)
    logger.close
  }

  override def onHitWall(event: HitWallEvent) = {
    moveDirection = -moveDirection
  }

  def pointGunAtEnemy(enemyBearing:Double): Unit = {
    setTurnGunRight(normalizeBearing(getHeading - getGunHeading + enemyBearing)) // point gun at enemy, most efficient path possible
  }

  override def onHitRobot(event: HitRobotEvent) = {
    pointGunAtEnemy(event.getBearing)

    logToFile(s"Robot collision from robot at ${event.getBearing}")
    fireUntilMiss(Rules.MAX_BULLET_POWER, 0)
    moveRandomly
  }

  def fireAtRobot(power: Double, distanceToTarget:Double):Option[Bullet] = {
    logToFile(s"Firing at ${getGunHeading} with power $power")
    Option(fireBullet((400 / distanceToTarget))) match {
      case Some(victim) => logToFile("HIT the robot!"); Some(victim)
      case None => logToFile("MISSED the robot!"); None
    }
  }
  /**
   * We were hit!  Turn perpendicular to the bullet,
   * so our seesaw might avoid a future shot.
   */
  override def onHitByBullet(event: HitByBulletEvent) {
    logToFile(s"Hit by bullet from robot at ${event.getBearing}")
    pointGunAtEnemy(event.getBullet.getHeading)
    fireAtRobot(Rules.MAX_BULLET_POWER, 500) // unknown distance, we'll make it up.
    turnLeft(90 - event.getBearing())
  }
}
