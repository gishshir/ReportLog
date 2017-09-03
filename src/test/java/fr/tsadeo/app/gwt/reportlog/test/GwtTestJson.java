package fr.tsadeo.app.gwt.reportlog.test;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.junit.client.GWTTestCase;

import fr.tsadeo.app.gwt.reportlog.client.util.JsObjectHelper;

public class GwtTestJson extends GWTTestCase {
	
	 private final static Logger log = Logger.getLogger("GwtTestJson");

	final String json = "{\"paramsFilterBean\":{\"logLevel\": \"true\",\"logLevelValue\": \"INFO\",\"logLevelToFilter\": \"SUP_OR_EQUALS\",\"keyToFind\": \"false\",\"cutLines\": \"false\",\"fromLineNumber\": 0,\"toLineNumber\": 0,\"listKeyValueBean\":{\"typeKeyValue\": \"none\",\"listKeyValueBean\":[]}},\"paramsDisplayBean\":{\"linesBean\":{\"lineNumbers\": \"true\",\"levelColor\": \"true\"},\"listKeyToDisplayBlockBean\":[]},\"paramsLineRuleBean\":{\"beginWithLogLevel\": \"true\",\"beginWithKeys\": \"false\",\"cutBeginBean\":{\"cutBeginning\": \"false\",\"regEx\": \"false\"},\"listBeginWith\":{\"typeKeyValue\": \"none\",\"listKeyValueBean\":[]},\"levels\":[{\"name\": \"ERROR\",\"value\": \"[ERROR\"},{\"name\": \"WARN\",\"value\": \"[WARN\"},{\"name\": \"INFO\",\"value\": \"[INFO\"},{\"name\": \"DEBUG\",\"value\": \"[DEBUG\"},{\"name\": \"TRACE\",\"value\": \"[TRACE\"}]}}";

	@Override
	public String getModuleName() {
		return "fr.tsadeo.app.gwt.reportlog.TestReportlog";
	}

	  /**
	   * Add as many tests as you like.
	   */
	  public void testSimple() { 
		  log.config("testSimple()");
	    assertTrue(true);
	  }

	  public void testParseJson() {
		  
		  final JavaScriptObject paramsJs = JsObjectHelper.parseJson(json);
		  assertNotNull("paramsJs cannot be null!", paramsJs);
	  }
}
