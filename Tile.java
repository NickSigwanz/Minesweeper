package com.revan.minesweeper;

import javax.swing.JButton;

public class Tile extends JButton {

	private static final long serialVersionUID = 1L;
	int row;
	int col;

	public Tile(int row, int col) {
		this.row = row;
		this.col = col;

	}

}
