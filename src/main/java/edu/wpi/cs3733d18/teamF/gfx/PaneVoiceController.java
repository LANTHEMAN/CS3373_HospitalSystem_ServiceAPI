package edu.wpi.cs3733d18.teamF.gfx;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;


public class PaneVoiceController extends PaneController {
    public PaneVoiceController(Pane root) {
        super(root);

        addDrawable(pane -> {
            Circle circle = new Circle(10, Color.DEEPSKYBLUE);
            circle.setCenterX(pane.getWidth() / 2);
            circle.setCenterY(pane.getHeight() / 1.3);

            FontAwesomeIconView mic = new FontAwesomeIconView(FontAwesomeIcon.MICROPHONE);

            mic.setScaleX(5);
            mic.setScaleY(5);

            mic.setTranslateX(circle.getCenterX() - 5);
            mic.setTranslateY(circle.getCenterY() + 2);

            mic.setVisible(true);

            final Boolean[] isUp = {false};
            final Integer[] timer = {0};

            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10), event -> {
                timer[0]++;
                if (isUp[0]) {
                    circle.setRadius(circle.getRadius() + 1);
                    if (circle.getRadius() > 90) {
                        isUp[0] = false;
                    }
                }
                if (!isUp[0]) {
                    circle.setRadius(circle.getRadius() - 1);
                    if (circle.getRadius() < 40) {
                        isUp[0] = true;
                    }
                }
                if (timer[0] == 500) {
                    circle.setRadius(0);
                    mic.setVisible(false);
                }
            }));
            timeline.setCycleCount(500);
            timeline.play();
            pane.getChildren().addAll(circle, mic);
        });
    }


}
