package fr.tsadeo.app.gwt.reportlog.client.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.tsadeo.app.gwt.reportlog.client.wigdet.AbstractWidgetAndAccessKey.AccessKey;
import fr.tsadeo.app.gwt.reportlog.client.wigdet.ButtonAndAccessKey;

public class WidgetUtils {

	public static Panel buildSimplePlanel(final Widget widget, String width) {

		final Panel panel = new SimplePanel();
		if (width != null) {
			panel.setWidth(width);
		}
		panel.add(widget);
		panel.addStyleName("margin10");
		return panel;
	}

	public static boolean controlNumeric(final String value) {

		boolean success = false;
		try {
			Integer.parseInt(value);
			success = true;
		} catch (NumberFormatException e) {
			success = false;
		}
		return success;
	}

	public static int getTextBoxValueLength(final TextBox textBox) {
		if (textBox == null || textBox.getText() == null) {
			return 0;
		}
		return textBox.getText().trim().length();
	}

	public final static DialogBox buildModalDialogBox(final String title,
			final String[] messages, final Widget widget,
			final ButtonAndAccessKey actionButton, final boolean withCancel,
			final boolean withClose, final IActionCallback actionCallback) {
		return _buildDialogBox(true, title, messages, widget, actionButton,
				withCancel, withClose, actionCallback);

	}

	public final static DialogBox buildNoModalDialogBox(final String title,
			final String[] messages, final Widget widget,
			final ButtonAndAccessKey actionButton, final boolean withCancel,
			final boolean withClose, final IActionCallback actionCallback) {
		return _buildDialogBox(false, title, messages, widget, actionButton,
				withCancel, withClose, actionCallback);
	}

	private final static DialogBox _buildDialogBox(final boolean modal,
			final String title, final String[] messages, final Widget widget,
			final ButtonAndAccessKey actionButton, final boolean withCancel,
			final boolean withClose, final IActionCallback actionCallback) {

		final DialogBox dialogBox = new DialogBox(false, modal);
		dialogBox.setText(title);
		dialogBox.setAnimationEnabled(true);

		final List<ButtonAndAccessKey> listButtons = new ArrayList<ButtonAndAccessKey>();

		final VerticalPanel vPanelInfo = new VerticalPanel();
		vPanelInfo.setSpacing(IConstants.SPACING_MIN);

		if (messages != null) {
			for (int i = 0; i < messages.length; i++) {
				vPanelInfo.add(new Label(messages[i]));
			}
		}

		if (widget != null) {
			vPanelInfo.add(widget);
		}

		final HorizontalPanel panelButton = new HorizontalPanel();
		panelButton.setSpacing(IConstants.SPACING_MIN);
		panelButton.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		if (withClose) {
			final ButtonAndAccessKey closeButton = new ButtonAndAccessKey(
					"close", new AccessKey('C', IConstants.SHORTCUT_CLOSE),
					"close dialog", null, true);
			closeButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					dialogBox.hide();
					if (actionCallback != null) {
						actionCallback.onClose();
					}
				}
			});
			closeButton.setFocus(true);
			panelButton.add(closeButton);
			listButtons.add(closeButton);
		}

		if (actionButton != null) {
			panelButton.add(actionButton);
			listButtons.add(actionButton);
		}

		if (withCancel) {
			final ButtonAndAccessKey cancelButton = new ButtonAndAccessKey(
					"cancel", new AccessKey('A', IConstants.SHORTCUT_CANCEL),
					"cancel action", null, true);
			cancelButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					dialogBox.hide();
					if (actionCallback != null) {
						actionCallback.onCancel();
					}

				}
			});
			cancelButton.setFocus(true);
			panelButton.add(cancelButton);
			listButtons.add(cancelButton);
		}

		vPanelInfo.add(panelButton);
		vPanelInfo.setCellHorizontalAlignment(panelButton,
				HasHorizontalAlignment.ALIGN_RIGHT);

		manageAccessKeyInDialogBox(dialogBox, listButtons);
		dialogBox.setWidget(vPanelInfo);
		return dialogBox;
	}

	private final static void manageAccessKeyInDialogBox(DialogBox dialogBox,
			final List<ButtonAndAccessKey> buttons) {

		dialogBox.addDomHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {

				if (!event.isAltKeyDown()) {
					return;
				}

				if (event.getNativeKeyCode() == KeyCodes.KEY_ALT) {
					// montrer les accesskeys qd on appuie sur la touche ALT
					for (ButtonAndAccessKey buttonAndAccessKey : buttons) {

						if (buttonAndAccessKey.isEnabled()) {
							buttonAndAccessKey.showAccessKey(true);
						}
					}
					return;
				}

				int code = event.getNativeKeyCode();
				for (ButtonAndAccessKey buttonAndAccessKey : buttons) {
					if (buttonAndAccessKey.isEnabled()
							&& buttonAndAccessKey.getAccessKey()
									.getKeyboardCode() == code) {
						buttonAndAccessKey.click();
						break;
					}
				}

			}
		}, KeyDownEvent.getType());

		dialogBox.addDomHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {

				if (event.getNativeKeyCode() == KeyCodes.KEY_ALT) {
					// masquer les accesskey qd on relache la touche ALT
					for (ButtonAndAccessKey buttonAndAccessKey : buttons) {
						buttonAndAccessKey.showAccessKey(false);
					}
				}
			}
		}, KeyUpEvent.getType());
	}

}
