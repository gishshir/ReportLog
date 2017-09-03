package fr.tsadeo.app.gwt.reportlog.client.service.bean;

import com.google.gwt.core.client.JavaScriptObject;

import fr.tsadeo.app.gwt.reportlog.client.util.IJsonNode;
import fr.tsadeo.app.gwt.reportlog.client.util.JsObjectHelper;
import fr.tsadeo.app.gwt.reportlog.client.util.JsonNode;
/**
 * Dans le cadre des Paramètres gérant les règles de construction des lignes :
 * - détermine la valeur minimale d'une ligne pour qu'elle soit considérée comme complète
 * @author sylvie
 *
 */
public class ParamsLineRuleLineWidthMinBean extends AbstractBean implements IJsonNode {
	
	// line width min
	private boolean _lineWidthMin = true;
	private String _value = "80";
	
	
	// calculé
	private int _intValue = -1;

	//---------------------------------- overriding IJsonNode
	@Override
	public JsonNode toJsonNode() {
		final JsonNode jsonNode = new JsonNode();
		jsonNode.addBooleanValue("lineWidthMin", this._lineWidthMin);
		jsonNode.addStringValue("value", this._value);
		return jsonNode;
	}

	@Override
	public void populateFromJavaScriptObject(final JavaScriptObject jsObject) {

		if (jsObject == null) return;
		this._lineWidthMin = JsObjectHelper.getKeyBoolean(jsObject, "lineWidthMin");
		this._value = JsObjectHelper.getKeyString(jsObject, "value");
	}
	// --------------------------------- overriding IReportLogBean
	@Override
	public void clean() {
		super.clean();
		this._lineWidthMin = true;
		this._value = null;
        this._intValue = -1;
	}

	public boolean isLineWidthMin() {
		return this._lineWidthMin;
	}

	public void setLineWidthMin(final boolean lineWidthMin) {
		this.hasValueChanged(this._lineWidthMin, lineWidthMin);
		this._lineWidthMin = lineWidthMin;
	}


	public void setValue(final String cutBeginningValue) {
		this.hasValueChanged(this._value, cutBeginningValue);
		this._value = cutBeginningValue;
		this._intValue = -1;
	}

	public String getValue() {
		return this._value;
	}

    public int getIntValue() {
    	if (this._intValue < 0) {
    		
    		try{
    		  this._intValue = Integer.parseInt(this._value);
    		}
    		catch (NumberFormatException ex) {
    			this._intValue = -1;
    		}
    	}
    	
    	return this._intValue;
    }

}