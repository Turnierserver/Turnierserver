package org.pixelgaffer.turnierserver.networking.messages;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import org.json.JSONArray;

/**
 * Diese Klasse speichert Informationen über eine Verbindung zum Worker ab.
 * Dieses Objekt wird immer am Anfang einer Verbindung gesendet.
 */
@AllArgsConstructor
@EqualsAndHashCode
public class WorkerConnectionType
{
	public static final char AI = 'A';
	public static final char BACKEND = 'B';
	public static final char SANDBOX = 'S';
	
	@Getter
	private char type;
	
	@Getter
	private UUID uuid;
	
	@Getter
	private String[] langs;
	
	@Override
	public String toString ()
	{
		return type + (uuid == null ? "" : uuid.toString());
	}
	
	/**
	 * Parst einen String der Form [ABS]UUID in einen WorkerConnectionType, wie
	 * die Methode {@link WorkerConnectionType#toString() toString()}
	 * zurückgibt.
	 */
	public static WorkerConnectionType parse (String data)
	{
		Pattern p = Pattern.compile("([ABS])(\\S+)?\\s*");
		Matcher m = p.matcher(data);
		if (m.matches())
		{
			char type = m.group(1).charAt(0);
			UUID uuid = (type == AI ? UUID.fromString(m.group(2)) : null);
			String langs[] = null;
			if (type == SANDBOX)
			{
				JSONArray jsonlangs = new JSONArray(m.group(2));
				langs = new String[jsonlangs.length()];
				for (int i = 0; i < langs.length; i++)
					langs[i] = jsonlangs.getString(i);
			}
			return new WorkerConnectionType(type, uuid, langs);
		}
		return null;
	}
}
