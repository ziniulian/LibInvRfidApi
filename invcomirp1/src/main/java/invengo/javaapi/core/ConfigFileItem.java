package invengo.javaapi.core;

public class ConfigFileItem {

	private String readerName = "Reader1";
	private String readerGroup = "Group1";
	private boolean enable = true;
	private String type = "RS232";
	private String protocol = "IRP1";
	private String connStr = "COM1,115200";

	public String getReaderName() {
		return Util.xmlString(readerName);
	}

	public void setReaderName(String readerName) {
		this.readerName = Util.xmlStringReplace(readerName);
	}

	public String getReaderGroup() {
		return Util.xmlString(readerGroup);
	}

	public void setReaderGroup(String readerGroup) {
		this.readerGroup = Util.xmlStringReplace(readerGroup);
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public String getType() {
		return Util.xmlString(type);
	}

	public void setType(String type) {
		this.type = Util.xmlStringReplace(type);
	}

	public String getProtocol() {
		return Util.xmlString(protocol);
	}

	public void setProtocol(String protocol) {
		this.protocol = Util.xmlStringReplace(protocol);
	}

	public String getConnStr() {
		return Util.xmlString(connStr);
	}

	public void setConnStr(String connStr) {
		this.connStr = Util.xmlStringReplace(connStr);
	}

	public ConfigFileItem() {

	}

	public ConfigFileItem(String name, String group, boolean enable,
			String portType, String protocol, String connStr) {
		this.readerName = Util.xmlStringReplace(name);
		this.readerGroup = Util.xmlStringReplace(group);
		this.enable = enable;
		this.type = Util.xmlStringReplace(portType);
		this.protocol = Util.xmlStringReplace(protocol);
		this.connStr = Util.xmlStringReplace(connStr);
	}

}
