package edu.wpi.cs3733d18.teamF.voice;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class TTS {
    private Voice voice;
    private VoiceManager voiceManager = VoiceManager.getInstance();

    public TTS(){
        setVoice("kevin16");
    }

    public void setVoice(String voiceName){
        voice = voiceManager.getVoice(voiceName);
        try {
            voice.allocate();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void speak(String toSay){
        voice.speak(toSay);
    }
}
