package com.dockermanager.service;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.dockermanager.domain.LocalContainer;
import com.google.common.collect.ImmutableList;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.ListContainersParam;
import com.spotify.docker.client.DockerClient.LogsParam;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.TopResults;

public class DockerManagerService {
	private DockerClient docker;
	
	List<Container> containers;
	ArrayList<LocalContainer> localContainers = new ArrayList<>();

	public ArrayList<LocalContainer> getLocalContainers() throws DockerException, InterruptedException {
		if (containers != null) {
			fillLists();
		}
		return localContainers;
	}

	public String connect(String url) throws DockerException, InterruptedException {
		docker = DefaultDockerClient.builder()
		        .uri(URI.create("http://"+url))
		        .build();
		fillLists();
		return docker.getHost();
	}

	private void fillLists() throws DockerException, InterruptedException {
		containers = docker.listContainers(ListContainersParam.allContainers());
        localContainers.clear();
		for (Container container : containers) {
			String names = "";
			for (String name : container.names()) names = names + name + "; ";
			LocalContainer cont = new LocalContainer(container.id(), container.image(), names, container.status());
			localContainers.add(cont);
		}
	}

	public String startContainer(String id) throws DockerException, InterruptedException {
		String started = "Image is already running!";
		for (Container container : containers) {
			if (container.id().equals(id) && container.status().substring(0, 4).equalsIgnoreCase("exit")) {
				docker.startContainer(id);
				started = container.image()+ " has been started.";
			}
		}
		return started;
	}

	public String stopContainer(String id) throws DockerException, InterruptedException {
		String stopped = "Image has already been stopped!";
		for (Container container : containers) {
			if (container.id().equals(id) && container.status().substring(0, 2).equalsIgnoreCase("up")) {
				docker.stopContainer(id, 0);
				stopped = container.image()+ " has been stopped.";
			}
		}
		return stopped;
	}
	
	public String containerInfo(String id) {
		LocalContainer cont = null;
		for (LocalContainer container : localContainers) {
			if (container.getId().equals(id)) {
				cont = container;
			}
		}
		return "ID: " + cont.getId() + "\nImage: " + cont.getImage() +  "\nNames: " + cont.getNames() + "\nStatus: " + cont.getStatus() + ".";
	}
	
	public String containerInspect(String id) throws DockerException, InterruptedException {
		final ContainerInfo inspect = docker.inspectContainer(id);
		return inspect.toString();
	}

	public String containerProcesses(String id) throws DockerException, InterruptedException {
		String ps_args = "aux";
		String message = "The container is not running!";
		for (Container container : containers) {
			if (container.id().equals(id) && container.status().substring(0, 2).equalsIgnoreCase("up")) {
				final TopResults topResults = docker.topContainer(id, ps_args);
				message = topResults.titles().toString() + "\n";
				for (ImmutableList<String> list : topResults.processes()){
					message += list.toString() + "\n";
				}
			}
		}
		return message;
	}

	public String containerLogs(String id) throws DockerException, InterruptedException {
		String logs = "Logs not found!";
		try (LogStream stream = docker.logs(id, LogsParam.stdout(), LogsParam.stderr())) {
			logs = stream.readFully();
		}
		return logs;
	}

}
