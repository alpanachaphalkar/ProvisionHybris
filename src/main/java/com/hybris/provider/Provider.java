package com.hybris.provider;

import static com.google.common.base.Charsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jclouds.ContextBuilder;
import org.jclouds.aws.ec2.reference.AWSEC2Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.ComputeServiceProperties;
import org.jclouds.domain.Credentials;
import org.jclouds.googlecloud.GoogleCredentialsFromJson;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.google.inject.Module;

public enum Provider {
	
	AmazonWebService("aws-ec2", "aws", Region.AWS_UsEast1), 
	GoogleCloudProvider("google-compute-engine", "gce", Region.GCP_UsEast1b),
	MicrosoftAzure("azurecompute", "azu", Region.AWS_UsEast1);
	
	private final String api;
	private final String code;
	private final Region region;
	private final Properties properties = new Properties();
	
	private Provider(String api, String code, Region region) {
		// TODO Auto-generated constructor stub
		this.api = api;
		this.code = code;
		this.region = region;
	}
	
	private String getIdentity() throws IOException{
		
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
	
	private String getGcpCredentialFromJsonKey(String filename){
		
		try {
            String fileContents = Files.toString(new File(filename), UTF_8);
            Supplier<Credentials> credentialSupplier = new GoogleCredentialsFromJson(fileContents);
            String credential = credentialSupplier.get().credential;
            return credential;
        } catch (IOException e) {
            System.err.println("Exception reading private key from '%s': " + filename);
            e.printStackTrace();
            System.exit(1);
            return null;
        }
		
	}
	
	private String getCredential() throws IOException{
		
		this.properties.load(Provider.class.getClassLoader().getResourceAsStream("cloudprovider.properties"));
		String credentials = "";
		
		switch (this) {
			case AmazonWebService:
				credentials = this.properties.getProperty("amazon.credential");
				break;
			case GoogleCloudProvider:
				credentials = this.getGcpCredentialFromJsonKey(this.properties.getProperty("googlecloud.credential"));
				break;
				
			default:
				credentials = "";
		}
		System.out.println(">> Get credential..");
		return credentials;
		
	}
	
	private Properties getOverrides(){
		
		Properties overrides = new Properties();
		
		long scriptTimeout = TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS);
		overrides.setProperty(ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE, scriptTimeout + "");
		
		if(this.equals(Provider.AmazonWebService)){
			overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY, "owner-id=137112412989;state=available;image-type=machine;root-device-type=ebs");
			overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_CC_AMI_QUERY, "");
		}
		
		
		System.out.println(">> Overrides Properties set..");
		return overrides;
	}
	
	public ComputeService getComputeService() throws Exception{
		Iterable<Module> modules = ImmutableSet.<Module> of( new SshjSshClientModule(),
				                                             new SLF4JLoggingModule());
		
		ContextBuilder builder = ContextBuilder.newBuilder(this.getApi())
			.credentials(this.getIdentity(), this.getCredential())
			.overrides(this.getOverrides())
			.modules(modules);
		
		System.out.printf(">> initializing %s%n", builder.getApiMetadata());
		ComputeServiceContext computeServiceContext = builder.buildView(ComputeServiceContext.class);
		return computeServiceContext.getComputeService();
	}
	
	public String getApi() {
		return api;
	}


	public String getCode() {
		return code;
	}

	public Region getRegion() {
		return region;
	}
	
}
