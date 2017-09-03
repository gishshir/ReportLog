package fr.tsadeo.app.gwt.reportlog.client.service.bean;

import com.google.gwt.core.client.JavaScriptObject;

import fr.tsadeo.app.gwt.reportlog.client.util.IJsonNode;
import fr.tsadeo.app.gwt.reportlog.client.util.JsObjectHelper;
import fr.tsadeo.app.gwt.reportlog.client.util.JsonNode;
/**
 * Dans un block des paramètres d'affichage pour une keyToFind avec mode attribut json,
 * attribut/value json à afficher
 * @author sylvie
 *
 */
public class AttributToDisplayBean  extends AbstractBean implements IJsonNode  {

	private boolean _selected = true;
	private final String _attributName;
	private String _attributValue;
	
	
	public final static String getAttributNameFromJsObject(final JavaScriptObject jsObject) {
		return (jsObject==null)?null:JsObjectHelper.getKeyString(jsObject, "attributName");
	}
	//------------------------------------ overriding IJsonNode
	@Override
	public JsonNode toJsonNode() {
		final JsonNode jsonNode = new JsonNode();
		jsonNode.addBooleanValue("selected", this._selected);
		jsonNode.addStringValue("attributName", this._attributName);
		jsonNode.addStringValue("attributValue", this._attributValue);
		return jsonNode;
	}

	@Override
	public void populateFromJavaScriptObject(final JavaScriptObject jsObject) {
		if (jsObject == null) return;
		if (this._attributName == null) {
			throw new RuntimeException("attributName cannot be null!");
		}
        this._selected = JsObjectHelper.getKeyBoolean(jsObject, "selected");
		this._attributValue = JsObjectHelper.getKeyString(jsObject, "attributValue");
	}
	// ----------------------------------- constructor
	AttributToDisplayBean(final String attributName) {
		this._attributName = attributName;
	}

	// ----------------------------------- accessor
	public boolean isSelected() {
		return _selected;
	}

	public void setSelected(boolean selected) {
		this.hasValueChanged(this._selected, selected);
		this._selected = selected;
	}

	public String getAttributValue() {
		return _attributValue;
	}

	public void setAttributValue(String attributValue) {
		this.hasValueChanged(this._attributValue, attributValue);
		this._attributValue = attributValue;
	}

	public String getAttributName() {
		return _attributName;
	}

	// ----------------------------------- overriding IReportLogBean
	@Override
	public void clean() {
		super.clean();
		this._selected = false;
		this._attributValue = null;
	}


}