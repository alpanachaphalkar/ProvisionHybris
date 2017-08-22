package com.hybris.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;

import com.hybris.ConfigurationKeys;
import com.hybris.HybrisRecipe;
import com.hybris.HybrisVersion;
import com.hybris.JavaVersion;
import com.hybris.provider.Provider;


public class Environment {
	
	public String project_code;
	public EnvironmentType environment_type;
	public Provider provider;
	public static final String SERVER_DOMAIN=".hybrishosting.com";
	
	public Environment(Provider provider, String projectCode, EnvironmentType environmentType) {
		// TODO Auto-generated constructor stub
		this.project_code = projectCode;
		this.environment_type = environmentType;
		this.provider = provider;
	}
	
	public Properties getConfigurationProps(HybrisVersion hybrisVersion, HybrisRecipe hybrisRecipe, JavaVersion javaVersion, String domainName){
		Properties configurationProps = new Properties();
		configurationProps.setProperty(ConfigurationKeys.hybris_version.name(), hybrisVersion.getHybrisVersion());
		configurationProps.setProperty(ConfigurationKeys.hybris_package.name(), hybrisVersion.getHybrisPackage());
		configurationProps.setProperty(ConfigurationKeys.hybris_recipe.name(), hybrisRecipe.getRecipeId());
		configurationProps.setProperty(ConfigurationKeys.java_version.name(), javaVersion.getJavaVersion());
		configurationProps.setProperty(ConfigurationKeys.java_package.name(), javaVersion.getPackageName());
		configurationProps.setProperty(ConfigurationKeys.solr_package.name(), hybrisVersion.getSolrPackage());
		configurationProps.setProperty(ConfigurationKeys.default_shop.name(), hybrisRecipe.getDefaultShop());
		configurationProps.setProperty(ConfigurationKeys.domain_name.name(), domainName);
		return configurationProps;
	}
	
	private ServerType getServerType(String hostname){
		
		String serverTypeCode = hostname.split("-")[3];
		ServerType serverType = ServerType.Admin;
		serverType = serverType.getServerType(serverTypeCode);
		return serverType;
	}
	
	private String getClusterId(String hostname){
		
		String clusterId="0";
		String hostCount=hostname.split("-")[4].substring(2, 3);
		
		ServerType serverType = this.getServerType(hostname);
		switch (serverType) {
		case Admin:
			int adminClusterId = Integer.parseInt(hostCount);
			adminClusterId -= 1;
			clusterId = Integer.toString(adminClusterId);
			break;
		case Application:
			int appClusterId = Integer.parseInt(hostCount);
			appClusterId -= 1;
			clusterId = "1" + Integer.toString(appClusterId);
			break;
		default:
			break;
		}
		
		return clusterId;
	}
	
	private HashMap<String, Template> getHostTemplates(Server server){
		HashMap<String, Template> hostTemplates = new HashMap<String, Template>();
		
		int serverCount = server.getSeverCount();
		ServerType serverType = server.getServerType();
		Template serverTemplate = server.getTemplate(this.provider, this.environment_type); 
		
		if(serverCount <= 0 || serverCount > 9){
			System.out.println("Please enter no. of " + serverType + " servers greater than 0 and less than 9");
			return hostTemplates;
		}
		
		for(int i=1; i<=serverCount; i++){
			hostTemplates.put(project_code + "-" + environment_type.getCode() + "-" + provider.getCode() + "-" 
							+ serverType.getCode() + "-00" + i + SERVER_DOMAIN
							, serverTemplate);
		}
		
		return hostTemplates;
	}
	
	public HashMap<ServerType, ArrayList<ServerInstance>> create(ComputeService computeService, Server[] servers, Properties configurationProps){
		
		HashMap<ServerType, ArrayList<ServerInstance>> environmentMap = new HashMap<ServerType, ArrayList<ServerInstance>>();
		
		if(servers.length == 0){
			System.out.println("Server list is empty!");
			return environmentMap;
		}
		System.out.println(">> Creating server templates ..");
		Server adminServers = null; ArrayList<ServerInstance> adminServerInstances = new ArrayList<ServerInstance>();
		Server appServers = null; ArrayList<ServerInstance> appServerInstances = new ArrayList<ServerInstance>();
		Server webServers = null; ArrayList<ServerInstance> webServerInstances = new ArrayList<ServerInstance>();
		Server searchServers = null; ArrayList<ServerInstance> searchServerInstances = new ArrayList<ServerInstance>();
		Server dbServers = null; ArrayList<ServerInstance> dbServerInstances = new ArrayList<ServerInstance>();
		
		HashMap<String, Template> adminHostTemplates = new HashMap<String, Template>(); 
		HashMap<String, Template> appHostTemplates = new HashMap<String, Template>(); 
		HashMap<String, Template> webHostTemplates = new HashMap<String, Template>(); 
		HashMap<String, Template> searchHostTemplates = new HashMap<String, Template>(); 
		HashMap<String, Template> dbHostTemplates = new HashMap<String, Template>(); 
		HashMap<String, Template> allHostTemplates = new HashMap<String, Template>();
		
		try {
			
			for(Server server:servers){
				
				if(server.getSeverCount()<0 || server.getSeverCount()>9){
					System.out.println("Please enter valid count for number of servers.");
					return environmentMap;
				}
				
				switch (server.getServerType()) {
					case Admin:
						adminHostTemplates = this.getHostTemplates(server);
						allHostTemplates.putAll(adminHostTemplates);
						adminServers = server;
						break;
					case Application:
						appHostTemplates = this.getHostTemplates(server);
						allHostTemplates.putAll(appHostTemplates);
						appServers = server;
						break;
					case Web:
						webHostTemplates = this.getHostTemplates(server);
						allHostTemplates.putAll(webHostTemplates);
						webServers = server;
						break;
					case Search:
						searchHostTemplates = this.getHostTemplates(server);
						allHostTemplates.putAll(searchHostTemplates);
						searchServers = server;
						break;
					case Database:
						dbHostTemplates = this.getHostTemplates(server);
						allHostTemplates.putAll(dbHostTemplates);
						dbServers = server;
						break;
					default:
						break;
				}
				
			}
			System.out.println("<< Server templates are created for hosts " + allHostTemplates.keySet());
			
			if(adminHostTemplates.isEmpty()){
				System.out.println(ServerType.Admin + " servers are not present in "  + this.project_code + "-" + this.environment_type.getCode());
			}else{
				
				for(String adminHost:adminHostTemplates.keySet()){

					ServerInstance adminServerInstance = adminServers.create(adminHostTemplates.get(adminHost), adminHost);
					adminServerInstance.provisionJava(configurationProps);
					adminServerInstances.add(adminServerInstance);
					String adminClusterId = this.getClusterId(adminHost);
					configurationProps.setProperty(ConfigurationKeys.cluster_id.name(), adminClusterId);
					adminServerInstance.provisionHybris(configurationProps);
					
				}
				environmentMap.put(ServerType.Admin, adminServerInstances);
			}
			
			if(appHostTemplates.isEmpty()){
				System.out.println(ServerType.Application + " servers are not present in "  + this.project_code + "-" + this.environment_type.getCode());
			}else{
				
				for(String appHost:appHostTemplates.keySet()){

					ServerInstance appServerInstance = appServers.create(appHostTemplates.get(appHost), appHost);
					appServerInstance.provisionJava(configurationProps);
					appServerInstances.add(appServerInstance);
					String appClusterId = this.getClusterId(appHost);
					configurationProps.setProperty(ConfigurationKeys.cluster_id.name(), appClusterId);
					appServerInstance.provisionHybris(configurationProps);
					
				}
				environmentMap.put(ServerType.Application, appServerInstances);
			}
			
			if(webHostTemplates.isEmpty()){
				System.out.println(ServerType.Web + " servers are not present in "  + this.project_code + "-" + this.environment_type.getCode());
			}else{
				String domainName = configurationProps.getProperty(ConfigurationKeys.domain_name.name());
				for(String webHost:webHostTemplates.keySet()){
					ServerInstance webServerInstance = webServers.create(webHostTemplates.get(webHost), webHost);
					webServerInstances.add(webServerInstance);
				}
				environmentMap.put(ServerType.Web, webServerInstances);
				System.out.println(webHostTemplates.keySet());
			}
			
			if(searchHostTemplates.isEmpty()){
				System.out.println(ServerType.Search + " servers are not present in "  + this.project_code + "-" + this.environment_type.getCode());
			}else{
				
				for(String searchHost:searchHostTemplates.keySet()){
					ServerInstance searchServerInstance = searchServers.create(searchHostTemplates.get(searchHost), searchHost);
					System.out.println();
					searchServerInstance.provisionJava(configurationProps);
					searchServerInstances.add(searchServerInstance);
					searchServerInstance.provisionSolr(configurationProps);
				}
				environmentMap.put(ServerType.Search, searchServerInstances);
				System.out.println(searchHostTemplates.keySet());
			}
			
			if(dbHostTemplates.isEmpty()){
				System.out.println(ServerType.Database + " servers are not present in "  + this.project_code + "-" + this.environment_type.getCode());
			}else{
				
				for(String dbHost:dbHostTemplates.keySet()){
					ServerInstance dbServerInstance = dbServers.create(dbHostTemplates.get(dbHost), dbHost);
					dbServerInstances.add(dbServerInstance);
				}
				environmentMap.put(ServerType.Database, dbServerInstances);
				System.out.println(dbHostTemplates.keySet());
			}
			
			ArrayList<ServerInstance> hybrisServerInstances = new ArrayList<ServerInstance>();
            hybrisServerInstances.addAll(adminServerInstances);
            hybrisServerInstances.addAll(appServerInstances);
            if(hybrisServerInstances.isEmpty() || searchServerInstances.isEmpty()){
            	System.out.println("Since there are no " + ServerType.Admin + ", " + ServerType.Application + ", "+ ServerType.Search + 
            						" servers in " + this.project_code + "-" + this.environment_type.getCode());
            	System.out.println("Integration of Solr on Hybris can not be performed");
            }else{
            	String srchHost = searchServerInstances.get(0).getHostname();
            	String srchIP = searchServerInstances.get(0).getNode().getPublicAddresses().iterator().next();
            	for(ServerInstance hybrisServerInstance:hybrisServerInstances){
                	hybrisServerInstance.integrateSolrOnHybris(configurationProps, srchHost, srchIP);
                }
            }
            
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return environmentMap;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args){
		
		long timeStart = System.currentTimeMillis();
		try{
			
			Provider provider = Provider.AmazonWebService;
			ComputeService computeService = provider.getComputeService();
			Server[] servers = {/*new Server(computeService, ServerType.Admin, 1),*/
								new Server(computeService, ServerType.Application, 1),
								/*new Server(computeService, ServerType.Web, 1),*/
								new Server(computeService, ServerType.Search, 1),
								/*new Server(computeService, ServerType.Database, 1)*/};
			String projectCode="tryb2c";
			Environment environment = new Environment(provider, projectCode, EnvironmentType.Development);
			Properties configurationProps = environment.getConfigurationProps(HybrisVersion.Hybris6_2_0, 
																			  HybrisRecipe.B2C_Accelerator, 
																			  JavaVersion.Java8u131, 
																			  "www." + projectCode + "b2cdemo.com");
			environment.create(computeService, servers, configurationProps);
			computeService.getContext().close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		long timeEnd = System.currentTimeMillis();
		long duration = timeEnd - timeStart;
		long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
		System.out.println("Time utilised for execution: " + minutes + " minutes");
	}
	
}