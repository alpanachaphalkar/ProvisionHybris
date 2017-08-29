package com.hybris.computeservice;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;

import com.hybris.provider.Cpu;
import com.hybris.provider.Region;

public interface CloudServiceAction {
	
	// creates node or an instance based on gropuname
	NodeMetadata createNode(ComputeService computeService, OsFamily os, Cpu cpu, int ramSize, double diskSize,
					Region region, String groupName);
	
	void executeCommand(ComputeService computeService, NodeMetadata node, String command);
	
	void executeScript(ComputeService computeService, NodeMetadata node, String pathToScript);
}
