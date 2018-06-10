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
	public String containers(Model model) {
		model.addAttribute("containers", dockerService.getLocalContainers());
		model.addAttribute("count",dockerService.getLocalContainers().size());
		return "containers";
	}
	
	@RequestMapping("/contact")
	public String contact() {
		return "contact";
	}
	
	@RequestMapping(value="/connectSuccess", method=RequestMethod.POST)
	public String addURL(@RequestParam("url") String url, Model model) {
		
			try {
				model.addAttribute("success",dockerService.connect(url)+" connected successfully.");
			} catch (DockerException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return "connect";
	}
	
	@RequestMapping(value="/containers", method=RequestMethod.POST, params="action=start")
	public String containerStart(@RequestParam("id") String id, Model model) {
		
//		dockerService.startContainer(id);
		model.addAttribute("containers", dockerService.getLocalContainers());
		model.addAttribute("count",dockerService.getLocalContainers().size());
		return "containers";
	}
}
