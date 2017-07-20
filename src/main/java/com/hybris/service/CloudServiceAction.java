package com.hybris.service;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.OsFamily;

import com.hybris.provider.specifications.*;

public interface CloudServiceAction {
	
	// creates node or an instance based on gropuname
	void createNode(ComputeService computeService, OsFamily os, Cpu cpu, RamSize ramSize, DiskSize diskSize,
					Region region, String groupName, String keyName, String pathToKey);
	
	void executeCommand(ComputeService computeService, String groupName, String command);
	
	void executeScript(ComputeService computeService, String groupName, String pathToScript);
}
