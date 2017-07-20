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
		
 		String groupName = "java-hybris-try-019";
 		String keyName = groupName;
 		
		/* ******************************************
		 *		AWS EC2 Create Node					*
		 * ******************************************/		
/* 		CloudService service = new CloudService(Provider.AmazonWebService);
 		ComputeService computeService = service.initComputeService();*/
 		
 		// Create Node or Instance
/*		service.createNode(computeService, OsFamily.UBUNTU, Cpu.Two64bit, RamSize.Aws_Eight, DiskSize.Ten, 
							Region.AWS_UsEast1, groupName, keyName, "C:\\cygwin64\\home\\D066624\\.ssh\\id_rsa");*/
		
		// Execute shell command on created instance.
/* 		System.out.println(">> Command execution Begins!");
		service.executeCommand(computeService, groupName, "sudo su");
		System.out.println("<< Command execution Completed!");*/
		
		// Execute shell script on created instances
		
		// Install Java on created instance
/*		System.out.println(">> Java Installation Begins!");
		service.executeScript(computeService, groupName, "C:\\Users\\D066624\\Google Drive\\Rough\\Eclipse\\ProvisionHybris\\src\\main\\resources\\install_java.sh");
		System.out.println("<< Java Installation Completed!");*/
		
		
		// Install Hybris on created instance
/*		System.out.println(">> Hybris Installation Begins!");
		service.executeScript(computeService, groupName, "C:\\Users\\D066624\\Google Drive\\Rough\\Eclipse\\ProvisionHybris\\src\\main\\resources\\install_hybris.sh");
		System.out.println("<< Hybris Installation Completed!");
		
		computeService.getContext().close();*/
		
		/* ******************************************
		 *		GCP Create Node						*
		 * ******************************************/
		CloudService service = new CloudService(Provider.GoogleCloudProvider);
  		ComputeService computeService = service.initComputeService();
  		
  		// Create Node or Instance
  		service.createNode(computeService, OsFamily.UBUNTU, Cpu.Two64bit, RamSize.Gcp_Eight, DiskSize.Ten, 
							Region.GCP_UsEast1b, groupName, keyName, "C:\\cygwin64\\home\\D066624\\.ssh\\id_rsa.pub");
		
  		// Install Java on created instance
  		System.out.println(">> Java Installation Begins!");
		service.executeScript(computeService, groupName, "C:\\Users\\D066624\\Google Drive\\Rough\\Eclipse\\ProvisionHybris\\src\\main\\resources\\install_java.sh");
		System.out.println("<< Java Installation Completed!");
		
		// Install Hybris on created instance
		System.out.println(">> Hybris Installation Begins!");
		service.executeScript(computeService, groupName, "C:\\Users\\D066624\\Google Drive\\Rough\\Eclipse\\ProvisionHybris\\src\\main\\resources\\install_hybris.sh");
		System.out.println("<< Hybris Installation Completed!");
		
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
