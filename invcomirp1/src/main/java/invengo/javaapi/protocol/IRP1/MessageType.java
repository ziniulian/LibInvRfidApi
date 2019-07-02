package invengo.javaapi.protocol.IRP1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

@SuppressWarnings("unchecked")
public class MessageType {

	public final static Map<String, Integer> msgClass = new HashMap<String, Integer>();
	public final static Map<Integer, String> msgType = new HashMap<Integer, String>();
	public final static Map<String, Integer> msgReadTag = new HashMap<String, Integer>();

	static {
		SAXBuilder sb = new SAXBuilder();
		try {
			Document doc = sb.build(new MessageType().getClass()
					.getResourceAsStream("/messages/IRP1Message.xml"));
			List<Element> nodes = doc.getRootElement().getChildren("Message");
			for (Element element : nodes) {
				if (element.getAttributeValue("Name").equals("ReadTag")) {
					List<Element> list = element.getChildren("Item");
					for (Element e : list) {
						msgReadTag.put(e.getAttributeValue("Name"),
								Integer.parseInt(e.getAttributeValue("Type"),
										16));
					}
				}
				msgClass.put(element.getAttributeValue("Name"),
								Integer.parseInt(element
										.getAttributeValue("Type"), 16));
				msgType.put(Integer.parseInt(element.getAttributeValue("Type"),
						16), element.getAttributeValue("Name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
