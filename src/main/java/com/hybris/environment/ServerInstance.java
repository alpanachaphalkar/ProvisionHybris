package com.hybris.environment;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;

import com.hybris.computeservice.CloudService;
import com.hybris.provider.Cpu;
import com.hybris.provider.DiskSize;
import com.hybris.provider.RamSize;
import com.hybris.provider.Region;

public class ServerInstance {

	private CloudService service;
	private OsFamily os;
	private Cpu cpu;
	private RamSize ramsize;
	private DiskSize disksize;
	private Region region;
	
	private static final String SERVER_DOMAIN=".hybrishosting.com";
	
	public ServerInstance(CloudService service, OsFamily os, Cpu cpu, RamSize ramSize, DiskSize diskSize, Region region) {
		// TODO Auto-generated constructor stub
		this.setService(service);
		this.setOs(os);
		this.setCpu(cpu);
		this.setRamsize(ramSize);
		this.setDisksize(diskSize);
		this.setRegion(region);
	}
	
	public NodeMetadata create(String hostname) throws Exception{
		
		ComputeService computeService = this.service.getProvider().initComputeService();
		int ramSize = this.ramsize.getSize(this.service.getProvider());
		String host = hostname.replace(SERVER_DOMAIN, "");
		NodeMetadata node = service.createNode(computeService, this.os, this.cpu, ramSize, this.disksize, this.region, host);
		System.out.println("<<	Server " + host + " is created with following details: ");
		System.out.println("	Name: " + node.getHostname());
		System.out.println("	ID: " + node.getId());
		System.out.println("	Private IP: " + node.getPrivateAddresses());
		System.out.println("	Public IP: " + node.getPublicAddresses());
		service.executeCommand(computeService, node, "hostnamectl set-hostname " + hostname);
		service.executeCommand(computeService, node, "echo \"127.0.0.1 `hostname`\" >>/etc/hosts");
		System.out.println("<<  Server "+ host +" with hostname " + hostname + " is set.");
		System.out.println();
		computeService.getContext().close();
		return node;
	}
	
	public CloudService getService() {
		return service;
	}

	public void setService(CloudService service) {
		this.service = service;
	}

	public OsFamily getOs() {
		return os;
	}

	public void setOs(OsFamily os) {
		this.os = os;
	}

	public Cpu getCpu() {
		return cpu;
	}

	public void setCpu(Cpu cpu) {
		this.cpu = cpu;
	}

	public RamSize getRamsize() {
		return ramsize;
	}

	public void setRamsize(RamSize ramsize) {
		this.ramsize = ramsize;
	}

	public DiskSize getDisksize() {
		return disksize;
	}

	public void setDisksize(DiskSize disksize) {
		this.disksize = disksize;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

}
