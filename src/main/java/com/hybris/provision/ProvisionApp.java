package com.hybris.provision;

import java.io.IOException;
import java.util.Set;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Location;

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
		CloudService service = new CloudService(Provider.AmazonWebService);
		
 		// Create Node or Instance
/*		service.createNode(computeService, OsFamily.UBUNTU, Cpu.Two64bit, RamSize.Aws_Eight, DiskSize.Ten, 
							Region.AWS_UsEast1, groupName, keyName, "C:\\cygwin64\\home\\D066624\\.ssh\\id_rsa");*/

		
		
		/* ******************************************
		 *		GCP Provider Compute Service		*
		 * ******************************************/
		/*CloudService service = new CloudService(Provider.GoogleCloudProvider);*/
		
				
  		// Compute Service Specifications
		ComputeService computeService = service.initComputeService();
  		String groupName = "hybris-demo-app-002";
 		String keyName = groupName;
 		OsFamily os = OsFamily.UBUNTU;
 		Cpu cpu = Cpu.Two64bit;
 		RamSize ramSize = RamSize.Eight;
 		DiskSize diskSize = DiskSize.Ten;
 		Region region = service.getRegion();
 		String javaInstallationScript = "C:\\Users\\D066624\\Google Drive\\Rough\\Eclipse\\ProvisionHybris\\src\\main\\resources\\install_java.sh";
  		String hybrisInstallationScript = "C:\\Users\\D066624\\Google Drive\\Rough\\Eclipse\\ProvisionHybris\\src\\main\\resources\\install_hybris.sh";
 		
  		// Create Node or Instance
  		service.createNode(computeService, os, cpu, service.getRamSize(ramSize), diskSize, 
							region, groupName, keyName, service.getKeyToSsh());
		
		// Execute shell command on created instance.
/* 		System.out.println(">> Command execution Begins!");
		service.executeCommand(computeService, groupName, "sudo su");
		System.out.println("<< Command execution Completed!");*/
  		
  		
  		// Install Java on created instance
  		System.out.println(">> Java Installation Begins!");
		service.executeScript(computeService, groupName, javaInstallationScript);
		System.out.println("<< Java Installation Completed!");
		
		// Install Hybris on created instance
		System.out.println(">> Hybris Installation Begins!");
		service.executeScript(computeService, groupName, hybrisInstallationScript);
		System.out.println("<< Hybris Installation Completed!");
		
		// Closing the compute service
		computeService.getContext().close();
		
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
