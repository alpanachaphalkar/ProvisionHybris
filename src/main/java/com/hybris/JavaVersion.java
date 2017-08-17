package com.hybris;

public enum JavaVersion {
	
	Java8u131("jdk-8u131-linux-x64.tar.gz", "java-8-oracle");
	
	private String packageName;
	private String javaVersion;
	
	private JavaVersion(String packageName, String javaVersion) {
		// TODO Auto-generated constructor stub
		this.setPackageName(packageName);
		this.setJavaVersion(javaVersion);
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getJavaVersion() {
		return javaVersion;
	}

	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}
	
}
