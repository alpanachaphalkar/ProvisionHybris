package com.hybris.provider.specifications;

public enum RamSize {
	
	Aws_Two(4), Aws_Eight(8), Aws_Sixteen(16),
	Gcp_Two(4 * 1024), Gcp_Eight(8 * 1024), Gcp_Sixteen(16 * 1024);
	
	private int size;
	
	private RamSize(int size) {
		// TODO Auto-generated constructor stub
		this.size = size;
	}
	
	public int getSize(){
		return this.size;
	}
	
	
}
