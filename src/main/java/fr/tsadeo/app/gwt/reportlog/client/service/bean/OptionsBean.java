package fr.tsadeo.app.gwt.reportlog.client.service.bean;

import com.google.gwt.core.client.JavaScriptObject;

import fr.tsadeo.app.gwt.reportlog.client.util.IJsonNode;
import fr.tsadeo.app.gwt.reportlog.client.util.JsObjectHelper;
import fr.tsadeo.app.gwt.reportlog.client.util.JsonNode;

/**
 * options de l'application
 * @author sylvie
 *
 */
public class OptionsBean implements IJsonNode {

	private boolean _saveParamAutomatically = true;
	private boolean _askBeforeRestoreParams = true;
	
	//----------------------------------- overriding IJsonNode
	@Override
	public JsonNode toJsonNode() {
		
		final JsonNode jsonNode = new JsonNode();
	    jsonNode.addBooleanValue("saveAutomatically", this._saveParamAutomatically);
	    jsonNode.addBooleanValue("askBeforeRestore", this._askBeforeRestoreParams);
	    return jsonNode;
	}
	@Override
	public void populateFromJavaScriptObject(JavaScriptObject jsObject) {
		this._saveParamAutomatically = JsObjectHelper.getKeyBoolean(jsObject, "saveAutomatically", true);
		this._askBeforeRestoreParams = JsObjectHelper.getKeyBoolean(jsObject, "askBeforeRestore", true);
	}
	//------------------------------------- accessors
	public boolean isSaveParamAutomatically() {
		return _saveParamAutomatically;
	}
	public void setSaveParamAutomatically(boolean saveParamAutomatically) {
		this._saveParamAutomatically = saveParamAutomatically;
	}
	public boolean isAskBeforeRestoreParams() {
		return _askBeforeRestoreParams;
	}
	public void setAskBeforeRestoreParams(boolean askBeforeRestoreParams) {
		this._askBeforeRestoreParams = askBeforeRestoreParams;
	}


	
	
}
