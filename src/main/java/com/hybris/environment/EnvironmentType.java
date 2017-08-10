package com.hybris.environment;

public enum EnvironmentType {
	
	Development("d"), QA("q"), Testing("t"), Staging("s"), Production("p");
	
	private String code;
	
	private EnvironmentType(String code) {
		// TODO Auto-generated constructor stub
		this.code=code;
	}
	
	public String getCode(){
		return this.code;
	}
}
