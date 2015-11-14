package org.pixelgaffer.turnierserver.backend;

import java.io.IOException;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONObject;
import org.pixelgaffer.turnierserver.Sentry;
import org.pixelgaffer.turnierserver.Parsers;
import org.pixelgaffer.turnierserver.backend.Games.GameImpl;
import org.pixelgaffer.turnierserver.backend.server.BackendFrontendConnectionHandler;
import org.pixelgaffer.turnierserver.backend.server.message.BackendFrontendCommandProcessed;
import org.pixelgaffer.turnierserver.backend.server.message.BackendFrontendResult;
import org.pixelgaffer.turnierserver.networking.DatastoreFtpClient;
import lombok.Getter;
import lombok.ToString;

@ToString
public class Tournament implements Runnable
{
	public static final int GAMES_PER_ENEMY = 2;
	
	@Getter
	private static Tournament currentTournament = null;
	private static Object lock = new Object();
	
	@Getter
	private int id;
	
	@Getter
	private int gameId;
	
	@Getter
	private int requestId;
	
	@Getter
	private int aisPerGame;
	
	public Tournament (int id, int gameId, int requestId)
	{
		this.id = id;
		this.gameId = gameId;
		this.requestId = requestId;
		
		BackendMain.getLogger().todo("assuming 2 ais per game, this shouldn't be hardcoded");
		this.aisPerGame = 2;
		
		synchronized (lock)
		{
			if (currentTournament != null)
				BackendMain.getLogger().warning("Überschreibe currentTournament (" + currentTournament + ") mit " + this);
			currentTournament = this;
		}
		
		new Thread(this, "Tournament-" + id).start();
	}
	
	@Override
	public void run ()
	{
		// die kis einlesen
		JSONArray ais = null;
		{
			Exception _e = null;
			for (int i = 0; (ais == null) && (i < 3); i++)
			{
				try
				{
					ais = new JSONArray(DatastoreFtpClient.retrieveTournamentAis(id));
				}
				catch (Exception e)
				{
					Sentry.log(e).printStackTrace();
					_e = e;
				}
			}
			if (ais == null)
			{
				try
				{
					crashed(_e);
				}
				catch (IOException e)
				{
					Sentry.log(e).printStackTrace();
				}
				return;
			}
		}
		
		// ich benötige mindestens die anzahl an kis pro spiel kis für das
		// turnier
		if (ais.length() < aisPerGame)
		{
			try
			{
				crashed("At least " + aisPerGame + " AIs required to start the tournament");
			}
			catch (IOException e)
			{
				Sentry.log(e).printStackTrace();
			}
			return;
		}
		
		try
		{
			processed();
		}
		catch (IOException e)
		{
			Sentry.log(e).printStackTrace();
		}
		
		BackendMain.getLogger().info("Starte Turnier " + this + " mit " + ais.length() + " KIs");
		
		int aiIds[] = new int[aisPerGame], c = 0, fails = 0;
		for (int i = 0; i < aiIds.length; i++)
			aiIds[i] = i;
		mainloop: do
		{
			int sandboxes = Workers.getStartableSandboxes(true);
			int games = sandboxes / aisPerGame;
			for (int i = 0; i < games; i++)
			{
				if (c >= GAMES_PER_ENEMY)
				{
					aiIds[aiIds.length - 1]++;
					c = 0;
				}
				for (int j = aiIds.length - 1; j >= 0; j--)
				{
					if (aiIds[j] < ais.length() - aiIds.length + j + 1)
						break;
					if (j == 0)
						break mainloop;
					aiIds[j - 1]++;
					aiIds[j] = aiIds[j - 1] + 1;
				}
				BackendMain.getLogger().critical("aiIds=" + Arrays.toString(aiIds) + "; c=" + c);
				String[] languages = new String[aisPerGame], aiNames = new String[aisPerGame];
				for (int j = 0; j < aisPerGame; j++)
				{
					JSONObject ai = ais.getJSONObject(aiIds[j]);
					languages[j] = ai.getString("lang");
					aiNames[j] = ai.getString("ai");
				}
				BackendMain.getLogger().critical("startGame(gameId=" + gameId + ", requestId=" + requestId
						+ ", tournament=true, languages=" + Arrays.toString(languages) + ", ais=" + Arrays.toString(aiNames));
				try
				{
					GameImpl g = Games.startGame(gameId, requestId, true, languages, aiNames);
					fails = 0;
					// das frontend informieren
					JSONObject json = new JSONObject();
					json.put("requestid", requestId);
					json.put("gameid", g.getUuid());
					json.put("ais", aiNames);
					BackendFrontendConnectionHandler.getFrontend().sendMessage(json.toString().getBytes());
				}
				catch (Exception e)
				{
					Sentry.log(e).printStackTrace();
					// try to restart that game
					if (++fails <= 3)
					{
						c--;
						i--;
					}
				}
				c++;
			}
			
			try
			{
				Workers.waitForAvailableWorker();
			}
			catch (InterruptedException e)
			{
				Sentry.log(e).printStackTrace();
			}
			
		} while (true);
		
		BackendMain.getLogger().todo("wait until all games associated with this tournament have finished");
		
		try
		{
			finished();
		}
		catch (IOException e)
		{
			Sentry.log(e).printStackTrace();
		}
	}
	
	private void processed () throws IOException
	{
		BackendFrontendConnectionHandler.getFrontend().sendMessage(
				Parsers.getFrontend().parse(new BackendFrontendCommandProcessed(getRequestId()), false));
	}
	
	private void crashed (Exception e) throws IOException
	{
		BackendMain.getLogger().critical("Das Turnier " + this + " ist gecrasht: " + e);
		synchronized (lock)
		{
			currentTournament = null;
		}
		BackendFrontendConnectionHandler.getFrontend().sendMessage(
				Parsers.getFrontend().parse(new BackendFrontendResult(getRequestId(), false, e), false));
	}
	
	private void crashed (String msg) throws IOException
	{
		BackendMain.getLogger().critical("Das Turnier " + this + " ist gecrasht: " + msg);
		synchronized (lock)
		{
			currentTournament = null;
		}
		BackendFrontendConnectionHandler.getFrontend().sendMessage(
				Parsers.getFrontend().parse(new BackendFrontendResult(getRequestId(), false, msg, null), false));
	}
	
	private void finished () throws IOException
	{
		BackendMain.getLogger().info("Das Turnier " + this + " ist fertig");
		synchronized (lock)
		{
			currentTournament = null;
		}
		BackendFrontendConnectionHandler.getFrontend().sendMessage(
				Parsers.getFrontend().parse(new BackendFrontendResult(getRequestId(), true), false));
	}
}
