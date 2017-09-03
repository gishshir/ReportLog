package fr.tsadeo.app.gwt.reportlog.client.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsonUtils;

public class JsonNode {

	private List<String> values;
	private Map<String, JsonNode> nodes;
	private Map<String, List<JsonNode>> nodeList;
	
	public JsonNode() {
	}
	

	
	public String toJson(int marge) {
		List<String> finalValues = new ArrayList<String>();
		String result = "";

		result += "{";
		
		// values
		if (values != null) {
		  for (String value : values) {
			finalValues.add(value);
		  }
		}
		
		// nodes
		if (nodes != null) {
		  for (String key : nodes.keySet()) {
		 	finalValues.add("\""+key+"\":"+nodes.get(key).toJson(marge+1)+"");
		  }
		}
		
		// array of nodes
		if (nodeList != null) {
		  for (String key : nodeList.keySet()) {
			
			String tmpstr = "\""+key+"\":[";
			List<JsonNode> nodes = nodeList.get(key); 
			int size = nodes.size();
			
			int count = 0;
			for ( int j = 0; j < nodes.size(); j++ ) {
				count++;
				String virg = ( count < size ) ? "," : "";
				tmpstr += nodes.get(j).toJson(marge+2)+virg;
			}
			tmpstr += "]";
			finalValues.add(tmpstr);
		  }
		}
		
		// build result
		for ( int i = 0; i < finalValues.size(); i++ ) {
			String virg = ( finalValues.indexOf(finalValues.get(i)) < finalValues.size() -1 ) ? "," : "";
			result += finalValues.get(i)+virg;
		}
		result += "}";
		return result;
	}
	
	public void addBooleanValue(String name, boolean value) {
		this.addStringValue(name, Boolean.toString(value));
	}

	// build name: 'value'
	public void addStringValue(String name, String value ) {
		if ( value != null  && name.length() > 0) {
			this.getValues().add(this.buildNameDeuxPoints(name) + JsonUtils.escapeValue(value));
		}
	}
	
	// build name: value
	public void addIntegerValue(String name, Integer value ) {
		if ( value != null && name.length() > 0 ) {
			this.getValues().add(this.buildNameDeuxPoints(name) + value);
		}
	}
	
	// build name: [x, y, ...]
	public void addIntListValue(String name, JsArrayInteger valueList ) {
		if ( valueList == null || valueList.length() == 0 || name.length() == 0)
			return;
		
		String value = "[";
		switch (valueList.length()) {
		case 0:
			return;
		case 1:
			value = value + valueList.get(0) +"]";
			break;
		default:
			for ( int i = 0; i< valueList.length(); i++) {
				value = value + valueList.get(i) + ",";
			}
			value = value.substring(0,value.length()-1) + "]";
			break;
		} 

		this.getValues().add(this.buildNameDeuxPoints(name) + value);
		
	}
	
	// build name: [ 'x', 'y', ...]
	public void addStringListValue(String name, List<String> valueList ) {
		if ( valueList == null || valueList.size() == 0 || name.length() == 0)
			return;
		
		String value = "[";
		switch (valueList.size()) {
		case 0:
			value = null; 
			break;
		case 1:
			value += " "+ JsonUtils.escapeValue(valueList.get(0))+"]";
			break;
		default:
			for ( int i = 0; i< valueList.size(); i++) {
				value += " "+ JsonUtils.escapeValue(valueList.get(i)) + ", ";
			}
			value = value.substring(0,value.length()-2) + " ]";
			break;
		} 

		this.getValues().add(this.buildNameDeuxPoints(name) + value);
		
	}
	
	public void addJsObjectValue(String name, JavaScriptObject jsObject) {
		if ( jsObject != null ) {
			this.getValues().add(this.buildNameDeuxPoints(name) + jsObject.toString());
		} 
	}
	
	public void addNode( String name, JsonNode node ) {
		if ( name.length() > 0 && node != null )
			this.getNodes().put(name,node);
	}
	public void addNodeList( String name, List<JsonNode> nodes ) {
		if ( name.length() > 0 && nodes != null )
			this.getNodeList().put(name,nodes);
	}
	
	//---------------------------------------------------- private methods
	private List<String> getValues() {
		if (this.values == null) {
			this.values = new ArrayList<String>();
		}
		return this.values;
	}
	private Map<String, JsonNode> getNodes() {
		if (this.nodes == null) {
			this.nodes = new LinkedHashMap<String, JsonNode>();
		}
		return this.nodes;
	}
	private Map<String, List<JsonNode>> getNodeList() {
		if(this.nodeList == null) {
			this.nodeList = new LinkedHashMap<String, List<JsonNode>>();
		}
		return this.nodeList;
	}
	
	private String buildNameDeuxPoints(final String name) {
		return "\""+name+"\": ";
	}
}
