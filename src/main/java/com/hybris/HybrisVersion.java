package com.hybris;

public enum HybrisVersion {
	
	Hybris6_2_0("Hybris-6.2.0", "hybris-commerce-suite-6.2.0.4.zip", "solr-multicore-for-hybris-6.2.0.tgz", JavaVersion.Java8u131),
	Hybris6_3_0("Hybris-6.3.0", "hybris-commerce-suite-6.3.0.5.zip", "solr-multicore-for-hybris-6.3.0.tgz", JavaVersion.Java8u131);
	
	private String version;
	private String packageName;
	private String solrPackage;
	private JavaVersion javaVersion;
	
	private HybrisVersion(String version, String packageName, String solrPackage, JavaVersion javaVersion) {
		// TODO Auto-generated constructor stub
		this.setVersion(version);
		this.setPackageName(packageName);
		this.setSolrPackage(solrPackage);
		this.setJavaVersion(javaVersion);
	}

	public String getSolrPackage() {
		return solrPackage;
	}

	public void setSolrPackage(String solrPackage) {
		this.solrPackage = solrPackage;
	}

	public JavaVersion getJavaVersion() {
		return javaVersion;
	}


	public void setJavaVersion(JavaVersion javaVersion) {
		this.javaVersion = javaVersion;
	}


	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}


	public String getPackageName() {
		return packageName;
	}


	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
}
