package io.github.albertopires.mjc;

import br.com.movbr.jvm.model.JdkVersion;

public class ServerConfiguration {
	private String host;
	private String port;
	private String user;
	private String password;
	private Boolean authenticate;
	private JdkVersion jdkVersion;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getAuthenticate() {
		return authenticate;
	}

	public void setAuthenticate(Boolean authenticate) {
		this.authenticate = authenticate;
	}

	public JdkVersion getJdkVersion() {
		return jdkVersion;
	}

	public void setJdkVersion(JdkVersion jdkVersion) {
		this.jdkVersion = jdkVersion;
	}
}