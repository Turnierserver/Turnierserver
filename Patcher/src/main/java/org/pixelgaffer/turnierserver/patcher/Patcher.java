package org.pixelgaffer.turnierserver.patcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class Patcher implements Runnable {
	
	public static final File rootDir = new File("..");
	public static final File configDir = new File(rootDir, "Turnierserver-Config");
	public static final File turnierserverDir = new File(rootDir, "Turnierserver");
	public static final File buildDir = new File(turnierserverDir, System.getProperty("turnierserver.patcher.build"));
	public static final File frontendDir = new File(turnierserverDir, System.getProperty("turnierserver.patcher.frontend"));
	public static final File compilerDir = new File(turnierserverDir, System.getProperty("turnierserver.patcher.compiler"));
	public static final File patcherDir = new File(turnierserverDir, System.getProperty("turnierserver.patcher"));
	public static final File networkingDir = new File(turnierserverDir, System.getProperty("turnierserver.patcher.networking"));
	public static final File gamelogicDir = new File(turnierserverDir, System.getProperty("turnierserver.patcher.gamelogic"));
	public static final File workerDir = new File(turnierserverDir, System.getProperty("turnierserver.patcher.worker"));
	public static final File backendDir = new File(turnierserverDir, System.getProperty("turnierserver.patcher.backend"));
	
	public static Process exe(File dir, String...command) throws IOException {
		ProcessBuilder builder = new ProcessBuilder(command);
		if(dir != null) {
			builder.directory(dir);
			builder.inheritIO();
		}
		return builder.start();
	}
	
	public static Process git(File dir, String...command) throws IOException {
		ArrayList<String> commands = new ArrayList<>();
		commands.add("git");
		commands.addAll(Arrays.asList(command));
		return exe(dir, commands.toArray(new String[commands.size()]));
	}
	
	public static void main(String[] args) throws IOException {
		if(!configDir.exists()) {
			git(rootDir, "clone", "https://" + System.getProperty("turnierserver.patcher.github.username") + ":" + System.getProperty("turnierserver.patcher.github.password") + "github.com/nicosio2/Turnierserver-Config.git");
		}
				
		Properties p = new Properties(System.getProperties());
		p.load(new FileInputStream(new File(configDir, "/turnierserver.prop")));
		System.setProperties(p);
		
		new Thread(new Patcher(Boolean.parseBoolean(args[0]), Boolean.parseBoolean(args[1]), Boolean.parseBoolean(args[2]), Boolean.parseBoolean(args[3]))).start();
	}
	
	private GitHub github;
	private GHRepository turnierserver;
	private GHRepository config;
	private GHCommit lastTurnierserverCommit;
	private GHCommit lastConfigCommit;
	
	private Process worker;
	private Process backend;
	private Process frontend;
	
	private boolean release;
	
	public Patcher(boolean worker, boolean backend, boolean frontend, boolean release) throws IOException {
		
		this.release = release;
		
		pull(turnierserverDir, turnierserver);
		pull(configDir, config);
		
		compile(networkingDir);
		compile(compilerDir);
		compile(workerDir);
		compile(gamelogicDir);
		compile(backendDir);
		
		if(worker) {
			restartWorker();
		}
		if(backend) {
			restartBackend();
		}
		if(frontend) {
			restartFrontend();
		}
		
		github = GitHub.connectUsingPassword(System.getProperty("turnierserver.patcher.github.username"), System.getProperty("turnierserver.patcher.github.password"));
		turnierserver = github.getRepository(System.getProperty("turnierserver.patcher.github.repos.turnierserver"));
		config = github.getRepository(System.getProperty("turnierserver.patcher.github.repos.turnierserver-config"));
		
		lastTurnierserverCommit = release ? turnierserver.getCommit(turnierserver.listReleases().asList().get(0).getName()) : turnierserver.listCommits().asList().get(0);
		lastConfigCommit = release ? config.getCommit(config.listReleases().asList().get(0).getName()) : config.listCommits().asList().get(0);
	}

	public void run() {
		double refreshrate = Double.parseDouble(System.getProperty("turnierserver.patcher.refreshrate"));
		
		while(true) {			
			try {
				List<String> filesModified = new ArrayList<>();

				for(GHCommit commit : getCommitsSince(lastConfigCommit, config)) {
					for(GHCommit.File file : commit.getFiles()) {
						filesModified.add(file.getFileName());
					}
				}
				
				boolean pulled = false;
				
				if(filesModified.contains("turnierserver.prop")) {
					if(!pulled) {
						pull(configDir, config);
					}
					restart();
				}
				if(filesModified.contains("_cfg.py")) {
					if(!pulled) {
						pull(configDir, config);
					}
					FileUtils.copyFileToDirectory(new File(configDir, "Frontend/_cfg.py"), new File(turnierserverDir, "Frontend"));
					restartFrontend();
				}
				
				lastConfigCommit = release ? config.getCommit(config.listReleases().asList().get(0).getName()) : config.listCommits().asList().get(0);
				
				List<String> directoriesModified = new ArrayList<>();
				for(GHCommit commit : getCommitsSince(lastTurnierserverCommit, turnierserver)) {
					for(GHCommit.File file : commit.getFiles()) {
						directoriesModified.add(file.getFileName().split("/")[4]);
					}
				}
				
				pulled = false;
								
				if(directoriesModified.contains(patcherDir.getName())) {
					if(!pulled) {
						pull(turnierserverDir, turnierserver);
					}
					restart();
				}
				
				if(frontend != null && directoriesModified.contains(frontendDir.getName())) {
					if(!pulled) {
						pull(turnierserverDir, turnierserver);
					}
					restartFrontend();
				}
				
				List<File> alreadyCompiled = new ArrayList<>();
				List<File> dependencyChainBackend = new ArrayList<>(Arrays.asList(networkingDir, gamelogicDir, backendDir));
				List<File> dependencyChainWorker = new ArrayList<>(Arrays.asList(networkingDir, compilerDir, workerDir));
				
				if(worker != null && compileDependencyChain(dependencyChainWorker, alreadyCompiled, directoriesModified, pulled)) {
					restartWorker();
				}
				if(backend != null && compileDependencyChain(dependencyChainBackend, alreadyCompiled, directoriesModified, pulled)) {
					pulled = true;
					restartBackend();
				}
				
				lastTurnierserverCommit = release ? turnierserver.getCommit(turnierserver.listReleases().asList().get(0).getName()) : turnierserver.listCommits().asList().get(0);
				
				Thread.sleep((long) (refreshrate * 1000));
				
			} catch (IOException | InterruptedException e1) {
				e1.printStackTrace();
				System.exit(1);
			}
		}
		
	}
	
	private boolean compileDependencyChain(List<File> dependencyChain, List<File> alreadyCompiled, List<String> toCompile, boolean alreadyPulled) throws IOException {
		boolean compile = false;
		for(File dependency : dependencyChain) {
			compile = compile || toCompile.contains(dependency.getName());
			if(compile) {
				if(!alreadyCompiled.contains(dependency)) {
					if(!alreadyPulled) {
						pull(turnierserverDir, turnierserver);
						alreadyPulled = true;
					}
					compile(dependency);
					alreadyCompiled.add(dependency);
				}
			}
		}
		return compile;
	}
	
	private void compile(File target) throws IOException {
		exe(target, "mvn", "clean", "package", "install", "dependency:copy-dependencies");
		exe(target, "cp", "target/.*jar", buildDir.getAbsolutePath());
		exe(target, "cp", "target/dependency/*", buildDir.getAbsolutePath());
	}
	
	private void restartWorker() throws IOException {
		stop(worker);
		worker = exe(buildDir, "worker.sh", "../../Turnierserver-Config/turnierserver.prop");
	}
	
	private void restartBackend() throws IOException {
		stop(backend);
		backend = exe(buildDir, "backend.sh", "../../Turnierserver-Config/turnierserver.prop");
	}
	
	private void restartFrontend() throws IOException {
		stop(frontend);
		frontend = exe(frontendDir, "python3", "app.py");
	}
	
	private void stop(Process process) {
		if(process != null) {
			process.destroy();
			try {
				process.waitFor();
			} catch(InterruptedException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
		
	
	private void restart() {
		stop(worker);
		stop(backend);
		stop(frontend);
		System.exit(0);
	}
	
	private List<GHCommit> getCommitsSince(GHCommit since, GHRepository repo) throws IOException {
		GHCommit lastRelease = release ? repo.getCommit(repo.listReleases().asList().get(0).getName()) : null;
		
		boolean lastReleaseHit = lastRelease == null;
		
		List<GHCommit> commits = new ArrayList<GHCommit>();
		
		if(lastRelease != null && lastRelease.getSHA1().equals(since.getSHA1())) {
			return commits;
		}
		
		for(GHCommit commit : repo.listCommits()) {
			if(!lastReleaseHit) {
				lastReleaseHit = lastRelease.getSHA1().equals(commit.getSHA1());
				if(lastReleaseHit) {
					commits.add(commit);
				}
				continue;
			}
			if(since.getSHA1().equals(commit.getSHA1())) {
				break;
			}
			commits.add(commit);
		}
		
		return commits;
	}
	
	private void pull(File repoFolder, GHRepository repo) throws IOException {
		git(repoFolder, "checkout", "head");
		git(repoFolder, "pull");
		if(release) {
			git(repoFolder, "checkout", repo.listReleases().asList().get(0).getName());
		}
	}
	
}
