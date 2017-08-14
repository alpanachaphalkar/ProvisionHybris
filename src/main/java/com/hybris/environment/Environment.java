package com.hybris.environment;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;

import com.google.common.collect.Iterables;
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
	private static final String PROVISION_JAVA_SCRIPT="http://" + REPO_SERVER + "/scripts/provision_java.sh";
	
	public Environment(Provider provider, String projectCode, EnvironmentType environmentType) {
		// TODO Auto-generated constructor stub
		this.project_code = projectCode;
		this.environment_type = environmentType;
		this.provider = provider;
	}
	
	private ServerType getServerType(String hostname){
		
		String serverTypeCode = hostname.split("-")[3];
		ServerType serverType = ServerType.Admin;
		serverType = serverType.getServerType(serverTypeCode);
		return serverType;
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
			String javaVersionFolder = javaVersion.getFolderName();
			
			switch (serverType) {
				case Admin:
					System.out.println(">> Provisioning java on " + hostname);
					serverIntance.executeCommand("mkdir "+ SCRIPTS_DIR +"; wget " + PROVISION_JAVA_SCRIPT + " -P " + SCRIPTS_DIR);
					serverIntance.executeCommand("chmod -R 775 " + SCRIPTS_DIR + "; chown -R root:root " + SCRIPTS_DIR);
					serverIntance.executeCommand("sudo " + SCRIPTS_DIR +"provision_java.sh " + javaPackage + " " + javaVersionFolder);
					System.out.println("<< Provisioning of java completed on " + hostname);
					break;
				case Application:
					System.out.println(">> Provisioning java on " + hostname);
					serverIntance.executeCommand("mkdir "+ SCRIPTS_DIR +"; wget " + PROVISION_JAVA_SCRIPT + " -P " + SCRIPTS_DIR);
					serverIntance.executeCommand("chmod -R 775 " + SCRIPTS_DIR + "; chown -R root:root " + SCRIPTS_DIR);
					serverIntance.executeCommand("sudo " + SCRIPTS_DIR +"provision_java.sh " + javaPackage + " " + javaVersionFolder);
					System.out.println("<< Provisioning of java completed on " + hostname);
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
	
	public HashMap<String, NodeMetadata> create(ComputeService computeService, Server[] servers){
		
		HashMap<String, NodeMetadata> environmentMap = new HashMap<String, NodeMetadata>();
		
		if(servers.length == 0){
			System.out.println("Server list is empty!");
			return environmentMap;
		}
		
		try {
			System.out.println(">> Creating templates and hostnames");
			HashMap<String, Template> hostTemplates = new HashMap<String, Template>();
			for(Server server:servers){
				hostTemplates.putAll(this.getHostTemplates(server));
			}
			System.out.println(hostTemplates.keySet());
			
			if(hostTemplates.isEmpty()){
				System.out.println("Hostnames are not generated for " + this.project_code + "-" + this.environment_type.getCode());
				return environmentMap;
			}
			System.out.println(">> Creating instances");
			for(Map.Entry<String, Template> hostTemplate:hostTemplates.entrySet()){
				String host = hostTemplate.getKey().replace(SERVER_DOMAIN, "");
				NodeMetadata node = Iterables.getOnlyElement(computeService.createNodesInGroup(host, 1, hostTemplate.getValue()));
				System.out.println("<<	Server " + hostTemplate.getKey() + " is created with following details: ");
				System.out.println("	Name: " + node.getHostname());
				System.out.println("	ID: " + node.getId());
				System.out.println("	Private IP: " + node.getPrivateAddresses());
				System.out.println("	Public IP: " + node.getPublicAddresses());
				environmentMap.put(hostTemplate.getKey(), node);
			}
			System.out.println(environmentMap.keySet());
			
			if(environmentMap.isEmpty()){
				System.out.println("Environment is not created for " + this.project_code + "-" + this.environment_type.getCode());
				return environmentMap;
			}
			System.out.println(">> Setting hostnames to instances");
			for(Map.Entry<String, NodeMetadata> environmentEntry:environmentMap.entrySet()){
				String host = environmentEntry.getKey();
				NodeMetadata node = environmentEntry.getValue();
				ServerInstance serverInstance = new ServerInstance(computeService, node);
				serverInstance.executeCommand("hostnamectl set-hostname " + host);
				serverInstance.executeCommand("echo \"127.0.0.1 `hostname`\" >>/etc/hosts");
				System.out.println("<< Host " + host + " is set");
			}
			System.out.println(">> Provisioning on " + this.project_code + "-" + this.environment_type.getCode() + " begins");
			this.provision(computeService, environmentMap, JavaVersion.Java8u131, HybrisVersion.Hybris6_2_0, HybrisRecipe.B2C_Accelerator);
			
			
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
								new Server(computeService, ServerType.Application, 1),
								new Server(computeService, ServerType.Web, 1),
								new Server(computeService, ServerType.Search, 1),
								/*new Server(computeService, ServerType.Database, 1)*/};
			Environment environment = new Environment(provider, "try6", EnvironmentType.Development);
			environment.create(computeService, servers);
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