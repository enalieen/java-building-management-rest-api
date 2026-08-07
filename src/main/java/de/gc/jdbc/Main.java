package de.gc.jdbc;
import server.Server;

public class Main {
	// docker containers need to be built first:
	// docker-compose up --build
	// The -d flag (from --detach) in Docker means to run the container in the background (detached) mode
	public static void main(String[] args) {
		Server.start();
		// Server.stop();
	}
}
