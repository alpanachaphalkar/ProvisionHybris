package com.hybris.provider;

public enum Cpu {
	
	One64bit(1.0, true), Two64bit(2.0, true), Four64bit(4.0, true), Eight64bit(8.0, true), Sixteen64bit(16.0, true),
	One32bit(1.0, false), Two32bit(2.0, false), Four32bit(4.0, false), Eight32bit(8.0, false), Sixteen32bit(16.0, false);
	
	private double number;
	private boolean type;
	
	private Cpu(double number, boolean type){
		this.number = number;
		this.type = type;
	}
	
	public double getCores(){
		return this.number;
	}
	
	public boolean getType(){
		return this.type;
	}
	
}
