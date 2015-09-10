package org.pixelgaffer.turnierserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileOwnerChanger
{
	public static void changeOwner (File f, String user) throws IOException
	{
		changeOwner(f.toPath(), user);
	}
	
	public static void changeOwner (Path p, String user) throws IOException
	{
		new Logger().todo("funktionierendes set owner zeugs in java");
//		UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
//		UserPrincipal userPrincipal = lookupService.lookupPrincipalByName(user);
//		Files.setOwner(p, userPrincipal);
	}
}
