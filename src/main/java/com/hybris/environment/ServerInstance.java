package com.hybris.environment;

import java.io.File;
import java.io.IOException;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.scriptbuilder.domain.Statements;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class ServerInstance {

	//private NodeMetadata node;
	private String nodeId;
	private ComputeService computeService;
	private String hostname;
	public static final String DOMAIN=".hybrishosting.com";
/*	private static final String REPO_SERVER="54.84.166.214";
	private static final String SCRIPTS_DIR="/opt/scripts/";
	private static final String PROVISION_JAVA_SCRIPT="http://" + REPO_SERVER + "/scripts/provision_java.sh";
	private static final String PROVISION_HYBRIS_SCRIPT="http://" + REPO_SERVER + "/scripts/provision_hybris.sh";
	private static final String INITIALIZE_DB_SCRIPT="http://" + REPO_SERVER + "/scripts/initialize_db.sh";
	private static final String PROVISION_SOLR_SCRIPT="http://" + REPO_SERVER + "/scripts/provision_solr.sh";
	private static final String INTEGRATE_SOLR_ON_HYBRIS_SCRIPT="http://" + REPO_SERVER + "/scripts/integrate_srch_on_hybris.sh";
	private static final String PROVISION_WEB_SCRIPT="http://" + REPO_SERVER + "/scripts/provision_web.sh";
	private static final String PROVISION_MYSQL_SCRIPT="http://" + REPO_SERVER + "/scripts/provision_mysql.sh";
	private static final String SETUP_NFS_SERVER_SCRIPT="http://" + REPO_SERVER + "/scripts/setup_nfs_server.sh";*/
	
	public ServerInstance(ComputeService computeService, String nodeId, String hostname) {
		// TODO Auto-generated constructor stub
		this.setComputeservice(computeService);
		this.setNodeId(nodeId);
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
			ExecResponse responses = this.computeService.runScriptOnNode(this.getNodeId(), Statements.exec(command), 
						TemplateOptions.Builder.runScript(command).overrideLoginCredentials(login).runAsRoot(true).wrapInInitScript(false));
			System.out.println(responses.getOutput());
	}
	
	public void executeScript(String pathToScript){
		File script = new File(pathToScript);
	    LoginCredentials login = this.getLoginForProvision();
	    try {
			ExecResponse responses = this.computeService.runScriptOnNode(this.getNodeId(), Files.toString(script, Charsets.UTF_8), 
										TemplateOptions.Builder.runScript(Files.toString(script, Charsets.UTF_8))
										.overrideLoginCredentials(login)
										.wrapInInitScript(false));
			System.out.println(responses.getOutput());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
/*	public void setupNfsServer(Properties configurationProps){
		System.out.println();
		System.out.println(">> Setting NFS server on " + this.hostname);
		this.executeCommand("wget " + SETUP_NFS_SERVER_SCRIPT + " -P " + SCRIPTS_DIR);
		this.executeCommand("chmod -R 775 " + SCRIPTS_DIR + "; chown -R root:root " + SCRIPTS_DIR);
		String appHostIp = configurationProps.getProperty(ConfigurationKeys.app_host_ip.name());
		this.executeCommand(SCRIPTS_DIR + "setup_nfs_server.sh " + appHostIp);
		System.out.println("<< NFS server is set on " + this.hostname);
		System.out.println();
	}
	
	public void setupNfsClient(Properties configurationProps){
		System.out.println();
		System.out.println(">> Setting NFS Client on " + this.hostname);
		String adminHostIp = configurationProps.getProperty(ConfigurationKeys.adm_host_ip.name());
		this.executeCommand("apt-get install -y nfs-common; apt-get update");
		this.executeCommand("mkdir -p /mnt/nfs/var/nfs");
		this.executeCommand("mount " + adminHostIp + ":/var/nfs /mnt/nfs/var/nfs/; df -h");
		System.out.println("<< NFS Client is set on " + this.hostname);
		System.out.println();
	}
	
	public void provisionMySql(Properties configurationProps){
		
		System.out.println();
		System.out.println(">> Provisioning MySql on " + this.hostname);
		this.executeCommand("mkdir "+ SCRIPTS_DIR +"; wget " + PROVISION_MYSQL_SCRIPT + " -P " + SCRIPTS_DIR);
		this.executeCommand("chmod -R 775 " + SCRIPTS_DIR + "; chown -R root:root " + SCRIPTS_DIR);
		this.executeCommand(SCRIPTS_DIR + "provision_mysql.sh");
		this.executeCommand("rm -r " + SCRIPTS_DIR);
		System.out.println("<< Provision of MySql completed on " + this.hostname);
		System.out.println();
	}
	
	public void integrateSolrOnHybris(Properties configurationProps){
		System.out.println();
		System.out.println(">> Integrating solr on " + this.hostname);
		this.executeCommand("wget " + INTEGRATE_SOLR_ON_HYBRIS_SCRIPT + " -P " + SCRIPTS_DIR);
		this.executeCommand("chmod -R 775 " + SCRIPTS_DIR + "; chown -R root:root " + SCRIPTS_DIR);
		String srchHost = configurationProps.getProperty(ConfigurationKeys.srch_host_name.name());
		String srchIP = configurationProps.getProperty(ConfigurationKeys.srch_host_ip.name());
		String defaultShop = configurationProps.getProperty(ConfigurationKeys.default_shop.name());
		this.executeCommand(SCRIPTS_DIR +"integrate_srch_on_hybris.sh " + srchHost + " " + srchIP + " " + defaultShop);
		this.executeCommand("rm -r " + SCRIPTS_DIR);
		System.out.println("<< Integration of solr completed on " + this.hostname);
		System.out.println();
	}
	
	public void provisionWeb(Properties configurationProps, String appHost, String appIp){
		System.out.println();
		System.out.println(">> Provisioning web on " + this.hostname);
		this.executeCommand("mkdir "+ SCRIPTS_DIR +"; wget " + PROVISION_WEB_SCRIPT + " -P " + SCRIPTS_DIR);
		this.executeCommand("chmod -R 775 " + SCRIPTS_DIR + "; chown -R root:root " + SCRIPTS_DIR);
		String domainName = configurationProps.getProperty(ConfigurationKeys.domain_name.name());
		this.executeCommand(SCRIPTS_DIR + "provision_web.sh " + appHost + " " + appIp + " " + domainName);
		this.executeCommand("rm -r " + SCRIPTS_DIR);
		System.out.println("<< Provision of web completed on " + this.hostname);
		System.out.println();
	}
	
	public void provisionSolr(Properties configurationProps){
		System.out.println();
		System.out.println(">> Provisioning solr on " + this.hostname);
		this.executeCommand("wget " + PROVISION_SOLR_SCRIPT + " -P " + SCRIPTS_DIR);
		this.executeCommand("chmod -R 775 " + SCRIPTS_DIR + "; chown -R root:root " + SCRIPTS_DIR);
		String solrPackage = configurationProps.getProperty(ConfigurationKeys.solr_package.name());
		this.executeCommand(SCRIPTS_DIR +"provision_solr.sh " + solrPackage);
		this.executeCommand("rm -r " + SCRIPTS_DIR);
		System.out.println("<< Provision of solr completed on " + this.hostname);
		System.out.println();
	}
	
	public void provisionJava(Properties configurationProps){
		System.out.println();
		System.out.println(">> Downloading scripts on " + this.hostname);
		this.executeCommand("mkdir "+ SCRIPTS_DIR +"; wget " + PROVISION_JAVA_SCRIPT + " -P " + SCRIPTS_DIR);
		this.executeCommand("chmod -R 775 " + SCRIPTS_DIR + "; chown -R root:root " + SCRIPTS_DIR);
		String javaVersion = configurationProps.getProperty(ConfigurationKeys.java_version.name());
		System.out.println(">> Provisioning java on " + this.hostname);
		String javaPackage = configurationProps.getProperty(ConfigurationKeys.java_package.name());
		this.executeCommand("source " + SCRIPTS_DIR +"provision_java.sh " + javaPackage + " " + javaVersion);
		System.out.println("<< Provision of java completed on " + this.hostname);
		System.out.println();
	}
	
	public void provisionHybris(Properties configurationProps) {
		System.out.println();
		System.out.println(">> Provisioning hybris on " + this.hostname);
		this.executeCommand("wget " + PROVISION_HYBRIS_SCRIPT + " -P " + SCRIPTS_DIR);
		this.executeCommand("chmod -R 775 " + SCRIPTS_DIR + "; chown -R root:root " + SCRIPTS_DIR);
		String hybrisVersion = configurationProps.getProperty(ConfigurationKeys.hybris_version.name());
		String hybrisPackage = configurationProps.getProperty(ConfigurationKeys.hybris_package.name());
		String acceleratorType = configurationProps.getProperty(ConfigurationKeys.hybris_recipe.name());
		String clusterId = configurationProps.getProperty(ConfigurationKeys.cluster_id.name());
		String dbHostName = configurationProps.getProperty(ConfigurationKeys.db_host_name.name());
		String dbHostIp = configurationProps.getProperty(ConfigurationKeys.db_host_ip.name());
		System.out.println(">> Hybris Version " + hybrisVersion + " is selected.");
		System.out.println(">> Hybris Recipe " + acceleratorType + " is selected");
		
		this.executeCommand("source " + SCRIPTS_DIR + "provision_hybris.sh " + hybrisVersion + " " + hybrisPackage
																		   + " " + acceleratorType + " " + clusterId
																		   + " " + dbHostName + " " + dbHostIp);
		configurationProps.remove(ConfigurationKeys.cluster_id.name());
		System.out.println("<< Provision of hybris completed on " + this.hostname);
		System.out.println();
	}
	
	public void initializeDB(Properties configurationProps){
		System.out.println();
		System.out.println(">> Initializing DB on " + this.hostname);
		this.executeCommand("wget " + INITIALIZE_DB_SCRIPT + " -P " + SCRIPTS_DIR);
		this.executeCommand("chmod -R 775 " + SCRIPTS_DIR + "; chown -R root:root " + SCRIPTS_DIR);
		String hybrisVersion = configurationProps.getProperty(ConfigurationKeys.hybris_version.name());
		this.executeCommand("source " + SCRIPTS_DIR + "initialize_db.sh " + hybrisVersion);
		System.out.println("<< Initialization of DB completed on " + this.hostname);
		System.out.println();
	}*/
	
	public ComputeService getComputeservice() {
		return computeService;
	}

	public void setComputeservice(ComputeService computeservice) {
		this.computeService = computeservice;
	}

	

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	
}
