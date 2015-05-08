package org.pixelgaffer.turnierserver.backend.server;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import lombok.NonNull;
import naga.NIOSocket;
import naga.SocketObserver;

import org.pixelgaffer.turnierserver.backend.BackendMain;

/**
 * Diese Klasse ist der ConnectionHandler für den BackendServer.
 */
public class BackendConnectionHandler implements SocketObserver
{
	private enum State
	{
		/** Das Socket ist nicht verbunden. */
		DISCONNECTED,
		/** Das Socket ist verbunden, hat aber noch kein Passwort geschickt. */
		CONNECTED,
		/** Das Socket ist verbunden und hat das richtige Passwort geschickt. */
		VERIFIED
	}
	
	/** Der aktuelle Status der Connection. */
	private State state = State.DISCONNECTED;
	
	/** Der Salt, den der Client dem Passwort zufügen muss. */
	private byte[] salt;
	/** Der erwartete Passworthash. */
	private byte[] hash;
	
	/** Der lokale Buffer mit den noch nicht gelesenen bytes. */
	private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	
	/** Gibt die ersten length bytes zurück und schneidet diese vom buffer ab. */
	private byte[] cutFromBuffer (int length)
	{
		if (buffer.size() < length)
			return null;
		byte b[] = new byte[length];
		System.arraycopy(buffer.toByteArray(), 0, b, 0, length);
		byte buf[] = new byte[buffer.size() - length];
		System.arraycopy(buffer.toByteArray(), length, buf, 0, buf.length);
		buffer.reset();
		buffer.write(buf, 0, buf.length);
		return b;
	}
	
	public BackendConnectionHandler (@NonNull NIOSocket socket)
	{
		salt = BackendMain.generateSalt(20);
		hash = BackendMain.sha256(System.getProperty("turnierserver.backend.server.password").getBytes(), salt, 50);
		System.out.println(new String(hash));
		
		// client = socket;
		socket.listen(this);
	}
	
	@Override
	public void connectionOpened (NIOSocket socket)
	{
		BackendMain.getLogger().info("BackendConnectionHandler: connection opened: " + socket);
		state = State.CONNECTED;
		socket.write(salt);
	}
	
	@Override
	public void connectionBroken (NIOSocket nioSocket, Exception exception)
	{
		
	}
	
	@Override
	public void packetReceived (NIOSocket socket, byte[] packet)
	{
		switch (state)
		{
			case CONNECTED:
				// wenn das Passwort noch nicht gesendet wurde, solange
				// mitschneiden, bis das Passwort gelesen wurde
				buffer.write(packet, 0, packet.length);
				byte receivedHash[] = cutFromBuffer(hash.length);
				if (receivedHash != null && !Arrays.equals(receivedHash, hash))
				{
					BackendMain.getLogger().warning("BackendConnectionHandler: Wrong hash received from " + socket);
					socket.close();
					return;
				}
				break;
		}
	}
	
	@Override
	public void packetSent (NIOSocket socket, Object tag)
	{
		
	}
}
