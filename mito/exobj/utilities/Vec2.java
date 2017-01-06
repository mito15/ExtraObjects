package com.mito.exobj.utilities;

public class Vec2 {

	public double xCoord;
	public double yCoord;
	
	public Vec2(double a, double b){
		this.xCoord = a;
		this.yCoord = b;
	}
	
	public double dotProduct(Vec2 v){
		return this.xCoord * v.xCoord + this.yCoord * v.yCoord;
	}
	
	public double crossProduct(Vec2 v){
		return this.xCoord * v.yCoord - this.yCoord * v.xCoord;
	}
	
	public Vec2 sum(Vec2 v){
		return new Vec2(this.xCoord + v.xCoord, this.yCoord + v.yCoord);
	}
	
	public Vec2 scalarProduct(double d){
		return new Vec2(this.xCoord * d, this.yCoord * d);
	}
	
	public double absolute(){
		return Math.sqrt(this.xCoord * this.xCoord + this.yCoord * this.yCoord);
	}

}
