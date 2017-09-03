package fr.tsadeo.app.gwt.reportlog.client.service.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import fr.tsadeo.app.gwt.reportlog.client.util.IJsonNode;
import fr.tsadeo.app.gwt.reportlog.client.util.JsObjectHelper;
import fr.tsadeo.app.gwt.reportlog.client.util.JsonNode;

/**
 * Tous les paramètres gérant l'affichage :
 * - affichage des numéros de ligne et couleur de ligne
 * - paramètre d'affichage pour chaque keyToFind
 * @author sylvie
 *
 */
public class ParamsDisplayBean extends AbstractBean implements IJsonNode {

	// private final static Logger log = Logger.getLogger("ParamsDisplayBean");
	private ParamsDisplayLinesBean _linesBean = new ParamsDisplayLinesBean();
    private final Map<String, KeyToDisplayBlockBean> _mapKeyToDisplayBlockBeanByKeyName = new HashMap<String, KeyToDisplayBlockBean>();
	
    //------------------------------------------- overriding IJsonNode
	@Override
	public JsonNode toJsonNode() {
		final JsonNode jsonNode = new JsonNode();
		jsonNode.addNode("linesBean", this._linesBean.toJsonNode());
		
		//keyToDisplay
		final List<JsonNode> nodes = new ArrayList<JsonNode>();
		for (KeyToDisplayBlockBean keyToDisplayBlock : this._mapKeyToDisplayBlockBeanByKeyName.values()) {
			nodes.add(keyToDisplayBlock.toJsonNode());
		}
		jsonNode.addNodeList("listKeyToDisplayBlockBean", nodes);
		return jsonNode;
	}

	@Override
	public void populateFromJavaScriptObject(final JavaScriptObject jsObject) {
		
		if (jsObject == null) return;
		//log.config("populateFromJavaScriptObject(): " + JsObjectHelper.toJsonString(jsObject));
		this.setHasChanged();
		
		final JavaScriptObject linesJs = JsObjectHelper.getKeyObject(jsObject, "linesBean");
		if (linesJs != null) {
			this._linesBean.populateFromJavaScriptObject(linesJs);
		}
		
		//keyToDisplay
		this._mapKeyToDisplayBlockBeanByKeyName.clear();
		final JsArray<JavaScriptObject> list = JsObjectHelper.getKeyJsArray(jsObject, "listKeyToDisplayBlockBean");
		//log.config("list: " + JsObjectHelper.toJsonString(list));
		
		if (list != null) {
			for (int i = 0; i < list.length(); i++) {
				final JavaScriptObject keyToDisplayBlockJs = list.get(i);
				if (keyToDisplayBlockJs == null) {
					continue;
				}
				
				final String keyToFindName = KeyToDisplayBlockBean.getKeyToFindNameFromJsObject(keyToDisplayBlockJs);
				//final KeyValueBean keyToFind = AppApplication.get().getKeyValueBean(keyToFindName);
				if (keyToFindName == null) {
					continue;
				}
				
				final KeyToDisplayBlockBean keyToDisplayBlock = this.createKeyToDisplayBlock(keyToFindName);
				keyToDisplayBlock.populateFromJavaScriptObject(keyToDisplayBlockJs);
			}
		}
	}

	//--------------------------------------------------------
	public KeyToDisplayBlockBean createKeyToDisplayBlock(
			final String keyToFindName) {
		final KeyToDisplayBlockBean keyToDisplayBlock = new KeyToDisplayBlockBean(
				keyToFindName);

		this._mapKeyToDisplayBlockBeanByKeyName.put(keyToFindName, keyToDisplayBlock);
		this.setHasChanged();
		return keyToDisplayBlock;
	}
	

	public KeyToDisplayBlockBean getKeyToDisplayBlockBean (final String keyName) {
		return this._mapKeyToDisplayBlockBeanByKeyName.get(keyName);
	}
	// ----------------------------------- accessor
	public ParamsDisplayLinesBean getParamsDisplayLinesBean() {
		return this._linesBean;
	}

    public void setLineNumbers(final boolean lineNumbers) {
    	this._linesBean.setLineNumbers(lineNumbers);
    }
    public boolean isLineNumbers() {
    	return this._linesBean.isLineNumbers();
    }
    
    public void setLevelColor(final boolean levelColor) {
    	this._linesBean.setLevelColor(levelColor);
    }
    public boolean isLevelColor() {
    	return this._linesBean.isLevelColor();
    }
	// ----------------------------------- overriding IReportLogBean
	@Override
	public void clean() {
		super.clean();
		this._linesBean.clean();
		//this._listKeyToDisplay.clear();
		this._mapKeyToDisplayBlockBeanByKeyName.clear();
	}
	@Override
	public void setHasBeenProcessed() {
		super.setHasBeenProcessed();
		this._linesBean.setHasBeenProcessed();
		
		final Iterator<KeyToDisplayBlockBean> iterKeyToDisplayBlockBean = this._mapKeyToDisplayBlockBeanByKeyName.values().iterator();
		while (iterKeyToDisplayBlockBean.hasNext()) {
			iterKeyToDisplayBlockBean.next().setHasBeenProcessed();;
			
		}
		
	}


}
