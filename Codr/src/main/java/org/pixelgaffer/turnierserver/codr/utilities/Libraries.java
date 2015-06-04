package org.pixelgaffer.turnierserver.codr.utilities;


import java.io.File;

import org.pixelgaffer.turnierserver.codr.MainApp;
import org.pixelgaffer.turnierserver.compile.LibraryDownloader;



public class Libraries implements LibraryDownloader {
	
	@Override public File[] getAiLibs(String language) {
		return new File(Paths.ailibrary(MainApp.actualGameType.get(), language)).listFiles();
	}
	
	
	@Override public File[] getLib(String language, String name) {
		throw new UnsupportedOperationException();
	}
	
}
