package fr.tsadeo.app.gwt.reportlog.client.util;

import com.google.gwt.core.client.JavaScriptObject;

public class JSNIUtils {
	
	private static JSNIUtils instance = new JSNIUtils();
	public static JSNIUtils getInstance() {
		return instance;
	}
	
	
	
	
	public native void copyClipboard(final String txt)
	/*-{
if(window.clipboardData) {

  window.clipboardData.clearData();

  window.clipboardData.setData("Text", txt);

} else if(navigator.userAgent.indexOf("Opera") != -1) {

window.location = txt;

} else if (window.netscape) {

try {

netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");

} catch (e) {

alert("You need set 'signed.applets.codebase_principal_support=true'at about:config'");

return false;

}

var clip =

 Components.classes['@mozilla.org/widget/clipboard;1'].createInstance

(Components.interfaces.nsIClipboard);

if (!clip)

return;

var trans = 

Components.classes['@mozilla.org/widget/transferable;1'].createInstance

(Components.interfaces.nsITransferable);

if (!trans)

return;

trans.addDataFlavor('text/unicode');

var str = new Object();

var len = new Object();

var str = 

Components.classes["@mozilla.org/supports-string;1"].createInstance

(Components.interfaces.nsISupportsString);

var copytext = txt;

str.data = copytext;

trans.setTransferData("text/unicode",str,copytext.length*2);

var clipid = Components.interfaces.nsIClipboard;

if (!clip)

return false;

clip.setData(trans,null,clipid.kGlobalClipboard);

}
	}-*/;

}
