import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(ApplicationExtension.class)
class Testing {
    private MIDIFile midiFile = new MIDIFile();

    @Start
    void onStart(Stage stage) {
        Button button = new Button("Testing Button");
        button.setOnAction(e -> {
            midiFile.setFolderToOpen(new File("MidiFile"));
            button.setText("Button Clicked!!");
        });
        stage.setScene(new Scene(new StackPane(button), 100, 100));
        stage.show();
    }

    @Test
    void checkFolderExists(FxRobot robot) {
        robot.clickOn(".button");
        assertNotNull(midiFile.getFolderToOpen());
    }

    @Test
    void checkTextFileExists(FxRobot robot) {
        robot.clickOn(".button");
        midiFile.initialise();
        File text = new File(midiFile.getFolderToOpen().toString() + "\\MidiData.txt");
        assertTrue(text.exists());
    }

    @Test
    void checkTextFileContainsValues(FxRobot robot) {
        robot.clickOn(".button");
        midiFile.initialise();
        File text = new File(midiFile.getFolderToOpen().toString() + "\\MidiData.txt");
        assertTrue(text.length() != 0);
    }

    @Test
    void checkArrayContainsValues(FxRobot robot) {
        robot.clickOn(".button");
        midiFile.initialise();
        midiFile.readDataFromFile();
        assertTrue(!midiFile.getEveryNote().isEmpty());
        assertTrue(!midiFile.getEveryVolume().isEmpty());
    }

    @Test
    void checkNoteObjectArrayContainsValues(FxRobot robot) {
        robot.clickOn(".button");
        midiFile.initialise();
        midiFile.readDataFromFile();
        midiFile.noteObject();
        assertTrue(!midiFile.getNoteArray().isEmpty());
        assertTrue(!midiFile.getRestArray().isEmpty());
    }

    @Test
    void checkMarkovChainArrayContainsValues(FxRobot robot) {
        robot.clickOn(".button");
        midiFile.initialise();
        midiFile.readDataFromFile();
        midiFile.noteObject();
        midiFile.markovChain();
        assertTrue(!midiFile.getMarkovNotes().isEmpty());
        assertTrue(!midiFile.getMarkovRest().isEmpty());
    }

    @Test
    void checkMIDIFileProduced(FxRobot robot) {
        robot.clickOn(".button");
        midiFile.initialise();
        midiFile.readDataFromFile();
        midiFile.noteObject();
        midiFile.markovChain();
        midiFile.makeMIDIFile();
        File text = new File(midiFile.getFolderToOpen().toString() + "\\midifile.mid");
        assertTrue(text.exists());
    }

    @Test
    void checkRefreshMethodWorks(FxRobot robot) {
        robot.clickOn(".button");
        midiFile.initialise();
        midiFile.readDataFromFile();
        midiFile.noteObject();
        midiFile.markovChain();
        midiFile.makeMIDIFile();
        midiFile.refreshCode();
        assertTrue(midiFile.getNoteArray().isEmpty());
        assertTrue(midiFile.getRestArray().isEmpty());
        assertTrue(midiFile.getMarkovNotes().isEmpty());
        assertTrue(midiFile.getMarkovRest().isEmpty());
    }
}