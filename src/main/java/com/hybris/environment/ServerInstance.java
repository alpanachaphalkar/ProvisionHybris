package com.hybris.environment;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.scriptbuilder.domain.Statements;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class ServerInstance {

	private NodeMetadata node;
	private ComputeService computeService;
	
	public ServerInstance(ComputeService computeService, NodeMetadata node) {
		// TODO Auto-generated constructor stub
		this.setComputeservice(computeService);
		this.setNode(node);
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
					TemplateOptions.Builder.runScript(command).overrideLoginCredentials(login));
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
	
}
