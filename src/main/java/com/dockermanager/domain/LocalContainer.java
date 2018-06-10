package com.dockermanager.domain;

public class LocalContainer {
	private String id;
	private String image;
	private String names;
	private String status;
	
	public LocalContainer(String id, String image, String names, String status) {
		this.id = id;
		this.image = image;
		this.names = names;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return image + ", status: " + status + "]";
	}
	
}
