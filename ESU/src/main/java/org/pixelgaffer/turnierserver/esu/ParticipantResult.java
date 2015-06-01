package org.pixelgaffer.turnierserver.esu;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import org.pixelgaffer.turnierserver.esu.utilities.ErrorLog;
import org.pixelgaffer.turnierserver.esu.utilities.Resources;

public class ParticipantResult {
	
	public final Game game;
	public String number;
	public String playerName;
	public String kiName;
	public String duration;
	public String moveCount;
	public String points;
	public String won;
	
	public ParticipantResult(Game ggame, String pplayerName, String kkiName, String dduration, String mmoveCount, String ppoints, String wwon){
		game = ggame;
		playerName = pplayerName;
		kiName = kkiName;
		duration = dduration;
		moveCount = mmoveCount;
		points = ppoints;
		won = wwon;
	}
	
	
	
	
}
