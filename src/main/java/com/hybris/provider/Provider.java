package com.hybris.provider;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.aws.ec2.AWSEC2Api;
import org.jclouds.aws.ec2.features.AWSKeyPairApi;
import org.jclouds.aws.ec2.reference.AWSEC2Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.ec2.domain.KeyPair;

public enum Provider {
	
	AmazonWebService("aws-ec2"), 
	GoogleCloudProvider("google-compute-engine");
	
	private final String providerApi;
	private final Properties properties = new Properties();
	
	private Provider(String api) {
		// TODO Auto-generated constructor stub
		this.providerApi = api;
	}
	
	public String getApi(){
		System.out.println(">> Set Provider..");
		return this.providerApi;
	}
	
	public Properties getOverrides(){
		
		Properties overrides = new Properties();
		
		if(this.equals(AmazonWebService)){
			overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY, "owner-id=137112412989;state=available;image-type=machine;root-device-type=ebs");
		}
		System.out.println(">> Overrides Properties set..");
		return overrides;
	}
	
	public String getKeypair(Properties sshKeyProperties) throws IOException{
		this.properties.load(Provider.class.getClassLoader().getResourceAsStream("cloudprovider.properties"));
		String keypairName = "";
		
		switch (this) {
			case AmazonWebService:
				keypairName = sshKeyProperties.getProperty("amazon.keypair");
				break;
			case GoogleCloudProvider:
				keypairName = sshKeyProperties.getProperty("googlecloud.keypair");
				break;
			default:
				keypairName = "";
		}
		System.out.println(">> Get key pair..");
		return keypairName;
	}
	
	public Properties setPublicKey(ComputeService computeService, String location, String keyName, String publicKey){
		
		Properties sshKeyProperties = new Properties();
		
		switch (this) {
			case AmazonWebService:
				AWSKeyPairApi keyPairApi = computeService.getContext().unwrapApi(AWSEC2Api.class).getKeyPairApiForRegion(location).get();
				KeyPair keyPair = keyPairApi.importKeyPairInRegion(location, keyName, publicKey);
				sshKeyProperties.setProperty("amazon.keypair", keyPair.getKeyName());
				sshKeyProperties.setProperty("googlecloud.keypair", "");
				break;
				
			case GoogleCloudProvider:
				sshKeyProperties.setProperty("amazon.keypair", "");
				sshKeyProperties.setProperty("googlecloud.keypair", "alpanchaphalkar");
				break;
			
			default:
				sshKeyProperties.setProperty("amazon.keypair", "");
				sshKeyProperties.setProperty("googlecloud.keypair", "");
		}
		System.out.println(">> Set key pair..");
		return sshKeyProperties;
	}
	
	
	public String getIdentity() throws IOException{
		
		this.properties.load(Provider.class.getClassLoader().getResourceAsStream("cloudprovider.properties"));
		String identity = "";
		
		switch (this) {
			case AmazonWebService:
				identity = this.properties.getProperty("amazon.identity");
				break;
			case GoogleCloudProvider:
				identity = this.properties.getProperty("googlecloud.identity");
				break;
				
			default:
				identity = "";
		}
		System.out.println(">> Get identity..");
		return identity;
		
	}
	
	public String getCredential() throws IOException{
		
		this.properties.load(Provider.class.getClassLoader().getResourceAsStream("cloudprovider.properties"));
		String credentials = "";
		
		switch (this) {
			case AmazonWebService:
				credentials = this.properties.getProperty("amazon.credential");
				break;
			case GoogleCloudProvider:
				credentials = "Implement GCP credential pending";
				break;
				
			default:
				credentials = "";
		}
		System.out.println(">> Get credential..");
		return credentials;
		
	}
	
}
