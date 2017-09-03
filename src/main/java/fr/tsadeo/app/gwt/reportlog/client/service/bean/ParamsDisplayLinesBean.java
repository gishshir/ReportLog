package fr.tsadeo.app.gwt.reportlog.client.service.bean;

import com.google.gwt.core.client.JavaScriptObject;

import fr.tsadeo.app.gwt.reportlog.client.util.IJsonNode;
import fr.tsadeo.app.gwt.reportlog.client.util.JsObjectHelper;
import fr.tsadeo.app.gwt.reportlog.client.util.JsonNode;
/**
 * paramètres d'affichage des numéros de ligne et couleur de ligne
 * @author sylvie
 *
 */
public class ParamsDisplayLinesBean extends AbstractBean implements IJsonNode {
	
	private boolean _lineNumbers;
	private boolean _levelColor;
	
	 //------------------------------------------- overriding IJsonNode
	@Override
	public JsonNode toJsonNode() {
		final JsonNode jsonNode = new JsonNode();
		jsonNode.addBooleanValue("lineNumbers", this._lineNumbers);
		jsonNode.addBooleanValue("levelColor", this._levelColor);
		return jsonNode;
	}
	@Override
	public void populateFromJavaScriptObject(final JavaScriptObject jsObject) {

		if (jsObject == null) return;
		this._lineNumbers = JsObjectHelper.getKeyBoolean(jsObject, "lineNumbers");
		this._levelColor = JsObjectHelper.getKeyBoolean(jsObject, "levelColor");
	}
	 //------------------------------------------- accessors
    public void setLineNumbers(final boolean lineNumbers) {
    	if (lineNumbers != this._lineNumbers) {
    		this.setHasChanged();
    	}
    	this._lineNumbers = lineNumbers;
    }
    public boolean isLineNumbers() {
    	return this._lineNumbers;
    }
    
    public void setLevelColor(final boolean levelColor) {
    	if (levelColor != this._levelColor) {
    		this.setHasChanged();
    	}
    	this._levelColor = levelColor;
    }
    public boolean isLevelColor() {
    	return this._levelColor;
    }

}
