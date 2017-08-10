package com.hybris.environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;

import com.hybris.computeservice.CloudService;
import com.hybris.provider.Cpu;
import com.hybris.provider.DiskSize;
import com.hybris.provider.Provider;
import com.hybris.provider.RamSize;
import com.hybris.provider.Region;

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
	
	public ArrayList<String> getHostNames(Server server){
		ArrayList<String> hostNames = new ArrayList<String>();
		
		int serverCount = server.getSeverCount();
		ServerType serverType = server.getServerType();
		
		if(serverCount <= 0 || serverCount > 9){
			System.out.println("Please enter no. of " + serverType + " servers greater than 0 and less than 9");
			return hostNames;
		}
		
		for(int i=1; i<=serverCount; i++){
			hostNames.add(project_code + "-" + environment_type.getCode() + "-" + provider.getCode() + "-" 
							+ serverType.getCode() + "-00" + i + SERVER_DOMAIN);
		}
		
		return hostNames;
	}
	
	public HashMap<String, NodeMetadata> create(Server[] servers){
		
		HashMap<String, NodeMetadata> environmentMap = new HashMap<String, NodeMetadata>();
		
		if(servers.length == 0){
			System.out.println("Server list is empty!");
			return environmentMap;
		}
		
		try {
			
			// create map of servers present in environment
			HashMap<Server, ArrayList<String>> serverMap = new HashMap<Server, ArrayList<String>>();
			for(Server server:servers){
				if(server.getSeverCount() <= 0 || server.getSeverCount() > 9){
					System.out.println("Invalid count provider for " + server.getServerType() + " server.");
				}else{
					ArrayList<String> hostNameList = getHostNames(server);
					serverMap.put(server, hostNameList);
				}
			}
			
			
			if(serverMap.isEmpty()){
				System.out.println("Server Map is empty!");
				return environmentMap;
			}
			for(Map.Entry<Server, ArrayList<String>> serverEntry:serverMap.entrySet()){
				for(String hostName:serverEntry.getValue()){
					NodeMetadata node = serverEntry.getKey().getServerInstance().create(hostName);
					environmentMap.put(hostName, node);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return environmentMap;
	}
	
	public static void main(String[] args) {
		try{
			Provider provider = Provider.AmazonWebService;
			CloudService service = new CloudService(provider);
			OsFamily os = OsFamily.UBUNTU;
			Cpu cpu = Cpu.Two64bit;
			RamSize ramSize = RamSize.Eight;
			DiskSize diskSize = DiskSize.Ten;
			Region region = service.getRegion();
			Environment env = new Environment(provider, "demo", EnvironmentType.Development);
			ServerInstance AdminServer = new ServerInstance(service, os, cpu, ramSize, diskSize, region);
			Server[] servers = { new Server(ServerType.Admin, AdminServer, 1),
								 new Server(ServerType.Application, new ServerInstance(service, os, cpu, ramSize, diskSize, region), 2),
								 new Server(ServerType.Web, new ServerInstance(service, os, cpu, ramSize, diskSize, region), 1)};
			System.out.println(env.create(servers));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
