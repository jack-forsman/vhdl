package se.liu.jacfo794.tetris;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class HighscoreList
{
    private List<Highscore> highscores;
    private String highscoreJsonPath = System.getProperty("user.dir") + "/src/se/liu/jacfo794/tetris" + File.separator + "Highscores.json";

    public HighscoreList() {
	highscores = new ArrayList<>();
    }

    public void addScore(Highscore newHighscore) throws IOException {
	try {
	    readJsonFile();
	}
	catch(IOException ignored) {
	    highscores = new ArrayList<>();
	}
	highscores.add(newHighscore);
	highscores.sort(new ScoreComparator());

	saveJsonFile();

    }

    public void readJsonFile() throws IOException {
	File input = new File(highscoreJsonPath);
	Gson gson = new Gson();

	try (FileReader fileInput = new FileReader(input)) {
	    JsonElement fileElem = JsonParser.parseReader(fileInput);
	    JsonArray elemAsJsonArray = fileElem.getAsJsonArray();
	    List<Highscore> highscoreList = new ArrayList<>();
	    for (JsonElement score : elemAsJsonArray) {
		Highscore newHighscore = gson.fromJson(score, Highscore.class);
		highscoreList.add(newHighscore);
	    }
	    highscores = highscoreList;
	}
	catch (IOException e) {
	    throw new IOException(e);
	}
    }

    public void saveJsonFile() throws IOException {
	String tempFileString = System.getProperty("user.dir") + "/src/se/liu/jacfo794/tetris" + File.separator + "tempHighscores.json";
	File tempFile = new File(tempFileString);
	Gson gson = new GsonBuilder().setPrettyPrinting().create();

	File finalFile = new File(highscoreJsonPath);

	// Write the temporary highscore file
	try (FileWriter jsonWriter = new FileWriter(tempFileString)) {
	    gson.toJson(highscores, jsonWriter);
	}

	// Replace or create the final highscore file
	if (finalFile.exists() && !finalFile.delete() || !tempFile.renameTo(finalFile)) {
	    throw new IOException();
	}
    }


    public Highscore getScore(int index) {
	return highscores.get(index);
    }

    public int getLength(){
	final int maxHighscoresDisplayed = 10;
	return Math.min(highscores.size(), maxHighscoresDisplayed);
    }

    @Override
    public String toString() {
	StringBuilder highscoreListText = new StringBuilder();
	highscoreListText.append("Highscorelist:");

	for (int i = 0; i < getLength(); i++) {
	    Highscore highscore = getScore(i);
	    highscoreListText.append("\n");
	    highscoreListText.append(i + 1).append(". ");
	    highscoreListText.append(highscore.getName()).append(": ");
	    highscoreListText.append(highscore.getScore());
	}

	return highscoreListText.toString();
    }

    public void createNewHighscoreFile() throws IOException {
	File newFile = new File(highscoreJsonPath);
	if (!newFile.createNewFile()) {
	    throw new IOException("Failed to create new highscore file.");
	}

	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	try (FileWriter jsonWriter = new FileWriter(newFile)) {
	    gson.toJson(new JsonArray(), jsonWriter); // Write an empty JSON array
	}
    }

    public boolean doesHighscoreFileExist() {
	File file = new File(highscoreJsonPath);
	return file.exists();
    }



}
