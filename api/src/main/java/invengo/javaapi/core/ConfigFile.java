package invengo.javaapi.core;

import invengo.javaapi.core.Util.LogType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

public class ConfigFile {

	private Document doc = new Document();
	private XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
	private Element readers;
	private String fn = APIPath.folderName + "Sysit.xml";
	private File file = new File(fn);

	public ConfigFile() {
		if (!file.exists()) {
			create();
		}
		try {
			doc = new SAXBuilder().build(fn);
			readers = doc.getRootElement();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Element getReaders() {
		return readers;
	}

	public void setReaders(Element readers) {
		this.readers = readers;
	}

	private void create() {
		try {
			Element rootElement = new Element("Readers");
			doc.setRootElement(rootElement);
			ConfigFileItem cfi = new ConfigFileItem();
			Element reader = new Element("Reader");
			reader.setAttribute("Name", cfi.getReaderName());
			reader.setAttribute("Group", cfi.getReaderGroup());
			reader.setAttribute("Enable", String.valueOf(cfi.isEnable())
					.toLowerCase());
			Element port = new Element("Port");
			port.setAttribute("Type", cfi.getType());
			port.setAttribute("Protocol", cfi.getProtocol());
			port.addContent(cfi.getConnStr());
			reader.addContent(port);
			rootElement.addContent(reader);
			xmlOut.output(doc, new FileOutputStream(new File(fn)));
		} catch (Exception e) {
			 Util.logAndTriggerApiErr("Sysit.xml", "FF13", e.getMessage(),
					LogType.Fatal);
		}
	}

	public void addReaderItem(ConfigFileItem item) {
		if (item == null) {
			Util.logAndTriggerApiErr("Sysit.xml", "FF14", "", LogType.Debug);
			return;
		}
		try {
			Element element = (Element) XPath.newInstance(
					"//Reader[@Name='" + item.getReaderName() + "']")
					.selectSingleNode(doc);
			if (element == null) {
				Element reader = new Element("Reader");
				reader.setAttribute("Name", item.getReaderName());
				reader.setAttribute("Group", item.getReaderGroup());
				reader.setAttribute("Enable", String.valueOf(item.isEnable())
						.toLowerCase());
				Element port = new Element("Port");
				port.setAttribute("Type", item.getType());
				port.setAttribute("Protocol", item.getProtocol());
				port.addContent(item.getConnStr());
				reader.addContent(port);
				readers.addContent(reader);
				xmlOut.output(doc, new FileOutputStream(new File(fn)));
			} else {
				 Util.logAndTriggerApiErr("Sysit.xml", "FF15", "",
				 LogType.Debug);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeReaderItem(String readerName) {
		try {
			readerName = Util.xmlStringReplace(readerName);
			Element element = (Element) XPath.newInstance(
					"//Reader[@Name='" + readerName + "']").selectSingleNode(
					doc);
			if (element != null) {
				doc.removeContent(element);
				xmlOut.output(doc, new FileOutputStream(fn));
			} else {
				 Util.logAndTriggerApiErr("Sysit.xml", "FF16", "",
				 LogType.Debug);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ConfigFileItem findReaderItem(String readerName) {
		ConfigFileItem instance = null;
		try {
			readerName = Util.xmlStringReplace(readerName);
			Element element = (Element) XPath.newInstance(
					"//Reader[@Name='" + readerName + "']").selectSingleNode(
					doc);
			if (element != null) {
				instance = new ConfigFileItem();
				instance.setReaderName(element.getAttributeValue("Name")
						.toString());
				instance.setReaderGroup(element.getAttributeValue("Group")
						.toString());
				instance.setEnable(Boolean.parseBoolean(element
						.getAttributeValue("Enable").toString()));
				instance.setType(element.getChild("Port").getAttributeValue(
						"Type"));
				instance.setProtocol(element.getChild("Port")
						.getAttributeValue("Protocol"));
				instance.setConnStr(element.getChildText("Port"));
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return instance;
	}

	public void modifyReaderItem(String targetReaderName, ConfigFileItem item) {
		targetReaderName = Util.xmlStringReplace(targetReaderName);
		try {
			Element element = (Element) XPath.newInstance(
					"//Reader[@Name='" + targetReaderName + "']")
					.selectSingleNode(doc);
			if (element != null) {
				element.getAttribute("Name").setValue(item.getReaderName());
				element.getAttribute("Group").setValue(item.getReaderGroup());
				element.getAttribute("Enable").setValue(
						String.valueOf(item.isEnable()));
				element.getChild("Port").getAttribute("Type").setValue(
						item.getType());
				element.getChild("Port").getAttribute("Protocol").setValue(
						item.getProtocol());
				element.getChild("Port").setText(item.getConnStr());
				xmlOut.output(doc, new FileOutputStream(new File(fn)));
			} else {
				 Util.logAndTriggerApiErr("Sysit.xml", "FF16", "",
				 LogType.Debug);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
