package com.hybris.provider;

import org.jclouds.azurecompute.AzureComputeApi;
import org.jclouds.azurecompute.domain.VMImage;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.domain.Location;


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
		Provider provider = Provider.MicrosoftAzure;
		ComputeService computeService = provider.getComputeService();
		
		for(Location location:computeService.listAssignableLocations()){
			System.out.println(location);
		}
		System.out.println("");
		System.out.println("");
        for(VMImage vm:computeService.getContext().unwrapApi(AzureComputeApi.class).getVMImageApi().list()){
           System.out.println(vm);	 
        }
        System.out.println("");
		System.out.println("");
		for(Hardware hrd:computeService.listHardwareProfiles()){
			System.out.println(hrd);
		}
    }

}