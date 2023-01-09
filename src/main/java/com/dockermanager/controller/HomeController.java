package com.dockermanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.dockermanager.service.DockerManagerService;
import com.spotify.docker.client.exceptions.DockerException;

@Controller
public class HomeController {
	
	DockerManagerService dockerService = new DockerManagerService();

	@RequestMapping(value={"/", "/index"})
	public String home() {
		return "index";
	}
	
	@RequestMapping("/connect")
	public String connect() {
		return "connect";
	}
	
	@RequestMapping("/containers")
	public String containers(Model model) throws DockerException, InterruptedException {
		String message = "";
		if (dockerService.getHost() != "") {
			if (dockerService.getLocalContainers().isEmpty()) {
				message = "No containers to show.";			
			} else {
				model.addAttribute("containers", dockerService.getLocalContainers());
				model.addAttribute("count",dockerService.getLocalContainers().size());
			}
		} else {
			message = "Please connect to a running docker on Connect page.";
		}
		model.addAttribute("message", message);
		return "containers";
	}

	@RequestMapping(value="/create")
	public String create(Model model) {
		if (dockerService.getHost() == "") {
			model.addAttribute("message", "Please connect to a running docker on Connect page.");
		}
		return "create";
	}
	
	@RequestMapping("/contact")
	public String contact() {
		return "contact";
	}
	
	@RequestMapping(value="/connect", method=RequestMethod.POST, params="action=Connect")
	public String addURL(@RequestParam("url") String url, Model model) {
			try {
				model.addAttribute("message",dockerService.connect(url)+" connected successfully.");
			} catch (DockerException | InterruptedException e) {
				model.addAttribute("message", "Unable to connect to the host: (" + e.getMessage() + ")");
			}
		return "connect";
	}
	
	@RequestMapping(value="/connect", method=RequestMethod.POST, params="action=Disconnect")
	public String disconnect(Model model) {
		if (dockerService.getHost() == "") {
				model.addAttribute("message","No Docker host connected.");
			} else {
				model.addAttribute("message", dockerService.closeConnection());
			}
		return "connect";
	}
	
	@RequestMapping(value="/containers", method=RequestMethod.POST, params="action=Start")
	public String containerStart(@RequestParam("id") String id, Model model) throws DockerException, InterruptedException {
		try {
			model.addAttribute("message", dockerService.startContainer(id));
		} catch (DockerException | InterruptedException e) {
			e.printStackTrace();
		}
		model.addAttribute("containers", dockerService.getLocalContainers());
		model.addAttribute("count",dockerService.getLocalContainers().size());
		model.addAttribute("id",id);
		return "containers";
	}
	
	@RequestMapping(value="/containers", method=RequestMethod.POST, params="action=Stop")
	public String containerStop(@RequestParam("id") String id, Model model) throws DockerException, InterruptedException {
		try {
			model.addAttribute("message", dockerService.stopContainer(id));
		} catch (DockerException | InterruptedException e) {
			e.printStackTrace();
		}
		model.addAttribute("containers", dockerService.getLocalContainers());
		model.addAttribute("count",dockerService.getLocalContainers().size());
		model.addAttribute("id",id);
		return "containers";
	}
	
	@RequestMapping(value="/containers", method=RequestMethod.POST, params="action=Remove")
	public String containerRemove(@RequestParam("id") String id, Model model) throws DockerException, InterruptedException {
		model.addAttribute("message", dockerService.removeContainer(id));
		model.addAttribute("containers", dockerService.getLocalContainers());
		model.addAttribute("count",dockerService.getLocalContainers().size());
		return "containers";
	}
	
	@RequestMapping(value="/containers", method=RequestMethod.POST, params="action=Info")
	public String containerInfo(@RequestParam("id") String id, Model model) throws DockerException, InterruptedException {
		model.addAttribute("containers", dockerService.getLocalContainers());
		model.addAttribute("message", dockerService.containerInfo(id));
		model.addAttribute("count",dockerService.getLocalContainers().size());
		model.addAttribute("id",id);
		return "containers";
	}
	
	@RequestMapping(value="/containers", method=RequestMethod.POST, params="action=Inspect")
	public String containerInspect(@RequestParam("id") String id, Model model) throws DockerException, InterruptedException {
		model.addAttribute("containers", dockerService.getLocalContainers());
		model.addAttribute("message", dockerService.containerInspect(id));
		model.addAttribute("count",dockerService.getLocalContainers().size());
		model.addAttribute("id",id);
		return "containers";
	}
	
	@RequestMapping(value="/containers", method=RequestMethod.POST, params="action=Top processes")
	public String containerProcesses(@RequestParam("id") String id, Model model) throws DockerException, InterruptedException{
		model.addAttribute("containers", dockerService.getLocalContainers());
		model.addAttribute("message", dockerService.containerProcesses(id));
		model.addAttribute("count",dockerService.getLocalContainers().size());
		model.addAttribute("id",id);
		return "containers";
	}
	
	@RequestMapping(value="/containers", method=RequestMethod.POST, params="action=Logs")
	public String containerLogs(@RequestParam("id") String id, Model model) throws DockerException, InterruptedException {
		model.addAttribute("containers", dockerService.getLocalContainers());
		model.addAttribute("message", dockerService.containerLogs(id));
		model.addAttribute("count",dockerService.getLocalContainers().size());
		model.addAttribute("id",id);
		return "containers";
	}
			
	@RequestMapping(value="/create", method=RequestMethod.POST, params="action=Create")
	public String createContainer(@RequestParam("image") String image, @RequestParam("ports") String ports,
			@RequestParam("cmd") String cmd, Model model) throws DockerException, InterruptedException {
		if (image == "" | ports == "" | cmd == "") {
			model.addAttribute("message", "Please fill all input texts!");
		} else {
			if (dockerService.getHost() == "") {
				model.addAttribute("message", "Docker server not connected! Please connect to a Docker server!");
			} else {
				model.addAttribute("message", dockerService.createContainer(image, ports, cmd) + " has been created successfully.");
			}
		}
		return "create";
	}
}
