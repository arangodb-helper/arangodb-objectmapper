package org.arangodb.objectmapper;

//////////////////////////////////////////////////////////////////////////////////////////
//
//Object mapper for ArangoDB by triAGENS GmbH Cologne.
//
//Copyright triAGENS GmbH Cologne.
//
//////////////////////////////////////////////////////////////////////////////////////////

public class Version {
	
	private String version;
	
	private String server;
	
	private String license;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

}
