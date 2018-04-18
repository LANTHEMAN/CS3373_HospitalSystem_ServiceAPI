package edu.wpi.cs3733d18.teamF.api.voice;

import com.github.fedy2.weather.YahooWeatherService;
import com.github.fedy2.weather.data.Channel;
import com.github.fedy2.weather.data.unit.DegreeUnit;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VoiceCommandVerification extends Observable implements Observer {
    private final Boolean canSayCommand[] = {false};
    private ConcurrentLinkedQueue<String> commands = new ConcurrentLinkedQueue<>();
    private TTS voice = new TTS();

    private Timeline commandExecutor = new Timeline(new KeyFrame(Duration.millis(100), event -> {
        String command = commands.poll();
        if (command != null) {
            if (isActivation(command)) {
                canSayCommand[0] = true;
                signalClassChanged("Activate");
                new Timer(true).schedule(new TimerTask() {
                    @Override
                    public void run() {
                        canSayCommand[0] = false;
                    }
                }, 5000);
            } else {
                if (!canSayCommand[0]) {
                    return;
                }
                canSayCommand[0] = false;

                if (command.equals("HELP")) {
                    signalClassChanged("Help");
                    voice.speak("Here is the help menu");
                } else if (command.contains("WEATHER")) {
                    YahooWeatherService service = null;
                    try {
                        service = new YahooWeatherService();
                    } catch (JAXBException e) {
                        e.printStackTrace();
                    }
                    Channel channel = null;
                    try {
                        channel = service.getForecast("2523945", DegreeUnit.FAHRENHEIT);
                    } catch (JAXBException | IOException e) {
                        e.printStackTrace();
                    }
                    voice.speak(String.format("The temperature is %d degrees fahrenheit", channel.getItem().getCondition().getTemp()));
                    voice.speak(channel.getAtmosphere().toString());
                } else if (command.contains("RAP")) {
                    voice.speak("Boots and Cats and Boots and Cats and Boots and Cats and Boots and Cats and Boots" +
                            "and Cats and Boots and Cats and Boots and Cats and Boots and Cats and Boots and Cats and Boots");
                }
            }
        }
    }));

    public VoiceCommandVerification() {
        commandExecutor.setCycleCount(Timeline.INDEFINITE);
        commandExecutor.play();
    }

    public boolean isActivation(String command) {
        return command.contains("HELLO KIOSK") ||
                command.contains("HEY KIOSK");
    }

    private void signalClassChanged(Object args) {
        this.setChanged();
        this.notifyObservers(args);
        System.out.println("args.toString() = " + args.toString());
    }

    @Override
    public void update(Observable o, Object arg) {
        if (!(o instanceof VoiceLauncher)) {
            return;
        }

        if (arg instanceof String) {
            commands.add((String) arg);
        }
    }
}
