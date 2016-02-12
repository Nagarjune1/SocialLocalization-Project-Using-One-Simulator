/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package movement;

import java.util.ArrayList;
import java.util.List;

import core.Coord;
import core.DTNHost;
import core.Settings;
import core.SimScenario;
import core.World;

/**
 * Random Walk movement model
 * 
 * @author Frans Ekman
 */
public class LeaderMovement extends MovementModel implements SwitchableMovement {

	private Coord lastWaypoint;
	private double minDistance;
	private double maxDistance;
	private Coord finalDestination;
	private double range;
	private int numOfLeaders;
	
	public LeaderMovement(Settings settings) {
		super(settings);
		minDistance = 0;
		maxDistance = 50;
		
	}
	
	private LeaderMovement(LeaderMovement rwp) {
		super(rwp);
		minDistance = rwp.minDistance;
		maxDistance = rwp.maxDistance;
		finalDestination = new Coord(10000,10000);
		range = 500;
		numOfLeaders = 3;
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
		
		
		double x = 2000;
		double y = 2000;
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
		
		Coord nextPosition = null;
		while (true) {
			
//			double angle = rng.nextDouble() * 2 * Math.PI;
//			double distance = minSpeed + rng.nextDouble() * 
//				(maxSpeed - minSpeed);
//			
//			double x = lastWaypoint.getX() + distance * Math.cos(angle);
//			double y = lastWaypoint.getY() + distance * Math.sin(angle);
		
			World w  = SimScenario.getInstance().getWorld();
			List<DTNHost> nodeList = w.getHosts();
			List<DTNHost> otherLeaders = new ArrayList<DTNHost>();
			List<DTNHost> connectedPair = new ArrayList<DTNHost>();
//			System.out.println("current Node: " + h.getAddress());
			int tempNext = h.getAddress()+1;
			if(tempNext == numOfLeaders){
				tempNext = 0;
			}
			int tempPrevious = h.getAddress()-1;
			if(tempPrevious == -1){
				tempPrevious = numOfLeaders - 1;
			}
			for(DTNHost n: nodeList){
				if(!n.equals(h)&&n.getGroupId().equalsIgnoreCase("L")){
					otherLeaders.add(n);
				}
				
				if((n.getAddress()==tempNext||n.getAddress()==tempPrevious)&&n.getGroupId().equalsIgnoreCase("L")){
					connectedPair.add(n);
				}
//				System.out.println("address: " + n.getAddress() + " location: " + n.getLocation().toString());
			}
			for(DTNHost n: connectedPair){
				System.out.println("address: " + n.getAddress() + " location: " + n.getLocation().toString() + " Name: " + n.getName());
			}
			System.out.println(h.getName() + h.getLocation().toString() +" " + lastWaypoint.toString());
			
			
			Coord L1 = connectedPair.get(0).getLocation();
			Coord L2 = connectedPair.get(1).getLocation();
			
			double x = 0;
			double y = 0;
			double angle = rng.nextDouble() * 2 * Math.PI;
			if(L1.getX()==L2.getX()&&L1.getY()==L2.getY()){
				double distance = minSpeed + rng.nextDouble() * 
						(maxSpeed - minSpeed);
					
				x = lastWaypoint.getX() + distance * Math.cos(angle);
				y = lastWaypoint.getY() + distance * Math.sin(angle);
			}else{
				//find the answer for the intersection points of two circles.
				double p1 = Math.pow(L1.getX(), 2) + Math.pow(L1.getY(), 2) - Math.pow(L2.getX(), 2) - Math.pow(L2.getY(), 2);
				p1 = p1/(2*(L1.getX()-L2.getX()));
				
				double p2 = 2*(L1.getY() - L2.getY());
				p2 = p2/(2*(L1.getX()-L2.getX()));
				
				double a = Math.pow(p2, 2)+1;
				double b = -(2*(p1-L1.getX())*p2 + 2*L1.getY());
				double c = Math.pow(p1-L1.getX(), 2) + Math.pow(L1.getY(), 2) - Math.pow(range, 2);
				
				if(Math.pow(b, 2) - 4*a*c<=0){
					System.out.println("error!");
					double tempX = (L1.getX() + L2.getX())/2;
					double tempY = (L1.getY() + L2.getY())/2;
					
				}else {
				double y1 = (-b+Math.pow(Math.pow(b, 2) - 4*a*c, 0.5))/(2*a);
				double y2 = (-b-Math.pow(Math.pow(b, 2) - 4*a*c, 0.5))/(2*a);
				
				double x1 = p1 - p2*y1;
				double x2 = p1- p2*y2;
				
				System.out.println("(x1,y1): "+x1 + " " + y1);
				System.out.println("(x2,y2): "+x2 + " " + y2);
				
				double distance1 = Math.pow(x1-finalDestination.getX(), 2) + Math.pow(y1 - finalDestination.getY(), 2);
				double distance2 = Math.pow(x2-finalDestination.getX(), 2) + Math.pow(y2 - finalDestination.getY(), 2);
				Coord intersection = new Coord(0, 0);
				//find the intersection which is more near to the destination
				if(distance1<distance2){
					intersection.setLocation(x1, y1);
				}else{
					intersection.setLocation(x2, y2);
				}
				//move towards the selected intersection.
				
				double distance = minSpeed + rng.nextDouble() * 
						(maxSpeed - minSpeed);
				//use angle to calculate
				angle = Math.atan((intersection.getY()-lastWaypoint.getY())/(intersection.getX() - lastWaypoint.getX()));
				System.out.println("angle: " +angle + " " + (intersection.getY()-lastWaypoint.getY()) + " " +(intersection.getX() - lastWaypoint.getX()));
			
				if(angle<0){
					angle = angle*(-1);
				}
				if(distance > 0.5 * Math.pow(Math.pow(intersection.getY()-lastWaypoint.getY(), 2) + Math.pow(intersection.getX() - lastWaypoint.getX(), 2), 0.5)){
					distance = 0.5 * Math.pow(Math.pow(intersection.getY()-lastWaypoint.getY(), 2) + Math.pow(intersection.getX() - lastWaypoint.getX(), 2), 0.5);
				}
				System.out.println("cos: " + Math.cos(angle) + " sin: "+ Math.sin(angle));
				if(intersection.getX()>lastWaypoint.getX()){
					x = lastWaypoint.getX() + distance * Math.cos(angle);
				}else {
					x = lastWaypoint.getX() - distance * Math.cos(angle);
				}
				if(intersection.getY()>lastWaypoint.getY()){
					y = lastWaypoint.getY() + distance * Math.sin(angle);
				}else{
					y = lastWaypoint.getY() - distance * Math.sin(angle);
				}
				System.out.println("move to (x,y): " +x + " "+y);
				
				//use linear function y =ax+b
//				a = (intersection.getY()-lastWaypoint.getY())/(intersection.getX() - lastWaypoint.getX());
//				b = intersection.getY() - a* intersection.getX();
				
				
				}
			}
			nextPosition = new Coord(x,y);
			if (x > 0 && y > 0 && x < maxX && y < maxY) {
				break;
			}
		}
		
		p.addWaypoint(nextPosition);
		
		this.lastWaypoint = nextPosition;
		return p;
	}
	
	@Override
	public LeaderMovement replicate() {
		return new LeaderMovement(this);
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
