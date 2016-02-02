package config;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Server{
	private ObjectInputStream inputStream = null;
	private ObjectOutputStream outputStream = null;
	private String name, ip;
	private int port;
	
	@Override
	public String toString() {
		return "Server " + getName() +
				"@" + getIp() + ":" +
				getPort();
	};
	
	public ObjectInputStream getInput() {
		return inputStream;
	}

	public void setInput(ObjectInputStream input) {
		this.inputStream = input;
	}

	public ObjectOutputStream getOutput() {
		return outputStream;
	}

	public void setOutput(ObjectOutputStream output) {
		this.outputStream = output;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
}