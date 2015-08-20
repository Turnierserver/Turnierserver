package org.pixelgaffer.turnierserver.worker;

import static org.apache.commons.compress.archivers.tar.TarArchiveOutputStream.BIGNUMBER_POSIX;
import static org.apache.commons.compress.archivers.tar.TarArchiveOutputStream.LONGFILE_POSIX;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.pixelgaffer.turnierserver.compile.LibraryDownloader;
import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;
import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

public class LibraryCache implements LibraryDownloader
{
	public static final File cachedir = new File("/var/cache/worker/libs");
	
	private static LibraryCache cache = null;
	
	public static LibraryCache getCache ()
	{
		if (cache == null)
			cache = new LibraryCache();
		return cache;
	}
	
	private LibraryCache ()
	{
		if (!cachedir.exists())
			cachedir.mkdirs();
	}
	
	private void cacheLibrary (String language, String name)
			throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException,
			FTPListParseException
	{
		File libdir = new File(cachedir, language + "/" + name);
		WorkerMain.getLogger().info("Caching Library " + name + " (" + language + ") to " + libdir.getAbsolutePath());
		libdir.mkdirs();
		DatastoreFtpClient.retrieveLibrary(name, language, libdir);
		File libtar = new File(cachedir, language + "/" + name + ".tar.bz2");
		TarArchiveOutputStream tar = new TarArchiveOutputStream(new BZip2CompressorOutputStream(new FileOutputStream(libtar)));
		tar.setBigNumberMode(BIGNUMBER_POSIX);
		tar.setLongFileMode(LONGFILE_POSIX);
		addToTar(libdir, tar, "");
		tar.close();
	}
	
	private void addToTar (File dir, TarArchiveOutputStream tar, String prefix) throws IOException
	{
		for (String file : dir.list())
		{
			File f = new File(dir, file);
			if (f.isDirectory())
				addToTar(f, tar, prefix + file + "/");
			else
			{
				TarArchiveEntry entry = new TarArchiveEntry(prefix + file);
				entry.setSize(f.length());
				tar.putArchiveEntry(entry);
				FileInputStream in = new FileInputStream(f);
				byte buf[] = new byte[8192];
				int read;
				while ((read = in.read(buf)) > 0)
					tar.write(buf, 0, read);
				in.close();
				tar.closeArchiveEntry();
			}
		}
	}
	
	public File getLibDir (String language, String name)
			throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException,
			FTPListParseException
	{
		File libdir = new File(cachedir, language + "/" + name);
		if (!libdir.exists())
			cacheLibrary(language, name);
		return libdir;
	}
	
	public File getLibTarBz2 (String language, String name)
			throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException,
			FTPListParseException
	{
		File libtar = new File(cachedir, language + "/" + name + ".tar.bz2");
		if (!libtar.exists())
			cacheLibrary(language, name);
		return libtar;
	}
	
	// ### LibraryDownloader
	
	@Override
	public LibraryDownloaderMode getMode ()
	{
		return LibraryDownloaderMode.LIBS_ONLY;
	}
	
	@Override
	public File[] getAiLibs (String language)
	{
		throw new UnsupportedOperationException("Please check getMode() first!");
	}
	
	@Override
	public File[] getLib (String language, String name)
	{
		try
		{
			return getLibDir(language, name).listFiles();
		}
		catch (IOException | FTPIllegalReplyException | FTPException | FTPDataTransferException | FTPAbortedException
				| FTPListParseException e)
		{
			throw new RuntimeException(e);
		}
	}
}
