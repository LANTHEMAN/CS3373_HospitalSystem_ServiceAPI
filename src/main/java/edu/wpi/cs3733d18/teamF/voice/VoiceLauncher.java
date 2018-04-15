package edu.wpi.cs3733d18.teamF.voice;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;

import java.io.IOException;
import java.util.Observable;

public class VoiceLauncher extends Observable implements Runnable {

    Configuration configuration = new Configuration();
    private boolean terminate = false;

    private VoiceLauncher() {
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("3075.dic");
        configuration.setLanguageModelPath("3075.lm");
    }

    public static VoiceLauncher getInstance() {
        return LazyInitializer.INSTANCE;
    }

    public void run() {
        try {
            LiveSpeechRecognizer recognize = new LiveSpeechRecognizer(configuration);

            recognize.startRecognition(true);

            //Create SpeechResult Object
            SpeechResult result;
            String command;

            //Checking if recognizer has recognized the speech
            while ((result = recognize.getResult()) != null && !terminate) {
                //Get the recognize speech
                command = result.getHypothesis();
                signalClassChanged(command);
            }
            recognize.stopRecognition();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void signalClassChanged(Object args) {
        this.setChanged();
        this.notifyObservers(args);
    }

    public void terminate() {
        terminate = true;
    }

    private static class LazyInitializer {
        static final VoiceLauncher INSTANCE = new VoiceLauncher();
    }
}

