import java.io.*;
import java.text.*;
import java.util.*;
import java.util.zip.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class GenerateSyntaxZip
{
	static String xmlElement (Element e, int indent, boolean hasEnd)
	{
		StringBuilder xml = new StringBuilder();
		for (int i = 0; i < indent; i++) xml.append("\t");
		xml.append("<").append(e.getTagName());
		NamedNodeMap nl = e.getAttributes();
		if (nl != null)
		{
			for (int i = 0; i < nl.getLength(); i++)
			{
				Node n = nl.item(i);
				if (n instanceof Attr)
				{
					Attr a = (Attr)n;
					xml.append(" ").append(a.getName());
					String value = a.getValue();
					value = value.replace("&", "&amp;");
					value = value.replace("<", "&lt;");
					value = value.replace(">", "&gt;");
					value = value.replace("\"", "&quot;");
					xml.append("=\"").append(value).append("\"");
				}
			}
		}
		xml.append(hasEnd ? ">" : " />");
		return xml.toString();
	}
	
	static String xmlElementWithChildren (Element e, int indent)
	{
		StringBuilder xml = new StringBuilder();
		NodeList nl = e.getChildNodes();
		if (nl == null || nl.getLength() == 0)
		{
			String cdata = e.getTextContent().trim();
			if (cdata == null || cdata.length() == 0)
				xml.append(xmlElement(e, indent, false));
			else
			{
				cdata = cdata.replace("&", "&amp;");
				cdata = cdata.replace("<", "&lt;");
				cdata = cdata.replace(">", "&gt;");
				cdata = cdata.replace("\"", "&quot;");
				xml.append(xmlElement(e, indent, true));
				xml.append(cdata).append("</").append(e.getTagName()).append(">");
			}
		}
		else
		{
			xml.append(xmlElement(e, indent, true)).append("\n");
			int elements = 0;
			for (int in = 0; in < nl.getLength(); in++)
			{
				Node n = nl.item(in);
				if (n instanceof Element)
				{
					elements++;
					xml.append(xmlElementWithChildren((Element)n, indent + 1)).append("\n");
				}
			}
			if (elements == 0)
			{
				for (int i = 0; i <= indent; i++) xml.append("\t");
				xml.append(e.getTextContent().trim()).append("\n");
			}
			for (int i = 0; i < indent; i++) xml.append("\t");
			xml.append("</").append(e.getTagName()).append(">");
		}
		return xml.toString();
	}
	
	static Map<String, List<String>> lists;
	
	static void handleElement (Element e, PrintWriter out, int indent, Properties props, String lang) throws Throwable
	{
		if (e.getTagName().equals("lists"))
		{
			NodeList nl = e.getChildNodes();
			if (nl != null)
			{
				for (int i = 0; i < nl.getLength(); i++)
				{
					Node n = nl.item(i);
					if (n instanceof Element)
					{
						handleElement((Element)n, out, indent + 1, props, lang);
					}
				}
			}
		}
		else if (e.getTagName().equals("list"))
		{
			List<String> list = new ArrayList<>();
			NodeList nl = e.getChildNodes();
			if (nl != null)
			{
				for (int i = 0; i < nl.getLength(); i++)
				{
					Node n = nl.item(i);
					if (n instanceof Element)
					{
						Element elem = (Element)n;
						if (elem.getTagName().equals("item"))
						{
							list.add(elem.getTextContent());
						}
						else
						{
							System.err.println("Unbekanntes Element in Liste:");
							System.err.println(xmlElementWithChildren(elem, 0));
						}
					}
				}
			}
			String name = e.getAttribute("name");
			lists.put(name, list);
		}
		else if (e.getTagName().equals("itemDatas"))
		{
			out.println(xmlElement(e, 2, true));
			NodeList nl = e.getChildNodes();
			if (nl != null)
			{
				for (int i = 0; i < nl.getLength(); i++)
				{
					Node n = nl.item(i);
					if (n instanceof Element)
					{
						Element itemData = (Element)n;
						if (itemData.getTagName().equals("itemData"))
						{
							NamedNodeMap attributes = itemData.getAttributes();
							String name = null, defStyleNum = null;
							Map<String, String> styleInfo = new HashMap<>();
							boolean ask = false;
							if (attributes != null)
							{
								for (int j = 0; j < attributes.getLength(); j++)
								{
									Node n0 = attributes.item(j);
									if (n0 instanceof Attr)
									{
										Attr attribute = (Attr)n0;
										if (attribute.getName().equals("name"))
											name = attribute.getValue();
										else if (attribute.getName().equals("defStyleNum"))
											defStyleNum = attribute.getValue();
										else if (!attribute.getName().equals("spellChecking"))
										{
											ask = true;
											styleInfo.put(attribute.getName(), attribute.getValue());
										}
									}
								}
							}
							
							if (ask)
							{
								String newDefault = props.getProperty(lang + "." + name + ".newDefault");
								if (newDefault == null)
								{
									System.out.print("Das itemData-Element für " + name + " (default: " + defStyleNum + ") enthält eigene Style-Informationen (" + xmlElement(itemData, 0, false) + "). Neues default: ");
									newDefault = new BufferedReader(new InputStreamReader(System.in)).readLine();
								}
								if (newDefault.isEmpty())
								{
									out.println(xmlElement(itemData, 3, false));
								}
								else
								{
									out.println("\t\t\t<itemData name=\"" + name + "\" defStyleNum=\"" + newDefault + "\" />");
								}
								props.put(lang + "." + name + ".newDefault", newDefault);
							}
							else
								out.println(xmlElement(itemData, 3, false));
						}
						else
						{
							System.out.println("Warnung: Unbekanntes Element in " + xmlElement(e, 0, true) + ":");
							System.out.println(xmlElementWithChildren(e, 0));
							out.println(xmlElementWithChildren(e, 3));
						}
					}
				}
			}
			out.println("\t\t</itemDatas>");
		}
		else
			out.println(xmlElementWithChildren(e, indent));
	}
	
	public static void main (String args[]) throws Throwable
	{
		File propsFile = new File(System.getProperty("user.home"), ".generateSyntaxZip.prop");
		Properties props = new Properties();
		if (propsFile.exists())
			props.load(new FileInputStream(propsFile));
		
		File dir = new File("/usr/share/katepart5/syntax");
		File zipFile = new File("syntax.zip");
		ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipFile));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(zip, "utf8"), true);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		for (String filename : dir.list())
		{
			if (!filename.endsWith(".xml"))
				continue;
			File file = new File(dir, filename);
			lists = new HashMap<>();
			
			System.out.println("Lese Datei " + file.getAbsolutePath());
			
			Document doc = db.parse(file);
			Element root = doc.getDocumentElement();
			String lang = root.getAttribute("name");
			String answer = props.getProperty(lang + ".include");
			if (answer == null)
			{
				System.out.print("Soll die Sprache " + lang + " in die syntax.zip-Datei geschrieben werden?");
				answer = new BufferedReader(new InputStreamReader(System.in)).readLine();
			}
			props.put(lang + ".include", answer);
			boolean include = !answer.startsWith("n");
			if (!include)
			{
				props.store(new FileOutputStream(propsFile), "user input");
				continue;
			}
			
			ZipEntry entry = new ZipEntry(filename);
			zip.putNextEntry(entry);
			
			out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			out.println("<!DOCTYPE language SYSTEM \"language.dtd\">");
			out.println("<!-- read from " + file.getAbsolutePath() + " at " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, Locale.UK).format(new Date()) + " -->");
			out.println(xmlElement(root, 0, true));
			
			NodeList nl = root.getChildNodes();
			if (nl != null)
			{
				for (int i = 0; i < nl.getLength(); i++)
				{
					Node n = nl.item(i);
					if (n instanceof Element)
					{
						Element e = (Element)n;
						if (e.getTagName().equals("highlighting"))
						{
							out.println(xmlElement(e, 1, true));
							
							NodeList nl1 = e.getChildNodes();
							if (nl1 != null)
							{
								for (int i1 = 0; i1 < nl1.getLength(); i1++)
								{
									Node n1 = nl1.item(i1);
									if (n1 instanceof Element)
									{
										Element e1 = (Element) n1;
										handleElement(e1, out, 2, props, lang);
									}
								}
							}
							
							for (String name : lists.keySet())
							{
								out.println("\t\t<list name=\"" + name + "\">");
								for (String item : lists.get(name))
								{
									item = item.replace("&", "&amp;");
									item = item.replace("<", "&lt;");
									item = item.replace(">", "&gt;");
									item = item.replace("\"", "&quot;");
									out.println("\t\t\t<item>" + item + "</item>");
								}
								out.println("\t\t</list>");
							}
							
							out.println("\t</highlighting>");
						}
						else
							out.println(xmlElementWithChildren(e, 1));
					}
				}
			}
			
			out.println("</" + root.getTagName() + ">");
			props.store(new FileOutputStream(propsFile), "user input");
		}
		
		zip.close();
		props.store(new FileOutputStream(propsFile), "user input");
	}
}
