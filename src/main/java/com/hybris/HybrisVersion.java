package com.hybris;

public enum HybrisVersion {
	
	Hybris6_2_0("Hybris-6.2.0", "hybris-commerce-suite-6.2.0.4.zip"),
	Hybris6_3_0("Hybris-6.3.0", "hybris-commerce-suite-6.3.0.5.zip");
	
	private String hybrisVersion;
	private String hybrisPackage;
	
	private HybrisVersion(String hybrisVersion, String hybrisPackage) {
		// TODO Auto-generated constructor stub
		this.hybrisVersion = hybrisVersion;
		this.hybrisPackage = hybrisPackage;
	}
	
	public String getHybrisPackage(){
		return this.hybrisPackage;
	}
	
	public String getHybrisVersion() {
		return this.hybrisVersion;
	}
	
}
