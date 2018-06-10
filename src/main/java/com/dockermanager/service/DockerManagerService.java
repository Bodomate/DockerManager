package com.dockermanager.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.dockermanager.domain.LocalContainer;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;

public class DockerManagerService {
	private DockerClient docker;
	
	List<Container> containers;
	ArrayList<LocalContainer> localContainers = new ArrayList<>();

	public ArrayList<LocalContainer> getLocalContainers() {
		return localContainers;
	}

	public String connect(String url) throws DockerException, InterruptedException {
		docker = DefaultDockerClient.builder()
		        .uri(URI.create("http://"+url))
		        .build();
		
		containers = docker.listContainers(ListContainersParam.allContainers());
        localContainers.clear();
		for (Container container : containers) {
			String names = "";
			for (String name : container.names()) names = names + ";" + name;
			LocalContainer cont = new LocalContainer(container.id(), container.image(), names, container.status());
			localContainers.add(cont);
		}
		return docker.getHost();
	}

//	public void startContainer(String id) {
//		// TODO Auto-generated method stub
//		
//	}

}
