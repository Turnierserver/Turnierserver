/*
 * FileOwnerChanger.java
 *
 * Copyright (C) 2015 Pixelgaffer
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 *
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pixelgaffer.turnierserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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
