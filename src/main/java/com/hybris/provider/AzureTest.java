package com.hybris.provider;

import java.util.List;

import org.jclouds.azurecompute.AzureComputeApi;
import org.jclouds.azurecompute.AzureManagementApiMetadata;
import org.jclouds.azurecompute.compute.config.AzureComputeServiceContextModule;
import org.jclouds.azurecompute.compute.extensions.AzureComputeSecurityGroupExtension;
import org.jclouds.azurecompute.compute.options.AzureComputeTemplateOptions;
import org.jclouds.azurecompute.config.AzureComputeProperties;
import org.jclouds.azurecompute.domain.CreateAffinityGroupParams;
import org.jclouds.azurecompute.domain.NetworkConfiguration;
import org.jclouds.azurecompute.features.VirtualNetworkApi;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.domain.ResourceMetadata;

import com.google.common.collect.Iterables;
import com.hybris.environment.ServerType;


public class AzureTest {
    
	
	public AzureTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		/*Iterable<Module> modules = ImmutableSet.<Module> of( new SshjSshClientModule(),
                                                             new SLF4JLoggingModule());
		ContextBuilder builder = ContextBuilder.newBuilder("azurecompute")
				                 .endpoint("https://eus2-agentservice-prod-1.azure-automation.net/accounts/3f802fc1-3d77-4dd1-96ba-c60ee26aede4")
				                 .credentials(P12FILE, "")
				                 .modules(modules);
		System.out.printf(">> initializing %s%n", builder.getApiMetadata());*/
		/*Provider provider = Provider.MicrosoftAzure;
		ComputeService computeService = provider.getComputeService();*/
		/*System.out.println(computeService.getContext().unwrapApi(AzureComputeApi.class).getCloudServiceApi().getProperties(""));*/
		//System.out.println(computeService.getContext().unwrapApi(AzureComputeApi.class).getVirtualNetworkApi().list());
		/*Location azureLocation = null;
		for(Location location:computeService.listAssignableLocations()){
			if(location.getId().equals(provider.getRegion().getID())){
				azureLocation = location;
			}
		}
		System.out.println(computeService.getSecurityGroupExtension().get().listSecurityGroupsInLocation(azureLocation));*/
		
		/*System.out.println("");
		System.out.println("");
		
		System.out.println("Azure Hardware Profiles:");
		for(Hardware hrd:computeService.listHardwareProfiles()){
			System.out.println(hrd);
		}
		System.out.println("");
		System.out.println("");*/
		/*System.out.println("Azure Images:");
		for(Image img:computeService.listImages()){
			if(img.getId().contains("Ubuntu-16") && img.getId().contains("US")){
				System.out.println(img);
			}
		}
		System.out.println("");
		System.out.println("");*/
/*		String azuImageId = "b39f27a8b8c64d52b05eac6a62ebad85__Ubuntu-16_04-LTS-amd64-server-20161221-en-us-30GB/Central US";
		String azuHardwareId = "STANDARD_D1";
		String azureSecurityGroup = "demo-security-group";
		TemplateBuilder azureTemplateBuilder = computeService.templateBuilder().locationId(provider.getRegion().getID())
		                                                                            .os64Bit(true)
		                                                                            .imageId(azuImageId)
		                                                                            .hardwareId(azuHardwareId);
		Template template = azureTemplateBuilder.build();
		TemplateOptions azuTemplateOptions = template.getOptions();
		azuTemplateOptions.as(AzureComputeTemplateOptions.class).securityGroups(azureSecurityGroup)
		                                                        .userMetadata("resourceGroups", "Default-Networking");
		NodeMetadata instance = Iterables.getOnlyElement(computeService.createNodesInGroup("default-networking", 1, template));
		System.out.println("<<	Server is created with following details: ");
		System.out.println("	Name: " + instance.getHostname());
		System.out.println("	ID: " + instance.getId());
		System.out.println("	Private IP: " + instance.getPrivateAddresses());
		System.out.println("	Public IP: " + instance.getPublicAddresses());
		computeService.getContext().close();*/
    }

}