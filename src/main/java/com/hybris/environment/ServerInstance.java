package com.hybris.environment;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.scriptbuilder.domain.Statements;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.hybris.ConfigurationKeys;

public class ServerInstance {

	private NodeMetadata node;
	private ComputeService computeService;
	private String hostname;
	public static final String SERVER_DOMAIN=".hybrishosting.com";
	private static final String REPO_SERVER="54.210.0.102";
	private static final String SCRIPTS_DIR="/opt/scripts/";
	private static final String PROVISION_JAVA_SCRIPT="http://" + REPO_SERVER + "/scripts/provision_java.sh";
	private static final String PROVISION_HYBRIS_SCRIPT="http://"+ REPO_SERVER +"/scripts/provision_hybris.sh";
	private static final String PROVISION_SOLR_SCRIPT="http://" + REPO_SERVER + "/scripts/provision_solr.sh";
	
	public ServerInstance(ComputeService computeService, NodeMetadata node, String hostname) {
		// TODO Auto-generated constructor stub
		this.setComputeservice(computeService);
		this.setNode(node);
		this.setHostname(hostname);
	}
	
	private LoginCredentials getLoginForProvision(){
		
		try{
			
			String user = "ubuntu";
			//String privateKey = Files.toString(new File("C:\\cygwin64\\home\\D066624\\.ssh\\id_rsa"), Charsets.UTF_8);
			String privateKey = Files.toString(new 
					File("C:\\Users\\D066624\\Google Drive\\Rough\\Eclipse\\ProvisionHybris\\src\\main\\resources\\id_rsa"), Charsets.UTF_8);
			return LoginCredentials.builder().user(user).privateKey(privateKey).build();
		
		} catch (Exception e) {
			System.err.println("Error Reading Private Key: " + e.getMessage());
			System.exit(1);
		}
		
		return null;
		
	}
	
	public void executeCommand(String command){
	
			LoginCredentials login = this.getLoginForProvision();
			ExecResponse responses = this.computeService.runScriptOnNode(this.node.getId(), Statements.exec(command), 
						TemplateOptions.Builder.runScript(command).overrideLoginCredentials(login).runAsRoot(true));
			System.out.println(responses.getOutput());
	}
	
	public void executeScript(String pathToScript){
		File script = new File(pathToScript);
	    LoginCredentials login = this.getLoginForProvision();
	    try {
			ExecResponse responses = this.computeService.runScriptOnNode(this.node.getId(), Files.toString(script, Charsets.UTF_8), 
										TemplateOptions.Builder.runScript(Files.toString(script, Charsets.UTF_8)).overrideLoginCredentials(login));
	    
			System.out.println(responses.getOutput());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void provisionSolr(Properties configurationProps, String hostname){
		System.out.println();
		System.out.println(">> Provisioning solr on " + hostname);
		this.executeCommand("wget " + PROVISION_SOLR_SCRIPT + " -P " + SCRIPTS_DIR);
		this.executeCommand("chmod -R 775 " + SCRIPTS_DIR + "; chown -R root:root " + SCRIPTS_DIR);
		String solrPackage = configurationProps.getProperty(ConfigurationKeys.solr_package.name());
		this.executeCommand(SCRIPTS_DIR +"provision_solr.sh " + solrPackage);
		//this.executeCommand("rm -r " + SCRIPTS_DIR);
		System.out.println("<< Provisioning of solr completed on " + hostname);
		System.out.println();
	}
	
	public void provisionJava(Properties configurationProps, String hostname){
		System.out.println();
		System.out.println(">> Downloading scripts on " + hostname);
		this.executeCommand("mkdir "+ SCRIPTS_DIR +"; wget " + PROVISION_JAVA_SCRIPT + " -P " + SCRIPTS_DIR);
		this.executeCommand("chmod -R 775 " + SCRIPTS_DIR + "; chown -R root:root " + SCRIPTS_DIR);
		String javaVersion = configurationProps.getProperty(ConfigurationKeys.java_version.name());
		System.out.println(">> Provisioning java on " + hostname);
		String javaPackage = configurationProps.getProperty(ConfigurationKeys.java_package.name());
		this.executeCommand("source " + SCRIPTS_DIR +"provision_java.sh " + javaPackage + " " + javaVersion);
		System.out.println("<< Provisioning of java completed on " + hostname);
		System.out.println();
	}
	
	public void provisionHybris(Properties configurationProps, String hostname) {
		System.out.println();
		System.out.println(">> Provisioning hybris on " + hostname);
		this.executeCommand("wget " + PROVISION_HYBRIS_SCRIPT + " -P " + SCRIPTS_DIR);
		this.executeCommand("chmod -R 775 " + SCRIPTS_DIR + "; chown -R root:root " + SCRIPTS_DIR);
		String hybrisVersion = configurationProps.getProperty(ConfigurationKeys.hybris_version.name());
		String hybrisPackage = configurationProps.getProperty(ConfigurationKeys.hybris_package.name());
		String acceleratorType = configurationProps.getProperty(ConfigurationKeys.hybris_recipe.name());
		String clusterId = configurationProps.getProperty(ConfigurationKeys.cluster_id.name());
		
		System.out.println(">> Hybris Version " + hybrisVersion + " is selected.");
		System.out.println(">> Hybris Recipe " + acceleratorType + " is selected");
		
		this.executeCommand("source " + SCRIPTS_DIR + "provision_hybris.sh " + hybrisVersion + " " + hybrisPackage
																		   + " " + acceleratorType + " " + clusterId);
		//this.executeCommand("rm -r " + SCRIPTS_DIR);
		configurationProps.remove(ConfigurationKeys.cluster_id.name());
		System.out.println("<< Provisioning of hybris completed on " + hostname);
		System.out.println();
	}
	
	public ComputeService getComputeservice() {
		return computeService;
	}

	public void setComputeservice(ComputeService computeservice) {
		this.computeService = computeservice;
	}

	public NodeMetadata getNode() {
		return node;
	}

	public void setNode(NodeMetadata node) {
		this.node = node;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
}
