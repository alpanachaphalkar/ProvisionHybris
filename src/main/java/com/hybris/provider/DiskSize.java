package com.hybris.provider;

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
	
	/*public double getSize(Provider provider){
		switch (provider) {
		
		case AmazonWebService:
			return this.size;
		case GoogleCloudProvider:
			return this.size * 1024;

		default:
			return 0;
		}
	}*/
	
}
