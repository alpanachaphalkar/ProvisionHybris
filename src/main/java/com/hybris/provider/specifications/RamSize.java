package com.hybris.provider.specifications;

public enum RamSize {
	
	Two(4), Eight(8), Sixteen(16);
	
	private int size;
	
	private RamSize(int size) {
		// TODO Auto-generated constructor stub
		this.size = size;
	}
	
	public int getSize(){
		return this.size;
	}
	
}
