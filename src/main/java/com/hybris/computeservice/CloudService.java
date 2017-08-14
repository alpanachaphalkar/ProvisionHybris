package com.hybris.computeservice;

import static com.google.common.base.Charsets.UTF_8;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jclouds.ContextBuilder;
import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.aws.ec2.reference.AWSEC2Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.config.ComputeServiceProperties;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.googlecloud.GoogleCredentialsFromJson;
import org.jclouds.googlecomputeengine.compute.options.GoogleComputeEngineTemplateOptions;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.sshj.config.SshjSshClientModule;

import com.google.common.base.Charsets;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import com.google.inject.Module;
import com.hybris.provider.Cpu;
import com.hybris.provider.Provider;
import com.hybris.provider.Region;

public class CloudService implements CloudServiceAction{
	
	
	private final Provider provider;
	private final Properties properties = new Properties();
	
	public CloudService(Provider provider){
		// TODO Auto-generated constructor stub
		this.provider = provider;
	}
	
	public Provider getProvider(){
		return this.provider;
	}
	
	public Provider getProvider(ComputeService compute){
		
		String providerApiCode = compute.getContext().unwrap().getId();
		
		if(providerApiCode.equals(Provider.AmazonWebService.getApi())){
			return Provider.AmazonWebService;
		}else if(providerApiCode.equals(Provider.GoogleCloudProvider)){
			return Provider.GoogleCloudProvider;
		}else{
			return null;
		}
		 
	}
	
	private static LoginCredentials getLoginForProvision(){
		
		try{
			
			String user = "ubuntu";
			//String privateKey = Files.toString(new File("C:\\cygwin64\\home\\D066624\\.ssh\\id_rsa"), Charsets.UTF_8);
			String privateKey = Files.toString(new 
					File("C:\\Users\\D066624\\Google Drive\\Rough\\Eclipse\\ProvisionHybris\\src\\main\\resources\\id_rsa"), Charsets.UTF_8);
			return LoginCredentials.builder().user(user).privateKey(privateKey).build();
		
		} catch (Exception e) {
			System.err.println("Error Reading Private Key: " + e.getMessage());
			System.exit(1);
		}
		
		return null;
		
	}
	
	public String getKeyToSsh(){
		
		String pathToPublicKey = "C:\\Users\\D066624\\Google Drive\\Rough\\Eclipse\\ProvisionHybris\\src\\main\\resources\\id_rsa.pub";
 	    String pathToPrivateKey = "C:\\Users\\D066624\\Google Drive\\Rough\\Eclipse\\ProvisionHybris\\src\\main\\resources\\id_rsa";
 	    String keyToSsh = null;
 	    
 	    switch (this.provider) {
		case AmazonWebService:
			keyToSsh = pathToPrivateKey;
			break;
		case GoogleCloudProvider:
			keyToSsh = pathToPublicKey;
			break;
		default:
			break;
		}
 	    
 	    return keyToSsh;
	}	
	
	public Region getRegion(){
		Region region = null;
		
		switch (this.provider) {
		case AmazonWebService:
			region = Region.AWS_UsEast1;
			break;
		
		case GoogleCloudProvider:
			region = Region.GCP_UsEast1b;
			break;
			
		default:
			break;
		}
		
		return region;
	}
	
	public String getIdentity() throws IOException{
		
		this.properties.load(Provider.class.getClassLoader().getResourceAsStream("cloudprovider.properties"));
		String identity = "";
		
		switch (this.provider) {
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
		
		switch (this.provider) {
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
	
	public Properties getOverrides(){
		
		Properties overrides = new Properties();
		
		if(this.provider.equals(Provider.AmazonWebService)){
			overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY, "owner-id=137112412989;state=available;image-type=machine;root-device-type=ebs");
		}
		
		long scriptTimeout = TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS);
		overrides.setProperty(ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE, scriptTimeout + "");
		System.out.println(">> Overrides Properties set..");
		return overrides;
	}
	
	public ComputeService initComputeService() throws Exception{
		
		Iterable<Module> modules = ImmutableSet.<Module> of( new SshjSshClientModule());
		
		ContextBuilder builder = ContextBuilder.newBuilder(this.provider.getApi())
			.credentials(this.getIdentity(), this.getCredential())
			.overrides(this.getOverrides())
			.modules(modules);
		
		System.out.printf(">> initializing %s%n", builder.getApiMetadata());
		ComputeServiceContext computeServiceContext = builder.buildView(ComputeServiceContext.class);
		return computeServiceContext.getComputeService();
		
	}
	
	public NodeMetadata createNode(ComputeService computeService, OsFamily os, Cpu cpu, int ramSize, double diskSize,
							Region region, String host) {
		// TODO Auto-generated method stub
		
		String keyName = "alpanachaphalkar";
		String pathToKey = "C:\\cygwin64\\home\\D066624\\.ssh\\id_rsa.pub";
		//String pathToKey = "C:\\Users\\D066624\\Google Drive\\Rough\\Eclipse\\ProvisionHybris\\src\\main\\resources\\id_rsa.pub";
		
		try {
			
			System.out.printf(">> adding node to group %s%n", host);
			System.out.println();
			
			TemplateBuilder templateBuilder = computeService.templateBuilder()
														.os64Bit(cpu.getType())
														.minCores(cpu.getCores())
														.minRam(ramSize)
														.minDisk(diskSize)
														.osFamily(os)
														.locationId(region.getID());
			Template template = templateBuilder.build();
			TemplateOptions templateOptions = template.getOptions();
			
			switch (this.provider) {
			
			case AmazonWebService:
				
				templateOptions.as(AWSEC2TemplateOptions.class).userMetadata("Name", host);

				System.out.printf(">> Importing public key %s%n", keyName);
				System.out.println();
				
				templateOptions.as(AWSEC2TemplateOptions.class).keyPair(keyName);
				templateOptions.as(AWSEC2TemplateOptions.class).subnetId("subnet-13d3fb5b").securityGroups("sg-8651acf6");
				break;
			
			case GoogleCloudProvider:
				
				// To use project-wide SSH keys
				templateOptions.as(GoogleComputeEngineTemplateOptions.class).autoCreateKeyPair(false);
				
				// Imports local ssh keys to node
				String GcpPublicKey = Files.toString(new File(pathToKey), Charsets.UTF_8);
				System.out.printf(">> Importing public key %s%n", keyName);
				System.out.println();
				templateOptions.as(GoogleComputeEngineTemplateOptions.class).userMetadata("ssh-keys", GcpPublicKey);
				
				ArrayList<String> tags = new ArrayList<String>();
				tags.add("demo-hybris-firewall");
				templateOptions.as(GoogleComputeEngineTemplateOptions.class).tags(tags);
				
				break;
				
			}
			
			System.out.println(">> creation of " + host + " instance is beginning.. ");
			NodeMetadata node = Iterables.getOnlyElement(computeService.createNodesInGroup(host, 1, template));
			
			/*System.out.println("<< node: " + node.getName() + "  with ID: " + node.getId() + "  with Private IP: " + node.getPrivateAddresses()
			+ "  and Public IP: " + node.getPublicAddresses() + "  is created.");*/
			
			return node;
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		
		
	}

	public void executeCommand(ComputeService computeService, NodeMetadata node, String command) {
		// TODO Auto-generated method stub
		LoginCredentials login = getLoginForProvision();
	    
		//execute command in groups
		   /*try {
		     Map<? extends NodeMetadata, ExecResponse> responses = 
		    		  computeService.runScriptOnNodesMatching(NodePredicates.inGroup(groupName), 
		    				  Statements.exec(command), 
		    				  TemplateOptions.Builder.runScript(command).overrideLoginCredentials(login));
		    	
		    	for (Map.Entry<? extends NodeMetadata, ExecResponse> response : responses.entrySet()) {
			        System.out.printf("<< node %s: %s%n", response.getKey().getId(), 
			          Iterables.concat(response.getKey().getPrivateAddresses(), response.getKey().getPublicAddresses()));
			        System.out.printf("<<     %s%n", response.getValue());
			    }
		    
		    }
		    catch (RunScriptOnNodesException e) {
		      e.printStackTrace();
		    }*/
		
	      ExecResponse responses = computeService.runScriptOnNode(node.getId(), Statements.exec(command), 
	    		  												TemplateOptions.Builder.runScript(command).overrideLoginCredentials(login));
	      System.out.println(responses.getOutput());
	      
	}

	public void executeScript(ComputeService computeService, NodeMetadata node, String pathToScript) {
		// TODO Auto-generated method stub
		
		File script = new File(pathToScript);
	    LoginCredentials login = getLoginForProvision();
	    
	    /*try {
		      Map<? extends NodeMetadata, ExecResponse> responses = 
		      computeService.runScriptOnNodesMatching(NodePredicates.inGroup(groupName), 
		      Files.toString(script, Charsets.UTF_8), 
		      TemplateOptions.Builder.runScript(Files.toString(script, Charsets.UTF_8)).overrideLoginCredentials(login));
		      
	      for (Map.Entry<? extends NodeMetadata, ExecResponse> response : responses.entrySet()) {
	        System.out.printf("<< node %s: %s%n", response.getKey().getId(), Iterables.concat(response.getKey().getPrivateAddresses(), response.getKey().getPublicAddresses()));
	        System.out.printf("<<     %s%n", response.getValue());
	      }
	      
	    }
	    catch (RunScriptOnNodesException e) {
	      e.printStackTrace();
	    }
	    catch (IOException e) {
	      e.printStackTrace();
	    }*/
	    
	    try {
			ExecResponse responses = computeService.runScriptOnNode(node.getId(), Files.toString(script, Charsets.UTF_8), 
										TemplateOptions.Builder.runScript(Files.toString(script, Charsets.UTF_8)).overrideLoginCredentials(login));
	    
			System.out.println(responses.getOutput());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
		
}
