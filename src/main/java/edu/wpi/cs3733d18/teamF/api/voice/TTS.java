package edu.wpi.cs3733d18.teamF.api.voice;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import java.util.concurrent.atomic.AtomicBoolean;

public class TTS {
    private Voice voice;
    private VoiceManager voiceManager = VoiceManager.getInstance();
    private AtomicBoolean canSpeak;

    public TTS() {
        setVoice("kevin16");
        canSpeak = new AtomicBoolean(true);
    }

    public void setVoice(String voiceName) {
        voice = voiceManager.getVoice(voiceName);
        try {
            voice.allocate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void speak(String toSay) {
        new Thread(() -> {
            while (!canSpeak.compareAndSet(true, false)) {
            }
            voice.speak(toSay);
            canSpeak.set(true);
            return;
        }).start();
    }
}
