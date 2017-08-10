package com.hybris.environment;

public class Server {

	private ServerType serverType;
	private ServerInstance serverInstance;
	private int severCount;
	
	public Server(ServerType type, ServerInstance instance, int count) {
		// TODO Auto-generated constructor stub
		this.setServerType(type);
		this.setSeverCount(count);
		this.setServerInstance(instance);
	}
	
	public ServerType getServerType() {
		return serverType;
	}
	public void setServerType(ServerType serverType) {
		this.serverType = serverType;
	}
	public int getSeverCount() {
		return severCount;
	}
	public void setSeverCount(int severCount) {
		this.severCount = severCount;
	}

	public ServerInstance getServerInstance() {
		return serverInstance;
	}

	public void setServerInstance(ServerInstance serverInstance) {
		this.serverInstance = serverInstance;
	}
	
}
