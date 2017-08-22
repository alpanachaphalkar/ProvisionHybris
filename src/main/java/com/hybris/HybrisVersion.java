package com.hybris;

public enum HybrisVersion {
	
	Hybris6_2_0("Hybris-6.2.0", "hybris-commerce-suite-6.2.0.4.zip", "solr-multicore-for-hybris-6.2.0.tgz"),
	Hybris6_3_0("Hybris-6.3.0", "hybris-commerce-suite-6.3.0.5.zip", "solr-multicore-for-hybris-6.3.0.tgz");
	
	private String hybrisVersion;
	private String hybrisPackage;
	private String solrPackage;
	
	private HybrisVersion(String hybrisVersion, String hybrisPackage, String solrPackage) {
		// TODO Auto-generated constructor stub
		this.setHybrisVersion(hybrisVersion);
		this.setHybrisPackage(hybrisPackage);
		this.setSolrPackage(solrPackage);
	}
	

	public String getSolrPackage() {
		return solrPackage;
	}

	public void setSolrPackage(String solrPackage) {
		this.solrPackage = solrPackage;
	}


	public String getHybrisVersion() {
		return hybrisVersion;
	}


	public void setHybrisVersion(String hybrisVersion) {
		this.hybrisVersion = hybrisVersion;
	}


	public String getHybrisPackage() {
		return hybrisPackage;
	}


	public void setHybrisPackage(String hybrisPackage) {
		this.hybrisPackage = hybrisPackage;
	}
	
}
