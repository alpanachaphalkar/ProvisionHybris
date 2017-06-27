package com.hybris.provider.specifications;

public enum Region {
	
	AWS_ApNorthEast2("ap-northeast-2"), AWS_ApNorthEast1("ap-northeast-1"), AWS_ApNorthEast2a("ap-northeast-2a"), AWS_ApNorthEast2c("ap-northeast-2c"), AWS_ApNorthEast1a("ap-northeast-1a"), AWS_ApNorthEast1c("ap-northeast-1c"),
	AWS_ApSouth1("ap-south-1"), AWS_ApSouth1a("ap-south-1a"), AWS_ApSouth1b("ap-south-1b"),
	AWS_ApSouthEast1("ap-southeast-1"), AWS_ApSouthEast1a("ap-southeast-1a"), AWS_ApSouthEast1b("ap-southeast-1b"),
	AWS_ApSouthEast2("ap-southeast-2"), AWS_ApSouthEast2a("ap-southeast-2a"), AWS_ApSouthEast2b("ap-southeast-2b"), AWS_ApSouthEast2c("ap-southeast-2c"),
	AWS_SaEast1("sa-east-1"), AWS_SaEast1a("sa-east-1a"), AWS_SaEast1b("sa-east-1b"), AWS_SaEast1c("sa-east-1c"),
	AWS_UsEast1("us-east-1"), AWS_UsEast1a("us-east-1a"), AWS_UsEast1b("us-east-1b"), AWS_UsEast1c("us-east-1c"), AWS_UsEast1d("us-east-1d"), AWS_UsEast1e("us-east-1e"),
	AWS_UsWest1("us-west-1"), AWS_UsWest1a("us-west-1a"), AWS_UsWest1b("us-west-1b"),
	AWS_UsWest2("us-west-2"), AWS_UsWest2a("us-west-2a"), AWS_UsWest2b("us-west-2b"), AWS_UsWest2c("us-west-2c"),
	AWS_EuWest1("eu-west-1"), AWS_EuWest1a("eu-west-1a"), AWS_EuWest1b("eu-west-1b"), AWS_EuWest1c("eu-west-1c"),
	AWS_EuCentral1("eu-central-1"), AWS_EuCentral1a("eu-central-1a"), AWS_EuCentral1b("eu-central-1b"), AWS_EuCentral1c("eu-central-1c"),
	
	GCP_AsiaEast1a("asia-east1-a"), GCP_AsiaEast1b("asia-east1-b"), GCP_AsiaEast1c("asia-east1-c"),
	GCP_AsiaNorthEast1a("asia-northeast1-a"), GCP_AsiaNorthEast1b("asia-northeast1-b"), GCP_AsiaNorthEast1c("asia-northeast1-c"),
	GCP_AsiaSouthEast1a("asia-southeast1-a"), GCP_AsiaSouthEast1b("asia-southeast1-b"),
	GCP_AustraliaSouthEast1a("australia-southeast1-a"), GCP_AustraliaSouthEast1b("australia-southeast1-b"), GCP_AustraliaSouthEast1c("australia-southeast1-c"),
	GCP_EuropeWest1b("europe-west1-b"), GCP_EuropeWest1c("europe-west1-c"), GCP_EuropeWest1d("europe-west1-d"),
	GCP_EuropeWest2a("europe-west2-a"), GCP_EuropeWest2b("europe-west2-b"), GCP_EuropeWest2c("europe-west2-c"),
	GCP_UsCentral1a("us-central1-a"), GCP_UsCentral1b("us-central1-b"), GCP_UsCentral1c("us-central1-c"), GCP_UsCentral1f("us-central1-f"),
	GCP_UsEast1b("us-east1-b"), GCP_UsEast1c("us-east1-c"), GCP_UsEast1d("us-east1-d"),
	GCP_UsEast4a("us-east4-a"), GCP_UsEast4b("us-east4-b"), GCP_UsEast4c("us-east4-c"),
	GCP_UsWest1a("us-west1-a"), GCP_UsWest1b("us-west1-b"), GCP_UsWest1c("us-west1-c");
	
	private String regionID;
	
	private Region(String locationId) {
		// TODO Auto-generated constructor stub
		this.regionID = locationId;
	}
	
	public String getID(){
		return this.regionID;
	}
	
}
