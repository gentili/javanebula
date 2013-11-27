package ca.mcpnet.demurrage.GameClient;

import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.PopupWindow;
import de.matthiasmann.twl.Widget;

public class PopupWindow2 extends PopupWindow {

	public PopupWindow2(Widget owner) {
		super(owner);
		this.setTheme("popupwindow");
	}

	@Override
	protected boolean handleEventPopup(Event evt) {
		if(evt.isKeyPressedEvent() && evt.getKeyCode() == Event.KEY_RETURN) {
            requestPopupClose();
            return true;
        }
		return false;
	}
}
