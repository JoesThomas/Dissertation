import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.sound.midi.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public class MIDIFile extends Application {

    private File folderToOpen;                                                                               //Location of files where all of the processes take place
    private List<List<Integer>> everyNote = new ArrayList<>();                                               //ArrayList containing every single note pitch used
    private List<List<Integer>> everyVolume = new ArrayList<>();                                             //ArrayList containing every single volume used
    private List<List<Integer>> everyNoteDuration = new ArrayList<>();                                       //ArrayList containing every single note Duration used
    private List<List<Integer>> everyRestDuration = new ArrayList<>();                                       //ArrayList containing every single note Duration used
    private List<List<Integer>> everyPitchRest = new ArrayList<>();                                          //ArrayList containing every single note Duration used
    private ArrayList<Integer> everyTempo = new ArrayList<>();                                               //ArrayList containing every single tempo used
    private ArrayList<Integer> everyResolution = new ArrayList<>();                                          //ArrayList containing every single tempo used
    private ArrayList<Float> everyQuarterNote = new ArrayList<>();                                           //ArrayList containing every single quarter note used
    private int valueToChangeNoteBy = 0;                                                                     //Integer value that is used to change a note pitch
    private float valueToChangeVolumeBy = 1;                                                                 //Float value that is used to change a note volume
    private float valueToChangeNoteDurationBy = 1;                                                           //Float value that is used to change a note duration
    private float valueToChangeRestDurationBy = 1;                                                           //Float value that is used to change a rest duration
    private float[] values = new float[4];                                                                   //Float array that is used to contain the previous four values
    private List<List<Note>> noteObjectsUsed = new ArrayList<>();                                            //ArrayList containing the notes used in the piece
    private List<List<Rest>> restObjectsUsed = new ArrayList<>();                                            //ArrayList containing the rests used in the piece
    private List<List<List<Note>>> noteObjectsUsedDifferentStyle = new ArrayList<>();                        //ArrayList containing the notes used in a piece of different style
    private List<List<List<Rest>>> restObjectsUsedDifferentStyle = new ArrayList<>();                        //ArrayList containing the rests used in a piece of different style
    private List<List<Note>> noteArray = new ArrayList<>();                                                  //ArrayList containing every single note object used
    private List<List<Rest>> restArray = new ArrayList<>();                                                  //ArrayList containing every single rest object used
    private List<List<List<Note>>> noteArrayDifferentStyle = new ArrayList<>();                              //ArrayList containing every single note object used in a piece of a different style
    private List<List<List<Rest>>> restArrayDifferentStyle = new ArrayList<>();                              //ArrayList containing every single rest object used in a piece of a different style
    private List<List<List<Note>>> markovNotes = new ArrayList<>();                                          //ArrayList containing the markov chain used for the notes
    private List<List<List<Rest>>> markovRest = new ArrayList<>();                                           //ArrayList containing the markov chain used for the rests
    private List<List<List<List<Note>>>> markovNotesDifferentStyle = new ArrayList<>();                      //ArrayList containing the markov chain used for the notes in a piece of a different style
    private List<List<List<List<Rest>>>> markovRestsDifferentStyle = new ArrayList<>();                      //ArrayList containing the markov chain used for the rests in a piece of a different style
    private ArrayList<Float> everyPieceLength = new ArrayList<>();                                           //ArrayList containing every single piece length used
    private List<List<List<Integer>>> everyNotePitchDifferentStyle = new ArrayList<>();                      //ArrayList containing every single note pitch used in pieces with different styles
    private List<List<List<Integer>>> everyVolumeDifferentStyle = new ArrayList<>();                         //ArrayList containing every single note volume used in pieces with different styles
    private List<List<List<Integer>>> everyNoteDurationDifferentStyle = new ArrayList<>();                   //ArrayList containing every single note Duration used in pieces with different styles
    private List<List<List<Integer>>> everyRestDurationDifferentStyle = new ArrayList<>();                   //ArrayList containing every single rest Duration used in pieces with different styles
    private List<List<List<Integer>>> everyPitchRestDifferentStyle = new ArrayList<>();                      //ArrayList containing every single rest pitch used in pieces with different styles
    private boolean programFinished = false;                                                                 //Booelean that checks whetehr or not the code has worked correctly
    private List<String> result;                                                                             //Arraylist containing the files that will be used in a string format

    /**
     * @return This gets "FolderToOpen" object
     */
    File getFolderToOpen() {
        return folderToOpen;
    }

    /**
     * @param folderToOpen This sets the "FolderToOpen" object
     */
    void setFolderToOpen(File folderToOpen) {
        this.folderToOpen = folderToOpen;
    }

    /**
     * @return This gets the "everyNote" arraylist
     */
    List<List<Integer>> getEveryNote() {
        return everyNote;
    }

    /**
     * @return This gets the "everyVolume" arraylist
     */
    List<List<Integer>> getEveryVolume() {
        return everyVolume;
    }

    /**
     * @return This gets the "noteArray" arraylist
     */
    List<List<Note>> getNoteArray() {
        return noteArray;
    }

    /**
     * @return This gets the "noteArray" arraylist
     */
    List<List<Rest>> getRestArray() {
        return restArray;
    }

    /**
     * @return This gets the "markovNotes" arraylist
     */
    List<List<List<Note>>> getMarkovNotes() {
        return markovNotes;
    }

    /**
     * @return This gets the "markovRests" arraylist
     */
    List<List<List<Rest>>> getMarkovRest() {
        return markovRest;
    }

    /**
     * Sets up the files for the program to use
     */
    void initialise() {


        File file = new File(folderToOpen + "\\midifile.mid");                              //Checks to see if there is a previous midi file for these files, if so deletes it and prints out a message to the terminal determining on the outcome
        if (file.delete()) {
            System.out.println("Previous Midi File deleted successfully");

        } else {
            System.out.println("Failed to delete the previous MIDI file");
        }
        File keyFileData = new File(folderToOpen.toString() + "\\MidiData.txt");            //Makes a new text file in the folder accessed
        PrintWriter writer = null;                                                                    //Makes a new printwriter that will work with the file
        try {
            writer = new PrintWriter(keyFileData);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert writer != null;
        writer.close();

        try (Stream<Path> walk = Files.walk(Paths.get(folderToOpen.toString()))) {                    //Iterates through the folder I have given the program and finds any .mid files and adds them to a list
            System.out.println("Files Found:");
            result = walk.map(Path::toString)
                    .filter(f -> f.endsWith(".mid")).collect(Collectors.toList());

            result.forEach(System.out::println);

            for (String aResult : result) {
                readMIDIDataToFile(new File(aResult));                                               //Once this has been done the file will use the method readMIDIDatTOFile
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * This reads in the data from the files and places the data into a text file
     */
    private void readMIDIDataToFile(File file) {
        ArrayList<Integer> keysUsed = new ArrayList<>();                                                                                //This ArrayList stores all the keys(notes) used
        ArrayList<Integer> volumeUsed = new ArrayList<>();                                                                              //This ArrayList stores all the volumes used
        ArrayList<Long> durationPlaying = new ArrayList<>();                                                                            //This ArrayList stores all the note durations used
        ArrayList<Long> durationResting = new ArrayList<>();                                                                            //This ArrayList stores all the rest durations used
        ArrayList<Integer> pitchResting = new ArrayList<>();                                                                            //This ArrayList stores all the rest notes pitch used
        Sequencer sequencer;
        final int NOTE_ON = 0x90;                                                                                                       //This is the int of note on signals
        final int NOTE_OFF = 0x80;                                                                                                      //This is the int of note off signals
        try {                                                                                                                           //This uses a try catch to open and read MIDI files
            PrintWriter print = new PrintWriter(new FileWriter(folderToOpen.toString() + "//MidiData.txt", true));     // This opens a print writer which writes to the text file created earlier in the program
            print.println("Directory " + file);                                                                                         //This outputs the midi file address
            sequencer = MidiSystem.getSequencer();                                                                                      //This gets the sequencer of the midi file which is then opened
            sequencer.open();
            Sequence fileSequence = MidiSystem.getSequence(file);
            sequencer.setSequence(fileSequence);
            print.println("Length " + Math.round(sequencer.getMicrosecondLength()));
            print.println("Quarter Note " + Math.round(sequencer.getTempoInMPQ()));                                                     //This gets the Micro seconds per quarter note value
            print.println("Tempo " + Math.round(sequencer.getTempoInBPM()));                                                            //This gets the beats per minute
            print.println("Resolution " + fileSequence.getResolution());
            print.close();
            int trackNumber = 0;
            for (Track track : fileSequence.getTracks()) {
                trackNumber++;
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);
                    MidiMessage message = event.getMessage();
                    if (message instanceof ShortMessage) {
                        ShortMessage sm = (ShortMessage) message;
                        if (sm.getCommand() == NOTE_ON) {
                            int key = sm.getData1();
                            int volume = sm.getData2();
                            if (volume != 0) {
                                durationPlaying.add(event.getTick());
                                keysUsed.add(key);
                                volumeUsed.add(volume);
                            }
                        }
                        if (sm.getCommand() == NOTE_OFF || sm.getData2() == 0) {
                            durationResting.add(event.getTick());
                            pitchResting.add(sm.getData1());
                        }
                    }
                }
                PrintWriter printWriter = new PrintWriter(new FileWriter(folderToOpen.toString() + "//MidiData.txt", true));    //This allows the values to be written from the values to a text file
                printWriter.println("Track " + trackNumber);
                if (keysUsed.size() != 0) {
                    printWriter.println("Note value " + keysUsed.size() + keysUsed.toString());
                }
                if (volumeUsed.size() != 0) {
                    printWriter.println("Volume " + volumeUsed.size() + volumeUsed.toString());
                }
                if (pitchResting.size() != 0) {
                    printWriter.println("Note Rest " + pitchResting.size() + pitchResting.toString());
                }
                if (durationResting.size() != 0) {
                    Collections.reverse(durationResting);
                    for (int j = 0; j < durationResting.size() - 1; j++) {
                        if (durationResting.get(j) > durationResting.get(j + 1)) {                                                  //This gets the difference between ticks otherwise it would just increase constantly and creates actual values
                            durationResting.set(j, durationResting.get(j) - durationResting.get(j + 1));
                        }
                    }
                    Collections.reverse(durationResting);
                    printWriter.println("Rest " + durationResting.size() + durationResting.toString());
                }
                if (durationPlaying.size() != 0) {
                    Collections.reverse(durationPlaying);
                    for (int j = 0; j < durationPlaying.size() - 1; j++) {
                        if (durationPlaying.get(j) > durationPlaying.get(j + 1)) {                                                  //This also gets the difference between ticks otherwise it would just increase constantly and creates actual values
                            durationPlaying.set(j, durationPlaying.get(j) - durationPlaying.get(j + 1));
                        }
                    }
                    Collections.reverse(durationPlaying);
                    printWriter.println("Duration " + durationPlaying.size() + durationPlaying.toString());
                }
                printWriter.close();                                                                                                //This closes the print writer to help with garbage collection
            }
            sequencer.close();                                                                                                      //This closes the print writer to help with garbage collection

        } catch (MidiUnavailableException | IOException | InvalidMidiDataException e) {
            e.printStackTrace();
        }
        System.out.println("Data of " + file + " written to text file");
    }

    /**
     * This method reads in the data from the text file and places it into specific arrays.
     */
    void readDataFromFile() {
        BufferedReader reader;                                                                                  //Makes a new buffered reader so that I can read the file in
        int arrayHolder = -1;                                                                                   //This integer is used to work out which track the user is adding to
        int nextLineInt = 0;                                                                                    //This int finds the amount of lines the text file contains
        boolean addNewTempo = false;                                                                            //This boolean is used to check if a piece has a different style
        try {
            reader = new BufferedReader(new FileReader(folderToOpen.toString() + "\\MidiData.txt"));   //Tries to find the file to open as it is using external files
            while (true) {                                                                                      //While the file has another line
                String line = reader.readLine();                                                                //Turns each of the lines read by the buffered reader into a string
                nextLineInt++;
                if (line == null) {                                                                             //This breaks the loop if any lines are empty as it means the file is finished
                    break;
                }
                if (line.length() > 5) {
                    if (line.substring(0, 5).equals("Tempo")) {                                                 //This checks the first few chars in the string and if so will add the tempo to the previously initialised array
                        boolean tempoExists = false;
                        addNewTempo = false;
                        int tempo = Integer.parseInt((line.substring(6)));
                        if (!everyTempo.isEmpty()) {                                                            //This is used to check whether or not the tempo is different by a factor of 9 so that the different style is discovered
                            for (int i = -9; i < 18; i++) {
                                if (everyTempo.contains(tempo + i)) {
                                    tempoExists = true;
                                }
                            }
                        } else {
                            tempoExists = true;
                        }
                        if (!tempoExists) {                                                                     //If this does occur then the steps are made to add to the different style arrays
                            everyTempo.add(tempo);
                            addNewTempo = true;
                            everyNotePitchDifferentStyle.add(new ArrayList<>());
                            everyVolumeDifferentStyle.add(new ArrayList<>());
                            everyNoteDurationDifferentStyle.add(new ArrayList<>());
                            everyRestDurationDifferentStyle.add(new ArrayList<>());
                            everyPitchRestDifferentStyle.add(new ArrayList<>());
                        } else everyTempo.add(tempo);
                    }
                    if (line.substring(0, 5).equals("Track")) {                                                 //This checks the first few chars in the string so will change the track value
                        if (nextLineInt != Files.readAllLines(Paths.get(folderToOpen.toString() + "\\MidiData.txt")).size()) {
                            String nextLine = Files.readAllLines(Paths.get(folderToOpen.toString() + "\\MidiData.txt")).get(nextLineInt);
                            if (!nextLine.substring(0, 5).equals("Track")) {
                                arrayHolder++;
                            }
                        }
                    }
                }

                if (line.length() > 12) {                                                                       //This checks to prevent against a null pointer exception from occuring
                    if (line.substring(0, 12).equals("Quarter Note")) {                                         //This checks the first few chars in the string and if so will add the Quarter Note to the previously initialised array
                        float quarterNote = Integer.parseInt(line.substring(13));
                        everyQuarterNote.add(quarterNote / 1000000);                                            //This converts from microseconds to seconds to make it easier to deal with
                    }
                }
                if (line.substring(0, 4).equals("Rest")) {
                    if (addNewTempo && everyTempo.size() > 1) {
                        everyRestDurationDifferentStyle.get(everyRestDurationDifferentStyle.size() - 1).add(new ArrayList<>());
                        int RestArraySize = Integer.parseInt(line.substring(5, line.indexOf("[")));
                        int[] RestArray = new int[RestArraySize];
                        line = line.substring(line.indexOf("["));                                                   //This breaks up the string and gets the string I am looking for
                        line = line.replace("[", " ");
                        line = line.substring(0, line.length() - 1) + ",";
                        int comma = line.indexOf(",");
                        int space = line.indexOf(" ");
                        int i = 0;
                        while (comma >= 0) {                                                                        //This will get all the values between a space and a comma and turn them into integers and add them to an array
                            RestArray[i] = (Integer.parseInt(line.substring((space) + 1, comma)));
                            comma = line.indexOf(",", comma + 1);
                            space = line.indexOf(" ", space + 1);
                            i++;
                        }
                        for (int aRestArray : RestArray) {                                                          //Using a for each loop the values are added to the array
                            everyRestDurationDifferentStyle.get(everyRestDurationDifferentStyle.size() - 1).get(arrayHolder).add((int) (aRestArray * valueToChangeRestDurationBy));
                        }
                    } else {
                        if (everyRestDuration.size() < arrayHolder + 1) {
                            everyRestDuration.add(new ArrayList<>());
                        }
                        int RestArraySize = Integer.parseInt(line.substring(5, line.indexOf("[")));
                        int[] RestArray = new int[RestArraySize];
                        line = line.substring(line.indexOf("["));                                                   //This breaks up the string and gets the string I am looking for
                        line = line.replace("[", " ");
                        line = line.substring(0, line.length() - 1) + ",";
                        int comma = line.indexOf(",");
                        int space = line.indexOf(" ");
                        int i = 0;
                        while (comma >= 0) {                                                                        //This will get all the values between a space and a comma and turn them into integers and add them to an array
                            RestArray[i] = (Integer.parseInt(line.substring((space) + 1, comma)));
                            comma = line.indexOf(",", comma + 1);
                            space = line.indexOf(" ", space + 1);
                            i++;
                        }
                        for (int aRestArray : RestArray) {                                                          //Using a for each loop the values are added to the array
                            everyRestDuration.get(arrayHolder).add((int) (aRestArray * valueToChangeRestDurationBy));
                        }
                    }
                }
                if (line.length() > 6) {
                    if (line.substring(0, 6).equals("Length")) {
                        float length = Integer.parseInt(line.substring(7));
                        everyPieceLength.add(length / 1000000);
                    }
                    if (line.substring(0, 6).equals("Volume")) {
                        if (addNewTempo && everyTempo.size() > 1) {                                                 //This checks that the values are of a different style
                            everyVolumeDifferentStyle.get(everyVolumeDifferentStyle.size() - 1).add(new ArrayList<>());
                            int RestArraySize = Integer.parseInt(line.substring(7, line.indexOf("[")));
                            int[] RestArray = new int[RestArraySize];
                            line = line.substring(line.indexOf("["));
                            line = line.replace("[", " ");
                            line = line.substring(0, line.length() - 1) + ",";
                            int comma = line.indexOf(",");
                            int space = line.indexOf(" ");
                            int i = 0;
                            while (comma >= 0) {
                                RestArray[i] = (Integer.parseInt(line.substring((space) + 1, comma)));
                                comma = line.indexOf(",", comma + 1);
                                space = line.indexOf(" ", space + 1);
                                i++;
                            }
                            for (int aRestArray : RestArray) {
                                everyVolumeDifferentStyle.get(everyVolumeDifferentStyle.size() - 1).get(arrayHolder).add((int) (aRestArray * valueToChangeRestDurationBy));
                            }
                        } else {
                            if (everyVolume.size() < arrayHolder + 1) {
                                everyVolume.add(new ArrayList<>());                                                 //This makes sure that there are not more tracks added to the arraylsits than there need to be
                            }
                            int RestArraySize = Integer.parseInt(line.substring(7, line.indexOf("[")));
                            int[] RestArray = new int[RestArraySize];
                            line = line.substring(line.indexOf("["));
                            line = line.replace("[", " ");
                            line = line.substring(0, line.length() - 1) + ",";
                            int comma = line.indexOf(",");
                            int space = line.indexOf(" ");
                            int i = 0;
                            while (comma >= 0) {
                                RestArray[i] = (Integer.parseInt(line.substring((space) + 1, comma)));
                                comma = line.indexOf(",", comma + 1);
                                space = line.indexOf(" ", space + 1);
                                i++;
                            }
                            for (int aRestArray : RestArray) {
                                everyVolume.get(arrayHolder).add((int) (aRestArray * valueToChangeVolumeBy));
                            }
                        }

                    }
                }
                if (line.length() > 8) {
                    if (line.substring(0, 8).equals("Duration")) {
                        if (addNewTempo && everyTempo.size() > 1) {
                            everyNoteDurationDifferentStyle.get(everyNoteDurationDifferentStyle.size() - 1).add(new ArrayList<>());
                            int RestArraySize = Integer.parseInt(line.substring(9, line.indexOf("[")));
                            int[] RestArray = new int[RestArraySize];
                            line = line.substring(line.indexOf("["));
                            line = line.replace("[", " ");
                            line = line.substring(0, line.length() - 1) + ",";
                            int comma = line.indexOf(",");
                            int space = line.indexOf(" ");
                            int i = 0;
                            while (comma >= 0) {
                                RestArray[i] = (Integer.parseInt(line.substring((space) + 1, comma)));
                                comma = line.indexOf(",", comma + 1);
                                space = line.indexOf(" ", space + 1);
                                i++;
                            }
                            for (int aRestArray : RestArray) {
                                everyNoteDurationDifferentStyle.get(everyNoteDurationDifferentStyle.size() - 1).get(arrayHolder).add((int) (aRestArray * valueToChangeNoteDurationBy));
                            }
                        } else {
                            if (everyNoteDuration.size() < arrayHolder + 1) {
                                everyNoteDuration.add(new ArrayList<>());
                            }
                            int RestArraySize = Integer.parseInt(line.substring(9, line.indexOf("[")));
                            int[] RestArray = new int[RestArraySize];
                            line = line.substring(line.indexOf("["));
                            line = line.replace("[", " ");
                            line = line.substring(0, line.length() - 1) + ",";
                            int comma = line.indexOf(",");
                            int space = line.indexOf(" ");
                            int i = 0;
                            while (comma >= 0) {
                                RestArray[i] = (Integer.parseInt(line.substring((space) + 1, comma)));
                                comma = line.indexOf(",", comma + 1);
                                space = line.indexOf(" ", space + 1);
                                i++;
                            }
                            for (int aRestArray : RestArray) {
                                everyNoteDuration.get(arrayHolder).add((int) (aRestArray * valueToChangeNoteDurationBy));
                            }
                        }
                    }
                }
                if (line.length() > 9) {
                    if (line.substring(0, 9).equals("Directory")) {
                        arrayHolder = -1;
                    }
                    if (line.substring(0, 9).equals("Note Rest")) {
                        if (addNewTempo && everyTempo.size() > 1) {
                            everyPitchRestDifferentStyle.get(everyPitchRestDifferentStyle.size() - 1).add(new ArrayList<>());
                            int RestArraySize = Integer.parseInt(line.substring(10, line.indexOf("[")));
                            int[] RestArray = new int[RestArraySize];
                            line = line.substring(line.indexOf("["));
                            line = line.replace("[", " ");
                            line = line.substring(0, line.length() - 1) + ",";
                            int comma = line.indexOf(",");
                            int space = line.indexOf(" ");
                            int i = 0;
                            while (comma >= 0) {
                                RestArray[i] = (Integer.parseInt(line.substring((space) + 1, comma)));
                                comma = line.indexOf(",", comma + 1);
                                space = line.indexOf(" ", space + 1);
                                i++;
                            }
                            for (int aRestArray : RestArray) {
                                everyPitchRestDifferentStyle.get(everyPitchRestDifferentStyle.size() - 1).get(arrayHolder).add(aRestArray + valueToChangeNoteBy);
                            }
                        } else {
                            if (everyPitchRest.size() < arrayHolder + 1) {
                                everyPitchRest.add(new ArrayList<>());
                            }
                            int RestArraySize = Integer.parseInt(line.substring(10, line.indexOf("[")));
                            int[] RestArray = new int[RestArraySize];
                            line = line.substring(line.indexOf("["));
                            line = line.replace("[", " ");
                            line = line.substring(0, line.length() - 1) + ",";
                            int comma = line.indexOf(",");
                            int space = line.indexOf(" ");
                            int i = 0;
                            while (comma >= 0) {
                                RestArray[i] = (Integer.parseInt(line.substring((space) + 1, comma)));
                                comma = line.indexOf(",", comma + 1);
                                space = line.indexOf(" ", space + 1);
                                i++;
                            }
                            for (int aRestArray : RestArray) {
                                everyPitchRest.get(arrayHolder).add((int) (aRestArray * valueToChangeRestDurationBy));
                            }
                        }


                    }
                }
                if (line.length() > 10) {
                    if (line.substring(0, 10).equals("Note value")) {
                        if (addNewTempo && everyTempo.size() > 1) {
                            everyNotePitchDifferentStyle.get(everyNotePitchDifferentStyle.size() - 1).add(new ArrayList<>());
                            int RestArraySize = Integer.parseInt(line.substring(11, line.indexOf("[")));
                            int[] RestArray = new int[RestArraySize];
                            line = line.substring(line.indexOf("["));
                            line = line.replace("[", " ");
                            line = line.substring(0, line.length() - 1) + ",";
                            int comma = line.indexOf(",");
                            int space = line.indexOf(" ");
                            int i = 0;
                            while (comma >= 0) {
                                RestArray[i] = (Integer.parseInt(line.substring((space) + 1, comma)));
                                comma = line.indexOf(",", comma + 1);
                                space = line.indexOf(" ", space + 1);
                                i++;
                            }
                            for (int aRestArray : RestArray) {
                                everyNotePitchDifferentStyle.get(everyNotePitchDifferentStyle.size() - 1).get(arrayHolder).add(aRestArray + valueToChangeNoteBy);
                            }
                        } else {
                            if (everyNote.size() < arrayHolder + 1) {
                                everyNote.add(new ArrayList<>());
                            }
                            int RestArraySize = Integer.parseInt(line.substring(11, line.indexOf("[")));
                            int[] RestArray = new int[RestArraySize];
                            line = line.substring(line.indexOf("["));
                            line = line.replace("[", " ");
                            line = line.substring(0, line.length() - 1) + ",";
                            int comma = line.indexOf(",");
                            int space = line.indexOf(" ");
                            int i = 0;
                            while (comma >= 0) {
                                RestArray[i] = (Integer.parseInt(line.substring((space) + 1, comma)));
                                comma = line.indexOf(",", comma + 1);
                                space = line.indexOf(" ", space + 1);
                                i++;
                            }
                            for (int aRestArray : RestArray) {
                                everyNote.get(arrayHolder).add(aRestArray + valueToChangeNoteBy);
                            }
                        }
                    } else if (line.substring(0, 10).equals("Resolution")) {
                        int resolution = Integer.parseInt(line.substring(11));
                        everyResolution.add(resolution);
                    }
                }
            }
            reader.close();                                                                                         //This will close the reader to help with garbage collection
        } catch (
                IOException e)

        {
            e.printStackTrace();
        }

    }

    /**
     * This method converts all of the data in arrays into objects of "note" and "rest" type and then placed into other arrays
     */
    void noteObject() {
        for (int i = 0; i < everyNote.size(); i++) {                                                                //Using a for loop the values will be combined into note and rest objects
            noteArray.add(new ArrayList<>());
            for (int j = 0; j < everyNote.get(i).size(); j++) {
                Note newNote = new Note(everyNote.get(i).get(j), everyVolume.get(i).get(j), everyNoteDuration.get(i).get(j));
                noteArray.get(i).add(newNote);
            }
        }
        everyNote.clear();                                                                                          //These ArrayLists are cleared to help with garbage collection and make the program more efficient
        everyVolume.clear();
        everyNoteDuration.clear();
        for (int i = 0; i < everyRestDuration.size(); i++) {
            restArray.add(new ArrayList<>());
            for (int j = 0; j < everyRestDuration.get(i).size(); j++) {
                Rest newRest = new Rest(everyRestDuration.get(i).get(j), everyPitchRest.get(i).get(j));
                restArray.get(i).add(newRest);
            }
        }
        everyRestDuration.clear();
        everyPitchRest.clear();
        for (int i = 0; i < everyNotePitchDifferentStyle.size(); i++) {                                             //If there are different styles used then these will be used and new arrays will be made
            noteArrayDifferentStyle.add(new ArrayList<>());
            for (int j = 0; j < everyNotePitchDifferentStyle.get(i).size(); j++) {
                noteArrayDifferentStyle.get(i).add(new ArrayList<>());
                for (int k = 0; k < everyNotePitchDifferentStyle.get(i).get(j).size(); k++) {
                    Note newNote = new Note(everyNotePitchDifferentStyle.get(i).get(j).get(k), everyVolumeDifferentStyle.get(i).get(j).get(k), everyNoteDurationDifferentStyle.get(i).get(j).get(k));
                    noteArrayDifferentStyle.get(i).get(j).add(newNote);
                }
            }
        }
        everyNotePitchDifferentStyle.clear();
        everyVolumeDifferentStyle.clear();
        everyNoteDurationDifferentStyle.clear();
        for (int i = 0; i < everyRestDurationDifferentStyle.size(); i++) {
            restArrayDifferentStyle.add(new ArrayList<>());
            for (int j = 0; j < everyRestDurationDifferentStyle.get(i).size(); j++) {
                restArrayDifferentStyle.get(i).add(new ArrayList<>());
                for (int k = 0; k < everyRestDurationDifferentStyle.get(i).get(j).size(); k++) {
                    Rest newRest = new Rest(everyRestDurationDifferentStyle.get(i).get(j).get(k), everyPitchRestDifferentStyle.get(i).get(j).get(k));
                    restArrayDifferentStyle.get(i).get(j).add(newRest);
                }
            }
        }
        everyRestDurationDifferentStyle.clear();
        everyPitchRestDifferentStyle.clear();
    }

    /**
     * This method analyses the data and coverts the note and rest values into markov chains
     */
    void markovChain() {
        System.out.println("Markov Chains Being produced");                                                         //This allows the user to be able to see that the program is working correctly and where the code is in the process
        boolean value = false;                                                                                      //This is used by the program to determine whether or not the note to be used exists in the note objects used array and finds the index of the value
        for (int i = 0; i < noteArray.size(); i++) {
            noteObjectsUsed.add(new ArrayList<>());                                                                 //This ArrayList is used to contain all the different types of values that occur
            markovNotes.add(new ArrayList<>());                                                                     //This ArrayList is used to contain all of the notes that occur after the note indexed in the aforementioned ArrayList
            for (int j = 0; j < noteArray.get(i).size(); j++) {
                System.out.println(Math.round((float) j / noteArray.get(i).size() * 100) + "%/100 " + (1 + i) + " of " + noteArray.size() + " 1 of 2");     //This is used to show how much progress the user has reached so that they can be sure that the code is working
                markovNotes.get(i).add(new ArrayList<>());
                for (int k = 0; k < noteArray.get(i).size() - 1; k++) {
                    for (Note aNoteObjectsUsed : noteObjectsUsed.get(i)) {
                        value = noteArray.get(i).get(j).getPitch() == aNoteObjectsUsed.getPitch() && noteArray.get(i).get(j).getDuration() == aNoteObjectsUsed.getDuration() && noteArray.get(i).get(j).getVolume() == aNoteObjectsUsed.getVolume();
                        if (value) {
                            break;
                        }
                    }
                    if (value) {
                        break;
                    }
                    if (noteArray.get(i).get(j).getPitch() == noteArray.get(i).get(k).getPitch() && noteArray.get(i).get(j).getDuration() == noteArray.get(i).get(k).getDuration() && noteArray.get(i).get(j).getVolume() == noteArray.get(i).get(k).getVolume()) {
                        markovNotes.get(i).get(markovNotes.get(i).size() - 1).add(noteArray.get(i).get(k + 1));
                    }
                }
                if (!value) {
                    noteObjectsUsed.get(i).add(noteArray.get(i).get(j));
                }
                if (value) {
                    markovNotes.get(i).remove(markovNotes.get(i).size() - 1);
                    value = false;
                }
            }
        }
        noteArray.clear();                                                                                          //This is cleared to help with garbage collectij and efficiency
        for (int i = 0; i < restArray.size(); i++) {
            restObjectsUsed.add(new ArrayList<>());
            markovRest.add(new ArrayList<>());
            for (int j = 0; j < restArray.get(i).size(); j++) {
                System.out.println(Math.round((float) j / restArray.get(i).size() * 100) + "%/100 " + (1 + i) + " of " + restArray.size() + " 2 of 2");
                markovRest.get(i).add(new ArrayList<>());
                for (int k = 0; k < restArray.get(i).size() - 1; k++) {
                    for (Rest aRestObjectsUsed : restObjectsUsed.get(i)) {
                        value = restArray.get(i).get(j).getDuration() == aRestObjectsUsed.getDuration() && restArray.get(i).get(j).getPitch() == aRestObjectsUsed.getPitch();
                        if (value) {
                            break;
                        }
                    }
                    if (value) {
                        break;
                    }
                    if (restArray.get(i).get(j).getDuration() == restArray.get(i).get(k).getDuration() && restArray.get(i).get(j).getPitch() == restArray.get(i).get(k).getPitch()) {
                        markovRest.get(i).get(markovRest.get(i).size() - 1).add(restArray.get(i).get(k + 1));
                    }
                }
                if (!value) {
                    restObjectsUsed.get(i).add(restArray.get(i).get(j));
                }
                if (value) {
                    markovRest.get(i).remove(markovRest.get(i).size() - 1);
                    value = false;
                }
            }
        }
        restArray.clear();
        for (int i = 0; i < noteArrayDifferentStyle.size(); i++) {                                                  //THis is a bit more complicated as it contains another level of integrated ArrayLists
            noteObjectsUsedDifferentStyle.add(new ArrayList<>());
            markovNotesDifferentStyle.add(new ArrayList<>());
            for (int j = 0; j < noteArrayDifferentStyle.get(i).size(); j++) {
                noteObjectsUsedDifferentStyle.get(i).add(new ArrayList<>());
                markovNotesDifferentStyle.get(i).add(new ArrayList<>());
                for (int l = 0; l < noteArrayDifferentStyle.get(i).get(j).size(); l++) {
                    System.out.println(Math.round((float) l / noteArrayDifferentStyle.get(i).get(j).size() * 100) + "%/100 " + (1 + i) + " of " + noteArrayDifferentStyle.size() + " 1 of 2");
                    markovNotesDifferentStyle.get(i).get(j).add(new ArrayList<>());
                    for (int k = 0; k < noteArrayDifferentStyle.get(i).get(j).size() - 1; k++) {
                        for (Note anoteObjectsUsedDifferentStyle : noteObjectsUsedDifferentStyle.get(i).get(j)) {
                            value = noteArrayDifferentStyle.get(i).get(j).get(l).getDuration() == anoteObjectsUsedDifferentStyle.getDuration() && noteArrayDifferentStyle.get(i).get(j).get(l).getPitch() == anoteObjectsUsedDifferentStyle.getPitch() && noteArrayDifferentStyle.get(i).get(j).get(l).getVolume() == anoteObjectsUsedDifferentStyle.getVolume();
                            if (value) {
                                break;
                            }
                        }
                        if (value) {
                            break;
                        }
                        if (noteArrayDifferentStyle.get(i).get(j).get(l).getDuration() == noteArrayDifferentStyle.get(i).get(j).get(k).getDuration() && noteArrayDifferentStyle.get(i).get(j).get(l).getPitch() == noteArrayDifferentStyle.get(i).get(j).get(k).getPitch() && noteArrayDifferentStyle.get(i).get(j).get(l).getVolume() == noteArrayDifferentStyle.get(i).get(j).get(k).getVolume()) {
                            markovNotesDifferentStyle.get(i).get(j).get(markovNotesDifferentStyle.get(i).get(j).size() - 1).add(noteArrayDifferentStyle.get(i).get(j).get(k + 1));
                        }
                    }
                    if (!value) {
                        noteObjectsUsedDifferentStyle.get(i).get(j).add(noteArrayDifferentStyle.get(i).get(j).get(l));
                    }
                    if (value) {
                        markovNotesDifferentStyle.get(i).get(j).remove(markovNotesDifferentStyle.get(i).get(j).size() - 1);
                        value = false;
                    }
                }
            }
        }
        noteArrayDifferentStyle.clear();
        for (int i = 0; i < restArrayDifferentStyle.size(); i++) {
            restObjectsUsedDifferentStyle.add(new ArrayList<>());
            markovRestsDifferentStyle.add(new ArrayList<>());
            for (int j = 0; j < restArrayDifferentStyle.get(i).size(); j++) {
                restObjectsUsedDifferentStyle.get(i).add(new ArrayList<>());
                markovRestsDifferentStyle.get(i).add(new ArrayList<>());
                for (int l = 0; l < restArrayDifferentStyle.get(i).get(j).size(); l++) {
                    System.out.println(Math.round((float) l / restArrayDifferentStyle.get(i).get(j).size() * 100) + "%/100 " + (1 + i) + " of " + restArrayDifferentStyle.size() + " 2 of 2");
                    markovRestsDifferentStyle.get(i).get(j).add(new ArrayList<>());
                    for (int k = 0; k < restArrayDifferentStyle.get(i).get(j).size() - 1; k++) {
                        for (Rest arestObjectsUsedDifferentStyle : restObjectsUsedDifferentStyle.get(i).get(j)) {
                            value = restArrayDifferentStyle.get(i).get(j).get(l).getDuration() == arestObjectsUsedDifferentStyle.getDuration() && restArrayDifferentStyle.get(i).get(j).get(l).getPitch() == arestObjectsUsedDifferentStyle.getPitch();
                            if (value) {
                                break;
                            }
                        }
                        if (value) {
                            break;
                        }
                        if (restArrayDifferentStyle.get(i).get(j).get(l).getDuration() == restArrayDifferentStyle.get(i).get(j).get(k).getDuration() && restArrayDifferentStyle.get(i).get(j).get(l).getPitch() == restArrayDifferentStyle.get(i).get(j).get(k).getPitch()) {
                            markovRestsDifferentStyle.get(i).get(j).get(markovRestsDifferentStyle.get(i).get(j).size() - 1).add(restArrayDifferentStyle.get(i).get(j).get(k + 1));
                        }
                    }
                    if (!value) {
                        restObjectsUsedDifferentStyle.get(i).get(j).add(restArrayDifferentStyle.get(i).get(j).get(l));
                    }
                    if (value) {
                        markovRestsDifferentStyle.get(i).get(j).remove(markovRestsDifferentStyle.get(i).get(j).size() - 1);
                        value = false;
                    }
                }
            }
        }
        restArrayDifferentStyle.clear();
    }

    /**
     * This method uses all of the data that has been gathered and manipulated in the previous methods and converts it into a MIDI file
     */
    void makeMIDIFile() {
        List<List<Note>> notesToPlay = new ArrayList<>();                                                           //This ArrayList is used to work out the order to play the notes in so that the order can be determined and then added to the MIDIFile
        List<List<Rest>> restToPlay = new ArrayList<>();                                                            //This ArrayList is used to work out the order to play the notes in so that the order can be determined and then added to the MIDIFile
        List<List<List<Note>>> notesToPlayDifferentStyle = new ArrayList<>();                                       //This ArrayList is used to work out the order to play the notes in which is of a different style so that the order can be determined and then added to the MIDIFile
        List<List<List<Rest>>> restToPlayDifferentStyle = new ArrayList<>();                                        //This ArrayList is used to work out the order to play the notes in which is of a different style so that the order can be determined and then added to the MIDIFile
        Random random = new Random();
        int ticksPerSecond = (int) (everyResolution.get(random.nextInt(everyResolution.size())) * (everyTempo.get(random.nextInt(everyTempo.size())) / 60.0));      //This is use to determine how many ticks per second the piece being produced will have
        float check = (everyPieceLength.get(random.nextInt(everyPieceLength.size()))) * ticksPerSecond;             //This is used to "check" that the file produced is the right length
        for (int a = 0; a < noteObjectsUsed.size(); a++) {
            notesToPlay.add(new ArrayList<>());
            boolean alternative = false;
            notesToPlay.get(a).add(noteObjectsUsed.get(a).get(0));
            int notesLength = 0;
            while (check > notesLength) {                                                                           //This is used to make use of the previously made "markov chain arrays" and will use them to determine which note to use next
                if (noteObjectsUsed.get(a).contains(notesToPlay.get(notesToPlay.size() - 1).get(notesToPlay.get(notesToPlay.size() - 1).size() - 1))) {
                    int markovChainIndex = noteObjectsUsed.get(a).indexOf(notesToPlay.get(a).get(notesToPlay.get(a).size() - 1));
                    if (markovNotes.get(a).get(markovChainIndex).size() > 0) {
                        notesToPlay.get(a).add(markovNotes.get(a).get(markovChainIndex).get(random.nextInt(markovNotes.get(a).get(markovChainIndex).size())));
                        notesLength = notesLength + notesToPlay.get(a).get(notesToPlay.get(a).size() - 1).getDuration();
                        alternative = false;
                    } else if (notesToPlay.get(a).size() > 1) {                                                     //If the markov chain index does not contain any values the code will use the first value of the array
                        if (noteObjectsUsed.get(a).contains(notesToPlay.get(notesToPlay.size() - 1).get(notesToPlay.get(notesToPlay.size() - 1).size() - 2))) {
                            int markovChainIndexAlternative = noteObjectsUsed.get(a).indexOf(notesToPlay.get(notesToPlay.size() - 1).get(notesToPlay.get(notesToPlay.size() - 1).size() - 2));
                            if (markovNotes.get(a).get(markovChainIndexAlternative).size() > 0 && !alternative) {
                                notesToPlay.get(a).add(markovNotes.get(a).get(markovChainIndexAlternative).get(random.nextInt(markovNotes.get(a).get(markovChainIndexAlternative).size())));
                                notesLength = notesLength + notesToPlay.get(notesToPlay.size() - 1).get(notesToPlay.get(notesToPlay.size() - 1).size() - 1).getDuration();
                                notesLength = notesLength + notesToPlay.get(a).get(notesToPlay.get(a).size() - 1).getDuration();
                                alternative = true;                                                                 //This is used to make sure that the program does not use this more than once
                            } else {
                                notesToPlay.get(a).add(notesToPlay.get(a).get(random.nextInt(notesToPlay.get(a).size())));
                                notesLength = notesLength + notesToPlay.get(a).get(notesToPlay.get(a).size() - 1).getDuration();
                                alternative = false;
                            }
                        } else {
                            notesToPlay.get(a).add(notesToPlay.get(a).get(random.nextInt(notesToPlay.get(a).size())));
                            notesLength = notesLength + notesToPlay.get(a).get(notesToPlay.get(a).size() - 1).getDuration();
                            alternative = false;                                                                    //This gets a random value and adds it to hopefully refresh the code
                        }
                    } else {
                        notesToPlay.get(a).add(notesToPlay.get(a).get(random.nextInt(notesToPlay.get(a).size())));
                        notesLength = notesLength + notesToPlay.get(a).get(notesToPlay.get(a).size() - 1).getDuration();
                        alternative = false;
                    }
                } else if (notesToPlay.get(a).size() > 1) {
                    if (noteObjectsUsed.get(a).contains(notesToPlay.get(notesToPlay.size() - 1).get(notesToPlay.get(notesToPlay.size() - 1).size() - 2))) {
                        int markovChainIndexAlternative = noteObjectsUsed.get(a).indexOf(notesToPlay.get(notesToPlay.size() - 1).get(notesToPlay.get(notesToPlay.size() - 1).size() - 2));
                        if (markovNotes.get(a).get(markovChainIndexAlternative).size() > 0 && !alternative) {
                            notesToPlay.get(a).add(markovNotes.get(a).get(markovChainIndexAlternative).get(random.nextInt(markovNotes.get(a).get(markovChainIndexAlternative).size())));
                            notesLength = notesLength + notesToPlay.get(notesToPlay.size() - 1).get(notesToPlay.get(notesToPlay.size() - 1).size() - 1).getDuration();
                            notesLength = notesLength + notesToPlay.get(a).get(notesToPlay.get(a).size() - 1).getDuration();
                            alternative = true;
                        } else {
                            notesToPlay.get(a).add(notesToPlay.get(a).get(random.nextInt(notesToPlay.get(a).size())));
                            notesLength = notesLength + notesToPlay.get(a).get(notesToPlay.get(a).size() - 1).getDuration();
                            alternative = false;
                        }
                    } else {
                        notesToPlay.get(a).add(notesToPlay.get(a).get(random.nextInt(notesToPlay.get(a).size())));
                        notesLength = notesLength + notesToPlay.get(a).get(notesToPlay.get(a).size() - 1).getDuration();
                        alternative = false;
                    }
                } else {
                    notesToPlay.get(a).add(notesToPlay.get(a).get(random.nextInt(notesToPlay.get(a).size())));
                    notesLength = notesLength + notesToPlay.get(a).get(notesToPlay.get(a).size() - 1).getDuration();
                    alternative = false;
                }
            }
        }
        for (int a = 0; a < noteObjectsUsedDifferentStyle.size(); a++) {
            notesToPlayDifferentStyle.add(new ArrayList<>());
            for (int b = 0; b < noteObjectsUsedDifferentStyle.get(a).size(); b++) {
                notesToPlayDifferentStyle.get(a).add(new ArrayList<>());
                boolean alternative = false;
                notesToPlayDifferentStyle.get(a).get(b).add(noteObjectsUsedDifferentStyle.get(a).get(b).get(0));
                int notesLength = 0;
                while (check > notesLength) {
                    if (noteObjectsUsedDifferentStyle.get(a).get(b).contains(notesToPlayDifferentStyle.get(a).get(b).get(notesToPlayDifferentStyle.get(a).get(b).size() - 1))) {
                        int markovChainIndex = noteObjectsUsedDifferentStyle.get(a).get(b).indexOf(notesToPlayDifferentStyle.get(a).get(b).get(notesToPlayDifferentStyle.get(a).get(b).size() - 1));
                        if (markovNotesDifferentStyle.get(a).get(b).get(markovChainIndex).size() > 0) {
                            notesToPlayDifferentStyle.get(a).get(b).add(markovNotesDifferentStyle.get(a).get(b).get(markovChainIndex).get(random.nextInt(markovNotesDifferentStyle.get(a).get(b).get(markovChainIndex).size())));
                            notesLength = notesLength + notesToPlayDifferentStyle.get(a).get(b).get(notesToPlayDifferentStyle.get(a).get(b).size() - 1).getDuration();
                            alternative = false;
                        } else if (notesToPlayDifferentStyle.get(a).size() > 1) {
                            if (noteObjectsUsedDifferentStyle.get(a).get(b).contains(notesToPlayDifferentStyle.get(a).get(b).get(notesToPlayDifferentStyle.get(a).get(b).size() - 2))) {
                                int markovChainIndexAlternative = noteObjectsUsedDifferentStyle.get(a).get(b).indexOf(notesToPlayDifferentStyle.get(a).get(b).get(notesToPlayDifferentStyle.get(a).get(b).size() - 2));
                                if (markovNotesDifferentStyle.get(a).get(b).get(markovChainIndexAlternative).size() > 0 && !alternative) {
                                    notesToPlayDifferentStyle.get(a).get(b).add(markovNotesDifferentStyle.get(a).get(b).get(markovChainIndexAlternative).get(random.nextInt(markovNotesDifferentStyle.get(a).get(b).get(markovChainIndexAlternative).size())));
                                    notesLength = notesLength + notesToPlayDifferentStyle.get(a).get(b).get(notesToPlayDifferentStyle.get(a).get(b).size() - 1).getDuration();
                                    alternative = true;
                                } else {
                                    notesToPlayDifferentStyle.get(a).get(b).add(notesToPlayDifferentStyle.get(a).get(b).get(random.nextInt(notesToPlayDifferentStyle.get(a).get(b).size())));
                                    notesLength = notesLength + notesToPlayDifferentStyle.get(a).get(b).get(notesToPlayDifferentStyle.get(a).get(b).size() - 1).getDuration();
                                    alternative = false;
                                }
                            } else {
                                notesToPlayDifferentStyle.get(a).get(b).add(notesToPlayDifferentStyle.get(a).get(b).get(random.nextInt(notesToPlayDifferentStyle.get(a).get(b).size())));
                                notesLength = notesLength + notesToPlayDifferentStyle.get(a).get(b).get(notesToPlayDifferentStyle.get(a).get(b).size() - 1).getDuration();
                                alternative = false;
                            }
                        } else {
                            notesToPlayDifferentStyle.get(a).get(b).add(notesToPlayDifferentStyle.get(a).get(b).get(random.nextInt(notesToPlayDifferentStyle.get(a).get(b).size())));
                            notesLength = notesLength + notesToPlayDifferentStyle.get(a).get(b).get(notesToPlayDifferentStyle.get(a).get(b).size() - 1).getDuration();
                            alternative = false;
                        }
                    } else if (notesToPlayDifferentStyle.get(a).size() > 1) {
                        if (noteObjectsUsedDifferentStyle.get(a).get(b).contains(notesToPlayDifferentStyle.get(a).get(b).get(notesToPlayDifferentStyle.get(a).get(b).size() - 2))) {
                            int markovChainIndexAlternative = noteObjectsUsedDifferentStyle.get(a).get(b).indexOf(notesToPlayDifferentStyle.get(a).get(b).get(notesToPlayDifferentStyle.get(a).get(b).size() - 2));
                            if (markovNotesDifferentStyle.get(a).get(b).get(markovChainIndexAlternative).size() > 0 && !alternative) {
                                notesToPlayDifferentStyle.get(a).get(b).add(markovNotesDifferentStyle.get(a).get(b).get(markovChainIndexAlternative).get(random.nextInt(markovNotesDifferentStyle.get(a).get(b).get(markovChainIndexAlternative).size())));
                                notesLength = notesLength + notesToPlayDifferentStyle.get(a).get(b).get(notesToPlayDifferentStyle.get(a).get(b).size() - 1).getDuration();
                                alternative = true;
                            } else {
                                notesToPlayDifferentStyle.get(a).get(b).add(notesToPlayDifferentStyle.get(a).get(b).get(random.nextInt(notesToPlayDifferentStyle.get(a).get(b).size())));
                                notesLength = notesLength + notesToPlayDifferentStyle.get(a).get(b).get(notesToPlayDifferentStyle.get(a).get(b).size() - 1).getDuration();
                                alternative = false;
                            }
                        } else {
                            notesToPlayDifferentStyle.get(a).get(b).add(notesToPlayDifferentStyle.get(a).get(b).get(random.nextInt(notesToPlayDifferentStyle.get(a).get(b).size())));
                            notesLength = notesLength + notesToPlayDifferentStyle.get(a).get(b).get(notesToPlayDifferentStyle.get(a).get(b).size() - 1).getDuration();
                            alternative = false;
                        }
                    } else {
                        notesToPlayDifferentStyle.get(a).get(b).add(notesToPlayDifferentStyle.get(a).get(b).get(random.nextInt(notesToPlayDifferentStyle.get(a).get(b).size())));
                        notesLength = notesLength + notesToPlayDifferentStyle.get(a).get(b).get(notesToPlayDifferentStyle.get(a).get(b).size() - 1).getDuration();
                        alternative = false;
                    }
                }
            }
        }
        for (int a = 0; a < restObjectsUsed.size(); a++) {
            restToPlay.add(new ArrayList<>());
            boolean alternative = false;
            restToPlay.get(a).add(restObjectsUsed.get(a).get(0));
            int restsLength = 0;
            while (check > restsLength) {
                if (restObjectsUsed.get(a).contains(restToPlay.get(restToPlay.size() - 1).get(restToPlay.get(restToPlay.size() - 1).size() - 1))) {
                    int markovChainIndex = restObjectsUsed.get(a).indexOf(restToPlay.get(restToPlay.size() - 1).get(restToPlay.get(restToPlay.size() - 1).size() - 1));
                    if (markovRest.get(a).get(markovChainIndex).size() > 0) {
                        restToPlay.get(a).add(markovRest.get(a).get(markovChainIndex).get(random.nextInt(markovRest.get(a).get(markovChainIndex).size())));
                        restsLength = restsLength + restToPlay.get(a).get(restToPlay.get(a).size() - 1).getDuration();
                        alternative = false;
                    } else if (restToPlay.get(a).size() > 1) {
                        if (restObjectsUsed.get(a).contains(restToPlay.get(restToPlay.size() - 1).get(restToPlay.get(restToPlay.size() - 1).size() - 2))) {
                            int markovChainIndexAlternative = restObjectsUsed.get(a).indexOf(restToPlay.get(restToPlay.size() - 1).get(restToPlay.get(restToPlay.size() - 1).size() - 2));
                            if (markovRest.get(a).get(markovChainIndexAlternative).size() > 0 && !alternative) {
                                restToPlay.get(a).add(markovRest.get(a).get(markovChainIndexAlternative).get(random.nextInt(markovRest.get(a).get(markovChainIndexAlternative).size())));
                                restsLength = restsLength + restToPlay.get(restToPlay.size() - 1).get(restToPlay.get(restToPlay.size() - 1).size() - 1).getDuration();
                                restsLength = restsLength + restToPlay.get(a).get(restToPlay.get(a).size() - 1).getDuration();
                                alternative = true;
                            } else {
                                restToPlay.get(a).add(restToPlay.get(a).get(random.nextInt(restToPlay.get(a).size())));
                                restsLength = restsLength + restToPlay.get(a).get(restToPlay.get(a).size() - 1).getDuration();
                                alternative = false;
                            }
                        } else {
                            restToPlay.get(a).add(restToPlay.get(a).get(random.nextInt(restToPlay.get(a).size())));
                            restsLength = restsLength + restToPlay.get(a).get(restToPlay.get(a).size() - 1).getDuration();
                            alternative = false;
                        }
                    } else {
                        restToPlay.get(a).add(restToPlay.get(a).get(random.nextInt(restToPlay.get(a).size())));
                        restsLength = restsLength + restToPlay.get(a).get(restToPlay.get(a).size() - 1).getDuration();
                        alternative = false;
                    }
                } else if (restToPlay.get(a).size() > 1) {
                    if (restObjectsUsed.get(a).contains(restToPlay.get(restToPlay.size() - 1).get(restToPlay.get(restToPlay.size() - 1).size() - 2))) {
                        int markovChainIndexAlternative = restObjectsUsed.get(a).indexOf(restToPlay.get(restToPlay.size() - 1).get(restToPlay.get(restToPlay.size() - 1).size() - 2));
                        if (markovRest.get(a).get(markovChainIndexAlternative).size() > 0 && !alternative) {
                            restToPlay.get(a).add(markovRest.get(a).get(markovChainIndexAlternative).get(random.nextInt(markovRest.get(a).get(markovChainIndexAlternative).size())));
                            restsLength = restsLength + restToPlay.get(restToPlay.size() - 1).get(restToPlay.get(restToPlay.size() - 1).size() - 1).getDuration();
                            restsLength = restsLength + restToPlay.get(a).get(restToPlay.get(a).size() - 1).getDuration();
                            alternative = true;
                        } else {
                            restToPlay.get(a).add(restToPlay.get(a).get(random.nextInt(restToPlay.get(a).size())));
                            restsLength = restsLength + restToPlay.get(a).get(restToPlay.get(a).size() - 1).getDuration();
                            alternative = false;
                        }
                    } else {
                        restToPlay.get(a).add(restToPlay.get(a).get(random.nextInt(restToPlay.get(a).size())));
                        restsLength = restsLength + restToPlay.get(a).get(restToPlay.get(a).size() - 1).getDuration();
                        alternative = false;
                    }
                } else {
                    restToPlay.get(a).add(restToPlay.get(a).get(random.nextInt(restToPlay.get(a).size())));
                    restsLength = restsLength + restToPlay.get(a).get(restToPlay.get(a).size() - 1).getDuration();
                    alternative = false;
                }
            }
        }
        for (int a = 0; a < restObjectsUsedDifferentStyle.size(); a++) {
            restToPlayDifferentStyle.add(new ArrayList<>());
            for (int b = 0; b < restObjectsUsedDifferentStyle.get(a).size(); b++) {
                restToPlayDifferentStyle.get(a).add(new ArrayList<>());
                boolean alternative = false;
                restToPlayDifferentStyle.get(a).get(b).add(restObjectsUsedDifferentStyle.get(a).get(b).get(0));
                int restsLength = 0;
                while (check > restsLength) {
                    if (restObjectsUsedDifferentStyle.get(a).get(b).contains(restToPlayDifferentStyle.get(a).get(b).get(restToPlayDifferentStyle.get(a).get(b).size() - 1))) {
                        int markovChainIndex = restObjectsUsedDifferentStyle.get(a).get(b).indexOf(restToPlayDifferentStyle.get(a).get(b).get(restToPlayDifferentStyle.get(a).get(b).size() - 1));
                        if (markovRestsDifferentStyle.get(a).get(b).get(markovChainIndex).size() > 0) {
                            restToPlayDifferentStyle.get(a).get(b).add(markovRestsDifferentStyle.get(a).get(b).get(markovChainIndex).get(random.nextInt(markovRestsDifferentStyle.get(a).get(b).get(markovChainIndex).size())));
                            restsLength = restsLength + restToPlayDifferentStyle.get(a).get(b).get(restToPlayDifferentStyle.get(a).get(b).size() - 1).getDuration();
                            alternative = false;
                        } else if (restToPlayDifferentStyle.get(a).size() > 1) {
                            if (restObjectsUsedDifferentStyle.get(a).get(b).contains(restToPlayDifferentStyle.get(a).get(b).get(restToPlayDifferentStyle.get(a).get(b).size() - 2))) {
                                int markovChainIndexAlternative = restObjectsUsedDifferentStyle.get(a).get(b).indexOf(restObjectsUsedDifferentStyle.get(a).get(b).get(restObjectsUsedDifferentStyle.get(a).get(b).size() - 2));
                                if (markovRestsDifferentStyle.get(a).get(b).get(markovChainIndexAlternative).size() > 0 && !alternative) {
                                    restToPlayDifferentStyle.get(a).get(b).add(markovRestsDifferentStyle.get(a).get(b).get(markovChainIndexAlternative).get(random.nextInt(markovRestsDifferentStyle.get(a).get(b).get(markovChainIndexAlternative).size())));
                                    restsLength = restsLength + restToPlayDifferentStyle.get(a).get(b).get(restToPlayDifferentStyle.get(a).get(b).size() - 1).getDuration();
                                    alternative = true;
                                } else {
                                    restToPlayDifferentStyle.get(a).get(b).add(restToPlayDifferentStyle.get(a).get(b).get(random.nextInt(restToPlayDifferentStyle.get(a).get(b).size())));
                                    restsLength = restsLength + restToPlayDifferentStyle.get(a).get(b).get(restToPlayDifferentStyle.get(a).get(b).size() - 1).getDuration();
                                    alternative = false;
                                }
                            }
                        } else {
                            restToPlayDifferentStyle.get(a).get(b).add(restToPlayDifferentStyle.get(a).get(b).get(random.nextInt(restToPlayDifferentStyle.get(a).get(b).size())));
                            restsLength = restsLength + restToPlayDifferentStyle.get(a).get(b).get(restToPlayDifferentStyle.get(a).get(b).size() - 1).getDuration();
                            alternative = false;
                        }
                    } else if (restToPlayDifferentStyle.get(a).get(b).size() > 1) {
                        if (restObjectsUsedDifferentStyle.get(a).get(b).contains(restToPlayDifferentStyle.get(a).get(b).get(restToPlayDifferentStyle.get(a).get(b).size() - 2))) {
                            int markovChainIndexAlternative = restObjectsUsedDifferentStyle.get(a).get(b).indexOf(restObjectsUsedDifferentStyle.get(a).get(b).get(restObjectsUsedDifferentStyle.get(a).get(b).size() - 2));
                            if (markovRestsDifferentStyle.get(a).get(b).get(markovChainIndexAlternative).size() > 0 && !alternative) {
                                restToPlayDifferentStyle.get(a).get(b).add(markovRestsDifferentStyle.get(a).get(b).get(markovChainIndexAlternative).get(random.nextInt(markovRestsDifferentStyle.get(a).get(b).get(markovChainIndexAlternative).size())));
                                restsLength = restsLength + restToPlayDifferentStyle.get(a).get(b).get(restToPlayDifferentStyle.get(a).get(b).size() - 1).getDuration();
                                alternative = true;
                            }
                        } else {
                            restToPlayDifferentStyle.get(a).get(b).add(restToPlayDifferentStyle.get(a).get(b).get(random.nextInt(restToPlayDifferentStyle.get(a).get(b).size())));
                            restsLength = restsLength + restToPlayDifferentStyle.get(a).get(b).get(restToPlayDifferentStyle.get(a).get(b).size() - 1).getDuration();
                            alternative = false;
                        }
                    } else {
                        restToPlayDifferentStyle.get(a).get(b).add(restToPlayDifferentStyle.get(a).get(b).get(random.nextInt(restToPlayDifferentStyle.get(a).get(b).size())));
                        restsLength = restsLength + restToPlayDifferentStyle.get(a).get(b).get(restToPlayDifferentStyle.get(a).size() - 1).getDuration();
                        alternative = false;
                    }
                }
            }
        }
        try {
            int ticksPerBeat = (int) (ticksPerSecond * everyQuarterNote.get(random.nextInt(everyQuarterNote.size())));
            Sequence s = new Sequence(javax.sound.midi.Sequence.PPQ, 24);                                 //Create a new MIDI sequence with 24 ticks per beat
            if (notesToPlayDifferentStyle.size() == 0) {                                                            //This checks whether or not there are different styles in the pieces used
                for (int i = 0; i < notesToPlay.size(); i++) {
                    int ticks = 0;
                    float restDuration;
                    Track t = s.createTrack();
                    byte[] b = {(byte) 0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte) 0xF7};                                  //Turn on General MIDI sound set
                    SysexMessage sm = new SysexMessage();
                    sm.setMessage(b, 6);
                    MidiEvent me = new MidiEvent(sm, (long) 0);
                    t.add(me);
                    MetaMessage mt = new MetaMessage();                                                             //Set tempo
                    byte[] bt = {0x02, (byte) 0x00, 0x00};
                    mt.setMessage(0x51, bt, 3);
                    me = new MidiEvent(mt, (long) everyTempo.get(0));
                    t.add(me);
                    mt = new MetaMessage();                                                                         //Set track name
                    String TrackName = "Track";
                    mt.setMessage(0x03, TrackName.getBytes(), TrackName.length());
                    me = new MidiEvent(mt, (long) 0);
                    t.add(me);
                    ShortMessage mm = new ShortMessage();                                                           //Set omni on
                    mm.setMessage(0xB0, 0x7D, 0x00);
                    me = new MidiEvent(mm, (long) 0);
                    t.add(me);
                    mm = new ShortMessage();                                                                        //Set poly on
                    mm.setMessage(0xB0, 0x7F, 0x00);
                    me = new MidiEvent(mm, (long) 0);
                    t.add(me);
                    mm = new ShortMessage();                                                                        //Set instrument to Piano
                    mm.setMessage(0xC0, 0x00, 0x00);
                    me = new MidiEvent(mm, (long) 0);
                    t.add(me);
                    float noteToRest = (float) restObjectsUsed.get(i).size() / (float) noteObjectsUsed.get(i).size();
                    float noteToRestPercentage = noteToRest * 100;
                    for (int j = 0; j < notesToPlay.get(i).size(); j++) {
                        if (check > ticks) {                                                                        //Note on
                            mm = new ShortMessage();
                            mm.setMessage(0x90, notesToPlay.get(i).get(j).getPitch(), notesToPlay.get(i).get(j).getVolume());
                            me = new MidiEvent(mm, (long) ticks);
                            t.add(me);
                            float noteDuration = notesToPlay.get(i).get(j).getDuration();
                            if (noteDuration < ticksPerBeat && noteDuration != 0) {                                 //This checks whether or not the note is more than a quarter note and then rounds the duration to a specific note length to improve the music quality
                                int quaver = ticksPerBeat / 2;
                                int semiQuaver = ticksPerBeat / 4;
                                int demiSemiQuaver = ticksPerBeat / 8;
                                int dottedQuaver = quaver + (quaver / 2);
                                int dottedSemiQuaver = semiQuaver + (semiQuaver / 2);
                                int dottedDemiSemiQuaver = demiSemiQuaver + (demiSemiQuaver / 2);
                                int[] smallerNotes = new int[]{quaver, semiQuaver, demiSemiQuaver, dottedQuaver, dottedSemiQuaver, dottedDemiSemiQuaver};
                                int[] comparison = new int[]{(int) (quaver - noteDuration), (int) (semiQuaver - noteDuration), (int) (demiSemiQuaver - noteDuration), (int) (dottedQuaver - noteDuration), (int) (dottedSemiQuaver - noteDuration), (int) (dottedDemiSemiQuaver - noteDuration)};
                                if (comparison[0] < 0) {
                                    comparison[0] = Math.round(comparison[0]);
                                }
                                int min = comparison[0];
                                int index = 0;
                                for (int k = 0; k < comparison.length; k++) {
                                    if (comparison[k] < 0) {
                                        comparison[k] = Math.abs(comparison[k]);
                                    }
                                    if (min > comparison[k]) {
                                        min = comparison[k];
                                        index = k;
                                    }
                                }
                                noteDuration = smallerNotes[index];
                                ticks = (int) (ticks + noteDuration);
                            } else if (noteDuration > ticksPerBeat && noteDuration != 0) {
                                int minim = ticksPerBeat * 2;
                                int semiBreve = ticksPerBeat * 4;
                                int dottedCrotchet = ticksPerBeat + (ticksPerBeat / 2);
                                int dottedMinim = minim + (minim / 2);
                                int dottedSemiBreve = semiBreve + (semiBreve / 2);
                                int[] biggerNotes = new int[]{ticksPerBeat, minim, semiBreve, dottedCrotchet, dottedMinim, dottedSemiBreve};
                                int[] comparison = new int[]{(int) (ticksPerBeat - noteDuration), (int) (minim - noteDuration), (int) (semiBreve - noteDuration), (int) (dottedCrotchet - noteDuration), (int) (dottedMinim - noteDuration), (int) (dottedSemiBreve - noteDuration)};
                                if (comparison[0] < 0) {
                                    comparison[0] = Math.abs(comparison[0]);
                                }
                                int min = comparison[0];
                                int index = 0;
                                for (int k = 0; k < comparison.length; k++) {
                                    if (comparison[k] < 0) {
                                        comparison[k] = Math.abs(comparison[k]);
                                    }
                                    if (min > comparison[k]) {
                                        min = comparison[k];
                                        index = k;
                                    }
                                }
                                noteDuration = biggerNotes[index];
                                ticks = (int) (ticks + noteDuration);
                            } else {
                                ticks = (int) (ticks + noteDuration);

                            }
                            mm = new ShortMessage();                                                                //Note off
                            mm.setMessage(0x80, notesToPlay.get(i).get(j).getPitch(), 0);
                            me = new MidiEvent(mm, (long) ticks);
                            t.add(me);
                            if (j < restToPlay.get(i).size()) {
                                restDuration = restToPlay.get(i).get(j).getDuration();
                            } else {
                                restDuration = restToPlay.get(i).get(random.nextInt(restToPlay.get(i).size())).getDuration();
                            }
                            if (random.nextInt(100) < noteToRestPercentage) {
                                if (restDuration < ticksPerBeat && restDuration != 0) {
                                    int quaver = ticksPerBeat / 2;
                                    int semiQuaver = ticksPerBeat / 4;
                                    int demiSemiQuaver = ticksPerBeat / 8;
                                    int dottedQuaver = quaver + (quaver / 2);
                                    int dottedSemiQuaver = semiQuaver + (semiQuaver / 2);
                                    int dottedDemiSemiQuaver = demiSemiQuaver + (demiSemiQuaver / 2);
                                    int[] smallerNotes = new int[]{quaver, semiQuaver, demiSemiQuaver, dottedQuaver, dottedSemiQuaver, dottedDemiSemiQuaver};
                                    int[] comparison = new int[]{(int) (quaver - restDuration), (int) (semiQuaver - restDuration), (int) (demiSemiQuaver - restDuration), (int) (dottedQuaver - restDuration), (int) (dottedSemiQuaver - restDuration), (int) (dottedDemiSemiQuaver - restDuration)};
                                    if (comparison[0] < 0) {
                                        comparison[0] = Math.round(comparison[0]);
                                    }
                                    int min = comparison[0];
                                    int index = 0;
                                    for (int k = 0; k < comparison.length; k++) {
                                        if (comparison[k] < 0) {
                                            comparison[k] = Math.abs(comparison[k]);
                                        }
                                        if (min > comparison[k]) {
                                            min = comparison[k];
                                            index = k;
                                        }
                                    }
                                    restDuration = smallerNotes[index];
                                    ticks = (int) (ticks + restDuration);
                                } else if (restDuration > ticksPerBeat && restDuration != 0) {
                                    int minim = ticksPerBeat * 2;
                                    int semiBreve = ticksPerBeat * 4;
                                    int dottedCrotchet = ticksPerBeat + (ticksPerBeat / 2);
                                    int dottedMinim = minim + (minim / 2);
                                    int dottedSemiBreve = semiBreve + (semiBreve / 2);
                                    int[] biggerNotes = new int[]{ticksPerBeat, minim, semiBreve, dottedCrotchet, dottedMinim, dottedSemiBreve};
                                    int[] comparison = new int[]{(int) (ticksPerBeat - restDuration), (int) (minim - restDuration), (int) (semiBreve - restDuration), (int) (dottedCrotchet - restDuration), (int) (dottedMinim - restDuration), (int) (dottedSemiBreve - restDuration)};
                                    if (comparison[0] < 0) {
                                        comparison[0] = Math.round(comparison[0]);
                                    }
                                    int min = comparison[0];
                                    int index = 0;
                                    for (int k = 0; k < comparison.length; k++) {
                                        if (comparison[k] < 0) {
                                            comparison[k] = Math.abs(comparison[k]);
                                        }
                                        if (min > comparison[k]) {
                                            min = comparison[k];
                                            index = k;
                                        }
                                    }
                                    restDuration = biggerNotes[index];
                                    ticks = (int) (ticks + restDuration);
                                } else {
                                    ticks = (int) (ticks + restDuration);
                                }
                            }
                        }
                    }
                    mt = new MetaMessage();                                                                         //Set end of track
                    byte[] bet = {}; // empty array
                    mt.setMessage(0x2F, bet, 0);
                    me = new MidiEvent(mt, (long) ticks + 10);
                    t.add(me);
                }
                File f = new File(folderToOpen + "\\midifile.mid");                                       //Write the MIDI sequence to a MIDI file
                MidiSystem.write(s, 1, f);
                System.out.println("MidiFile created ");
                programFinished = true;
            } else {                                                                                                //This is for when the code has different styles
                int ticks = 0;
                int pieceAmount = notesToPlayDifferentStyle.size() + 1;
                for (int i = 0; i < notesToPlay.size(); i++) {
                    float restDuration;
                    Track t = s.createTrack();
                    byte[] b = {(byte) 0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte) 0xF7};
                    SysexMessage sm = new SysexMessage();
                    sm.setMessage(b, 6);
                    MidiEvent me = new MidiEvent(sm, (long) 0);
                    t.add(me);
                    MetaMessage mt = new MetaMessage();
                    byte[] bt = {0x02, (byte) 0x00, 0x00};
                    mt.setMessage(0x51, bt, 3);
                    me = new MidiEvent(mt, (long) everyTempo.get(0));
                    t.add(me);
                    mt = new MetaMessage();
                    String TrackName = "midifile track";
                    mt.setMessage(0x03, TrackName.getBytes(), TrackName.length());
                    me = new MidiEvent(mt, (long) 0);
                    t.add(me);
                    ShortMessage mm = new ShortMessage();
                    mm.setMessage(0xB0, 0x7D, 0x00);
                    me = new MidiEvent(mm, (long) 0);
                    t.add(me);
                    mm = new ShortMessage();
                    mm.setMessage(0xB0, 0x7F, 0x00);
                    me = new MidiEvent(mm, (long) 0);
                    t.add(me);
                    mm = new ShortMessage();
                    mm.setMessage(0xC0, 0x00, 0x00);
                    me = new MidiEvent(mm, (long) 0);
                    t.add(me);
                    float noteToRest = (float) restObjectsUsed.get(i).size() / (float) noteObjectsUsed.get(i).size();
                    float noteToRestPercentage = noteToRest * 100;
                    for (int j = 0; j < notesToPlay.get(i).size(); j++) {
                        if (check / pieceAmount > ticks) {                                                          //This checks that the music produced is in incremental durations so that the piece will have a normal length but with different styles used in different parts of the piece
                            mm = new ShortMessage();
                            mm.setMessage(0x90, notesToPlay.get(i).get(j).getPitch(), notesToPlay.get(i).get(j).getVolume());
                            me = new MidiEvent(mm, (long) ticks);
                            t.add(me);
                            float noteDuration = notesToPlay.get(i).get(j).getDuration();
                            if (noteDuration < ticksPerBeat && noteDuration != 0) {
                                int quaver = ticksPerBeat / 2;
                                int semiQuaver = ticksPerBeat / 4;
                                int demiSemiQuaver = ticksPerBeat / 8;
                                int dottedQuaver = quaver + (quaver / 2);
                                int dottedSemiQuaver = semiQuaver + (semiQuaver / 2);
                                int dottedDemiSemiQuaver = demiSemiQuaver + (demiSemiQuaver / 2);
                                int[] smallerNotes = new int[]{quaver, semiQuaver, demiSemiQuaver, dottedQuaver, dottedSemiQuaver, dottedDemiSemiQuaver};
                                int[] comparison = new int[]{(int) (quaver - noteDuration), (int) (semiQuaver - noteDuration), (int) (demiSemiQuaver - noteDuration), (int) (dottedQuaver - noteDuration), (int) (dottedSemiQuaver - noteDuration), (int) (dottedDemiSemiQuaver - noteDuration)};
                                if (comparison[0] < 0) {
                                    comparison[0] = Math.round(comparison[0]);
                                }
                                int min = comparison[0];
                                int index = 0;
                                for (int k = 0; k < comparison.length; k++) {
                                    if (comparison[k] < 0) {
                                        comparison[k] = Math.abs(comparison[k]);
                                    }
                                    if (min > comparison[k]) {
                                        min = comparison[k];
                                        index = k;
                                    }
                                }
                                noteDuration = smallerNotes[index];
                                ticks = (int) (ticks + noteDuration);
                            } else if (noteDuration > ticksPerBeat && noteDuration != 0) {
                                int minim = ticksPerBeat * 2;
                                int semiBreve = ticksPerBeat * 4;
                                int dottedCrotchet = ticksPerBeat + (ticksPerBeat / 2);
                                int dottedMinim = minim + (minim / 2);
                                int dottedSemiBreve = semiBreve + (semiBreve / 2);
                                int[] biggerNotes = new int[]{ticksPerBeat, minim, semiBreve, dottedCrotchet, dottedMinim, dottedSemiBreve};
                                int[] comparison = new int[]{(int) (ticksPerBeat - noteDuration), (int) (minim - noteDuration), (int) (semiBreve - noteDuration), (int) (dottedCrotchet - noteDuration), (int) (dottedMinim - noteDuration), (int) (dottedSemiBreve - noteDuration)};
                                if (comparison[0] < 0) {
                                    comparison[0] = Math.abs(comparison[0]);
                                }
                                int min = comparison[0];
                                int index = 0;
                                for (int k = 0; k < comparison.length; k++) {
                                    if (comparison[k] < 0) {
                                        comparison[k] = Math.abs(comparison[k]);
                                    }
                                    if (min > comparison[k]) {
                                        min = comparison[k];
                                        index = k;
                                    }
                                }
                                noteDuration = biggerNotes[index];
                                ticks = (int) (ticks + noteDuration);
                            } else {
                                ticks = (int) (ticks + noteDuration);
                            }
                            mm = new ShortMessage();
                            mm.setMessage(0x80, notesToPlay.get(i).get(j).getPitch(), 0);
                            me = new MidiEvent(mm, (long) ticks);
                            t.add(me);
                            if (j < restToPlay.get(i).size()) {
                                restDuration = restToPlay.get(i).get(j).getDuration();
                            } else {
                                restDuration = restToPlay.get(i).get(random.nextInt(restToPlay.get(i).size())).getDuration();
                            }
                            if (random.nextInt(100) < noteToRestPercentage) {
                                if (restDuration < ticksPerBeat && restDuration != 0) {
                                    int quaver = ticksPerBeat / 2;
                                    int semiQuaver = ticksPerBeat / 4;
                                    int demiSemiQuaver = ticksPerBeat / 8;
                                    int dottedQuaver = quaver + (quaver / 2);
                                    int dottedSemiQuaver = semiQuaver + (semiQuaver / 2);
                                    int dottedDemiSemiQuaver = demiSemiQuaver + (demiSemiQuaver / 2);
                                    int[] smallerNotes = new int[]{quaver, semiQuaver, demiSemiQuaver, dottedQuaver, dottedSemiQuaver, dottedDemiSemiQuaver};
                                    int[] comparison = new int[]{(int) (quaver - restDuration), (int) (semiQuaver - restDuration), (int) (demiSemiQuaver - restDuration), (int) (dottedQuaver - restDuration), (int) (dottedSemiQuaver - restDuration), (int) (dottedDemiSemiQuaver - restDuration)};
                                    if (comparison[0] < 0) {
                                        comparison[0] = Math.round(comparison[0]);
                                    }
                                    int min = comparison[0];
                                    int index = 0;
                                    for (int k = 0; k < comparison.length; k++) {
                                        if (comparison[k] < 0) {
                                            comparison[k] = Math.abs(comparison[k]);
                                        }
                                        if (min > comparison[k]) {
                                            min = comparison[k];
                                            index = k;
                                        }
                                    }
                                    restDuration = smallerNotes[index];
                                    ticks = (int) (ticks + restDuration);
                                } else if (restDuration > ticksPerBeat && restDuration != 0) {
                                    int minim = ticksPerBeat * 2;
                                    int semiBreve = ticksPerBeat * 4;
                                    int dottedCrotchet = ticksPerBeat + (ticksPerBeat / 2);
                                    int dottedMinim = minim + (minim / 2);
                                    int dottedSemiBreve = semiBreve + (semiBreve / 2);
                                    int[] biggerNotes = new int[]{ticksPerBeat, minim, semiBreve, dottedCrotchet, dottedMinim, dottedSemiBreve};
                                    int[] comparison = new int[]{(int) (ticksPerBeat - restDuration), (int) (minim - restDuration), (int) (semiBreve - restDuration), (int) (dottedCrotchet - restDuration), (int) (dottedMinim - restDuration), (int) (dottedSemiBreve - restDuration)};
                                    if (comparison[0] < 0) {
                                        comparison[0] = Math.round(comparison[0]);
                                    }
                                    int min = comparison[0];
                                    int index = 0;
                                    for (int k = 0; k < comparison.length; k++) {
                                        if (comparison[k] < 0) {
                                            comparison[k] = Math.abs(comparison[k]);
                                        }
                                        if (min > comparison[k]) {
                                            min = comparison[k];
                                            index = k;
                                        }
                                    }
                                    restDuration = biggerNotes[index];
                                    ticks = (int) (ticks + restDuration);
                                } else {
                                    ticks = (int) (ticks + restDuration);
                                }
                            }
                        }
                    }
                    for (int j = 0; j < notesToPlayDifferentStyle.size(); j++) {                                    //This adds the different style notes afterwards
                        if (!(notesToPlayDifferentStyle.get(j).size() < i)) {
                            for (int l = 0; l < notesToPlayDifferentStyle.get(j).get(i).size(); l++) {
                                if ((check / pieceAmount) * (j + 2) > ticks) {

                                    mm = new ShortMessage();
                                    mm.setMessage(0x90, notesToPlayDifferentStyle.get(j).get(i).get(l).getPitch(), notesToPlayDifferentStyle.get(j).get(i).get(l).getVolume());
                                    me = new MidiEvent(mm, (long) ticks);
                                    t.add(me);
                                    float noteDuration = notesToPlayDifferentStyle.get(j).get(i).get(l).getDuration();
                                    if (noteDuration < ticksPerBeat && noteDuration != 0) {
                                        int quaver = ticksPerBeat / 2;
                                        int semiQuaver = ticksPerBeat / 4;
                                        int demiSemiQuaver = ticksPerBeat / 8;
                                        int dottedQuaver = quaver + (quaver / 2);
                                        int dottedSemiQuaver = semiQuaver + (semiQuaver / 2);
                                        int dottedDemiSemiQuaver = demiSemiQuaver + (demiSemiQuaver / 2);
                                        int[] smallerNotes = new int[]{quaver, semiQuaver, demiSemiQuaver, dottedQuaver, dottedSemiQuaver, dottedDemiSemiQuaver};
                                        int[] comparison = new int[]{(int) (quaver - noteDuration), (int) (semiQuaver - noteDuration), (int) (demiSemiQuaver - noteDuration), (int) (dottedQuaver - noteDuration), (int) (dottedSemiQuaver - noteDuration), (int) (dottedDemiSemiQuaver - noteDuration)};
                                        if (comparison[0] < 0) {
                                            comparison[0] = Math.round(comparison[0]);
                                        }
                                        int min = comparison[0];
                                        int index = 0;
                                        for (int k = 0; k < comparison.length; k++) {
                                            if (comparison[k] < 0) {
                                                comparison[k] = Math.abs(comparison[k]);
                                            }
                                            if (min > comparison[k]) {
                                                min = comparison[k];
                                                index = k;
                                            }
                                        }
                                        noteDuration = smallerNotes[index];
                                        ticks = (int) (ticks + noteDuration);
                                    } else if (noteDuration > ticksPerBeat && noteDuration != 0) {
                                        int minim = ticksPerBeat * 2;
                                        int semiBreve = ticksPerBeat * 4;
                                        int dottedCrotchet = ticksPerBeat + (ticksPerBeat / 2);
                                        int dottedMinim = minim + (minim / 2);
                                        int dottedSemiBreve = semiBreve + (semiBreve / 2);
                                        int[] biggerNotes = new int[]{ticksPerBeat, minim, semiBreve, dottedCrotchet, dottedMinim, dottedSemiBreve};
                                        int[] comparison = new int[]{(int) (ticksPerBeat - noteDuration), (int) (minim - noteDuration), (int) (semiBreve - noteDuration), (int) (dottedCrotchet - noteDuration), (int) (dottedMinim - noteDuration), (int) (dottedSemiBreve - noteDuration)};
                                        if (comparison[0] < 0) {
                                            comparison[0] = Math.abs(comparison[0]);
                                        }
                                        int min = comparison[0];
                                        int index = 0;
                                        for (int k = 0; k < comparison.length; k++) {
                                            if (comparison[k] < 0) {
                                                comparison[k] = Math.abs(comparison[k]);
                                            }
                                            if (min > comparison[k]) {
                                                min = comparison[k];
                                                index = k;
                                            }
                                        }
                                        noteDuration = biggerNotes[index];
                                        ticks = (int) (ticks + noteDuration);
                                    } else {
                                        ticks = (int) (ticks + noteDuration);

                                    }
                                    mm = new ShortMessage();
                                    mm.setMessage(0x80, notesToPlayDifferentStyle.get(j).get(i).get(l).getPitch(), 0);
                                    me = new MidiEvent(mm, (long) ticks);
                                    t.add(me);
                                    if (l < restToPlayDifferentStyle.get(j).get(i).size()) {
                                        restDuration = restToPlayDifferentStyle.get(j).get(i).get(l).getDuration();
                                    } else {
                                        restDuration = restToPlayDifferentStyle.get(j).get(i).get(random.nextInt(restToPlayDifferentStyle.get(j).get(i).size())).getDuration();
                                    }
                                    if (random.nextInt(100) < noteToRestPercentage) {
                                        if (restDuration < ticksPerBeat && restDuration != 0) {
                                            int quaver = ticksPerBeat / 2;
                                            int semiQuaver = ticksPerBeat / 4;
                                            int demiSemiQuaver = ticksPerBeat / 8;
                                            int dottedQuaver = quaver + (quaver / 2);
                                            int dottedSemiQuaver = semiQuaver + (semiQuaver / 2);
                                            int dottedDemiSemiQuaver = demiSemiQuaver + (demiSemiQuaver / 2);
                                            int[] smallerNotes = new int[]{quaver, semiQuaver, demiSemiQuaver, dottedQuaver, dottedSemiQuaver, dottedDemiSemiQuaver};
                                            int[] comparison = new int[]{(int) (quaver - restDuration), (int) (semiQuaver - restDuration), (int) (demiSemiQuaver - restDuration), (int) (dottedQuaver - restDuration), (int) (dottedSemiQuaver - restDuration), (int) (dottedDemiSemiQuaver - restDuration)};
                                            if (comparison[0] < 0) {
                                                comparison[0] = Math.round(comparison[0]);
                                            }
                                            int min = comparison[0];
                                            int index = 0;
                                            for (int k = 0; k < comparison.length; k++) {
                                                if (comparison[k] < 0) {
                                                    comparison[k] = Math.abs(comparison[k]);
                                                }
                                                if (min > comparison[k]) {
                                                    min = comparison[k];
                                                    index = k;
                                                }
                                            }
                                            restDuration = smallerNotes[index];
                                            ticks = (int) (ticks + restDuration);
                                        } else if (restDuration > ticksPerBeat && restDuration != 0) {
                                            int minim = ticksPerBeat * 2;
                                            int semiBreve = ticksPerBeat * 4;
                                            int dottedCrotchet = ticksPerBeat + (ticksPerBeat / 2);
                                            int dottedMinim = minim + (minim / 2);
                                            int dottedSemiBreve = semiBreve + (semiBreve / 2);
                                            int[] biggerNotes = new int[]{ticksPerBeat, minim, semiBreve, dottedCrotchet, dottedMinim, dottedSemiBreve};
                                            int[] comparison = new int[]{(int) (ticksPerBeat - restDuration), (int) (minim - restDuration), (int) (semiBreve - restDuration), (int) (dottedCrotchet - restDuration), (int) (dottedMinim - restDuration), (int) (dottedSemiBreve - restDuration)};
                                            if (comparison[0] < 0) {
                                                comparison[0] = Math.round(comparison[0]);
                                            }
                                            int min = comparison[0];
                                            int index = 0;
                                            for (int k = 0; k < comparison.length; k++) {
                                                if (comparison[k] < 0) {
                                                    comparison[k] = Math.abs(comparison[k]);
                                                }
                                                if (min > comparison[k]) {
                                                    min = comparison[k];
                                                    index = k;
                                                }
                                            }
                                            restDuration = biggerNotes[index];
                                            ticks = (int) (ticks + restDuration);
                                        } else {
                                            ticks = (int) (ticks + restDuration);

                                        }
                                    }
                                }
                            }
                        }
                    }
                    mt = new MetaMessage();
                    byte[] bet = {}; // empty array
                    mt.setMessage(0x2F, bet, 0);
                    me = new MidiEvent(mt, (long) ticks + 10);
                    t.add(me);
                }
                File f = new File(folderToOpen + "\\midifile.mid");
                MidiSystem.write(s, 1, f);
                System.out.println("MidiFile created ");
                programFinished = true;
            }
        } catch (Exception e) {
            System.out.println("Exception caught " + e.toString());
            e.printStackTrace();
        }
        System.out.println("midifile end ");
    }

    /**
     * This method is designed to refresh the code so that it can be used multiple times
     */
    void refreshCode() {                                                                                            //All of these are global variables that need to be emptied so that the code can work correctly if used another time
        everyTempo.clear();
        everyResolution.clear();
        everyQuarterNote.clear();
        noteObjectsUsedDifferentStyle.clear();
        restObjectsUsedDifferentStyle.clear();
        noteObjectsUsed.clear();
        restObjectsUsed.clear();
        markovNotes.clear();
        markovNotesDifferentStyle.clear();
        markovRest.clear();
        markovRestsDifferentStyle.clear();
        result.clear();
        setFolderToOpen(new File(" "));
        programFinished = false;
        everyPieceLength.clear();
        valueToChangeNoteBy = 0;
        valueToChangeVolumeBy = 1;
        valueToChangeNoteDurationBy = 1;
        valueToChangeRestDurationBy = 1;
    }

    /**
     * This method converts helps the user to determine how mathematically successful the program is
     */
    private void compareMIDIFiles() {            //Compares values of MIDI file and the one created
        Random random = new Random();
        File file1 = new File(folderToOpen.toString() + "\\midifile.mid");                                //This is the first file to be used
        System.out.println(file1);
        File file2 = new File(result.get(random.nextInt(result.size())));                                           //This randomly selects a MIDI File from the ones used
        System.out.println(file2);
        ArrayList<Integer> keysUsed1 = new ArrayList<>();                                                           //This Arraylist stores all the keys(notes) used
        ArrayList<Integer> volumeUsed1 = new ArrayList<>();
        ArrayList<Integer> durationPlaying1 = new ArrayList<>();
        ArrayList<Integer> durationResting1 = new ArrayList<>();
        ArrayList<Integer> keysUsed2 = new ArrayList<>();                                                           //This Arraylist stores all the keys(notes) used
        ArrayList<Integer> volumeUsed2 = new ArrayList<>();
        ArrayList<Integer> durationPlaying2 = new ArrayList<>();
        ArrayList<Integer> durationResting2 = new ArrayList<>();
        float keysAmount = 0;
        float volumeAmount = 0;
        float durationAmount = 0;
        float restAmount = 0;
        Sequencer sequencer;
        final int NOTE_ON = 0x90;
        final int NOTE_OFF = 0x80;
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            Sequence fileSequence = MidiSystem.getSequence(file1);
            sequencer.setSequence(fileSequence);                                                                    //This gets the sequencer of the midi file
            for (Track track : fileSequence.getTracks()) {                                                          //This adds the values to arrays
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);
                    MidiMessage message = event.getMessage();
                    if (message instanceof ShortMessage) {
                        ShortMessage sm = (ShortMessage) message;
                        if (sm.getCommand() == NOTE_ON) {
                            int key = sm.getData1();
                            int volume = sm.getData2();
                            if (volume != 0) {
                                durationPlaying1.add((int) event.getTick());
                                keysUsed1.add(key);
                                volumeUsed1.add(volume);
                            }
                        }
                        if (sm.getCommand() == NOTE_OFF || sm.getData2() == 0) {
                            durationResting1.add((int) event.getTick());
                        }
                    }
                }
            }
            Sequence fileSequence2 = MidiSystem.getSequence(file2);
            sequencer.setSequence(fileSequence2);                                                                   //This gets the sequencer of the midi file
            for (Track track : fileSequence2.getTracks()) {                                                         //This adds the values to arrays
                for (int i = 0; i < track.size(); i++) {
                    MidiEvent event = track.get(i);
                    MidiMessage message = event.getMessage();
                    if (message instanceof ShortMessage) {
                        ShortMessage sm = (ShortMessage) message;
                        if (sm.getCommand() == NOTE_ON) {
                            int key = sm.getData1();
                            int volume = sm.getData2();
                            if (volume != 0) {
                                durationPlaying2.add((int) event.getTick());
                                keysUsed2.add(key);
                                volumeUsed2.add(volume);
                            }
                        }
                        if (sm.getCommand() == NOTE_OFF || sm.getData2() == 0) {
                            durationResting2.add((int) event.getTick());
                        }
                    }
                }
            }
            ArrayList<Integer> keys1 = new ArrayList<>();
            ArrayList<Integer> keys2 = new ArrayList<>();
            ArrayList<Integer> duration1 = new ArrayList<>();
            ArrayList<Integer> duration2 = new ArrayList<>();
            ArrayList<Integer> rest1 = new ArrayList<>();
            ArrayList<Integer> rest2 = new ArrayList<>();
            ArrayList<Integer> volume1 = new ArrayList<>();
            ArrayList<Integer> volume2 = new ArrayList<>();
            Collections.sort(keysUsed1);
            Collections.sort(keysUsed2);
            Collections.sort(durationPlaying1);
            Collections.sort(durationPlaying2);
            Collections.sort(durationResting1);
            Collections.sort(durationResting2);
            Collections.sort(volumeUsed1);
            Collections.sort(volumeUsed2);
            while (!keysUsed1.isEmpty()) {
                keys1.add(keysUsed1.get(0));
                keysUsed1.removeIf(keysUsed1.get(0)::equals);
            }
            while (!keysUsed2.isEmpty()) {
                keys2.add(keysUsed2.get(0));
                keysUsed2.removeIf(keysUsed2.get(0)::equals);
            }
            for (Integer aKeys1 : keys1) {
                for (Integer aKeys2 : keys2) {
                    if (aKeys1.equals(aKeys2)) {
                        keysAmount++;
                    }
                }
            }
            if (keysAmount != 0) {
                float keyPercentage = (keysAmount / keys1.size()) * 100;
                System.out.println("Pitch accuracy  " + keyPercentage + "%/100%");
            } else {
                System.out.println("Pitch accuracy  " + keysAmount + "%/100%");
            }
            while (!durationPlaying1.isEmpty()) {
                duration1.add(durationPlaying1.get(0));
                durationPlaying1.removeIf(durationPlaying1.get(0)::equals);
            }
            while (!durationPlaying2.isEmpty()) {
                duration2.add(durationPlaying2.get(0));
                durationPlaying2.removeIf(durationPlaying2.get(0)::equals);
            }
            for (Integer aDuration1 : duration1) {
                for (Integer aDuration2 : duration2) {
                    if (aDuration1.equals(aDuration2)) {
                        durationAmount++;
                    }
                }
            }
            if (durationAmount != 0) {
                float durationPercentage = (durationAmount / duration1.size()) * 100;
                System.out.println("Note Duration Accuracy" + durationPercentage + "%/100%");
            } else {
                System.out.println("Note Duration Accuracy" + durationAmount + "%/100%");
            }
            while (!durationResting1.isEmpty()) {
                rest1.add(durationResting1.get(0));
                durationResting1.removeIf(durationResting1.get(0)::equals);
            }
            while (!durationResting2.isEmpty()) {
                rest2.add(durationResting2.get(0));
                durationResting2.removeIf(durationResting2.get(0)::equals);
            }
            for (Integer aRest1 : rest1) {
                for (Integer aRest2 : rest2) {
                    if (aRest1.equals(aRest2)) {
                        restAmount++;
                    }
                }
            }
            if (restAmount != 0) {
                float restPercentage = (restAmount / rest1.size()) * 100;
                System.out.println("Rest Duration" + restPercentage + "%/100%");
            } else {
                System.out.println("Rest Duration" + restAmount + "%/100");
            }
            while (!volumeUsed1.isEmpty()) {
                volume1.add(volumeUsed1.get(0));
                volumeUsed1.removeIf(volumeUsed1.get(0)::equals);
            }
            while (!volumeUsed2.isEmpty()) {
                volume2.add(volumeUsed2.get(0));
                volumeUsed2.removeIf(volumeUsed2.get(0)::equals);
            }
            for (Integer aVolume1 : volume1) {
                for (Integer aVolume2 : volume2) {
                    if (aVolume1.equals(aVolume2)) {
                        volumeAmount++;
                    }
                }
            }
            if (volumeAmount != 0) {
                float volumePercentage = (volumeAmount / volume1.size()) * 100;
                System.out.println("Note Volume" + volumePercentage + "%/100%");
            } else {
                System.out.println("Note Volume" + volumeAmount + "%/100");
            }
        } catch (MidiUnavailableException | InvalidMidiDataException | IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param stage This method creates a GUI that the user can use to run the program
     * @throws Exception An exception is thrown if the JavaFX applicatio does not work
     */
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("JavaFX App");
        AtomicBoolean valuesChanged = new AtomicBoolean(false);                                     //This is used to check that the program has worked so that the program can react accordingly

        DirectoryChooser directoryChooser = new DirectoryChooser();

        javafx.scene.text.Text instructions = new javafx.scene.text.Text();
        instructions.setText("  To Run Program\n  1.Select Directory \n  2.Change the values if you want \n  3.Select Start Program Button");
        javafx.scene.control.Button folderButton = new javafx.scene.control.Button("Select Directory");
        folderButton.setId(".folder");
        folderButton.setOnAction(e -> {                                                                     //This is used to select the directory
            File selectedDirectory = directoryChooser.showDialog(stage);
            System.out.println(selectedDirectory.getAbsolutePath());
            folderToOpen = new File(selectedDirectory.getAbsolutePath());
            if (!valuesChanged.get()) {
                instructions.setText("  To Run Program\n  1.Directory Selected " + folderToOpen + "\n  2.Change the values if you want \n  3.Select Start Program Button");
            } else {
                instructions.setText("  To Run Program\n  1.Directory Selected " + folderToOpen + "\n  2.Values Changed \n  3.Select Start Program Button");

            }
        });


        Slider noteValue = new Slider(-12, 12, 0);
        noteValue.setShowTickMarks(true);
        noteValue.setShowTickLabels(true);
        noteValue.setMajorTickUnit(3);
        noteValue.setBlockIncrement(1);
        noteValue.setSnapToTicks(true);
        javafx.scene.control.Label noteLabel = new javafx.scene.control.Label(" Note Pitch ");

        noteValue.valueProperty().addListener(                                                      //This is a slider which is inserted to change a value
                (observable, oldValue, newValue) -> {
                    noteValue.setValue(Math.round(newValue.doubleValue()));
                    values[0] = Math.round((Double) newValue);
                    noteLabel.setText(" Note Pitch  " + String.valueOf((int) values[0]));
                }
        );

        Slider volumeValue = new Slider(0.1, 2, 1);
        volumeValue.setShowTickMarks(true);
        volumeValue.setShowTickLabels(true);
        volumeValue.setMajorTickUnit(.5f);
        volumeValue.setBlockIncrement(.25f);
        javafx.scene.control.Label volumeLabel = new javafx.scene.control.Label(" Volume ");
        volumeValue.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    String decimalFloat = decimalFormat.format(newValue.doubleValue());
                    volumeValue.setValue(Float.parseFloat(decimalFloat));
                    values[1] = Float.parseFloat(decimalFloat);
                    volumeLabel.setText(" Volume " + String.valueOf(values[1]));

                }
        );

        Slider noteDuration = new Slider(0.1, 2, 1);
        noteDuration.setShowTickLabels(true);
        noteDuration.setMajorTickUnit(.5f);
        noteDuration.setBlockIncrement(.25f);
        javafx.scene.control.Label noteDurationLabel = new javafx.scene.control.Label(" Note Duration ");

        noteDuration.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    String decimalFloat = decimalFormat.format(newValue.doubleValue());
                    noteDuration.setValue(Float.parseFloat(decimalFloat));
                    values[2] = Float.parseFloat(decimalFloat);
                    noteDurationLabel.setText(" Note Duration " + String.valueOf(values[2]));

                }
        );

        Slider restDuration = new Slider(0.1, 2, 1);
        restDuration.setShowTickLabels(true);
        restDuration.setMajorTickUnit(.5f);
        restDuration.setBlockIncrement(.25f);
        javafx.scene.control.Label restDurationLabel = new javafx.scene.control.Label(" Rest Duration");

        restDuration.valueProperty().addListener(
                (observable, oldValue, newValue) -> {
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    String decimalFloat = decimalFormat.format(newValue.doubleValue());
                    restDuration.setValue(Float.parseFloat(decimalFloat));
                    values[3] = Float.parseFloat(decimalFloat);
                    restDurationLabel.setText(" Rest Duration " + String.valueOf(values[3]));
                }
        );


        javafx.scene.control.Button sliderButton = new Button("Change Values");

        sliderButton.setOnAction(e -> {                                                                     //This reads the slider values and makes them substantial here
            valuesChanged.set(true);
            valueToChangeNoteBy = (int) values[0];
            valueToChangeVolumeBy = values[1];
            valueToChangeNoteDurationBy = values[2];
            valueToChangeRestDurationBy = values[3];
            if (folderToOpen != null) {
                instructions.setText("  To Run Program\n  1.Directory Selected " + folderToOpen + "\n  2.Values changed \n  3.Select Start Program Button");
            } else {
                instructions.setText("  To Run Program\n  1.Select Directory \n  2.Values changed \n  3.Select Start Program Button");
            }

        });


        javafx.scene.text.Text progress = new javafx.scene.text.Text("");                           //This is used to update the user as to the progress of the appication

        javafx.scene.control.Button refresh = new Button("Refresh ");
        javafx.scene.control.Button playMidi = new Button("Play MIDI");
        javafx.scene.control.Button stopMidi = new Button("Stop playing MIDI");
        javafx.scene.control.Button compareMidifiles = new Button("Compare MIDI files");
        javafx.scene.control.Button startProgram = new Button("Start Program ");
        ScrollBar scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);


        playMidi.setVisible(false);
        stopMidi.setVisible(false);
        compareMidifiles.setVisible(false);
        refresh.setVisible(false);

        refresh.setOnAction(e -> {                                                                              //This button refreshes the GUI
            refreshCode();
            progress.setText(" ");
            playMidi.setVisible(false);
            compareMidifiles.setVisible(false);
            startProgram.setVisible(true);
            refresh.setVisible(false);
            folderButton.setVisible(true);
            noteValue.setVisible(true);
            noteLabel.setVisible(true);
            noteDuration.setVisible(true);
            noteDurationLabel.setVisible(true);
            volumeValue.setVisible(true);
            volumeLabel.setVisible(true);
            restDuration.setVisible(true);
            restDurationLabel.setVisible(true);
            sliderButton.setVisible(true);
            valuesChanged.set(false);
            instructions.setText("  To Run Program\n  1.Select Directory \n  2.Change the values if you want \n  3.Select Start Program Button");
        });


        if (folderToOpen == null) {
            startProgram.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText("Error");
                alert.setContentText("You need to add a directory file");
                alert.showAndWait();
            });
        }

        startProgram.setOnAction(e -> {                                                                     //This runs the entire code by calling the methods
            progress.setText("Program running");
            System.out.println("Program started");
            initialise();
            readDataFromFile();
            noteObject();
            markovChain();
            makeMIDIFile();
            if (new File(folderToOpen + "\\MidiData.txt").delete()) {
                System.out.println("Text File deleted successfully");
            } else {
                System.out.println("Failed to delete the text file");
            }
            if (programFinished) {
                progress.setText("Done");
                playMidi.setVisible(true);
                compareMidifiles.setVisible(true);
                startProgram.setVisible(false);
                refresh.setVisible(true);
                noteValue.setVisible(false);
                noteLabel.setVisible(false);
                noteDuration.setVisible(false);
                noteDurationLabel.setVisible(false);
                volumeValue.setVisible(false);
                volumeLabel.setVisible(false);
                restDuration.setVisible(false);
                restDurationLabel.setVisible(false);
                sliderButton.setVisible(false);
                folderButton.setVisible(false);
                instructions.setText("  To Continue Program\n  1.Refresh the program to use again \n  2.Compare the files similarity \n  3.Play the newly made file \n  4.Stop the newly made file");
            } else {
                progress.setText("Error Occured");
                instructions.setText("Please press the Refresh button");
                refresh.setVisible(true);
            }
        });


        Sequencer sequencer = MidiSystem.getSequencer();

        playMidi.setOnAction(e -> {
            System.out.println("Playing MIDI File");
            File MIDITOPlay;
            MIDITOPlay = new File(folderToOpen + "\\midifile.mid");
            try {
                sequencer.open();
                playMidi.setVisible(false);
                stopMidi.setVisible(true);
                InputStream inputStream = new BufferedInputStream(new FileInputStream(MIDITOPlay));
                sequencer.setSequence(inputStream);
                sequencer.start();
                inputStream.close();
            } catch (MidiUnavailableException | InvalidMidiDataException | IOException e1) {
                e1.printStackTrace();
            }
        });

        stopMidi.setOnAction(e -> {

            System.out.println("MIDI File Stopped");
            sequencer.close();
            stopMidi.setVisible(false);
            playMidi.setVisible(true);

        });


        javafx.scene.control.Button closeProgram = new Button("Close");
        closeProgram.setOnAction(e -> {
            stage.close();
            System.exit(0);
        });

        compareMidifiles.setOnAction(e -> {
            System.out.println("Comparing MIDI files");
            compareMIDIFiles();
        });

        noteValue.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, null, new BorderWidths(5))));
        volumeValue.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, null, new BorderWidths(5))));
        noteDuration.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, null, new BorderWidths(5))));
        restDuration.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, null, new BorderWidths(5))));

        VBox vBox = new VBox(instructions, folderButton, noteLabel, noteValue, volumeLabel, volumeValue, noteDurationLabel, noteDuration, restDurationLabel, restDuration, sliderButton, refresh, scrollBar, startProgram, progress, compareMidifiles, playMidi, stopMidi, closeProgram);
        scrollBar.valueProperty().addListener((ov, old_val, new_val) -> vBox.setLayoutY(-new_val.doubleValue()));
        vBox.setSpacing(10);


        Scene scene = new Scene(vBox, 1000, 600);

        stage.setScene(scene);
        stage.setMaximized(true);
        stage.initStyle(StageStyle.DECORATED);
        stage.show();
    }

    /**
     * @param args These are the arguments that the program is running from the previous method
     */
    static void launchApplication(String... args) {
        launch(args);
    }
}