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

public class Patcher implements Runnable
{
	
	public static File rootDir = new File("..");
	public static File configDir;
	public static File turnierserverDir = new File(rootDir, "Turnierserver");
	public static File buildDir;
	public static File frontendDir;
	public static File compilerDir;
	public static File patcherDir;
	public static File networkingDir;
	public static File gamelogicDir;
	public static File workerDir;
	public static File backendDir;
	
	public static Process exe (File dir, String ... command) throws IOException
	{
		ProcessBuilder builder = new ProcessBuilder(command);
		if (dir != null)
		{
			builder.directory(dir);
			builder.inheritIO();
		}
		return builder.start();
	}
	
	public static Process git (File dir, String ... command) throws IOException
	{
		ArrayList<String> commands = new ArrayList<>();
		commands.add("git");
		commands.addAll(Arrays.asList(command));
		return exe(dir, commands.toArray(new String[commands.size()]));
	}
	
	public static void main (String[] args) throws IOException, InterruptedException
	{
		
		Properties p = new Properties(System.getProperties());
		p.load(new FileInputStream(new File(rootDir, "patcher.prop")));
		System.setProperties(p);
		
		configDir = new File(rootDir, System.getProperty("turnierserver.patcher.github.repos.turnierserver-config")
				.split("/")[1]);
		
		if (!configDir.exists())
		{
			if (git(rootDir, "clone", "https://" + System.getProperty("turnierserver.patcher.github.username") + ":"
					+ System.getProperty("turnierserver.patcher.github.password") + "@"
					+ "github.com/nicosio2/Turnierserver-Config.git").waitFor() != 0)
				System.exit(1);
		}
		
		p = new Properties(System.getProperties());
		p.load(new FileInputStream(new File(configDir, "turnierserver.prop")));
		System.setProperties(p);
		
		turnierserverDir = new File(rootDir, System.getProperty("turnierserver.patcher.github.repos.turnierserver")
				.split("/")[1]);
		buildDir = new File(turnierserverDir, System.getProperty("turnierserver.patcher.build"));
		frontendDir = new File(turnierserverDir, System.getProperty("turnierserver.patcher.frontend"));
		compilerDir = new File(turnierserverDir, System.getProperty("turnierserver.patcher.compiler"));
		patcherDir = new File(turnierserverDir, System.getProperty("turnierserver.patcher"));
		networkingDir = new File(turnierserverDir, System.getProperty("turnierserver.patcher.networking"));
		gamelogicDir = new File(turnierserverDir, System.getProperty("turnierserver.patcher.gamelogic"));
		workerDir = new File(turnierserverDir, System.getProperty("turnierserver.patcher.worker"));
		backendDir = new File(turnierserverDir, System.getProperty("turnierserver.patcher.backend"));
		
		
		new Thread(new Patcher(Boolean.parseBoolean(args[0]), Boolean.parseBoolean(args[1]),
				Boolean.parseBoolean(args[2]), Boolean.parseBoolean(args[3]))).start();
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
	
	public Patcher (boolean worker, boolean backend, boolean frontend, boolean release) throws IOException, InterruptedException
	{
		
		this.release = release;
		
//		github = GitHub.connectUsingPassword(System.getProperty("turnierserver.patcher.github.username"),
//				System.getProperty("turnierserver.patcher.github.password"));
		github = GitHub.connectUsingOAuth("***REMOVED***");
		turnierserver = github.getRepository(System.getProperty("turnierserver.patcher.github.repos.turnierserver"));
		config = github.getRepository(System.getProperty("turnierserver.patcher.github.repos.turnierserver-config"));
		
		if (!pull(turnierserverDir, turnierserver))
			System.exit(1);
		if (!pull(configDir, config))
			System.exit(1);
		
		if (!compile(networkingDir) ||
				!compile(compilerDir) ||
				!compile(workerDir) ||
				!compile(gamelogicDir) ||
				!compile(backendDir))
			System.exit(1);
		
		if (worker)
		{
			restartWorker();
		}
		if (backend)
		{
			restartBackend();
		}
		if (frontend)
		{
			restartFrontend();
		}
		
		lastTurnierserverCommit = release ? turnierserver.getCommit(turnierserver.listReleases().asList().get(0)
				.getName()) : turnierserver.listCommits().asList().get(0);
		lastConfigCommit = release ? config.getCommit(config.listReleases().asList().get(0).getName()) : config
				.listCommits().asList().get(0);
	}
	
	public void run ()
	{
		double refreshrate = Double.parseDouble(System.getProperty("turnierserver.patcher.refreshrate"));
		
		while (true)
		{
			System.out.println("bin in der while-schleife der run-methode");
			try
			{
				List<String> filesModified = new ArrayList<>();
				
				System.out.println("hohle mir die commits des config-repos");
				for (GHCommit commit : getCommitsSince(lastConfigCommit, config))
				{
					for (GHCommit.File file : commit.getFiles())
					{
						filesModified.add(file.getFileName());
					}
				}
				System.out.println("bearbeitete dateien: " + filesModified);
				
				boolean pulled = false;
				
				if (filesModified.contains("turnierserver.prop"))
				{
					if (!pulled)
					{
						if (!pull(configDir, config))
							System.exit(1);
					}
					restart();
				}
				if (filesModified.contains("_cfg.py"))
				{
					if (!pulled)
					{
						if (!pull(configDir, config))
							System.exit(1);
					}
					FileUtils.copyFileToDirectory(new File(configDir, "Frontend/_cfg.py"),
							new File(turnierserverDir, "Frontend"));
					restartFrontend();
				}
				
				lastConfigCommit = release ? config.getCommit(config.listReleases().asList().get(0).getName()) : config
						.listCommits().asList().get(0);
				
				List<String> directoriesModified = new ArrayList<>();
				System.out.println("hole mir die commits des turnierserver-repos");
				for (GHCommit commit : getCommitsSince(lastTurnierserverCommit, turnierserver))
				{
					for (GHCommit.File file : commit.getFiles())
					{
						System.out.println(file.getFileName());
						directoriesModified.add(file.getFileName().split("/")[4]);
					}
				}
				System.out.println("veränderte verzeichnisse: " + directoriesModified);
				
				pulled = false;
				
				if (directoriesModified.contains(patcherDir.getName()))
				{
					if (!pulled)
					{
						if (!pull(turnierserverDir, turnierserver))
							System.exit(1);
					}
					restart();
				}
				
				if (frontend != null && directoriesModified.contains(frontendDir.getName()))
				{
					if (!pulled)
					{
						if (!pull(turnierserverDir, turnierserver))
							System.exit(1);
					}
					restartFrontend();
				}
				
				List<File> alreadyCompiled = new ArrayList<>();
				List<File> dependencyChainBackend = new ArrayList<>(Arrays.asList(networkingDir, gamelogicDir,
						backendDir));
				List<File> dependencyChainWorker = new ArrayList<>(Arrays.asList(networkingDir, compilerDir, workerDir));
				
				if (worker != null
						&& compileDependencyChain(dependencyChainWorker, alreadyCompiled, directoriesModified, pulled))
				{
					restartWorker();
				}
				if (backend != null
						&& compileDependencyChain(dependencyChainBackend, alreadyCompiled, directoriesModified, pulled))
				{
					pulled = true;
					restartBackend();
				}
				
				lastTurnierserverCommit = release ? turnierserver.getCommit(turnierserver.listReleases().asList()
						.get(0).getName()) : turnierserver.listCommits().asList().get(0);
				
				Thread.sleep((long)(refreshrate * 1000));
				
			}
			catch (IOException | InterruptedException e1)
			{
				e1.printStackTrace();
				System.exit(1);
			}
		}
		
	}
	
	private boolean compileDependencyChain (List<File> dependencyChain, List<File> alreadyCompiled,
											List<String> toCompile, boolean alreadyPulled)
			throws IOException, InterruptedException
	{
		boolean compile = false;
		for (File dependency : dependencyChain)
		{
			compile = compile || toCompile.contains(dependency.getName());
			if (compile)
			{
				if (!alreadyCompiled.contains(dependency))
				{
					if (!alreadyPulled)
					{
						if (!pull(turnierserverDir, turnierserver))
								return false;
						alreadyPulled = true;
					}
					if (!compile(dependency))
						return false;
					alreadyCompiled.add(dependency);
				}
			}
		}
		return compile;
	}
	
	private boolean compile (File target) throws IOException, InterruptedException
	{
		if (exe(target, "mvn", "package", "install", "dependency:copy-dependencies").waitFor() != 0)
				return false;
		
		File targetDir = new File(target, "target");
		File dependencyDir = new File(targetDir, "dependency");
		
		for (File f : targetDir.listFiles())
			if (f.isFile())
				FileUtils.copyFile(f, new File(buildDir, f.getName()));
		for (File f : dependencyDir.listFiles())
			if (f.isFile())
				FileUtils.copyFile(f, new File(buildDir, f.getName()));
		
		return true;
	}
	
	private Process startProgramm (String mainclass) throws IOException
	{
		String classpath = "./:*";
		return exe(buildDir, "java", "-cp", classpath, mainclass, new File(configDir, "turnierserver.prop").getAbsolutePath());
	}
	
	private void restartWorker () throws IOException
	{
		stop(worker);
//		worker = exe(buildDir, "worker.sh", "../../Turnierserver-Config/turnierserver.prop");
		worker = startProgramm("org.pixelgaffer.turnierserver.worker.WorkerMain");
	}
	
	private void restartBackend () throws IOException
	{
		stop(backend);
//		backend = exe(buildDir, "backend.sh", "../../Turnierserver-Config/turnierserver.prop");
		backend = startProgramm("org.pixelgaffer.turnierserver.backend.BackendMain");
	}
	
	private void restartFrontend () throws IOException
	{
		stop(frontend);
		frontend = exe(frontendDir, "python3", "app.py", "run");
	}
	
	private void stop (Process process)
	{
		if (process != null)
		{
			process.destroy();
			try
			{
				process.waitFor();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	
	private void restart ()
	{
		stop(worker);
		stop(backend);
		stop(frontend);
		System.exit(0);
	}
	
	private List<GHCommit> getCommitsSince (GHCommit since, GHRepository repo) throws IOException
	{
		GHCommit lastRelease = release ? repo.getCommit(repo.listReleases().asList().get(0).getName()) : null;
		
		boolean lastReleaseHit = lastRelease == null;
		
		List<GHCommit> commits = new ArrayList<GHCommit>();
		
		if (lastRelease != null && lastRelease.getSHA1().equals(since.getSHA1()))
		{
			return commits;
		}
		
		for (GHCommit commit : repo.listCommits())
		{
			System.out.println(commit.getSHA1());
			if (!lastReleaseHit)
			{
				lastReleaseHit = lastRelease.getSHA1().equals(commit.getSHA1());
				if (lastReleaseHit)
				{
					commits.add(commit);
				}
				continue;
			}
			if (since.getSHA1().equals(commit.getSHA1()))
			{
				System.out.println("beim aktuellen commit angekommen");
				break;
			}
			commits.add(commit);
		}
		
		System.out.println("neue commits: " + commits.size());
		return commits;
	}
	
	private boolean pull (File repoFolder, GHRepository repo) throws IOException, InterruptedException
	{
		if (git(repoFolder, "checkout", "HEAD").waitFor() != 0)
			return false;
		if (git(repoFolder, "pull").waitFor() != 0)
			return false;
		if (release)
		{
			if (git(repoFolder, "checkout", repo.listReleases().asList().get(0).getName()).waitFor() != 0)
				return false;
		}
		return true;
	}
	
}
