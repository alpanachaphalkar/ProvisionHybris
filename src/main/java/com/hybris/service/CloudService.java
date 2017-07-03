package com.hybris.service;

import static com.google.common.base.Charsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;
import org.jclouds.sshj.config.SshjSshClientModule;

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

	public void createNode(OsFamily os, Cpu cpu, RamSize ram, DiskSize disk,
							Region region, String groupName, String keyName, String pathToKey) {
		// TODO Auto-generated method stub
		
		NodeMetadata node = null;
		try {
			
			ComputeService computeService = initComputeService();
			
			System.out.printf(">> adding node to group %s%n", groupName);
			System.out.println();
			
			switch (this.provider) {
			
			case AmazonWebService:
				
				TemplateBuilder awsTemplateBuilder = computeService.templateBuilder().os64Bit(cpu.getType())
																					.minCores(cpu.getCores())
																					.minRam(ram.getSize())
																					.minDisk(disk.getSize())
																					.osFamily(os)
																					.locationId(region.getID());
				Template awsTemplate = awsTemplateBuilder.build();
		
				TemplateOptions AwsTemplateOptions = awsTemplate.getOptions();
				AwsTemplateOptions.as(AWSEC2TemplateOptions.class).userMetadata("Name", groupName);
				
				String AwsPublicKey = Files.toString(new File(pathToKey), UTF_8);
				Properties AwsSshKeyProperties = new Properties();
				AwsSshKeyProperties = this.provider.setPublicKey(computeService, region.getID(), keyName, AwsPublicKey);
				//templateOptions.as(AWSEC2TemplateOptions.class).keyPair(keyPairName);
				// Imports local ssh key to the node
				AwsTemplateOptions.as(AWSEC2TemplateOptions.class).keyPair(this.provider.getKeypair(AwsSshKeyProperties));
					
				System.out.println(">> creation of node is beginning.. ");
				node = Iterables.getOnlyElement(computeService.createNodesInGroup(groupName, 1, awsTemplate));
				
				System.out.println("<< node: " + node.getName() + "  with ID: " + node.getId() + "  with Private IP: " + node.getPrivateAddresses()
				+ "  and Public IP: " + node.getPublicAddresses() + "  is created.");
				
				break;
			
			case GoogleCloudProvider:
				
				TemplateBuilder GcpTemplateBuilder = computeService.templateBuilder().os64Bit(cpu.getType())
																					.minCores(cpu.getCores())
																					.minRam(ram.getSize() * 1024)
																					.minDisk(disk.getSize())
																					.osFamily(os)
																					.locationId(region.getID());
				Template GcpTemplate = GcpTemplateBuilder.build();
				
				TemplateOptions GcpTemplateOptions = GcpTemplate.getOptions();
				
				//String GcpPublicKey = Files.toString(new File(pathToKey), UTF_8);
				// Blocks project-wide SSH keys
				//GcpTemplateOptions.as(GoogleComputeEngineTemplateOptions.class).userMetadata("sshKeys", GcpPublicKey); 
				
				// To use project-wide SSH keys
				GcpTemplateOptions.as(GoogleComputeEngineTemplateOptions.class).autoCreateKeyPair(false);
				
				// Imports local ssh keys to node
				String GcpPublicKey = Files.toString(new File(pathToKey), UTF_8);
				GcpTemplateOptions.as(GoogleComputeEngineTemplateOptions.class).userMetadata("ssh-keys", GcpPublicKey);
				
				System.out.println(">> creation of node is beginning.. ");
				node = Iterables.getOnlyElement(computeService.createNodesInGroup(groupName, 1, GcpTemplate));
				
				System.out.println("<< node: " + node.getName() + "  with ID: " + node.getId() + "  with Private IP: " + node.getPrivateAddresses()
				+ "  and Public IP: " + node.getPublicAddresses() + "  is created.");
				
				break;
				
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}
	
	
	
}
