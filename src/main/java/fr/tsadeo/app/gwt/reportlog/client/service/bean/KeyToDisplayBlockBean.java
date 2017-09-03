package fr.tsadeo.app.gwt.reportlog.client.service.bean;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import fr.tsadeo.app.gwt.reportlog.client.service.bean.ParamsBean.DisplayMode;
import fr.tsadeo.app.gwt.reportlog.client.util.IJsonNode;
import fr.tsadeo.app.gwt.reportlog.client.util.JsObjectHelper;
import fr.tsadeo.app.gwt.reportlog.client.util.JsonNode;
/**
 * paramètre d'affichage pour une keyToFind
 * - mode d'affichage [toute la ligne / next json / attributs json]
 * - si mode attributs json, liste des attributs
 * @author sylvie
 *
 */
public class KeyToDisplayBlockBean  extends AbstractBean implements IJsonNode  {

	//private final KeyValueBean _keyToFind;
	private final String _keyToFindName;
	private DisplayMode _displayMode;
	private final List<AttributToDisplayBean> _listAttributToDisplay = new ArrayList<AttributToDisplayBean>();
	
	public static final String getKeyToFindNameFromJsObject(final JavaScriptObject jsObject) {
		return (jsObject== null)?null:JsObjectHelper.getKeyString(jsObject, "keyToFindName");
	}

   //------------------------------------- overriding IJsonNode
	@Override
	public JsonNode toJsonNode() {
		final JsonNode jsonNode = new JsonNode();
		jsonNode.addStringValue("keyToFindName", this._keyToFindName);
		jsonNode.addStringValue("displayMode", this._displayMode.name());
		
		final List<JsonNode> nodes = new ArrayList<JsonNode>();
		for (AttributToDisplayBean attributToDisplay : this._listAttributToDisplay) {
			nodes.add(attributToDisplay.toJsonNode());
		}
		jsonNode.addNodeList("listAttributToDisplay", nodes);
		return jsonNode;
	}
	@Override
	public void populateFromJavaScriptObject(final JavaScriptObject jsObject) {
		if (jsObject == null) return;
		
		if (this._keyToFindName == null) {
			throw new RuntimeException("keyToFind cannot be null!!");
		}
		final String displayModeStr = JsObjectHelper.getKeyString(jsObject, "displayMode");
		this._displayMode =(displayModeStr== null)?null: DisplayMode.valueOf(displayModeStr);
		
		// list of AttributToDisplayBean
		final JsArray<JavaScriptObject> list = JsObjectHelper.getKeyJsArray(jsObject, "listAttributToDisplay");
	    if (list == null) return;
	    
	    for (int i = 0; i < list.length(); i++) {
			final JavaScriptObject attributToDisplayJs = list.get(i);
			if (attributToDisplayJs == null) continue;
			final String attributName = AttributToDisplayBean.getAttributNameFromJsObject(attributToDisplayJs);
			final AttributToDisplayBean attributToDisplay =
					this.createAttributToDisplayBean(attributName);
			attributToDisplay.populateFromJavaScriptObject(attributToDisplayJs);
		}
	}

	// ----------------------------------- public method
	public AttributToDisplayBean createAttributToDisplayBean(
			final String attributName) {
		final AttributToDisplayBean attributToDisplayBean = new AttributToDisplayBean(
				attributName);
		this._listAttributToDisplay.add(attributToDisplayBean);
		this.setHasChanged();
		return attributToDisplayBean;
	}

	// ----------------------------------- constructor
	public KeyToDisplayBlockBean(String keyToFindName) {
		this._keyToFindName = keyToFindName;
	}

	// ----------------------------------- accessor
	public DisplayMode getDisplayMode() {
		return _displayMode;
	}

	public void setDisplayMode(DisplayMode displayMode) {
		this.hasValueChanged(this._displayMode, displayMode);
		this._displayMode = displayMode;
	}

	public String getKeyToFindName() {
		return _keyToFindName;
	}

	public List<AttributToDisplayBean> getListAttributToDisplay() {
		return _listAttributToDisplay;
	}

	// ----------------------------------- overriding IReportLogBean
	@Override
	public void clean() {
		super.clean();
		this._displayMode = DisplayMode.ALL_LINE;
		this._listAttributToDisplay.clear();
	}

	@Override
	public void setHasBeenProcessed() {
		super.setHasBeenProcessed();
		for (int i = 0; i < this._listAttributToDisplay.size(); i++) {
			this._listAttributToDisplay.get(i).setHasBeenProcessed();
		}
	}

}

