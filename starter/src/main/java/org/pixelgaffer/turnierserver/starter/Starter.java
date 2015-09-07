package org.pixelgaffer.turnierserver.starter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class Starter {
	
	public static void main(String args[]) {
		Starter starter = new Starter(args);
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			starter.terminate();
		}));
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			loop: while (true) {
				switch(br.readLine()) {
					case "start":
						starter.start();
						break;
					case "stop":
						starter.stop();
						break;
					case "terminate":
						starter.terminate();
						break;
					case "restart":
						starter.restart();
						break;
					case "exit":
						break loop;
					default:
						System.err.println("Command not found!");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		starter.stop();
		
		try {
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		System.exit(0);
	}
	
	
	
	
	private Process process;
	private boolean started;
	private String[] command;
	
	public Starter(String... command) {
		this.command = command;
	}
	
	public void start() {
		if (started) return;
		try {
			process = new ProcessBuilder().command(command).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		started = true;
	}
	
	public void stop() {
		if (!started) return;
		process.destroy();
		try {
			process.waitFor(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(process.isAlive()) {
			terminate();
		}
		started = false;
	}
	
	public void terminate() {
		if (!started) return;
		process.destroyForcibly();
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		started = false;
	}
	
	public void restart() {
		stop();
		start();
	}
	
}
