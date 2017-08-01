package com.hybris.provision;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Location;
import org.jclouds.scriptbuilder.ScriptBuilder;

import com.hybris.service.CloudService;
import com.hybris.provider.Provider;
import com.hybris.provider.specifications.*;

/**
 * Main Provision App
 *
 */
public class ProvisionApp {
	
	public static void main( String[] args ) throws IOException{
 		
		/* ******************************************
		 *		AWS Provider Compute Service		*
		 * ******************************************/	
		long timeStart = System.currentTimeMillis();
		/*CloudService service = new CloudService(Provider.AmazonWebService);*/
		
		
		/* ******************************************
		 *		GCP Provider Compute Service		*
		 * ******************************************/
		CloudService service = new CloudService(Provider.GoogleCloudProvider);
		
				
  		// Compute Service Specifications
		ComputeService computeService = service.initComputeService();
		String groupName = "hybris-demo-app-012";
		String hostName = groupName + ".hybrishosting.com";
 		String keyName = "alpanachaphalkar";
 		OsFamily os = OsFamily.UBUNTU;
 		Cpu cpu = Cpu.Two64bit;
 		RamSize ramSize = RamSize.Eight;
 		DiskSize diskSize = DiskSize.Ten;
 		Region region = service.getRegion();
 		String downloadScripts = "C:\\Users\\D066624\\Google Drive\\Rough\\Eclipse\\ProvisionHybris\\src\\main\\resources\\download_scripts.sh";
 		String cleanUpScript="C:\\Users\\D066624\\Google Drive\\Rough\\Eclipse\\ProvisionHybris\\src\\main\\resources\\clean_up.sh";
 		 		
  		// Create Node or Instance
 		String nodeId = service.createNode(computeService, os, cpu, service.getRamSize(ramSize), diskSize, 
							region, groupName, keyName, service.getKeyToSsh());
  		System.out.println("---------------------------------------------------------------------------");
  		
  		// Download scripts for provisioning
  		service.executeScript(computeService, nodeId, downloadScripts);
  		
  		System.out.println("---------------------------------------------------------------------------");
  		System.out.println(">> Java Installation Begins!");
  		service.executeCommand(computeService, nodeId, "sudo /opt/scripts/install_java.sh " + hostName);
  		System.out.println("<< Java Installation Completed!");
  		
  		System.out.println("---------------------------------------------------------------------------");
  		HybrisVersion selectedHybrisVersion = HybrisVersion.Hybris6_2_0;
  		String hybrisVersion = selectedHybrisVersion.getHybrisVersion();
  		String hybrisPackage = selectedHybrisVersion.getHybrisPackage();
  		System.out.println(">> " + hybrisVersion + " is selected!");
  		
  		HybrisRecipe selectedHybrisRecipe = HybrisRecipe.B2B_Accelerator;
  		String acceleratorType = selectedHybrisRecipe.getRecipeId();
  		System.out.println(">> Hybris " + selectedHybrisRecipe + " is selected!");
  		
  		System.out.println("---------------------------------------------------------------------------");
  		System.out.println(">> Hybris Installation Begins!");
  		service.executeCommand(computeService, nodeId, "sudo /opt/scripts/install_hybris.sh "+ 
  																			hybrisVersion + " " + hybrisPackage + " " + acceleratorType);
  		System.out.println("<< Hybris Installation Completed!");
  		
  		System.out.println("---------------------------------------------------------------------------");
  		System.out.println(">> Cleaning Up");
  		service.executeScript(computeService, nodeId, cleanUpScript);
  		System.out.println("<< Cleaned Up");
  		
		// Closing the compute service
		computeService.getContext().close();
		System.out.println("---------------------------------------------------------------------------");
		long timeEnd = System.currentTimeMillis();
		long duration = timeEnd - timeStart;
		long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
		System.out.println("Provision of Hybris on " + hostName + " of " + service.getProvider() + " took " + minutes + " minutes.");
		
		/* ******************************************
		 *		Listing machine types in GCP		*
		 * ******************************************/			
/*		Set<? extends Hardware> machineTypes = service.initComputeService().listHardwareProfiles();
		System.out.println("<< GCP Hardware Profiles List: ");
		
		for(Hardware machineType:machineTypes){
			
			if(machineType.getLocation().getId().equals("us-east1-b")){
				System.out.println("Name: " + machineType.getName() + "  Location: " + machineType.getLocation().getId() + "  Ram: " + machineType.getRam() 
							   + "  Type: " + machineType.getType() + "  Processor: " + machineType.getProcessors() + "  Volumes: " 
					           + machineType.getVolumes() + "  User data: " + machineType.getUserMetadata());
			}
		}*/
		
		
		
		/* ******************************************
		 *		Listing locations in GCP			*
		 * ******************************************/		
/* 		Set<? extends Location> locations = service.initComputeService().listAssignableLocations();
		System.out.println("<< GCP Locations List: ");
		for(Location location:locations){
			System.out.println("Name: " + location.getDescription() + "  ID: " + location.getId());
		}*/
		
		
		
		/* ******************************************
		 *		GCP initiate compute service		*
		 * ******************************************/		
/*		service.initComputeService();
		System.out.println("Initialization successful!");*/
		
	
		
		/* ******************************************
		 *		Listing locations in AWS			*
		 * ******************************************/
/*		Set<? extends Location> locations = service.initComputeService().listAssignableLocations();
		System.out.println("<< AWS Locations List: ");
		for(Location location:locations){
			System.out.println("Name: " + location.getDescription() + "  ID: " + location.getId());
		}*/
		
		
    }
}
