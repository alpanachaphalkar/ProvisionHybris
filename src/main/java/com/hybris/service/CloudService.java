package com.hybris.service;

import static com.google.common.base.Charsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.aws.ec2.AWSEC2Api;
import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.aws.ec2.features.AWSKeyPairApi;
import org.jclouds.aws.ec2.features.AWSSecurityGroupApi;
import org.jclouds.aws.ec2.options.AWSRunInstancesOptions;
import org.jclouds.aws.ec2.options.CreateSecurityGroupOptions;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunScriptOnNodesException;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.domain.SecurityGroup;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.enterprise.config.EnterpriseConfigurationModule;
import org.jclouds.googlecloud.config.GoogleCloudProperties;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApi;
import org.jclouds.googlecomputeengine.GoogleComputeEngineApiMetadata;
import org.jclouds.googlecomputeengine.compute.functions.FirewallTagNamingConvention;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;
import org.jclouds.googlecomputeengine.domain.Firewall;
import org.jclouds.googlecomputeengine.domain.Firewall.Rule;
import org.jclouds.googlecomputeengine.features.FirewallApi;
import org.jclouds.googlecomputeengine.options.FirewallOptions;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.net.domain.IpPermission;
import org.jclouds.net.domain.IpProtocol;
import org.jclouds.profitbricks.compute.config.ProfitBricksComputeServiceContextModule.ComputeConstants;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.sshj.config.SshjSshClientModule;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import com.google.inject.Module;

import com.hybris.provider.Provider;
import com.hybris.provider.specifications.*;

public class CloudService implements CloudServiceAction{
	
	
	private final Provider provider;
	
	public CloudService(Provider provider) {
		// TODO Auto-generated constructor stub
		this.provider = provider;
	}
	
	public Provider getProvider(){
		return this.provider;
	}
	
	public ComputeService initComputeService() throws IOException{
		
		/*Iterable<Module> modules = ImmutableSet.<Module> of(
															new SshjSshClientModule(),
															new SLF4JLoggingModule(),
															new EnterpriseConfigurationModule());
		 */
		
		Iterable<Module> modules = ImmutableSet.<Module> of( new SshjSshClientModule(), 
															 new SLF4JLoggingModule(),
															 new EnterpriseConfigurationModule());
		
		ContextBuilder builder = ContextBuilder.newBuilder(this.provider.getApi())
			.credentials(this.provider.getIdentity(), this.provider.getCredential())
			.overrides(this.provider.getOverrides())
			.modules(modules);
		
		System.out.printf(">> initializing %s%n", builder.getApiMetadata());
		ComputeServiceContext computeServiceContext = builder.buildView(ComputeServiceContext.class);
		System.out.println();
		return computeServiceContext.getComputeService();
		
	}
	
	private static LoginCredentials getLoginForProvision(){
		
		try{
			
			String user = "ubuntu";
			String privateKey = Files.toString(new File("C:\\cygwin64\\home\\D066624\\.ssh\\id_rsa"), Charsets.UTF_8);
			return LoginCredentials.builder().user(user).privateKey(privateKey).build();
		
		} catch (Exception e) {
			System.err.println("Error Reading Private Key: " + e.getMessage());
			System.exit(1);
		}
		
		return null;
		
	}
	
	public String getKeyToSsh(){
		
		String pathToPublicKey = "C:\\cygwin64\\home\\D066624\\.ssh\\id_rsa.pub";
 	    String pathToPrivateKey = "C:\\cygwin64\\home\\D066624\\.ssh\\id_rsa";
 	    String keyToSsh = null;
 	    
 	    switch (this.provider) {
		case AmazonWebService:
			keyToSsh = pathToPrivateKey;
			break;
		case GoogleCloudProvider:
			keyToSsh = pathToPublicKey;
			break;
		default:
			break;
		}
 	    
 	    return keyToSsh;
	}
	
	public int getRamSize(RamSize ramSize){
		
		switch (this.provider) {
		case AmazonWebService:
			return ramSize.getSize();
		case GoogleCloudProvider:
			return ramSize.getSize() * 1024;
		default:
			return 0;
		}
		
	}	
	
	public Region getRegion(){
		Region region = null;
		
		switch (this.provider) {
		case AmazonWebService:
			region = Region.AWS_UsEast1;
			break;
		
		case GoogleCloudProvider:
			region = Region.GCP_UsEast1b;
			break;
			
		default:
			break;
		}
		
		return region;
	}
	
	public String awsCreateSecurityGroup(ComputeService computeService, Region region, String groupName) throws IOException{
		
		String securityGroupName = "security-group-" + groupName;
		
		AWSSecurityGroupApi securityGroupApi = computeService.getContext().unwrapApi(AWSEC2Api.class).getSecurityGroupApiForRegion(region.getID()).get();
		
		/*String securityGroupId = securityGroupApi.createSecurityGroupInRegionAndReturnId(region.getID(), securityGroupName, 
																						 securityGroupName, securityGrpOptions);*/
		securityGroupApi.createSecurityGroupInRegion(region.getID(), securityGroupName, securityGroupName);
		securityGroupApi.authorizeSecurityGroupIngressInRegion(region.getID(), securityGroupName, IpProtocol.TCP, 22, 22, "0.0.0.0/0");
		securityGroupApi.authorizeSecurityGroupIngressInRegion(region.getID(), securityGroupName, IpProtocol.TCP, 0, 65535, "0.0.0.0/0");
		Set<SecurityGroup> securityGroups = securityGroupApi.describeSecurityGroupsInRegion(region.getID(), securityGroupName);
		String securityGroupId = "";
		for (SecurityGroup securityGroup : securityGroups) {
			securityGroupId = securityGroup.getId();
		}
		/*TemplateBuilder templateBuilder = computeService.templateBuilder().options(TemplateOptions.Builder.securityGroups(securityGroupName));
		templateBuilder.build();*/
		return securityGroupId;
	}
	
	public String createNode(ComputeService computeService, OsFamily os, Cpu cpu, int ramSize, DiskSize disk,
							Region region, String groupName, String keyName, String pathToKey) {
		// TODO Auto-generated method stub
		
		try {
			
			System.out.printf(">> adding node to group %s%n", groupName);
			System.out.println();
			
			TemplateBuilder templateBuilder = computeService.templateBuilder()
														.os64Bit(cpu.getType())
														.minCores(cpu.getCores())
														.minRam(ramSize)
														.minDisk(disk.getSize())
														.osFamily(os)
														.locationId(region.getID());
			Template template = templateBuilder.build();
			TemplateOptions templateOptions = template.getOptions();
			
			
			switch (this.provider) {
			
			case AmazonWebService:
				
				templateOptions.as(AWSEC2TemplateOptions.class).userMetadata("Name", groupName);
				
				// Create and Authorize security group
				
				String AwsPublicKey = Files.toString(new File(pathToKey + ".pub"), UTF_8);
				//String AwsPrivateKey = Files.toString(new File(pathToKey), UTF_8);
				AWSKeyPairApi keyPairApi = computeService.getContext().unwrapApi(AWSEC2Api.class).getKeyPairApiForRegion(region.getID()).get();
				KeyPair keyPair = keyPairApi.importKeyPairInRegion(region.getID(), keyName, AwsPublicKey);
				//Use existing key pair in aws by key pair name
				//templateOptions.as(AWSEC2TemplateOptions.class).keyPair(keyPairName);
				
				//templateOptions.overrideLoginPrivateKey(AwsPrivateKey);
				
				// Imports local ssh key to the node
				System.out.printf(">> Importing public key %s%n", keyName);
				System.out.println();
				templateOptions.as(AWSEC2TemplateOptions.class).keyPair(keyPair.getKeyName());
				templateOptions.as(AWSEC2TemplateOptions.class).securityGroupIds("sg-db2a18aa");
				//AWSRunInstancesOptions instanceOptions = new AWSRunInstancesOptions();
				//instanceOptions.withSecurityGroupId("sg-db2a18aa");
				
				break;
			
			case GoogleCloudProvider:
				
				// To use project-wide SSH keys
				templateOptions.as(GoogleComputeEngineTemplateOptions.class).autoCreateKeyPair(false);
				
				// Imports local ssh keys to node
				String GcpPublicKey = Files.toString(new File(pathToKey), UTF_8);
				System.out.printf(">> Importing public key %s%n", keyName);
				System.out.println();
				templateOptions.as(GoogleComputeEngineTemplateOptions.class).userMetadata("ssh-keys", GcpPublicKey);
				//rewallApi firewallApi = computeService.getContext().unwrapApi(GoogleComputeEngineApi.class).firewalls();
				ArrayList<String> tags = new ArrayList<String>();
				tags.add("hybris-demo-app-firewall");
				templateOptions.as(GoogleComputeEngineTemplateOptions.class).tags(tags);
				//int ports[] = {9001, 9002, 8983, 22, 80, 443};
				
				//templateOptions.as(GoogleComputeEngineTemplateOptions.class).inboundPorts(ports);
				
				break;
				
			}
			
			System.out.println(">> creation of node is beginning.. ");
			NodeMetadata node = Iterables.getOnlyElement(computeService.createNodesInGroup(groupName, 1, template));
			
			System.out.println("<< node: " + node.getName() + "  with ID: " + node.getId() + "  with Private IP: " + node.getPrivateAddresses()
			+ "  and Public IP: " + node.getPublicAddresses() + "  is created.");
			
			return node.getId();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		
		
	}

	public void executeCommand(ComputeService computeService, String nodeId, String command) {
		// TODO Auto-generated method stub
		LoginCredentials login = getLoginForProvision();
	    
		//execute command in groups
		   /*try {
		     Map<? extends NodeMetadata, ExecResponse> responses = 
		    		  computeService.runScriptOnNodesMatching(NodePredicates.inGroup(groupName), 
		    				  Statements.exec(command), 
		    				  TemplateOptions.Builder.runScript(command).overrideLoginCredentials(login));
		    	
		    	for (Map.Entry<? extends NodeMetadata, ExecResponse> response : responses.entrySet()) {
			        System.out.printf("<< node %s: %s%n", response.getKey().getId(), 
			          Iterables.concat(response.getKey().getPrivateAddresses(), response.getKey().getPublicAddresses()));
			        System.out.printf("<<     %s%n", response.getValue());
			    }
		    
		    }
		    catch (RunScriptOnNodesException e) {
		      e.printStackTrace();
		    }*/
		
	      ExecResponse responses = computeService.runScriptOnNode(nodeId, Statements.exec(command), 
	    		  												TemplateOptions.Builder.runScript(command).overrideLoginCredentials(login));
	      System.out.println(responses.getOutput());
	      
	}

	public void executeScript(ComputeService computeService, String nodeId, String pathToScript) {
		// TODO Auto-generated method stub
		
		File script = new File(pathToScript);
	    LoginCredentials login = getLoginForProvision();
	    
	    /*try {
		      Map<? extends NodeMetadata, ExecResponse> responses = 
		      computeService.runScriptOnNodesMatching(NodePredicates.inGroup(groupName), 
		      Files.toString(script, Charsets.UTF_8), 
		      TemplateOptions.Builder.runScript(Files.toString(script, Charsets.UTF_8)).overrideLoginCredentials(login));
		      
	      for (Map.Entry<? extends NodeMetadata, ExecResponse> response : responses.entrySet()) {
	        System.out.printf("<< node %s: %s%n", response.getKey().getId(), Iterables.concat(response.getKey().getPrivateAddresses(), response.getKey().getPublicAddresses()));
	        System.out.printf("<<     %s%n", response.getValue());
	      }
	      
	    }
	    catch (RunScriptOnNodesException e) {
	      e.printStackTrace();
	    }
	    catch (IOException e) {
	      e.printStackTrace();
	    }*/
	    
	    try {
			ExecResponse responses = computeService.runScriptOnNode(nodeId, Files.toString(script, Charsets.UTF_8), 
										TemplateOptions.Builder.runScript(Files.toString(script, Charsets.UTF_8)).overrideLoginCredentials(login));
			
			System.out.println(responses.getOutput());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	
	
	
}
