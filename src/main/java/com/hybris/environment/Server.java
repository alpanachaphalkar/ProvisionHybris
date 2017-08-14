package com.hybris.environment;

import java.io.File;
import java.util.ArrayList;

import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.hybris.provider.Provider;

public class Server {

	private ServerType serverType;
	private ComputeService computeservice;
	private int severCount;
	
	public Server(ComputeService computeService, ServerType type, int count) {
		// TODO Auto-generated constructor stub
		this.setComputeservice(computeService);
		this.setServerType(type);
		this.setSeverCount(count);
	}
	
	public ServerType getServerType() {
		return serverType;
	}
	public void setServerType(ServerType serverType) {
		this.serverType = serverType;
	}
	public int getSeverCount() {
		return severCount;
	}
	public void setSeverCount(int severCount) {
		this.severCount = severCount;
	}

	public ComputeService getComputeservice() {
		return computeservice;
	}

	public void setComputeservice(ComputeService computeservice) {
		this.computeservice = computeservice;
	}
	
	public Template getTemplate(Provider provider, EnvironmentType environmentType){
		
		Template template = null;
		String keyName = "alpanachaphalkar";
		try{
		switch (provider) {
			case AmazonWebService:
				
				String awsHardwareId = "t2.large";
				String awsImageId = "us-east-1/ami-841f46ff";
				String awsSubnetId = "subnet-13d3fb5b";
				String awsSecuritygroupId = "sg-8651acf6";
				String awsDeviceName = "/dev/sda1";
				TemplateBuilder awsTemplateBuilder = this.computeservice.templateBuilder().locationId(provider.getRegion().getID())
																				  .imageId(awsImageId)
																				  .hardwareId(awsHardwareId);
				template = awsTemplateBuilder.build();
				TemplateOptions awsTemplateOptions = template.getOptions();
				awsTemplateOptions.as(AWSEC2TemplateOptions.class).keyPair(keyName)
																.subnetId(awsSubnetId)
																.securityGroups(awsSecuritygroupId)
																.mapNewVolumeToDeviceName(awsDeviceName, 40, true);
				
				break;
			case GoogleCloudProvider:
				String pathToKey = "C:\\cygwin64\\home\\D066624\\.ssh\\id_rsa.pub";
				String GcePublicKey = Files.toString(new File(pathToKey), Charsets.UTF_8);
				String gceHardwareId = "https://www.googleapis.com/compute/v1/projects/provisionhybris/zones/us-east1-b/machineTypes/n1-standard-1";
				String gceImageId = "https://www.googleapis.com/compute/v1/projects/provisionhybris/global/images/dev-default-img";
				String gceSecurityGroupId = "demo-hybris-firewall";
				ArrayList<String> tags = new ArrayList<String>();
				tags.add(gceSecurityGroupId);
				TemplateBuilder gceTemplateBuilder = this.computeservice.templateBuilder().locationId(provider.getRegion().getID())
																							.os64Bit(true)
																							/*.minCores(2.0)
																							.minRam(8 * 1024)*/
																							.imageId(gceImageId)
																							.hardwareId(gceHardwareId);
				template = gceTemplateBuilder.build();
				TemplateOptions gceTemplateOptions = template.getOptions();
				gceTemplateOptions.as(GoogleComputeEngineTemplateOptions.class).autoCreateKeyPair(false)
																				.userMetadata("ssh-keys", GcePublicKey)
																				.tags(tags);
				break;
			default:
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return template;
		
	}
}
