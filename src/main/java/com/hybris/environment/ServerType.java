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
	
	public ServerType getServerType(String code){
		if(code.equals("adm")){
			return ServerType.Admin;
		}else if(code.equals("app")){
			return ServerType.Application;
		}else if(code.equals("web")){
			return ServerType.Web;
		}else if(code.equals("srch")){
			return ServerType.Search;
		}else if(code.equals("db")){
			return ServerType.Database;
		}else{
			return null;
		}
	}
}
