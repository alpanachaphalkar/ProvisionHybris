package com.hybris.service;

import static com.google.common.base.Charsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.jclouds.ContextBuilder;
import org.jclouds.aws.ec2.AWSEC2Api;
import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.aws.ec2.features.AWSKeyPairApi;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunScriptOnNodesException;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
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
	
	public ComputeService initComputeService() throws IOException{
		
		/*Iterable<Module> modules = ImmutableSet.<Module> of(
															new SshjSshClientModule(),
															new SLF4JLoggingModule(),
															new EnterpriseConfigurationModule());
		 */
		
		Iterable<Module> modules = ImmutableSet.<Module> of( new SshjSshClientModule() );
		
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
	
	public void createNode(ComputeService computeService, OsFamily os, Cpu cpu, RamSize ram, DiskSize disk,
							Region region, String groupName, String keyName, String pathToKey) {
		// TODO Auto-generated method stub
		
		NodeMetadata node = null;
		
		try {
			
			computeService = initComputeService();
			
			System.out.printf(">> adding node to group %s%n", groupName);
			System.out.println();
			
			TemplateBuilder templateBuilder = computeService.templateBuilder()
														.os64Bit(cpu.getType())
														.minCores(cpu.getCores())
														.minRam(ram.getSize())
														.minDisk(disk.getSize())
														.osFamily(os)
														.locationId(region.getID());
			Template template = templateBuilder.build();
			TemplateOptions templateOptions = template.getOptions();
			
			// Note this will create a user with the same name as you on the node. ex. you can connect via ssh publicip.
/*			Statement bootInstructions = AdminAccess.standard();
			templateBuilder.options(templateOptions.runScript(bootInstructions));*/
			
			switch (this.provider) {
			
			case AmazonWebService:
				
				templateOptions.as(AWSEC2TemplateOptions.class).userMetadata("Name", groupName);
				
				String AwsPublicKey = Files.toString(new File(pathToKey + ".pub"), UTF_8);
				//String AwsPrivateKey = Files.toString(new File(pathToKey), UTF_8);
				AWSKeyPairApi keyPairApi = computeService.getContext().unwrapApi(AWSEC2Api.class).getKeyPairApiForRegion(region.getID()).get();
				KeyPair keyPair = keyPairApi.importKeyPairInRegion(region.getID(), keyName, AwsPublicKey);
				
				//Use existing key pair in aws by key pair name
				//templateOptions.as(AWSEC2TemplateOptions.class).keyPair(keyPairName);
				
				// Imports local ssh key to the node
				System.out.printf(">> Importing public key %s%n", keyName);
				System.out.println();
				templateOptions.as(AWSEC2TemplateOptions.class).keyPair(keyPair.getKeyName());
				//templateOptions.overrideLoginPrivateKey(AwsPrivateKey);
				
				break;
			
			case GoogleCloudProvider:
				
				//String GcpPublicKey = Files.toString(new File(pathToKey), UTF_8);
				// Blocks project-wide SSH keys
				//GcpTemplateOptions.as(GoogleComputeEngineTemplateOptions.class).userMetadata("sshKeys", GcpPublicKey); 
				
				// To use project-wide SSH keys
				templateOptions.as(GoogleComputeEngineTemplateOptions.class).autoCreateKeyPair(false);
				
				// Imports local ssh keys to node
				String GcpPublicKey = Files.toString(new File(pathToKey), UTF_8);
				System.out.printf(">> Importing public key %s%n", keyName);
				System.out.println();
				templateOptions.as(GoogleComputeEngineTemplateOptions.class).userMetadata("ssh-keys", GcpPublicKey);
				
				break;
				
			}
			
			System.out.println(">> creation of node is beginning.. ");
			node = Iterables.getOnlyElement(computeService.createNodesInGroup(groupName, 1, template));
			
			System.out.println("<< node: " + node.getName() + "  with ID: " + node.getId() + "  with Private IP: " + node.getPrivateAddresses()
			+ "  and Public IP: " + node.getPublicAddresses() + "  is created.");
			
			computeService.getContext().close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}

/*	public void executeCommand(ComputeService computeService, String groupName, String command) {
		// TODO Auto-generated method stub
		LoginCredentials login = getLoginForProvision();
	    
	    try
	    {
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
	    catch (RunScriptOnNodesException e)
	    {
	      e.printStackTrace();
	    }
	}*/

	public void executeScript(ComputeService computeService, String groupName, String pathToScript) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
