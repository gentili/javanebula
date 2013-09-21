package ca.mcpnet.demurrage.GameEngine.GameClient;

import java.util.Vector;

import de.matthiasmann.twl.AnimationState;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.ThemeInfo;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.AnimationState.StateKey;
import de.matthiasmann.twl.renderer.Font;
import de.matthiasmann.twl.renderer.FontCache;
import de.matthiasmann.twl.utils.TextUtil;

public class ReadoutWidget extends Widget {

    public static final StateKey STATE_READOUT = StateKey.get("READOUT");

	private Font _font;
	private int _linespeed;
	private Vector<FontCache> _lines;

	private int _linecount;

	private int _curline;

	public ReadoutWidget() {
        this(null, false);
	}

	public ReadoutWidget(AnimationState animState) {
        this(animState, false);
	}

	public ReadoutWidget(AnimationState animState, boolean inherit) {
		super(animState, inherit);
		_lines = new Vector<FontCache>();
	}

	@Override
	protected void applyTheme(ThemeInfo themeInfo) {
		setFont(themeInfo.getFont("font"));
		setLineSpeed(themeInfo.getParameter("linespeed", 50));
		super.applyTheme(themeInfo);
	}
	
	public void setLineSpeed(int linespeed) {
		_linespeed = linespeed;
	}
	
	public int getLineSpeed() {
		return _linespeed;
	}

	public Font getFont() {
		return _font;
	}
	
	public void setFont(Font font) {
		_font = font;
	}
		
	public void newText(String text) {
        _linecount = 0;
        _lines.clear();
        _curline = 0;
        addText(text);
    }
	
	public void addText(String text) {
		if (text == null) {
			return;
		}
        int curstart = 0;
        for (int curend = text.indexOf('\n'); curstart < text.length(); curend = TextUtil.indexOf(text, '\n', curstart)) {
        	_lines.add(_font.cacheText(null, text, curstart, curend));
        	_linecount++;
        	curstart = curend +1;
        }
        getAnimationState().resetAnimationTime(STATE_READOUT);
	}

	@Override
	protected void paintWidget(GUI gui) {
		// Draw completed lines
		int cury = getY();
		gui.getRenderer().pushGlobalTintColor(0.0f, 1.0f, 0.0f, 1.0f);
		for (int line = 0; line < _curline; line++) {
			_lines.get(line).draw(getAnimationState(), getX(), cury);

			cury += _font.getLineHeight();
		}
		_font.cacheText(null, "");
		gui.getRenderer().popGlobalTintColor();
		// Draw final line
		if (_curline < _linecount) {
			gui.getRenderer().pushGlobalTintColor(1.0f, 1.0f, 1.0f, 1.0f);			
			_lines.get(_curline).draw(getAnimationState(), getX(), cury);
			gui.getRenderer().popGlobalTintColor();
			// Start the next line
			if (getAnimationState().getAnimationTime(STATE_READOUT) > _linespeed) {
				_curline++;
				if (_curline < _linecount) {
			        getAnimationState().resetAnimationTime(STATE_READOUT);
				} else {
					getAnimationState().dontAnimate(STATE_READOUT);
				}
			}
		}
	}

}
