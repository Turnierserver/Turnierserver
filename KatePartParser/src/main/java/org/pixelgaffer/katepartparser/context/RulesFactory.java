package org.pixelgaffer.katepartparser.context;

import java.lang.Float;
import java.lang.reflect.Field;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RulesFactory
{
	public static ContextRule parseRule (Element e)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException
	{
		Class<?> clazz = Class.forName(ContextRule.class.getPackage().getName() + "." + e.getTagName());
		ContextRule rule = (ContextRule)clazz.newInstance();
		
		NamedNodeMap attributes = e.getAttributes();
		if (attributes != null)
		{
			for (int i = 0; i < attributes.getLength(); i++)
			{
				Node n = attributes.item(i);
				if (n instanceof Attr)
				{
					Attr attribute = (Attr)n;
					Field f;
					try
					{
						if (attribute.getName().equals("char"))
							f = clazz.getField("character");
						else
							f = clazz.getField(attribute.getName());
					}
					catch (NoSuchFieldException nsfe)
					{
						System.err.println("Unbekanntes Attribut für " + e.getTagName() + ": " + attribute.getName());
						continue;
					}
					
					Class<?> type = f.getType();
					if (type == String.class)
						f.set(rule, attribute.getValue());
					else if (type == Boolean.TYPE)
						f.set(rule, Boolean.parseBoolean(attribute.getValue()));
					else if (type == Byte.TYPE)
						f.set(rule, Byte.parseByte(attribute.getValue()));
					else if (type == Character.TYPE)
						f.set(rule, attribute.getValue().charAt(0));
					else if (type == Double.TYPE)
						f.set(rule, Double.parseDouble(attribute.getValue()));
					else if (type == Float.TYPE)
						f.set(rule, Float.parseFloat(attribute.getValue()));
					else if (type == Integer.TYPE)
						f.set(rule, Integer.parseInt(attribute.getValue()));
					else if (type == Long.TYPE)
						f.set(rule, Long.parseLong(attribute.getValue()));
					else if (type == Short.TYPE)
						f.set(rule, Short.parseShort(attribute.getValue()));
					else
						System.out.println("Unknown type of Field: " + type.getName());
				}
			}
		}
		
		return rule;
	}
}
