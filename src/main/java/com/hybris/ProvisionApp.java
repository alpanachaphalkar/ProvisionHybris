package com.hybris;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Location;
import org.jclouds.scriptbuilder.ScriptBuilder;

import com.hybris.computeservice.CloudService;
import com.hybris.provider.Cpu;
import com.hybris.provider.DiskSize;
import com.hybris.provider.Provider;
import com.hybris.provider.RamSize;
import com.hybris.provider.Region;

/**
 * Main Provision App
 *
 */
public class ProvisionApp {
	
	public static void main( String[] args ) throws Exception{
 		
		/* ******************************************
		 *		AWS Provider Compute Service		*
		 * ******************************************/	
		long timeStart = System.currentTimeMillis();
		Provider provider = Provider.AmazonWebService;
		ComputeService computeService = provider.initComputeService();
		CloudService service = new CloudService(provider);
		
		
		/* ******************************************
		 *		GCP Provider Compute Service		*
		 * ******************************************/
		/*CloudService service = new CloudService(Provider.GoogleCloudProvider);*/
		
				
  		// Compute Service Specifications
		String host = "hybris-demo-srch-023";
		String hostName = host + ".hybrishosting.com";
 		//String keyName = "alpanachaphalkar";
 		OsFamily os = OsFamily.UBUNTU;
 		Cpu cpu = Cpu.Two64bit;
 		RamSize ramSize = RamSize.Eight;
 		DiskSize diskSize = DiskSize.Ten;
 		Region region = service.getRegion();
 		String downloadScripts = "C:\\Users\\D066624\\Google Drive\\Rough\\Eclipse\\ProvisionHybris\\src\\main\\resources\\download_scripts.sh";
 		 		
  		// Create Node or Instance
 		NodeMetadata node = service.createNode(computeService, os, cpu, ramSize.getSize(service.getProvider()), diskSize, 
							region, host);
  		
  		// Download scripts for provisioning
  		System.out.println("---------------------------------------------------------------------------");
  		System.out.println(">> Set Hostname!");
  		service.executeCommand(computeService, node, "sudo hostnamectl set-hostname " + hostName);
  		service.executeScript(computeService, node, downloadScripts);
  		System.out.println("<< Hostname is set!");
  		
  		/*System.out.println("---------------------------------------------------------------------------");
  		System.out.println(">> Java Installation Begins!");
  		service.executeCommand(computeService, nodeId, "sudo /opt/scripts/install_java.sh");
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
  		System.out.println("<< Cleaned Up");*/
  		
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
