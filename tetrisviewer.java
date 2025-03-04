package se.liu.jacfo794.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TetrisViewer {
    private final TetrisGame game;
    private final Board board;
    private final TetrisComponent tetrisComponent;
    private final JFrame frame;
    private final JMenuBar menuBar;

    public TetrisViewer(TetrisGame game, Board board) {
	this.game = game;
	this.board = board;
	this.tetrisComponent = new TetrisComponent(board);
	this.frame = new JFrame("Tetris");
	this.menuBar = new JMenuBar();

	board.addBoardListener(tetrisComponent);
    }

    public void show() {
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	StartScreenImage painter = new StartScreenImage();
	painter.showStartScreen(frame);

	frame.setLayout(new BorderLayout());
	frame.add(tetrisComponent, BorderLayout.CENTER);

	createMenu();

	frame.pack();
	frame.setVisible(true);
    }

    public void close() {
	frame.dispose();
    }

    public JFrame getFrame() {
	return frame;
    }

    public TetrisComponent getTetrisComponent() {
	return tetrisComponent;
    }

    private void createMenu() {
	final JMenu optionsMenu = new JMenu("Options");
	optionsMenu.setMnemonic('O');
	final JMenuItem quitGame = new JMenuItem("Quit", 'Q');
	optionsMenu.add(quitGame);

	quitGame.addActionListener(e -> {
	    int quit = JOptionPane.showConfirmDialog(frame,
						     "Are you sure you want to exit the game?",
						     "Quit?", JOptionPane.YES_NO_OPTION);
	    if (quit == JOptionPane.YES_OPTION) {
		System.exit(0);
	    }
	});

	menuBar.add(optionsMenu);
	frame.setJMenuBar(menuBar);
    }

    // Dialogs to be used in TetrisGame
    public String askForPlayerName() {
	return JOptionPane.showInputDialog(frame, "Game Over! Enter your name:");
    }

    public void showHighscores(HighscoreList highscoreList) {
	JOptionPane.showMessageDialog(frame, highscoreList);
    }

    public boolean askToStartNewGame() {
	int choice = JOptionPane.showConfirmDialog(frame,
						   "Start a new game?", "New Game", JOptionPane.YES_NO_OPTION);
	return choice == JOptionPane.YES_OPTION;
    }

    public boolean askToRetrySave() {
	int retry = JOptionPane.showConfirmDialog(frame,
						  "Could not save score file, try again?",
						  "Save Error", JOptionPane.YES_NO_OPTION);
	return retry == JOptionPane.YES_OPTION;
    }

    public void newFileCreationPopup() {
	JOptionPane.showMessageDialog(frame,
				      "No highscore file found.\nA new file will be created.",
				      "Highscore File",
				      JOptionPane.INFORMATION_MESSAGE);
    }

}
