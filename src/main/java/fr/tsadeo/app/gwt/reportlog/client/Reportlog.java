package fr.tsadeo.app.gwt.reportlog.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

import fr.tsadeo.app.gwt.reportlog.client.view.ReportLogView;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Reportlog implements EntryPoint {


	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel.get("reportLogView").add(new ReportLogView());
	}
}
