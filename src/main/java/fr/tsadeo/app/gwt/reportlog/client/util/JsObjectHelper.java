package fr.tsadeo.app.gwt.reportlog.client.util;


import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayString;

public class JsObjectHelper {
	
	public static final native boolean hasKey( JavaScriptObject jsObject, String key ) /*-{
		return ( typeof(jsObject[key]) != 'undefined' );
	}-*/;

	public static final native Integer getKeyNumber( JavaScriptObject jsObject, String key ) /*-{
		if ( typeof(jsObject[key]) === 'number' ) {
			return @fr.tsadeo.app.gwt.reportlog.client.util.JsObjectHelper::castInteger(I)(jsObject[key]);
		}
		return null;
	}-*/;
	
	private static Integer castInteger(int d) {
		return d;
	}
	public static final boolean getKeyBoolean( JavaScriptObject jsObject, String key, final boolean defaultValue ) {
		final String value = getKeyString(jsObject, key);
		return (value == null)?defaultValue:Boolean.valueOf(value);
	}
	public static final boolean getKeyBoolean( JavaScriptObject jsObject, String key ) {
		return getKeyBoolean(jsObject, key, false);
	}
	public static final native String getKeyString( JavaScriptObject jsObject, String key ) /*-{
		if ( typeof(jsObject[key]) === 'string' ) {
			return jsObject[key];
		}
		return null;
	}-*/;
	
	public static final native JavaScriptObject getKeyObject( JavaScriptObject jsObject, String key ) /*-{
		if ( typeof(jsObject[key]) === 'object' ) {
			return jsObject[key];
		}
		return null;
	}-*/;
	
	public static final native JsArray<JavaScriptObject> getKeyJsArray( JavaScriptObject jsObject, String key ) /*-{
		if ( typeof(jsObject[key]) === 'object' && typeof(jsObject[key].length) === 'number' ) {
			return jsObject[key];
		}
		return null;
	}-*/;
	
	public static final native JsArrayInteger getKeyJsArrayNumber( JavaScriptObject jsObject, String key ) /*-{
		if ( typeof(jsObject[key]) === 'object' && typeof(jsObject[key].length) === 'number' ) {
			return jsObject[key];
		}
		return null;
	}-*/;
	
	public static final native JsArrayString getKeyJsArrayString( JavaScriptObject jsObject, String key ) /*-{
		if ( typeof(jsObject[key]) === 'object' && typeof(jsObject[key].length) === 'number' ) {
			return jsObject[key];
		}
		return null;
	}-*/;

	public static final native String typeOf( JavaScriptObject j ) /*-{
		return typeof j;
	}-*/;
	
	public static final native String getString( JavaScriptObject j ) /*-{
		if ( typeof(j) === 'string' ) {
			return j;
		}
		return null;
	}-*/;

	public static final native String toJsonString( JavaScriptObject j ) /*-{
		return JSON.stringify(j);
	}-*/;

	/*
	 * Takes in a trusted JSON String and evals it.
	 * @param JSON String that you trust
	 * @return JavaScriptObject that you can cast to an Overlay Type
	 */
	public static native JavaScriptObject parseJson(String jsonStr) /*-{
	   return eval('(' + jsonStr + ')');
	}-*/;
}
