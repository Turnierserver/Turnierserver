/*
 * Game.java
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
package org.pixelgaffer.turnierserver.gamelogic.interfaces;

import java.io.IOException;
import java.util.List;

public interface Game {
	
	/**
	 * Gibt die Liste mitden diesem Spiel angehörigen Ais zurück
	 * 
	 * @return Die diesem Spiel zugehörigen Ais
	 */
	public List<? extends Ai> getAis ();
	
	/**
	 * Gibt das Frontend zurück
	 * 
	 * @return Das Frontend
	 */
	public Frontend getFrontend ();
	
	/**
	 * Disconnected alle Ais. Falls eine Ai schon disconnected wurde, passiert
	 * mit dieser nichts.
	 */
	public void finishGame () throws IOException;
	
}
