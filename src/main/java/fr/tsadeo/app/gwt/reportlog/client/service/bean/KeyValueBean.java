package fr.tsadeo.app.gwt.reportlog.client.service.bean;

import com.google.gwt.core.client.JavaScriptObject;

import fr.tsadeo.app.gwt.reportlog.client.service.AppApplication;
import fr.tsadeo.app.gwt.reportlog.client.util.IJsonNode;
import fr.tsadeo.app.gwt.reportlog.client.util.JsObjectHelper;
import fr.tsadeo.app.gwt.reportlog.client.util.JsonNode;


public final class KeyValueBean  extends AbstractBean  implements IJsonNode {

	private boolean _selected;
	private boolean _regex = false;
	private boolean _remove = false;
	private final String _keyName;
	private String _keyValue;
	
	// container thématique. default if not exist
	private int _viewId = ViewKeyValueBean.DEFAULT_ID;
	
	public static KeyValueBean buildKeyValueBeanFromJsObject(final JavaScriptObject jsObject) {
		
		if (jsObject == null){
			return null;
		}
		
		final String keyName = JsObjectHelper.getKeyString(jsObject, "keyName");
		KeyValueBean keyValueBean = new KeyValueBean(keyName);
		keyValueBean.populateFromJavaScriptObject(jsObject);

		return keyValueBean;
	}

	//---------------------------------- constructor
	public KeyValueBean(final String keyName) {
		this._keyName = keyName;
	}
	//----------------------------------- overriding IJsonNode
	@Override
	public JsonNode toJsonNode() {

        final JsonNode jsonNode = new JsonNode();
        jsonNode.addBooleanValue("selected", this._selected);
        jsonNode.addBooleanValue("regex", this._regex);
        jsonNode.addBooleanValue("remove", this._remove);
        jsonNode.addStringValue("keyName", this._keyName);
        jsonNode.addStringValue("keyValue", this._keyValue);
        jsonNode.addIntegerValue("viewId", this._viewId);
        
		return jsonNode;
	}

	@Override
	public void populateFromJavaScriptObject(final JavaScriptObject jsObject) {

		if (jsObject == null) return;
        this._selected = JsObjectHelper.getKeyBoolean(jsObject, "selected");
        this._regex =  JsObjectHelper.getKeyBoolean(jsObject, "regex");
        this._remove =  JsObjectHelper.getKeyBoolean(jsObject, "remove");
        this._keyValue = JsObjectHelper.getKeyString(jsObject, "keyValue");
        
        final Integer id = JsObjectHelper.getKeyNumber(jsObject, "viewId");
        // if null then build default view else populate
		this._viewId = (id == null)?ViewKeyValueBean.DEFAULT_ID:id.intValue();

	}
	// ----------------------------------- accessor
	public int getViewId() {
		return this._viewId;
	}
	public void setViewId(final int viewId) {
        this.hasValueChanged(this._viewId, viewId);
		this._viewId = viewId;
	}
	public boolean isSelected() {
		return _selected;
	}
	
	public boolean isRegex() {
		return this._regex;
	}
	
	public boolean isRemove() {
		return this._remove;
	}

	public void setSelected(boolean selected) {
		this.hasValueChanged(this._selected, selected);
		this._selected = selected;
	}
	
	public void setRegex(boolean regex) {
		this.hasValueChanged(this._regex, regex);
		this._regex = regex;
	}
	
	public void setRemove(boolean remove) {
		this.hasValueChanged(this._remove, remove);
		this._remove = remove;
	}

	public String getKeyName() {
		return _keyName;
	}

	public String getKeyValue() {
		return _keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.hasValueChanged(this._keyValue, keyValue);
		this._keyValue = keyValue;
	}
	

	// ----------------------------------- overriding IReportLogBean
	@Override
	public void clean() {
		super.clean();
		this._selected = false;
		this._regex = false;
		this._remove = false;
		this._keyValue = null;
	}

}