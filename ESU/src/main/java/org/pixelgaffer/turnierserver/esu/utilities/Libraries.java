package org.pixelgaffer.turnierserver.esu.utilities;

import java.io.File;

import org.pixelgaffer.turnierserver.compile.LibraryDownloader;
import org.pixelgaffer.turnierserver.esu.MainApp;

public class Libraries implements LibraryDownloader {

	@Override
	public File[] getAiLibs(String language) {
		return new File(Paths.ailibrary(MainApp.actualGameType.get(), language)).listFiles();
	}

	@Override
	public File[] getLib(String language, String name) {
		throw new UnsupportedOperationException();
	}

}
