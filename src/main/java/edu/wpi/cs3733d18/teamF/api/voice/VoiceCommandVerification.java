package edu.wpi.cs3733d18.teamF.api.voice;

import com.github.fedy2.weather.YahooWeatherService;
import com.github.fedy2.weather.data.Channel;
import com.github.fedy2.weather.data.unit.DegreeUnit;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
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
                
                if (command.contains("LANGUAGE") && command.contains("INTERPRETER")) {
                    signalClassChanged("Language");
                    voice.speak("Here is the Language interpreter service request");
                } else if (command.contains("RELIGIOUS") && command.contains("SERVICES")) {
                    signalClassChanged("Religious");
                    voice.speak("Here is the religious service service request");
                } else if (command.contains("SECURITY") && command.contains("REQUEST")) {
                    signalClassChanged("Security");
                    voice.speak("Here is the security service request");
                } else if (command.contains("CREATE") && command.contains("SERVICE")) {
                    signalClassChanged("Create");
                    voice.speak("Here create service request page");
                }else if(command.contains("SEARCH") && command.contains("SERVICE")){
                    signalClassChanged("Search");
                    voice.speak("Here is the search service request page");
                }else if(command.contains("USER") && command.contains("MANAGEMENT")){
                    signalClassChanged("User");
                    voice.speak("Here is the user management page");
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
