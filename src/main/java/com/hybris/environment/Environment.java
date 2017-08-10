package com.hybris.environment;

import java.io.IOException;
import java.util.ArrayList;

import org.jclouds.compute.ComputeService;

import com.hybris.computeservice.CloudService;
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
	
	public ArrayList<String> getHostNames(ServerType serverType, int count){
		ArrayList<String> hostNames = new ArrayList<String>();
		
		if(count <= 0 || count > 9){
			System.out.println("Please enter no. of " + serverType + " servers greater than 0 and less than 9");
			return hostNames;
		}
		
		for(int i=1; i<=count; i++){
			hostNames.add(project_code + "-" + environment_type.getCode() + "-" + provider.getCode() + "-" 
							+ serverType.getCode() + "-00" + i + SERVER_DOMAIN);
		}
		
		return hostNames;
	}
	
	public void create(int adm, int app, int web, int srch, int db){
		CloudService service = new CloudService(this.provider);
		
		try {
			ComputeService computeService = service.initComputeService();
			
			ArrayList<String> appHosts = getHostNames(ServerType.Application, app);
			ArrayList<String> admHosts = getHostNames(ServerType.Admin, adm);
			ArrayList<String> webHosts = getHostNames(ServerType.Web, web);
			ArrayList<String> srchHosts = getHostNames(ServerType.Search, srch);
			ArrayList<String> dbHosts = getHostNames(ServerType.Database, db);
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		
		Environment env = new Environment(Provider.GoogleCloudProvider, "demo", EnvironmentType.Development);
		ArrayList<String> appHosts = env.getHostNames(ServerType.Search, 9);
		System.out.println(appHosts);
		
	}
	
}
