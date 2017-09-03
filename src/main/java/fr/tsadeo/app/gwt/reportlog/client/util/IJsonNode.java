package fr.tsadeo.app.gwt.reportlog.client.util;

import com.google.gwt.core.client.JavaScriptObject;


public interface IJsonNode {

	public JsonNode toJsonNode();
	
	public void populateFromJavaScriptObject(final JavaScriptObject javaScriptObject);
}
