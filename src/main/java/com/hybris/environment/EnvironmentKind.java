package com.hybris.environment;

public enum EnvironmentKind {
	
	HybrisClustered(new Server[]{new Server(ServerType.Admin),
								 new Server(ServerType.Application),
								 new Server(ServerType.Web),
								 new Server(ServerType.Search),
								 new Server(ServerType.Database)}),
	
	NonClustered(new Server[]{new Server(ServerType.Application),
							  new Server(ServerType.Web),
							  new Server(ServerType.Search),
							  new Server(ServerType.Database)});
	
	private Server[] servers;
	
	private EnvironmentKind(Server[] servers){
		this.setServers(servers);
	}

	public Server[] getServers() {
		return servers;
	}

	public void setServers(Server[] servers) {
		this.servers = servers;
	}
}
