/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package movement;

import java.util.ArrayList;
import java.util.List;


import core.Coord;

/**
 * A Path between multiple Coordinates.
 */
public class Path  {
	/** coordinates of the path */
	private List<Coord> coords;
	/** speeds in the path legs */
	private List<Double> speeds;
	private int nextWpIndex;
	
	/**
	 * Creates a path with zero speed.
	 */
	public Path() {
		this.nextWpIndex = 0;
		this.coords = new ArrayList<Coord>();
		this.speeds = new ArrayList<Double>(1);
	}

	/**
	 * Copy constructor. Creates a copy of this path with a shallow copy of
	 * the coordinates and speeds.
	 * @param path The path to create the copy from
	 */
	public Path(Path path) {
		this.nextWpIndex = path.nextWpIndex;
		this.coords = new ArrayList<Coord>((ArrayList<Coord>)path.coords);
		this.speeds = new ArrayList<Double>((ArrayList<Double>)path.speeds);
	}
	
	/**
	 * Creates a path with constant speed
	 * @param speed The speed on the path
	 */
	public Path(double speed) {
		this();
		setSpeed(speed);
	}
	
	/**
	 * Sets a constant speed for the whole path. Any previously set speed(s)
	 * is discarded.
	 */
	public void setSpeed(double speed) {
		this.speeds = new ArrayList<Double>(1);
		speeds.add(speed);
	}
	
	/**
	 * Returns a reference to the coordinates of this path 
	 * @return coordinates of the path
	 */
	public List<Coord> getCoords() {
		return this.coords;
	}
	
	/**
	 * Adds a new waypoint to the end of the path.
	 * @param wp The waypoint to add
	 */
	public void addWaypoint(Coord wp) {
		assert this.speeds.size() <= 1 : "This method should be used only for" +
			" paths with constant speed";
		this.coords.add(wp);
	}
	
	/**
	 * Adds a new waypoint with a speed towards that waypoint
	 * @param wp The waypoint
	 * @param speed The speed towards that waypoint
	 */
	public void addWaypoint(Coord wp, double speed) {
		this.coords.add(wp);
		this.speeds.add(speed);
	}
	
	/**
	 * Returns the next waypoint on this path
	 * @return the next waypoint
	 */
	public Coord getNextWaypoint() {
		//assert hasNext() : "Path didn't have " + (nextWpIndex+1) + ". waypoint";
		if(hasNext() == false) {
			nextWpIndex = 0;
			return coords.get(nextWpIndex);
		}
		return coords.get(nextWpIndex++);
	}
	
	public Coord getNextWaypoint(int speed) {
		if(speed==0){
			return coords.get(nextWpIndex);
		}
		return coords.get(nextWpIndex++);
	}
	
	/**
	 * Returns true if the path has more waypoints, false if not
	 * @return true if the path has more waypoints, false if not
	 */
	public boolean hasNext() {
		return nextWpIndex < this.coords.size();
	}
	
	/**
	 * Returns the speed towards the next waypoint (asked with
	 * {@link #getNextWaypoint()}. 
	 * @return the speed towards the next waypoint
	 */
	public double getSpeed() {
		assert speeds.size() != 0 : "No speed set"; 
		assert nextWpIndex != 0 : "No waypoint asked";
		
		if (speeds.size() == 1) {
			return speeds.get(0);
		}
		else {
			return speeds.get(nextWpIndex-1);
		}
	}
	
	/**
	 * Returns a string presentation of the path's coordinates
	 * @return Path as a string
	 */
	public String toString() {
		String s ="";
		for (int i=0, n=coords.size(); i<n; i++) {
			Coord c = coords.get(i);
			s+= "->" + c;
			if (speeds.size() > 1) {
				s += String.format("@%.2f ",speeds.get(i));
			}
		}
		return s;
	}
	
	public List<Double> getSpeeds() {
		return this.speeds;
	}
	
	/**
	 * CS218
	 * Calculate probability to meet the destination
	 */
	public double encounterProbability(Path dstPath) {
		double probability = 0;
		int totalCount = 0;
		int encounterCount = 0;
		double txRange = 15;
		
		ArrayList<Coord> dstCoords = new ArrayList<Coord>();
		
		try {
			dstCoords = (ArrayList<Coord>) dstPath.getCoords();
			
			for (int i=0; i<this.coords.size(); i++) {
				Coord srcCoord = this.coords.get(i);
				double srcMinX = srcCoord.getX() - txRange;
				double srcMaxX = srcCoord.getX() + txRange;
				double srcMinY = srcCoord.getY() - txRange;
				double srcMaxY = srcCoord.getY() + txRange;
				
				for (int j=0; j<dstCoords.size(); j++) {
					totalCount += 1;
					
					Coord dstCoord = dstCoords.get(j);
					double dstX = dstCoord.getX();
					double dstY = dstCoord.getY();
					
					if(dstX >= srcMinX && dstX <= srcMaxX && dstY >= srcMinY && dstY <= srcMaxY) {
						encounterCount += 1;
					}
				}
			}
			
			if(totalCount == 0) {
				return 0;
			}
			
			probability = (double)encounterCount / (double)totalCount;
			
			return probability;
		} catch(NullPointerException e) {
			return 0;
		}
		
	}
}
