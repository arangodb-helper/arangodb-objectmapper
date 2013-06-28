package org.arangodb.objectmapper.test;

import org.arangodb.objectmapper.jackson.ArangoDbDocument;

public class Point extends ArangoDbDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7100161612921352369L;

	private Integer x;
	
	private Integer y;

	public Point() {
	}
	
	public Point(Integer x, Integer y) {
		this.x = x;
		this.y = y;
	}
	
	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}
	
	public String toString() {
		return "[point@" + getKey() + ":" + x + "x" + y + "]";
	}
}
