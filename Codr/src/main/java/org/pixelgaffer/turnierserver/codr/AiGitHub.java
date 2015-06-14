package org.pixelgaffer.turnierserver.codr;


public class AiGitHub extends AiSaved {
	
	public AiGitHub(String path) {
		super("GitHub", AiMode.gitHub);
	}
	
	
	private Version version;
	
	
	@Override public Version newVersion(NewVersionType type) {
		return null;
	}
	
	
	@Override public Version newVersion(NewVersionType type, String path) {
		return null;
	}
	
	
	@Override public Version lastVersion() {
		return version;
	}
	
	
}
