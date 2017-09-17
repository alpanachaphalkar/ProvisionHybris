package com.hybris.ansible;

import java.io.File;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.scriptbuilder.domain.Statements;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.hybris.environment.EnvironmentType;
import com.hybris.provider.Provider;

public class Ansible {
	
	private static final String INSTANCE_NAME="demo-ansible-server";
	private static final String HOSTNAME="yms-demo-aws-ansible-001.hybrishosting.com";
	private static final String PUBLIC_IP="54.84.166.214";
	private static final String PRIVATE_IP="10.10.1.21";
	private static final Provider PROVIDER = Provider.AmazonWebService;
	private static final String NODE_ID="us-east-1/i-0cec1dee114ec98fe";
	private ComputeService computeService;
	
	public static final String DEFAULT_ANSIBLE_DIR="/opt/ansible/";
	public static final String DEFAULT_INVENTORY_DIR = DEFAULT_ANSIBLE_DIR + "inventory/";
	public static final String DEFAULT_PLABOOKS_DIR = DEFAULT_ANSIBLE_DIR + "playbooks/";
	public static final String DEFAULT_ROLES_DIR = DEFAULT_PLABOOKS_DIR + "roles/";
	
	public Ansible() {
		// TODO Auto-generated constructor stub
		this.setComputeService(computeService);
	}

	public static String getInstanceName() {
		return INSTANCE_NAME;
	}

	public static String getHostname() {
		return HOSTNAME;
	}

	public static String getPublicIp() {
		return PUBLIC_IP;
	}

	public static String getPrivateIp() {
		return PRIVATE_IP;
	}

	public static Provider getProvider() {
		return PROVIDER;
	}

	public static String getNodeId() {
		return NODE_ID;
	}

	public ComputeService getComputeService() {
		return computeService;
	}

	public void setComputeService(ComputeService computeService) {
		try {
			this.computeService = getProvider().getComputeService();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void executeCommand(String command){
		LoginCredentials login = this.getLoginForProvision();
		ExecResponse responses = this.computeService.runScriptOnNode(getNodeId(), Statements.exec(command), 
				TemplateOptions.Builder.runScript(command).overrideLoginCredentials(login).runAsRoot(true));
		System.out.println(responses.getOutput());
	}
	
	public String getInventoryFile(String projectCode, EnvironmentType environmentType){
		return "/opt/ansible/inventory/" + projectCode + "_" + environmentType.getCode();
	}
	
	public String getGroupVarsFile(String projectCode, EnvironmentType environmentType){
		return "/opt/ansible/inventory/group_vars/" + projectCode + "_" + environmentType.getCode();
	}
	
	private LoginCredentials getLoginForProvision(){
		
		try{
			
			String user = "ubuntu";
			String privateKey = Files.toString(new 
					File("C:\\Users\\D066624\\Google Drive\\Rough\\Eclipse\\ProvisionHybris\\src\\main\\resources\\id_rsa"), Charsets.UTF_8);
			return LoginCredentials.builder().user(user).privateKey(privateKey).build();
		
		} catch (Exception e) {
			System.err.println("Error Reading Private Key: " + e.getMessage());
			System.exit(1);
		}
		
		return null;
		
	}
	
}
