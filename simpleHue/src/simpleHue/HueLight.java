/**
 * @author Shaun Parkison
 * Simple driver program for turning 
 * Philips Hue light(s) on/off
 */

package simpleHue;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

public class HueLight {

	private final SimpleIntegerProperty id;
	private final SimpleStringProperty name;
	private final ColorPicker onColor;
	private final CheckBox toggle;
	
	public HueLight(int id, String name){
		this.id = new SimpleIntegerProperty(id);
		this.name = new SimpleStringProperty(name);
		// Default to white
		this.onColor = new ColorPicker(new Color(1,1,1,1.0));
		this.toggle = new CheckBox();
	}
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id.get();
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name.get();
	}

	/**
	 * @return the onColor
	 */
	public ColorPicker getOnColor() {
		return onColor;
	}

	/**
	 * @return the toggle
	 */
	public CheckBox getToggle() {
		return toggle;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HueLight [" + (id != null ? "id=" + id.get() + ", " : "")
				+ (name != null ? "name=" + name.get() + ", " : "")
				+ (onColor != null ? "onColor=" + onColor.getValue() + ", " : "")
				+ (toggle != null ? "toggle=" + toggle.isSelected() : "") + "]";
	}

}
