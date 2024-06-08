
package com.revan.minesweeper;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Game implements ActionListener {

	ArrayList<Tile> mines;
	private static JMenuBar menuBar;
	private static JFrame frame;
	private static JPanel gameGrid;
	private static JPanel scoreBar;

	private Tile[][] tiles;
	private static JTextField mineCounter = new JTextField();
	private static JTextField clock = new JTextField();
	private static JButton resetBtn = new JButton();
	private int minesRemaining;
	private Font font = new Font("Arial Unicode MS", Font.PLAIN, 30);
	private Random rand = new Random();
	private boolean gameOver = false;
	private int numRows;
	private int numCols;
	private static int numMines;
	private static JMenu menu;
	private static JMenuItem small;
	private static JMenuItem medium;
	private static JMenuItem large;

	private int tilesClicked = 0;

	int elapsedTime = 0;
	int seconds = 0;
	int minutes = 0;

	String seconds_string = String.format("%02d", seconds);
	String minutes_string = String.format("%02d", minutes);
	private Timer timer;

	public Game(int numRows, int numCols, int numMines, int width, int height) {
		tiles = new Tile[numRows][numCols];
		this.numRows = numRows;
		this.numCols = numCols;
		Game.numMines = numMines;
		menuBar = new JMenuBar();
		menu = new JMenu("New Game");
		small = new JMenuItem("Small");
		small.addActionListener(this);
		medium = new JMenuItem("Medium");
		medium.addActionListener(this);
		large = new JMenuItem("Large");
		large.addActionListener(this);
		menu.add(small);
		menu.add(medium);
		menu.add(large);
		menuBar.add(menu);
		frame = new JFrame("Minesweeper");
		frame.setLocationRelativeTo(null);
		frame.setSize(width, height);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setJMenuBar(menuBar);
		gameGrid = new JPanel(new GridLayout(numRows, numCols));
		scoreBar = new JPanel(new GridLayout(1, 3, 5, 5));
		minesRemaining = numMines;

		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				Tile tile = new Tile(i, j);
				tiles[i][j] = tile;
				tile.setFont(font);
				tile.setMargin(new Insets(0, 0, 0, 0));
				tile.setFocusable(false);
				tile.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						if (gameOver) {
							return;
						}
						Tile tile = (Tile) e.getSource();

						if (e.getButton() == MouseEvent.BUTTON1) {
							start();
							if (tile.getText() == "") {
								if (mines.contains(tile)) {
									showMines();
								} else {
									checkTile(tile.row, tile.col);
								}
							}
						} else if (e.getButton() == MouseEvent.BUTTON3) {
							if (tile.getText() == "" && tile.isEnabled()) {
								tile.setText("🚩");
								minesRemaining--;
								mineCounter.setText(String.valueOf(minesRemaining));
								tile.setEnabled(false);

							} else if (tile.getText() == "🚩") {
								tile.setText("");
								minesRemaining++;
								mineCounter.setText(String.valueOf(minesRemaining));
								tile.setEnabled(true);

							}
						}
					}

				});

				gameGrid.add(tile);
			}
		}

		mineCounter.setEditable(false);
		mineCounter.setFont(font);
		clock.setEditable(false);
		clock.setFont(font);
		resetBtn.setFont(font);
		resetBtn.setText("😎");
		resetBtn.addActionListener(this);

		timer = new Timer(1000, new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				elapsedTime = elapsedTime + 1000;
				minutes = (elapsedTime / 60000) % 60;
				seconds = (elapsedTime / 1000) % 60;
				seconds_string = String.format("%02d", seconds);
				minutes_string = String.format("%02d", minutes);
				clock.setText(minutes_string + ":" + seconds_string);

			}

		});

		scoreBar.add(mineCounter);
		scoreBar.add(resetBtn);
		scoreBar.add(clock);
		frame.add(scoreBar, BorderLayout.NORTH);
		frame.add(gameGrid, BorderLayout.CENTER);
		frame.setVisible(true);

	}

	public static void main(String[] args) {

		Game game = new Game(9, 9, 10, 450, 450);
		game.init();

	}

	private void init() {
		mineCounter.setText(String.valueOf(numMines));
		mines = new ArrayList<Tile>();
		int m = numMines;
		while (m > 0) {
			int randRow = rand.nextInt(this.numRows);
			int randCol = rand.nextInt(this.numCols);
			if (!mines.contains(tiles[randRow][randCol])) {
				mines.add(tiles[randRow][randCol]);
				m--;
			}
		}

	}

	private void reset() {
		elapsedTime = 0;
		seconds = 0;
		minutes = 0;
		seconds_string = String.format("%02d", seconds);
		minutes_string = String.format("%02d", minutes);
		clock.setText(minutes_string + ":" + seconds_string);

		minesRemaining = numMines;
		gameOver = false;
		tilesClicked = 0;
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				tiles[r][c].setEnabled(true);
				tiles[r][c].setText("");
			}
		}
		init();
	}

	private void checkTile(int row, int col) {
		if (row < 0 || row >= this.numRows || col < 0 || col >= this.numCols) {
			return;
		}

		Tile tile = tiles[row][col];
		if (!tile.isEnabled()) {
			return;
		}

		tile.setEnabled(false);
		tilesClicked += 1;

		int mineCount = 0;

		mineCount += countMine(row - 1, col - 1);
		mineCount += countMine(row - 1, col);
		mineCount += countMine(row - 1, col + 1);

		mineCount += countMine(row, col - 1);
		mineCount += countMine(row, col + 1);

		mineCount += countMine(row + 1, col - 1);
		mineCount += countMine(row + 1, col);
		mineCount += countMine(row + 1, col + 1);
		if (mineCount > 0) {
			tile.setText(String.valueOf(mineCount));
		} else {
			tile.setText("");

			checkTile(row - 1, col - 1);
			checkTile(row - 1, col);
			checkTile(row - 1, col + 1);

			checkTile(row, col - 1);
			checkTile(row, col + 1);

			checkTile(row + 1, col - 1);
			checkTile(row + 1, col);
			checkTile(row + 1, col + 1);

		}

		if (tilesClicked == numRows * numCols - mines.size()) {
			gameOver = true;
			stop();
			JOptionPane.showMessageDialog(frame, "You win 😊");
			reset();

		}

	}

	private int countMine(int row, int col) {
		if (row < 0 || row >= numRows || col < 0 || col >= numCols)
			return 0;
		if (mines.contains(tiles[row][col]))
			return 1;
		return 0;
	}

	private void showMines() {
		for (int i = 0; i < numMines; i++) {
			Tile tile = mines.get(i);
			tile.setText("💣");
		}
		gameOver = true;
		stop();
		JOptionPane.showMessageDialog(frame, "You Lose 😢");
		reset();

	}

	private void start() {
		timer.start();
	}

	private void stop() {
		timer.stop();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == resetBtn) {
			reset();
		}

		if (e.getSource() == small) {
			frame.dispose();
			Game newGame = new Game(9, 9, 10, 450, 450);
			newGame.init();

		}
		if (e.getSource() == medium) {
			frame.dispose();
			Game newGame = new Game(16, 16, 40, 600, 600);
			newGame.init();

		}
		if (e.getSource() == large) {
			frame.dispose();
			Game newGame = new Game(16, 30, 99, 1280, 800);
			newGame.init();

		}
	}

}
