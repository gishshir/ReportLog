package fr.tsadeo.app.gwt.reportlog.client.service;

import com.google.gwt.core.client.JavaScriptObject;

import fr.tsadeo.app.gwt.reportlog.client.util.IJsonNode;
import fr.tsadeo.app.gwt.reportlog.client.util.JsObjectHelper;
import fr.tsadeo.app.gwt.reportlog.client.util.JsonNode;

public enum Level implements IJsonNode{
	
	ERROR("[ERROR"), WARN("[WARN"), INFO("[INFO"), DEBUG("[DEBUG"), TRACE("[TRACE");
	
	private String _value;
	public void setValue(final String value) {
		this._value = value;
	}
	public String getValue() {
		return this._value;
	}
	Level(final String value) {
		this._value = value;
	}
	
	public static final String getLevelNameFromJs(final JavaScriptObject jsObject) {
		if (jsObject == null) return null ;
		return JsObjectHelper.getKeyString(jsObject, "name");
	}
	//-------------------------------
	@Override
	public JsonNode toJsonNode() {
		final JsonNode jsonNode = new JsonNode();
		jsonNode.addStringValue("name", this.name());
		jsonNode.addStringValue("value", this.getValue());
		return jsonNode;
	}
	@Override
	public void populateFromJavaScriptObject(final JavaScriptObject jsObject) {
		if (jsObject == null) return;
		this._value = JsObjectHelper.getKeyString(jsObject, "value");
	}
};