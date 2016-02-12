/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package movement;

import core.Coord;
import core.DTNHost;
import core.Settings;

/**
 * Random Walk movement model
 * 
 * @author Frans Ekman
 */
public class FollowerMovement extends MovementModel implements SwitchableMovement {

	private Coord lastWaypoint;
	private double minDistance;
	private double maxDistance;
	
	public FollowerMovement(Settings settings) {
		super(settings);
		minDistance = 0;
		maxDistance = 50;
	}
	
	private FollowerMovement(FollowerMovement rwp) {
		super(rwp);
		minDistance = rwp.minDistance;
		maxDistance = rwp.maxDistance;
	}
	
	/**
	 * Returns a possible (random) placement for a host
	 * @return Random position on the map
	 */
	@Override
	public Coord getInitialLocation() {
//		assert rng != null : "MovementModel not initialized!";
//		double x = rng.nextDouble() * getMaxX();
//		double y = rng.nextDouble() * getMaxY();
		
		double x = 0;
		double y = 0;
		Coord c = new Coord(x,y);

		this.lastWaypoint = c;
		return c;
	}
	
	@Override
	public Path getPath(DTNHost h) {
		Path p;
		p = new Path(generateSpeed());
		p.addWaypoint(lastWaypoint.clone());
		double maxX = getMaxX();
		double maxY = getMaxY();
		
		Coord c = null;
		while (true) {
			
			double angle = rng.nextDouble() * 2 * Math.PI;
			double distance = minSpeed + rng.nextDouble() * 
				(maxSpeed - minSpeed);
			
			double x = lastWaypoint.getX() + distance * Math.cos(angle);
			double y = lastWaypoint.getY() + distance * Math.sin(angle);
		
			c = new Coord(x,y);
			
			if (x > 0 && y > 0 && x < maxX && y < maxY) {
				break;
			}
		}
		
		p.addWaypoint(c);
		
		this.lastWaypoint = c;
		return p;
	}
	
	@Override
	public FollowerMovement replicate() {
		return new FollowerMovement(this);
	}

	public Coord getLastLocation() {
		return lastWaypoint;
	}

	public void setLocation(Coord lastWaypoint) {
		this.lastWaypoint = lastWaypoint;
	}

	public boolean isReady() {
		return true;
	}
}
