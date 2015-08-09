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
