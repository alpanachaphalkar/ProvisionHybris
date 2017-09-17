package com.hybris;

public enum JavaVersion {
	
	Java8u131("jdk-8u131-linux-x64.tgz", "java-8-oracle");
	
	private String packageName;
	private String version;
	
	private JavaVersion(String packageName, String version) {
		// TODO Auto-generated constructor stub
		this.setPackageName(packageName);
		this.setVersion(version);
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}
