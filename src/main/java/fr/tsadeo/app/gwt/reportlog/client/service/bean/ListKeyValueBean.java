package fr.tsadeo.app.gwt.reportlog.client.service.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import fr.tsadeo.app.gwt.reportlog.client.util.IJsonNode;
import fr.tsadeo.app.gwt.reportlog.client.util.JsObjectHelper;
import fr.tsadeo.app.gwt.reportlog.client.util.JsonNode;

/**
 * list des TypeKeyValue avec le typage TypeKeyValue
 * @author sylvie
 *
 */
public class ListKeyValueBean extends ArrayList<KeyValueBean>  implements IJsonNode  {
	
	private static final long serialVersionUID = 1L;
	 private final static Logger log = Logger.getLogger("ListKeyValueBean");

	public enum TypeKeyValue {find, remove, both, none}
	
	
	/**
	 * Détermine si les cles à rechercher sont toutes du meme type (findKeys or removeKey) ou
	 * bien mélangées (find and remove)
	 */
	private TypeKeyValue _typeKeyValue = TypeKeyValue.none;
	
	//------------------------------------------------ overriding IJsonNode
	@Override
	public JsonNode toJsonNode() {

	    final JsonNode jsonNode = new JsonNode();
	    jsonNode.addStringValue("typeKeyValue", this.getTypeKeyValues().name());
	    
	    final List<JsonNode> nodes = new ArrayList<JsonNode>();
	    for (KeyValueBean keyValueBean : this) {
	    	nodes.add(keyValueBean.toJsonNode());
		}
	    jsonNode.addNodeList("listKeyValueBean", nodes);
		return jsonNode;
	}



	@Override
	public void populateFromJavaScriptObject(JavaScriptObject jsObject) {

		if (jsObject == null) return;
		this.clear();
        final String keyValueType = JsObjectHelper.getKeyString(jsObject, "typeKeyValue");
		this._typeKeyValue = (keyValueType == null)?TypeKeyValue.none
				:TypeKeyValue.valueOf(keyValueType);
		
		// list of keyValueBean
		final JsArray<JavaScriptObject> list = JsObjectHelper.getKeyJsArray(jsObject, "listKeyValueBean");
	    if (list == null) return;
	    
	    log.config("list: " + JsObjectHelper.toJsonString(list));
	    for (int i = 0; i < list.length(); i++) {
			final JavaScriptObject keyValueJs = list.get(i);
			if (keyValueJs == null) continue;
			this.add(KeyValueBean.buildKeyValueBeanFromJsObject(keyValueJs));			
		}
	}

	
	//------------------------------------------------ accessors
	public TypeKeyValue getTypeKeyValues() {
		if (this._typeKeyValue == TypeKeyValue.none) {
			for (KeyValueBean keyValueBean : this) {
				
				if (this._typeKeyValue == TypeKeyValue.both) break;
				if(!keyValueBean.isSelected()) break;
				
				// remove key
				if (keyValueBean.isRemove()) {
					switch (this._typeKeyValue) {
					  case none: this._typeKeyValue = TypeKeyValue.remove;
						break;
					  case find: this._typeKeyValue = TypeKeyValue.both;
						  break;
					  case remove:
						  break;
					}
				}
				else { //Find key
					switch (this._typeKeyValue) {
					  case none: this._typeKeyValue = TypeKeyValue.find;
						break;
					  case find: 
						  break;
					  case remove: this._typeKeyValue = TypeKeyValue.both;
						  break;
					}
				}
			}
		}
		return this._typeKeyValue;
	}

	 public void clear() {
		 this._typeKeyValue = TypeKeyValue.none;
		 super.clear();
	 }



}
