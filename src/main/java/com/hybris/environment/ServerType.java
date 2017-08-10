package com.hybris.environment;

public enum ServerType {

	Application("app"), Admin("adm"), Search("srch"), Web("web"), Database("db");
	
	private String code;
	
	private ServerType(String code) {
		// TODO Auto-generated constructor stub
		this.code = code;
	}
	
	public String getCode(){
		return code;
	}
}
