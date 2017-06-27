package com.hybris.provider.specifications;

public enum Location {
	
	ApNorthEast2("ap-northeast-2"), ApNorthEast1("ap-northeast-1"), ApNorthEast2a("ap-northeast-2a"), ApNorthEast2c("ap-northeast-2c"), ApNorthEast1a("ap-northeast-1a"), ApNorthEast1c("ap-northeast-1c"),
	ApSouth1("ap-south-1"), ApSouthEast1("ap-southeast-1"), ApSouthEast2("ap-southeast-2"), ApSouth1a("ap-south-1a"), ApSouth1b("ap-south-1b"),
	SaEast1("sa-east-1"), SaEast1a("sa-east-1a"), SaEast1b("sa-east-1b"), SaEast1c("sa-east-1c"),
	UsEast1("us-east-1"), UsWest1("us-west-1"), UsWest2("us-west-2"), 
	EuWest1("eu-west-1"), EuWest1a("eu-west-1a"), EuWest1b("eu-west-1b"), EuWest1c("eu-west-1c"),
	EuCentral1("eu-central-1"), EuCentral1a("eu-central-1a"), EuCentral1b("eu-central-1b"), EuCentral1c("eu-central-1c");
	
	private String locationID;
	
	private Location(String locationId) {
		// TODO Auto-generated constructor stub
		this.locationID = locationId;
	}
	
	public String getID(){
		return this.locationID;
	}
	
}
