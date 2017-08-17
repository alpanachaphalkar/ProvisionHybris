package com.hybris.environment;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;

import com.hybris.HybrisRecipe;
import com.hybris.HybrisVersion;
import com.hybris.JavaVersion;
import com.hybris.provider.Provider;


public class Environment {
	
	public String project_code;
	public EnvironmentType environment_type;
	public Provider provider;
	public static final String SERVER_DOMAIN=".hybrishosting.com";
	private static final String REPO_SERVER="54.210.0.102";
	private static final String SCRIPTS_DIR="/opt/scripts/";
	//private static final String JAVA_ENV_FILE="/etc/profile.d/java.sh";
	//private static final String HYBRIS_ENV_FILE="/etc/profile.d/hybris.sh";
	private static final String PROVISION_JAVA_SCRIPT="http://" + REPO_SERVER + "/scripts/provision_java.sh";
	private static final String PROVISION_HYBRIS_SCRIPT="http://"+ REPO_SERVER +"/scripts/provision_hybris.sh";
	private static final String SET_HYBRIS_ENV_SCRIPT="http://" + REPO_SERVER + "/scripts/set_hybris_env.sh";
	private static final String SET_JAVA_ENV_SCRIPT="http://"+ REPO_SERVER +"/scripts/set_java_env.sh";
	
	public Environment(Provider provider, String projectCode, EnvironmentType environmentType) {
		// TODO Auto-generated constructor stub
		this.project_code = projectCode;
		this.environment_type = environmentType;
		this.provider = provider;
	}
	
	public Properties getConfigurationProps(HybrisVersion hybrisVersion, HybrisRecipe hybrisRecipe, JavaVersion javaVersion, String domainName){
		Properties configurationProps = new Properties();
		configurationProps.setProperty("hybris.version", hybrisVersion.getHybrisVersion());
		configurationProps.setProperty("hybris.package", hybrisVersion.getHybrisPackage());
		configurationProps.setProperty("hybris.recipe", hybrisRecipe.getRecipeId());
		configurationProps.setProperty("java.version", javaVersion.getJavaVersion());
		configurationProps.setProperty("java.package", javaVersion.getPackageName());
		configurationProps.setProperty("domain.name", domainName);
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
	
	public void provision(ComputeService computeService, HashMap<String, NodeMetadata> environmentMap,
							JavaVersion javaVersion, HybrisVersion hybrisVersion, HybrisRecipe hybrisRecipe){
		for(Map.Entry<String, NodeMetadata> environmentEntry:environmentMap.entrySet()){
			String hostname = environmentEntry.getKey();
			NodeMetadata node = environmentEntry.getValue();
			ServerInstance serverIntance = new ServerInstance(computeService, node);
			ServerType serverType = this.getServerType(hostname);
			String javaPackage = javaVersion.getPackageName();
			String javaVersionFolder = javaVersion.getJavaVersion();
			String hybris_version = hybrisVersion.getHybrisVersion();
			String hybris_package = hybrisVersion.getHybrisPackage();
			String acceleratorType = hybrisRecipe.getRecipeId();
			
			switch (serverType) {
				case Admin:
					System.out.println(">> Downloading scripts..");
					serverIntance.executeCommand("mkdir "+ SCRIPTS_DIR +"; wget " + PROVISION_JAVA_SCRIPT + " -P " + SCRIPTS_DIR);
					serverIntance.executeCommand("wget " + PROVISION_HYBRIS_SCRIPT + " -P " + SCRIPTS_DIR);
					serverIntance.executeCommand("wget " + SET_JAVA_ENV_SCRIPT + " -P " + SCRIPTS_DIR);
					serverIntance.executeCommand("wget " + SET_HYBRIS_ENV_SCRIPT + " -P " + SCRIPTS_DIR);
					serverIntance.executeCommand("chmod -R 775 " + SCRIPTS_DIR + "; chown -R root:root " + SCRIPTS_DIR);
					System.out.println(">> Setting Hybris Environment on " + hostname);
					serverIntance.executeCommand("sudo source " + SCRIPTS_DIR + "set_java_env.sh " + javaVersionFolder);
					serverIntance.executeCommand("sudo source " + SCRIPTS_DIR + "set_hybris_env.sh " + hybris_version);
					/*serverIntance.executeCommand("source " + JAVA_ENV_FILE + "; source " + HYBRIS_ENV_FILE);*/
					System.out.println("<< Setting of Hybris environment completed on " + hostname);
					System.out.println();
					System.out.println(">> Provisioning java on " + hostname);
					serverIntance.executeCommand("sudo " + SCRIPTS_DIR +"provision_java.sh " + javaPackage + " " + javaVersionFolder);
					System.out.println("<< Provisioning of java completed on " + hostname);
					System.out.println();
					System.out.println(">> Provisioning hybris on " + hostname);
					String adminClusterId = this.getClusterId(hostname);
					serverIntance.executeCommand("sudo " + SCRIPTS_DIR + "provision_hybris.sh " + hybris_version + " " + hybris_package
																								+ " " + acceleratorType + " "
																								+ adminClusterId);
					serverIntance.executeCommand("rm -r " + SCRIPTS_DIR);
					System.out.println("<< Provisioning of hybris completed on " + hostname);
					break;
				case Application:
					System.out.println(">> Provisioning java on " + hostname);
					serverIntance.executeCommand("mkdir "+ SCRIPTS_DIR +"; wget " + PROVISION_JAVA_SCRIPT + " -P " + SCRIPTS_DIR);
					serverIntance.executeCommand("wget " + PROVISION_HYBRIS_SCRIPT + " -P " + SCRIPTS_DIR);
					serverIntance.executeCommand("chmod -R 775 " + SCRIPTS_DIR + "; chown -R root:root " + SCRIPTS_DIR);
					serverIntance.executeCommand("sudo " + SCRIPTS_DIR +"provision_java.sh " + javaPackage + " " + javaVersionFolder);
					System.out.println("<< Provisioning of java completed on " + hostname);
					System.out.println();
					System.out.println(">> Provisioning hybris on " + hostname);
					String appClusterId = this.getClusterId(hostname);
					serverIntance.executeCommand("sudo " + SCRIPTS_DIR + "provision_hybris.sh " + hybris_version + " " + hybris_package
																								+ " " + acceleratorType + " "
																								+ appClusterId);
					serverIntance.executeCommand("rm -r " + SCRIPTS_DIR);
					System.out.println("<< Provisioning of hybris completed on " + hostname);
					break;
				case Web:
					System.out.println(">> Provisioning apache2 on " + hostname);
					System.out.println("<< Provisioning of apache2 completed on " + hostname);
					break;
				case Search:
					System.out.println(">> Provisioning java on " + hostname);
					serverIntance.executeCommand("mkdir "+ SCRIPTS_DIR +"; wget " + PROVISION_JAVA_SCRIPT + " -P " + SCRIPTS_DIR);
					serverIntance.executeCommand("chmod -R 775 " + SCRIPTS_DIR + "; chown -R root:root " + SCRIPTS_DIR);
					serverIntance.executeCommand("sudo " + SCRIPTS_DIR +"provision_java.sh " + javaPackage + " " + javaVersionFolder);
					System.out.println("<< Provisioning of java completed on " + hostname);
					break;
				case Database:
					
					break;
				default:
					break;
			}
			
		}
	}
	
	public HashMap<String, NodeMetadata> create(ComputeService computeService, Server[] servers, Properties configurationProps){
		
		HashMap<String, NodeMetadata> environmentMap = new HashMap<String, NodeMetadata>();
		
		if(servers.length == 0){
			System.out.println("Server list is empty!");
			return environmentMap;
		}
		
		try {
			
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
			Server[] servers = {new Server(computeService, ServerType.Admin, 1),
								/*new Server(computeService, ServerType.Application, 1),
								new Server(computeService, ServerType.Web, 1),
								new Server(computeService, ServerType.Search, 1),
								new Server(computeService, ServerType.Database, 1)*/};
			String projectCode="try5";
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