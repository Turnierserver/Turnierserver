package org.pixelgaffer.turnierserver.networking;

import static org.pixelgaffer.turnierserver.PropertyUtils.FTP_CONNECTIONS;

import java.io.IOException;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import lombok.Getter;
import lombok.NonNull;

import org.pixelgaffer.turnierserver.PropertyUtils;

public class FtpConnectionPool
{
	private FtpConnection[] cons;
	
	@Getter
	private Object lock;
	
	public FtpConnectionPool ()
	{
		this(PropertyUtils.getIntRequired(FTP_CONNECTIONS));
	}
	
	public FtpConnectionPool (int size)
	{
		this(size, new Object());
	}
	
	public FtpConnectionPool (int size, @NonNull Object lock)
	{
		this.lock = lock;
		cons = new FtpConnection[size];
	}
	
	public FtpConnection getClient () throws IOException, FTPIllegalReplyException, FTPException
	{
		while (true)
		{
			synchronized (lock)
			{
				for (int i = 0; i < cons.length; i++)
				{
					if (cons[i] == null)
					{
						FTPClient c = new FTPClient();
						c = DatastoreFtpClient.connect(c);
						cons[i] = new FtpConnection(c);
						cons[i].setBusy(true);
						return cons[i];
					}
					
					if (!cons[i].isBusy())
					{
						cons[i].setBusy(true);
						cons[i].setFtpClient(DatastoreFtpClient.connect(cons[i].getFtpClient()));
						return cons[i];
					}
				}
				
				try
				{
					lock.wait();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
