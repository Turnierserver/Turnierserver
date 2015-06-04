package org.pixelgaffer.turnierserver.networking;

import it.sauronsoftware.ftp4j.FTPClient;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class FtpConnection
{
	@Getter
	@Setter
	@NonNull
	private FTPClient ftpClient;
	
	@Getter
	@Setter
	private boolean busy;
}
