package edu.wpi.cs3733d18.teamF.api.voice;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Observable;
import java.util.Observer;

public class VoiceLauncher extends Observable implements Runnable, Observer {

    Configuration configuration = new Configuration();
    private boolean terminate = false;

    private VoiceLauncher() {
        exportResource("sr.dic");
        exportResource("sr.lm");

        configuration.setDictionaryPath("sr.dic");
        configuration.setLanguageModelPath("sr.lm");
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
    }

    public static VoiceLauncher getInstance() {
        return LazyInitializer.INSTANCE;
    }

    /**
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param resourceName ie.: "/SmartLibrary.dll"
     * @return The path to the exported resource
     * @throws Exception
     */
    static private String exportResource(String resourceName) {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder = "";
        try {
            stream = VoiceLauncher.class.getResourceAsStream(resourceName);
            if (stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }
            int readBytes;
            byte[] buffer = new byte[4096];
            jarFolder = Paths.get("").toAbsolutePath().toString().replace('\\', '/');
            resStreamOut = new FileOutputStream(jarFolder + "/" + resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
                if (resStreamOut != null) {
                    resStreamOut.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jarFolder + resourceName;
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

    @Override
    public void update(Observable o, Object arg) {
        signalClassChanged(arg);
    }

    @Override
    public void finalize() {
        VoiceLauncher.getInstance().terminate();
    }

    private static class LazyInitializer {
        static final VoiceLauncher INSTANCE = new VoiceLauncher();
    }
}

