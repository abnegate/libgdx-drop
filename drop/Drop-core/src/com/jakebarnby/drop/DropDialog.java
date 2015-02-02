package com.jakebarnby.drop;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class DropDialog extends Dialog {

	public DropDialog(String title, Skin skin) {
		super(title, skin);
		
		button("Try again");
		button("Quit");
		text("\nYou looooose!\n\n");
	}

}
