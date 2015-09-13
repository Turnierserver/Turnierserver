/*
 * SHA256.java
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
package org.pixelgaffer.turnierserver.networking;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SHA256
{
	public static byte[] sha256 (byte password[], byte salt[], int repeats) throws NoSuchAlgorithmException
	{
		byte[] hash = password;
		for (int i = 0; i < repeats; i++)
		{
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt);
			md.update(hash);
			hash = md.digest();
		}
		return hash;
	}
}
