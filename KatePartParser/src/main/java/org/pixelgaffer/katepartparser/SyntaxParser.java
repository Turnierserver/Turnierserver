package org.pixelgaffer.katepartparser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import lombok.Getter;
import lombok.ToString;

import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import org.pixelgaffer.katepartparser.context.Context;
import org.pixelgaffer.katepartparser.context.ContextRule;
import org.pixelgaffer.katepartparser.context.RulesFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@ToString(exclude = { "lists", "contexts" })
public class SyntaxParser
{
	@Getter
	private File file;
	
	@Getter
	private Style style;
	
	// required attributes of the root element
	@Getter
	private String name;
	@Getter
	private String section;
	@Getter
	private Set<String> extensions;
	// optional attributes of the root element
	@Getter
	private String mimetype;
	@Getter
	private String version;
	@Getter
	private String kateversion;
	@Getter
	private int priority;
	@Getter
	private String author;
	@Getter
	private String license;
	
	// all lists inside the highlighting element
	@Getter
	private Map<String, List<String>> lists = new HashMap<>();
	
	// all contexts
	@Getter
	private Map<String, Context> contexts = new HashMap<>();
	
	// der default context
	private Context defaultContext;
	
	// all item datas
	@Getter
	private Map<String, NamedStyleEntry> itemDatas = new HashMap<>();
	
	public SyntaxParser (String filename, Style s) throws ParserConfigurationException, IOException, SAXException
	{
		this(filename == null ? null : new File(filename), s);
	}
	
	public SyntaxParser (File syntaxFile, Style s) throws ParserConfigurationException, IOException, SAXException
	{
		file = syntaxFile;
		style = s;
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setValidating(false);
		documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document doc = documentBuilder.parse(file);
		
		Element rootElement = doc.getDocumentElement();
		name = rootElement.getAttribute("name");
		section = rootElement.getAttribute("section");
		extensions = new HashSet<>(Arrays.asList(rootElement.getAttribute("extensions").split(";")));
		mimetype = rootElement.getAttribute("mimetype");
		version = rootElement.getAttribute("version");
		kateversion = rootElement.getAttribute("kateversion");
		try
		{
			priority = Integer.parseInt(rootElement.getAttribute("priority"));
		}
		catch (NumberFormatException nfe)
		{
		}
		author = rootElement.getAttribute("author");
		license = rootElement.getAttribute("license");
		
		NodeList nodeList = rootElement.getChildNodes();
		if (nodeList != null)
		{
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node n = nodeList.item(i);
				if (n instanceof Element)
				{
					Element e = (Element)n;
					if (e.getTagName().equals("highlighting"))
					{
						parseHighlighting(e);
					}
					else
					{
						System.err.println("Unbekanntes Element: " + e);
					}
				}
			}
		}
	}
	
	private void parseHighlighting (Element highlighting)
	{
		NodeList nodeList = highlighting.getChildNodes();
		if (nodeList != null)
		{
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node n = nodeList.item(i);
				if (n instanceof Element)
				{
					Element e = (Element)n;
					// ###Liste##############################################################################
					if (e.getTagName().equals("list"))
					{
						List<String> list = new ArrayList<>();
						NodeList nodeList1 = e.getElementsByTagName("item");
						if (nodeList1 != null)
						{
							for (int j = 0; j < nodeList1.getLength(); j++)
							{
								Node n1 = nodeList1.item(j);
								if (n1 instanceof Element)
								{
									Element e1 = (Element)n1;
									list.add(e1.getTextContent().trim());
								}
							}
						}
						lists.put(e.getAttribute("name"), list);
					}
					// ###Context############################################################################
					else if (e.getTagName().equals("contexts"))
					{
						NodeList contexts = e.getChildNodes();
						if (contexts != null)
						{
							for (int j = 0; j < contexts.getLength(); j++)
							{
								Node n1 = contexts.item(j);
								if (n1 instanceof Element)
								{
									Context context = parseContext((Element)n1);
									this.contexts.put(context.getName(), context);
								}
							}
						}
					}
					// ###ItemData###########################################################################
					else if (e.getTagName().equals("itemDatas"))
					{
						NodeList itemDatas = e.getChildNodes();
						if (itemDatas != null)
						{
							for (int j = 0; j < itemDatas.getLength(); j++)
							{
								Node n1 = itemDatas.item(j);
								if (n1 instanceof Element)
								{
									Element itemData = (Element)n1;
									if (!itemData.getTagName().equals("itemData"))
									{
										System.err
												.println("Unbekanntes Element in " + file.getName() + ": " + itemData);
										continue;
									}
									
									try
									{
										String name = itemData.getAttribute("name");
										StyleEntry parent = style.getEntry(itemData.getAttribute("defStyleNum"));
										NamedStyleEntry styleEntry;
										if (parent == null)
										{
											System.err.println("Undefinierter Standart-Stil: "
													+ itemData.getAttribute("defStyleNum"));
											styleEntry = new NamedStyleEntry();
										}
										else
											styleEntry = new NamedStyleEntry(parent);
										styleEntry.setName("itemData" + j);
										
										if (itemData.hasAttribute("color"))
											styleEntry.setColor(itemData.getAttribute("color"));
										
										this.itemDatas.put(name, styleEntry);
									}
									catch (NoSuchMethodException nsme)
									{
										System.err.println("Unbekannter Standart-Stil in " + file.getName() + ": "
												+ nsme.getMessage());
									}
									catch (SecurityException se)
									{
										se.printStackTrace();
									}
									catch (ReflectiveOperationException roe)
									{
										roe.printStackTrace();
									}
								}
							}
						}
					}
					
					else
					{
						System.err.println("Unbekanntes Element in " + file.getName() + ": " + e);
					}
				}
			}
		}
	}
	
	private Context parseContext (Element context)
	{
		Context c = new Context(context.getAttribute("name"), context.getAttribute("attribute"),
				context.getAttribute("lineEndContext"));
		if (context.getAttribute("fallthrough") != null)
		{
			c.setFallthrough(Boolean.parseBoolean(context.getAttribute("fallthrough")));
			c.setFallthroughContext(context.getAttribute("fallthroughContext"));
		}
		if (defaultContext == null)
			defaultContext = c;
		
		NodeList nodeList = context.getChildNodes();
		if (nodeList != null)
		{
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node n = nodeList.item(i);
				if (n instanceof Element)
				{
					Element e = (Element)n;
					if (e.getTagName().equals("IncludeRules"))
					{
						Context toInclude = contexts.get(e.getAttribute("context"));
						if (toInclude == null)
							System.err.println("Unbekannter Context in " + file.getName() + ": "
									+ e.getAttribute("context"));
						else
							c.getRules().addAll(toInclude.getRules());
					}
					else
					{
						try
						{
							ContextRule rule = RulesFactory.parseRule(e);
							c.getRules().add(rule);
						}
						catch (ClassNotFoundException cnfe)
						{
							System.err.println("Unbekannte Regel in " + file.getName() + ": " + cnfe.getMessage());
						}
						catch (SecurityException se)
						{
							se.printStackTrace();
						}
						catch (ReflectiveOperationException roe)
						{
							roe.printStackTrace();
						}
					}
				}
			}
		}
		
		return c;
	}
	
	public URL generateStylesheet (String id) throws IOException
	{
		File tmp = File.createTempFile("stylesheet", ".css");
		tmp.deleteOnExit();
		PrintWriter out = new PrintWriter(tmp);
		for (NamedStyleEntry entry : itemDatas.values())
			out.println(entry.toCss(false));
		out.println("#" + id + "{-fx-background-color:" + style.getNormal().getBgColor() + ";"
				+ "-fx-text-fill:" + style.getNormal().getColor() + ";"
				+ "-fx-prompt-text-fill:" + style.getNormal().getColor() + "}");
		out.close();
		return tmp.toURI().toURL();
	}
	
	public StyleSpans<Collection<String>> computeHighlighting (String text)
	{
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
		
		Deque<Context> context = new LinkedList<>();
		context.offer(defaultContext);
		
		Deque<String> lines = new LinkedList<>(Arrays.asList(text.split("\n")));
		
		String line = lines.pollFirst();
		while (line != null)
		{
			int pos = 0;
			lineloop: while (line.length() > pos)
			{
				if (context.isEmpty())
				{
					System.err.println("Fehler in " + file.getName() + ": Die Context-Liste ist leer");
					context.offer(defaultContext);
				}
				
				for (ContextRule rule : context.getFirst().getRules())
				{
					int chars = rule.matches(line, pos, lists);
					if (chars > 0)
					{
						String attribute = rule.getAttribute();
						if (attribute == null)
							attribute = context.getFirst().getAttribute();
						spansBuilder.add(Collections.singleton(itemDatas.get(attribute).getName()), chars);
						pos += chars;
						
						String c = rule.getContext();
						while (c.startsWith("#pop"))
						{
							c = c.substring(4);
							if (context.size() > 1)
								context.pollFirst();
							else
								System.err.println("#pop für oberstes Element in " + file.getName());
						}
						if (!c.isEmpty() && !c.equals("#stay"))
						{
							if (contexts.containsKey(c))
								context.addFirst(contexts.get(c));
							else
								System.err.println("Unbekannter Context in " + file.getName() + " (referenziert von " + rule + ")");
						}
						
						continue lineloop;
					}
				}
				String attribute = context.getFirst().getAttribute();
				spansBuilder.add(Collections.singleton(itemDatas.get(attribute).getName()), 1);
				pos++;
			}
			
			line = lines.pollFirst();
			if (line != null)
				spansBuilder.add(Collections.emptyList(), 1);
			String c = context.getFirst().getLineEndContext();
			while (c.startsWith("#pop"))
			{
				c = c.substring(4);
				if (context.size() > 1)
					context.pollFirst();
				else
					System.err.println("#pop für oberstes Element in " + file.getName());
			}
			if (!c.isEmpty() && !c.equals("#stay"))
			{
				if (contexts.containsKey(c))
					context.addFirst(contexts.get(c));
				else
					System.err.println("Unbekannter Context in " + file.getName() + " (referenziert von " + context.getFirst().getName() + ")");
			}
		}
		
		return spansBuilder.create();
	}
}
