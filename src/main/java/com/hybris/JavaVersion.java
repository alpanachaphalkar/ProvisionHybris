package com.hybris;

public enum JavaVersion {
	
	Java8u131("jdk-8u131-linux-x64.tar.gz", "java-8-oracle");
	
	private String packageName;
	private String folderName;
	
	private JavaVersion(String packageName, String folderName) {
		// TODO Auto-generated constructor stub
		this.setPackageName(packageName);
		this.setFolderName(folderName);
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
}
