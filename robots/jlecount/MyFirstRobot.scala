package jlecount

import robocode._

class MyFirstRobot extends Robot {

  override def run = {
    while (true) {
			ahead(100); // Move ahead 100
			turnGunRight(360); // Spin gun around
			back(100); // Move back 100
			turnGunRight(360); // Spin gun around
    }
  }

	/**
	 * Fire when we see a robot
	 */
	override def onScannedRobot(e: ScannedRobotEvent) = {
		fire(1);
	}

	/**
	 * We were hit!  Turn perpendicular to the bullet,
	 * so our seesaw might avoid a future shot.
	 */
	override def onHitByBullet(e: HitByBulletEvent) = {
		turnLeft(90 - e.getBearing());
	}
}
