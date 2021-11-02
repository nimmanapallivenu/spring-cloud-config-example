package com.zenika.controller.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EnvironmentDto {

	private String name;

	private String[] profiles = new String[0];

	private String label;

	private List<PropertySourceDto> propertySources = new ArrayList<>();

	private String version;

	private String state;

	public EnvironmentDto(String name, String[] profiles, String label, List<PropertySourceDto> propertySources,
			String version, String state) {
		super();
		this.name = name;
		this.profiles = profiles;
		this.label = label;
		this.propertySources = propertySources;
		this.version = version;
		this.state = state;
	}

	public EnvironmentDto() {
	}

	@JsonCreator
	public EnvironmentDto(@JsonProperty("name") String name, @JsonProperty("profiles") String[] profiles,
			@JsonProperty("label") String label, @JsonProperty("version") String version,
			@JsonProperty("state") String state) {
		super();
		this.name = name;
		this.profiles = profiles;
		this.label = label;
		this.version = version;
		this.state = state;
	}

	public void add(PropertySourceDto propertySource) {
		this.propertySources.add(propertySource);
	}

	public void addFirst(PropertySourceDto propertySource) {
		this.propertySources.add(0, propertySource);
	}

	public List<PropertySourceDto> getPropertySources() {
		return propertySources;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String[] getProfiles() {
		return profiles;
	}

	public void setProfiles(String[] profiles) {
		this.profiles = profiles;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "Environment [name=" + name + ", profiles=" + Arrays.asList(profiles) + ", label=" + label
				+ ", propertySources=" + propertySources + ", version=" + version + ", state=" + state + "]";
	}

}
