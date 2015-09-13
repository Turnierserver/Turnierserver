/*
 * AiFake.java
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
package org.pixelgaffer.turnierserver.codr;

import javafx.beans.property.ObjectProperty;
import javafx.scene.image.Image;


/**
 * Wird nur benutzt, um bei der Auswahl der eigenen KIs einen Eintrag "<Neue Ai>" zu erzeugen.
 * 
 * @author Philip
 */
public class AiFake extends AiBase {
	
	public AiFake() {
		super("<Neue Ai>", AiMode.fake);
	}
	
	
	@Override
	public ObjectProperty<Image> getPicture() {
		return null;
	}
	
	
	@Override
	public Version lastVersion() {
		return null;
	}
	
	
	@Override
	public void setPicture(Image img) {
		return;
	}
	
}
