package se.liu.jacfo794.tetris;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TetrisGame
{
    private Board board;
    private TetrisViewer viewer;
    private Timer clockTimer;
    private int currentTickSpeed = START_TICK_SPEED;

    private static final int START_TICK_SPEED = 200;
    private static final int FINAL_TICK_SPEED = 220;
    private static final int TWENTY_SECONDS = 20000;
    private static final int TICK_SPEED_INCREASE = 40;

    private final HighscoreList highscoreList;

    public TetrisGame(HighscoreList highscoreList) {
	this.highscoreList = highscoreList;
	this.board = new Board(15, 20);
	this.viewer = new TetrisViewer(this, board);
	this.board.addBoardListener(viewer.getTetrisComponent());
    }

    public void start() {
	viewer.show();
	startGameLoop();
    }

    private void startGameLoop() {
	final Action doOneStep = new AbstractAction()
	{
	    public void actionPerformed(ActionEvent e) {
		board.tick();
		if (board.gameOver) {
		    handleGameOver();
		}
	    }
	};

	clockTimer = new Timer(currentTickSpeed, doOneStep);
	clockTimer.setCoalesce(true);
	clockTimer.start();

	startSpeedIncreaseTimer();
    }

    private void startSpeedIncreaseTimer() {
	final Action increaseSpeed = new AbstractAction()
	{
	    public void actionPerformed(ActionEvent e) {
		if (currentTickSpeed > FINAL_TICK_SPEED) {
		    currentTickSpeed -= TICK_SPEED_INCREASE;
		    clockTimer.setDelay(currentTickSpeed);
		}
	    }
	};

	Timer speedIncreaseTimer = new Timer(TWENTY_SECONDS, increaseSpeed);
	speedIncreaseTimer.start();
    }

    private void handleGameOver() {
	clockTimer.stop();

	String name = viewer.askForPlayerName();
	Highscore highscore = new Highscore(board.getScore(), name);
	saveHighscore(highscore);

	viewer.showHighscores(highscoreList);

	boolean restart = viewer.askToStartNewGame();
	if (restart) {
	    restartGame();
	} else {
	    System.exit(0);
	}
    }

    private void saveHighscore(Highscore highscore) {
	while (true) {
	    try {
		if (!highscoreList.doesHighscoreFileExist()) {
		    viewer.newFileCreationPopup();
		}
		highscoreList.addScore(highscore);
	    }
	    catch (IOException ignored) {
		boolean retry = viewer.askToRetrySave();
		if (!retry) {
		    return;
		}
	    }
	}
    }


    public void restartGame() {
	if (clockTimer != null) {
	    clockTimer.stop();
	}
	viewer.close();

	TetrisGame newGame = new TetrisGame(highscoreList);
	newGame.start();
    }

    public static void main(String[] args) {
	HighscoreList highscoreList = new HighscoreList();
	TetrisGame game = new TetrisGame(highscoreList);
	game.start();
    }
}

