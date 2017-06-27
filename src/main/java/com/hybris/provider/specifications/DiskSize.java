package com.hybris.provider.specifications;

public enum DiskSize {
	
	Ten(10.0), Twenty(20.0), Fourty(40.0), Eighty(80.0), Hundred(100.0), TwoHundred(200.0);
	
	private double size;
	
	private DiskSize(double size) {
		// TODO Auto-generated constructor stub
		this.size = size;
	}
	
	public double getSize(){
		return this.size;
	}
	
}
