package com.hybris.computeservice;

import static com.google.common.base.Charsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jclouds.ContextBuilder;
import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.sshj.config.SshjSshClientModule;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import com.google.inject.Module;
import com.hybris.provider.Cpu;
import com.hybris.provider.DiskSize;
import com.hybris.provider.Provider;
import com.hybris.provider.RamSize;
import com.hybris.provider.Region;

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
		
		Iterable<Module> modules = ImmutableSet.<Module> of( new SshjSshClientModule());
		
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
				
/*				// Create new KeyPair 
				String AwsPublicKey = Files.toString(new File(pathToKey + ".pub"), UTF_8);
				AWSKeyPairApi keyPairApi = computeService.getContext().unwrapApi(AWSEC2Api.class).getKeyPairApiForRegion(region.getID()).get();
				KeyPair keyPair = keyPairApi.importKeyPairInRegion(region.getID(), keyName, AwsPublicKey);
				// Imports new KeyPair to the node
				System.out.printf(">> Importing public key %s%n", keyName);
				System.out.println();
				templateOptions.as(AWSEC2TemplateOptions.class).keyPair(keyPair.getKeyName());*/
				
				//templateOptions.overrideLoginPrivateKey(AwsPrivateKey);
				
				//Use existing key pair in aws by key pair name
				//String keyName = "alpanachaphalkar";
				System.out.printf(">> Importing public key %s%n", keyName);
				System.out.println();
				
				templateOptions.as(AWSEC2TemplateOptions.class).keyPair(keyName);
				templateOptions.as(AWSEC2TemplateOptions.class).subnetId("subnet-13d3fb5b").securityGroups("sg-8651acf6");
				
				break;
			
			case GoogleCloudProvider:
				
				// To use project-wide SSH keys
				templateOptions.as(GoogleComputeEngineTemplateOptions.class).autoCreateKeyPair(false);
				
				// Imports local ssh keys to node
				String GcpPublicKey = Files.toString(new File(pathToKey), UTF_8);
				System.out.printf(">> Importing public key %s%n", keyName);
				System.out.println();
				templateOptions.as(GoogleComputeEngineTemplateOptions.class).userMetadata("ssh-keys", GcpPublicKey);
				
				ArrayList<String> tags = new ArrayList<String>();
				tags.add("demo-hybris-firewall");
				templateOptions.as(GoogleComputeEngineTemplateOptions.class).tags(tags);
				/*templateOptions.as(GoogleComputeEngineTemplateOptions.class)
						.networks("demo-hybris-subnet");*/
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
			/*ExecResponse responses = computeService.runScriptOnNode(nodeId, Statements.exec(command), 
					TemplateOptions.Builder.runScript(command));*/
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
	    	/*ExecResponse responses = computeService.runScriptOnNode(nodeId, Files.toString(script, Charsets.UTF_8), 
					TemplateOptions.Builder.runScript(Files.toString(script, Charsets.UTF_8)));*/
			System.out.println(responses.getOutput());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	
	
	
}
