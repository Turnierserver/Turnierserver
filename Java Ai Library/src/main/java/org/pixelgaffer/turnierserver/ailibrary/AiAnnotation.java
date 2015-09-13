package org.pixelgaffer.turnierserver.ailibrary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface AiAnnotation {
	
	boolean value();
	
}
