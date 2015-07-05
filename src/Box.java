import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JTextArea;

public class Box {
	private int myX, myY; // x,y position on grid
	private boolean mySelected; // selected (true) or notSelected (false)
	private Color myColor; // Based on alive/dead rules
	private static Color DEFAULT_SELECTED = Color.GRAY;
	private Color selectedColor = new Color(0x97FFFF);
	private String myLabel = new String();

	public Box() {

	}

	public Box(int col, int row) {
		this(col, row, false, DEFAULT_SELECTED);
	}

	public Box(int col, int row, boolean selected, Color color) {
		mySelected = selected;
		myColor = color;
		myX = col;
		myY = row;
	}

	public boolean getSelected() {
		return mySelected;
	}

	public int getX() {
		return myX;
	}

	public int getY() {
		return myY;
	}

	public Color getColor() {
		return myColor;
	}

	public void setLabelText(String label) {
		myLabel = label;
	}

	public void setSelected(boolean selected) {
		mySelected = selected;
	}

	public void setSelected(boolean selected, Color color) {
		myColor = color;
		mySelected = selected;
	}

	public void setColor(Color color) {
		myColor = color;
	}

	public void setX(int x) {
		myX = x;
	}

	public void setY(int y) {
		myY = y;
	}

	public void draw(int boxX, int boxY, int x_offset, int y_offset, int width,
			int height, int thinLine, int thickLine, Graphics g) {

		int xLeft = boxX * width + x_offset;
		int yTop = boxY * height + y_offset;

		g.setColor(selectedColor);
		g.fillRect(xLeft + 3, yTop + 2, width - 5, height - 4);
	}
}
