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
