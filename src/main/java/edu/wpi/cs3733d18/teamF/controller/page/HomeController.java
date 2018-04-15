package edu.wpi.cs3733d18.teamF.controller.page;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733d18.teamF.ImageCacheSingleton;
import edu.wpi.cs3733d18.teamF.Map;
import edu.wpi.cs3733d18.teamF.MapSingleton;
import edu.wpi.cs3733d18.teamF.controller.*;
import edu.wpi.cs3733d18.teamF.controller.page.element.mapView.MapViewElement;
import edu.wpi.cs3733d18.teamF.controller.page.element.mapView.MapViewListener;
import edu.wpi.cs3733d18.teamF.db.DatabaseSingleton;
import edu.wpi.cs3733d18.teamF.gfx.PaneVoiceController;
import edu.wpi.cs3733d18.teamF.graph.*;
import edu.wpi.cs3733d18.teamF.qr.qrConverter;
import edu.wpi.cs3733d18.teamF.sr.*;
import edu.wpi.cs3733d18.teamF.voice.TTS;
import edu.wpi.cs3733d18.teamF.voice.VoiceCommandVerification;
import edu.wpi.cs3733d18.teamF.voice.VoiceLauncher;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class HomeController implements SwitchableController, Observer, MapViewListener {

    private static Image maps2D[] = ImageCacheSingleton.maps2D;
    private static Image maps3D[] = ImageCacheSingleton.maps3D;
    private final ObservableList<String> priority = FXCollections.observableArrayList("0", "1", "2", "3", "4", "5");
    private final ObservableList<String> status = FXCollections.observableArrayList("Incomplete", "In Progress", "Complete");
    private final ObservableList<String> type = FXCollections.observableArrayList("Language Interpreter", "Religious Services");
    private final ObservableList<String> privilegeOptions = FXCollections.observableArrayList("Staff", "Admin");
    private final ObservableList<String> filterOptions = FXCollections.observableArrayList("Priority", "Status", "Type");
    @FXML
    public ComboBox filterType, availableTypes;
    @FXML
    public TableView<ServiceRequest> searchResultTable;
    @FXML
    public TableColumn btnsCol;
    @FXML
    public TableColumn<ServiceRequest, Integer> idNumberCol, requestPriorityCol;
    @FXML
    public TableColumn<ServiceRequest, String> requestTypeCol, firstNameCol, lastNameCol, destinationCol, theStatusCol;
    @FXML
    public Label typeLabel, idLabel, firstNameLabel, lastNameLabel, locationLabel, statusLabel, completedByLabel, usernameLabel;
    @FXML
    public TextArea instructionsTextArea;
    @FXML
    public TableColumn chooseCol;
    @FXML
    public TableColumn<User, String> usernameCol, firstNameUserCol, lastNameUserCol, privilegeCol, occupationCol;
    @FXML
    TextField languageField, firstNameLanguage, lastNameLanguage, destinationLanguage;
    @FXML
    TextArea instructionsLanguage;
    @FXML
    Label languageRequiredLI, firstNameRequiredLI, lastNameRequiredLI, locationRequiredLI;
    @FXML
    TextField religionField, firstNameRS, lastNameRS, destinationRS;
    @FXML
    TextArea instructionsRS;
    @FXML
    Label religionRequiredRS, firstNameRequiredRS, lastNameRequiredRS, locationRequiredRS;
    MapViewElement mapViewElement;
    @FXML
    AnchorPane mapElementPane;
    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
    //REMOVE WHEN YA DONT WANT THE HAPPY BDAY BUTTON
    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
    TTS justinTTS = new TTS();
    private PaneSwitcher switcher;
    private ObservableResourceFactory resFactory = new ObservableResourceFactory();
    ///////////////////////////////
    //                           //
    //           Voice           //
    //                           //
    ///////////////////////////////
    private PaneVoiceController paneVoiceController;
    @FXML
    private Pane voicePane;
    /////////////////////////////////////////
    //                                     //
    //           Service Request           //
    //                                     //
    /////////////////////////////////////////
    private ServiceRequest serviceRequestPopUp;
    @FXML
    private AnchorPane editRequestPane;
    @FXML
    private JFXButton LI, RS, SR;
    /////////////////////////////////
    //       Search Services       //
    /////////////////////////////////
    private String privilegeChoice;
    private String searchType;
    private String filter;
    private User editedUser;
    private boolean newUser;
    @FXML
    private AnchorPane searchPane;
    @FXML
    private JFXTextField usernameSearch;
    @FXML
    private JFXListView usernameList;
    @FXML
    private JFXCheckBox completeCheck;
    @FXML
    private JFXNodesList serviceRequestList;
    @FXML
    private JFXButton newServiceRequest;
    //////////////////////////////////
    //       Language Service       //
    //////////////////////////////////
    @FXML
    private AnchorPane languageInterpreterPane;
    ///////////////////////////////////
    //       Religious Service       //
    ///////////////////////////////////
    @FXML
    private AnchorPane religiousServicesPane;
    ///////////////////////////////////
    //       Security Service        //
    ///////////////////////////////////
    @FXML
    private VBox securityPane;
    @FXML
    private JFXTextArea securityTextArea;
    @FXML
    private JFXTextField securityLocationField;
    @FXML
    private ToggleGroup securityToggle;
    @FXML
    private Label securityLocationRequired;
    //////////////////////////////////////////
    //                                      //
    //           Search Algorithm           //
    //                                      //
    //////////////////////////////////////////
    @FXML
    private HBox algorithmsBox;
    @FXML
    private JFXButton aStar, depthFirst, breathFirst;
    /////////////////////////
    //                     //
    //         Map         //
    //                     //
    /////////////////////////
    private Map map;
    @FXML
    private Text MainTitle;

    ////////////////////////////////
    //                            //
    //         Map Builder        //
    //                            //
    ////////////////////////////////
    @FXML
    private JFXNodesList floorNode;
    @FXML
    private JFXButton floorBtn, l2, l1, groundFloor, floor1, floor2, floor3;
    @FXML
    private JFXButton btn2D, btn3D;
    /////////////////////////////////
    //      Modify Node Panel      //
    /////////////////////////////////
    @FXML
    private GridPane gpaneNodeInfo;
    @FXML
    private TextField modNode_shortName, modNode_longName, modNode_x, modNode_y;
    ///////////////////////////////
    //      New Node Window      //
    ///////////////////////////////
    @FXML
    private VBox addLocationPopup;
    @FXML
    private TextField newNode_x, newNode_y, newNode_shortName;
    @FXML
    private ComboBox<String> newNode_type = new ComboBox<>(FXCollections.observableArrayList(NodeBuilder.getNodeTypes()));
    ///////////////////////////////
    //                           //
    //           Login           //
    //                           //
    ///////////////////////////////
    @FXML
    private JFXDrawer loginDrawer;
    @FXML
    private VBox loginBox, logoutBox;
    @FXML
    private JFXButton loginBtn;
    @FXML
    private JFXTextField loginUsername;
    @FXML
    private JFXPasswordField loginPassword;
    @FXML
    private FontAwesomeIconView loginCancel, logoutCancel;
    ///////////////////////////////////
    //                               //
    //       Search Location         //
    //                               //
    ///////////////////////////////////
    @FXML
    private HBox searchBar;
    @FXML
    private JFXTextField searchLocation, sourceLocation, destinationLocation;
    @FXML
    private JFXListView searchList, directionsList;
    private boolean sourceLocationActive = false;
    /////////////////////////////////
    //                             //
    //          Hamburger          //
    //                             //
    /////////////////////////////////
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXDrawer adminDrawer;
    @FXML
    private VBox adminBox;
    @FXML
    private JFXButton mapEditorBtn, editUsersBtn;
    /////////////////////////////
    //      Directions Box     //
    /////////////////////////////
    @FXML
    private VBox directionsBox;
    @FXML
    private JFXDrawer directionsDrawer;
    @FXML
    private JFXHamburger hamburgerD;
    @FXML
    private JFXTextArea txtDirections;
    @FXML
    private Pane qrImage;
    //////////////////////////////
    //                          //
    //        Edit User         //
    //                          //
    //////////////////////////////
    @FXML
    private AnchorPane editUserPane, newUserPane;
    @FXML
    private JFXTextField userTextField;
    @FXML
    private TableView<User> searchUserResultTable;
    @FXML
    private Label userLabel;
    @FXML
    private JFXCheckBox languageCheck, religiousCheck, securityCheck;
    @FXML
    private JFXTextField usernameField, passwordField, fnameField, lnameField, occupationField;
    @FXML
    private JFXComboBox privilegeCombo;
    /////////////////////////////////
    //                             //
    //           Help Pane         //
    //                             //
    /////////////////////////////////
    @FXML
    private Pane helpPane;
    @FXML
    private GridPane userInstructions, adminInstructions;
    /////////////////////////////////
    //                             //
    //           Date/Time         //
    //                             //
    /////////////////////////////////
    @FXML
    private Label time;
    @FXML
    private Label date;

    /**
     * Constructor for this class
     *
     * @param switcher
     */
    @Override
    public void initialize(PaneSwitcher switcher) {
        // initialize fundamentals
        this.switcher = switcher;
        map = MapSingleton.getInstance().getMap();

        // initialize element
        // init mapView
        Pair<MapViewElement, Pane> mapElementInfo = switcher.loadElement("mapView.fxml");
        mapViewElement = mapElementInfo.getKey();
        mapViewElement.initialize(this, map, mapElementPane);
        // init voice overlay
        paneVoiceController = new PaneVoiceController(voicePane);

        VoiceCommandVerification voice = new VoiceCommandVerification();
        voice.addObserver(this);
        VoiceLauncher.getInstance().addObserver(voice);


        // set up new node panel
        newNode_type.getItems().addAll(NodeBuilder.getNodeTypes());
        newNode_type.getSelectionModel().selectFirst();


        //floorBtn.setText("2");
        l2.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            map.setFloor("L2");
            MainTitle.setText("Brigham and Women's Hospital: Lower Level 2");
            //floorNode.animateList(false);
            reloadMap();
        });
        l1.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            map.setFloor("L1");
            MainTitle.setText("Brigham and Women's Hospital: Lower Level 1");
            //floorNode.animateList(false);
            reloadMap();
        });
        groundFloor.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            map.setFloor("0G");
            MainTitle.setText("Brigham and Women's Hospital: Ground Floor");
            //floorNode.animateList(false);
            reloadMap();
        });
        floor1.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            map.setFloor("01");
            MainTitle.setText("Brigham and Women's Hospital: Level 1");
            //floorNode.animateList(false);
            reloadMap();
        });
        floor2.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            map.setFloor("02");
            MainTitle.setText("Brigham and Women's Hospital: Level 2");
            //floorNode.animateList(false);
            reloadMap();
        });
        floor3.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            map.setFloor("03");
            MainTitle.setText("Brigham and Women's Hospital: Level 3");
            //floorNode.animateList(false);
            reloadMap();
        });


        floorNode.addAnimatedNode(floorBtn);
        floorNode.addAnimatedNode(l2);
        floorNode.addAnimatedNode(l1);
        floorNode.addAnimatedNode(groundFloor);
        floorNode.addAnimatedNode(floor1);
        floorNode.addAnimatedNode(floor2);
        floorNode.addAnimatedNode(floor3);


        // set the hamburger menu on top left accordingly
        if (PermissionSingleton.getInstance().getUserPrivilege().equals("Guest")) {
            setGuestMenu();
            loginBtn.setText("Login");
        } else if (PermissionSingleton.getInstance().getUserPrivilege().equals("Staff")) {
            setAdminMenu();
            loginBtn.setText(PermissionSingleton.getInstance().getCurrUser());
        } else if (PermissionSingleton.getInstance().isAdmin()) {
            setAdminMenu();
            loginBtn.setText(PermissionSingleton.getInstance().getCurrUser());
        } else {
            setGuestMenu();
            loginBtn.setText(PermissionSingleton.getInstance().getCurrUser());
        }


        directionsDrawer.setSidePane(directionsBox);
        adminDrawer.setSidePane(adminBox);
        privilegeCombo.getItems().addAll(privilegeOptions);

        // login
        loginBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            if (PermissionSingleton.getInstance().getUserPrivilege().equals("Guest")) {
                loginBox.setVisible(true);
                loginDrawer.setSidePane(loginBox);
                loginDrawer.setOverLayVisible(false);
                loginBtn.setText("Login");
            } else {
                logoutBox.setVisible(true);
                loginDrawer.setSidePane(logoutBox);
                loginDrawer.setOverLayVisible(false);
                loginBtn.setText(PermissionSingleton.getInstance().getCurrUser());
            }

            if (loginDrawer.isShown()) {
                // login in the user and close the drawer
                if (PermissionSingleton.getInstance().login(loginUsername.getText(), loginPassword.getText())) {
                    loginBtn.setText(PermissionSingleton.getInstance().getCurrUser());
                    if (PermissionSingleton.getInstance().getUserPrivilege().equals("Admin")) {
                        setAdminMenu();
                    } else if (PermissionSingleton.getInstance().getUserPrivilege().equals("Staff")) {
                        setStaffMenu();
                    } else {
                        setGuestMenu();
                    }
                    loginDrawer.close();
                    loginUsername.setText("");
                    loginPassword.setText("");

                } else {
                    loginPassword.setText("");
                    shakePasswordField(loginPassword);
                }

            } else {
                loginDrawer.open();
            }
        });

        loginCancel.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            loginDrawer.close();
        });

        logoutCancel.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            loginDrawer.close();
        });


        searchLocation.setOnKeyTyped((KeyEvent e) -> {
            String input = searchLocation.getText();
            input = input.concat("" + e.getCharacter());
            autoComplete(input, searchList, "Node", "longName");
        });

        sourceLocation.setOnKeyTyped((KeyEvent e) -> {
            String input = sourceLocation.getText();
            input = input.concat("" + e.getCharacter());
            autoComplete(input, directionsList, "Node", "longName");
            sourceLocationActive = true;
        });

        destinationLocation.setOnKeyTyped((KeyEvent e) -> {
            String input = destinationLocation.getText();
            input = input.concat("" + e.getCharacter());
            autoComplete(input, directionsList, "Node", "longName");
            sourceLocationActive = false;
        });

        usernameSearch.setOnKeyTyped((KeyEvent e) -> {
            String input = usernameSearch.getText();
            input = input.concat("" + e.getCharacter());
            autoComplete(input, usernameList, "HUser", "username");
        });

        userTextField.setOnKeyTyped((KeyEvent e) -> {
            String input = userTextField.getText();
            input = input.concat("" + e.getCharacter());
            ArrayList<User> list = autoCompleteUserSearch(input);
            displayInUserTable(list);
        });

        serviceRequestList.addAnimatedNode(newServiceRequest);


        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            Calendar cal = Calendar.getInstance();
            int second = cal.get(Calendar.SECOND);
            int minute = cal.get(Calendar.MINUTE);
            int hour = cal.get(Calendar.HOUR);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int month = cal.get(Calendar.MONTH);
            int year = cal.get(Calendar.YEAR);
            //System.out.println(hour + ":" + (minute) + ":" + second);
            time.setText(hour + ":" + (minute) + ":" + second);
            date.setText(month + "/" + day + "/" + year);
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

    }

    @Override
    public void update(Observable o, Object arg) {
        if (!(o instanceof VoiceCommandVerification)) {
            return;
        }

        if (arg instanceof Node) {
            Node voiceSelectedEnd = (Node) arg;
            // Path path = mapView.getPath(selectedNodeStart, voiceSelectedEnd);
            // mapDrawController.showPath(path);
            // displayTextDirections(path);
        } else if (arg instanceof HashSet) {
            HashSet<Node> potentialDestinations = (HashSet<Node>) arg;
        } else if (arg instanceof String) {
            String cmd = (String) arg;
            if (cmd.equalsIgnoreCase("Help")) {
                // popup the help menu
                onHelpPopup();
            } else if (cmd.equalsIgnoreCase("Activate")) {
                // got a string saying that the activation command has been said
                paneVoiceController.setVisibility(true);
            }
        }
        reloadMap();
    }

    // will filter the given ListView for the given input String
    private void autoComplete(String input, ListView listView, String table, String field) {
        if (input.length() > 0) {
            String sql = "SELECT " + field + " FROM " + table;
            ResultSet resultSet = DatabaseSingleton.getInstance().getDbHandler().runQuery(sql);
            ArrayList<String> autoCompleteStrings = new ArrayList<>();

            try {
                while (resultSet.next()) {
                    String username = resultSet.getString(1);
                    if (username.toLowerCase().contains(input.toLowerCase())) {
                        autoCompleteStrings.add(username);
                    }
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
            try {
                if (autoCompleteStrings.size() > 0) {
                    ObservableList<String> list = FXCollections.observableArrayList(autoCompleteStrings);
                    listView.setItems(list);
                    listView.setVisible(true);
                } else {
                    listView.setVisible(false);
                }
            } catch (Exception anyE) {
                anyE.printStackTrace();
            }
        } else {
            listView.setVisible(false);
        }
    }

    public void onBirthday() {
        justinTTS.speak("happy birthday to you, happy birthday to you, happy birthday dear justin, happy birthday you yaaaaaaaay");
    }

    // will shake the password field back and forth
    private void shakePasswordField(JFXPasswordField passwordField) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.1), passwordField);
        translateTransition.setFromX(7);
        translateTransition.setToX(-7);
        translateTransition.setCycleCount(4);
        translateTransition.setAutoReverse(true);
        translateTransition.setOnFinished((translateEvent) -> {
            TranslateTransition tt = new TranslateTransition(Duration.seconds(0.05), passwordField);
            tt.setFromX(7);
            tt.setToX(0);
            tt.setCycleCount(0);
            tt.play();
        });
        translateTransition.play();
    }

    @FXML
    private void onLogOutBtn() {
        PermissionSingleton.getInstance().logout();
        loginDrawer.close();
        onCancelDirectionsEvent();
        setCancelMenuEvent();
        serviceRequestList.animateList(false);
        serviceRequestList.animateList(false);
        setGuestMenu();
        if (algorithmsBox.isVisible()) {
            algorithmsBox.setVisible(false);
        }

        loginBtn.setText("Login");

    }

    private void changeFloorButtons(Path path) {
        String first_floor = path.getNodes().get(0).getFloor();
        String last_floor = path.getNodes().get(0).getFloor();
        editButtonColor(first_floor, "GREEN");
        for (int i = 0; i < path.getNodes().size(); i++) {
            if (!path.getNodes().get(i).getFloor().equals(last_floor)) {
                if (!last_floor.equals(first_floor)) {
                    editButtonColor(last_floor, "GRAY");
                }
                last_floor = path.getNodes().get(i).getFloor();
            }
        }
        editButtonColor(last_floor, "RED");
    }

    private void editButtonColor(String floor, String color) {
        JFXButton btn = l2;
        switch (floor) {
            case "L2":
                btn = l2;
                break;
            case "L1":
                btn = l1;
                break;
            case "0G":
                btn = groundFloor;
                break;
            case "01":
                btn = floor1;
                break;
            case "02":
                btn = floor2;
                break;
            case "03":
                btn = floor3;
                break;
        }
        switch (color) {
            case "GREEN":
                btn.setStyle("-fx-border-color: GREEN;" +
                        " -fx-border-width: 5px;" +
                        " -fx-border-radius: 100;" +
                        " -fx-background-color: #042E58;" +
                        " -fx-background-radius: 100;");
                break;
            case "GRAY":
                btn.setStyle("-fx-border-color: #606060;" +
                        " -fx-border-width: 5px;" +
                        " -fx-border-radius: 100;" +
                        " -fx-background-color: #042E58;" +
                        " -fx-background-radius: 100;");
                break;
            case "RED":
                btn.setStyle("-fx-border-color: RED;" +
                        " -fx-border-width: 5px;" +
                        " -fx-border-radius: 100;" +
                        " -fx-background-color: #042E58;" +
                        " -fx-background-radius: 100;");
                break;
        }
    }

    private void clearColors() {
        resetButtonStyle(l2);
        resetButtonStyle(l1);
        resetButtonStyle(groundFloor);
        resetButtonStyle(floor1);
        resetButtonStyle(floor2);
        resetButtonStyle(floor3);
    }

    private void resetButtonStyle(JFXButton btn) {
        btn.setStyle("-fx-border-color: #042E58;" +
                " -fx-border-width: 5px;" +
                " -fx-border-radius: 100;" +
                " -fx-background-color: #042E58;" +
                " -fx-background-radius: 100;");
    }

    /////////////////////////////////
    //                             //
    //          Hamburger          //
    //                             //
    /////////////////////////////////

    @FXML
    private void onHamburgerMenu() {
        if (adminDrawer.isHidden()) {
            adminDrawer.open();
            adminDrawer.toFront();
        }
    }

    @FXML
    private void onArrowEvent() {
        if (directionsDrawer.isHidden()) {
            directionsDrawer.open();
            directionsDrawer.toFront();
            destinationLocation.setText(searchLocation.getText());
        }
    }

    @FXML
    private void setCancelMenuEvent() {
        if (adminDrawer.isShown()) {
            adminDrawer.close();
            adminDrawer.toBack();
        }
        if (serviceRequestList.isExpanded()) {
            serviceRequestList.animateList(false);
        }
    }


    private void setGuestMenu() {
        hamburger.setVisible(false);
        hamburgerD.setVisible(false);
        //serviceRequestList.getChildren().clear();
        serviceRequestList.setRotate(0);
    }

    private void setAdminMenu() {
        hamburger.setVisible(true);
        hamburgerD.setVisible(true);
        mapEditorBtn.setVisible(true);
        editUsersBtn.setVisible(true);
        algorithmsBox.setVisible(true);
        if (serviceRequestList.getChildren().contains(LI)) {
            serviceRequestList.getChildren().remove(LI);
        }
        if (serviceRequestList.getChildren().contains(RS)) {
            serviceRequestList.getChildren().remove(RS);
        }
        if (serviceRequestList.getChildren().contains(SR)) {
            serviceRequestList.getChildren().remove(SR);
        }
        serviceRequestList.addAnimatedNode(LI);
        serviceRequestList.addAnimatedNode(RS);
        serviceRequestList.addAnimatedNode(SR);
        serviceRequestList.setRotate(-90);
    }

    private void setStaffMenu() {
        hamburger.setVisible(true);
        hamburgerD.setVisible(true);
        mapEditorBtn.setVisible(false);
        editUsersBtn.setVisible(false);
        if (serviceRequestList.getChildren().contains(LI)) {
            serviceRequestList.getChildren().remove(LI);
        }
        if (serviceRequestList.getChildren().contains(RS)) {
            serviceRequestList.getChildren().remove(RS);
        }
        if (serviceRequestList.getChildren().contains(SR)) {
            serviceRequestList.getChildren().remove(SR);
        }
        if (ServiceRequestSingleton.getInstance().isInTable(PermissionSingleton.getInstance().getCurrUser(), "LanguageInterpreter")) {
            serviceRequestList.addAnimatedNode(LI);
        }
        if (ServiceRequestSingleton.getInstance().isInTable(PermissionSingleton.getInstance().getCurrUser(), "ReligiousServices")) {
            serviceRequestList.addAnimatedNode(RS);
        }
        if (ServiceRequestSingleton.getInstance().isInTable(PermissionSingleton.getInstance().getCurrUser(), "SecurityRequest")) {
            serviceRequestList.addAnimatedNode(SR);
        }
        serviceRequestList.setRotate(-90);
    }

    /////////////////////////////
    //                         //
    //       Directions        //
    //                         //
    /////////////////////////////

    @FXML
    void onSearchLocation() {
        if (!searchLocation.getText().equals("")) {
            HashSet<Node> n = map.getNodes(node -> node.getLongName().equals(searchLocation.getText()));
            if (n.size() < 1) {
                return;
            }

            Node selectedNode = n.iterator().next();
            map.setFloor(selectedNode.getFloor());
            reloadMap();
        }
    }

    @FXML
    void switchLocations() {
        String temp = sourceLocation.getText();
        sourceLocation.setText(destinationLocation.getText());
        destinationLocation.setText(temp);

    }

    @FXML
    void setSourceSearch() {
        searchLocation.setText(searchList.getSelectionModel().getSelectedItem().toString());
        searchList.setVisible(false);
    }

    @FXML
    void setDirectionLocation() {
        String selection = directionsList.getSelectionModel().getSelectedItem().toString();
        if (sourceLocationActive) {
            sourceLocation.setText(selection);
        } else {
            destinationLocation.setText(selection);
        }
        directionsList.setVisible(false);
    }

    private void displayTextDirections(Path route) {
        List<String> directions = route.makeTextDirections();

        StringBuilder sb = new StringBuilder();
        for (String text : directions) {

            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(text);
        }
        txtDirections.setText(sb.toString());
        qrConverter qr = new qrConverter(sb.toString());
        qrImage.getChildren().add(qr.getQrView());
    }

    @FXML
    private void onCancelDirectionsEvent() {
        if (directionsDrawer.isShown()) {
            directionsDrawer.close();
            directionsDrawer.toBack();
            searchLocation.setText(destinationLocation.getText());
        }
    }

    @FXML
    void onNavigate() {
        sourceLocation.getText();
        destinationLocation.getText();
        if (!sourceLocation.getText().equals("") && !destinationLocation.getText().equals("")) {
            HashSet<Node> sourceNodeSet = map.getNodes(node -> node.getLongName().equals(sourceLocation.getText()));
            if (sourceNodeSet.size() < 1) {
                return;
            }
            HashSet<Node> destinationNodeSet = map.getNodes(node -> node.getLongName().equals(destinationLocation.getText()));
            if (destinationNodeSet.size() < 1) {
                return;
            }

            Node sourceNode = sourceNodeSet.iterator().next();
            Node destinationNode = destinationNodeSet.iterator().next();

            displayTextDirections(map.getPath(sourceNode, destinationNode));
            clearColors();
            changeFloorButtons(map.getPath(sourceNode, destinationNode));

            map.setFloor(sourceNode.getFloor());
            reloadMap();
        }
    }


    //////////////////////////////////////////
    //                                      //
    //           Search Algorithm           //
    //                                      //
    //////////////////////////////////////////

    @FXML
    private void onAStar() {
        MapSingleton.getInstance().getMap().setPathSelector(new AStar());
        aStar.setStyle("-fx-background-color: #303030");
        breathFirst.setStyle("-fx-background-color: #616161");
        depthFirst.setStyle("-fx-background-color: #616161");
    }

    @FXML
    private void onBreathFirst() {
        MapSingleton.getInstance().getMap().setPathSelector(new BreathSearch());
        aStar.setStyle("-fx-background-color: #616161");
        breathFirst.setStyle("-fx-background-color: #303030");
        depthFirst.setStyle("-fx-background-color: #616161");
    }

    @FXML
    private void onDepthFirst() {
        MapSingleton.getInstance().getMap().setPathSelector(new DepthSearch());
        aStar.setStyle("-fx-background-color: #616161");
        breathFirst.setStyle("-fx-background-color: #616161");
        depthFirst.setStyle("-fx-background-color: #303030");
    }


    ///////////////////////////
    //                       //
    //       Help Pane       //
    //                       //
    ///////////////////////////

    @FXML
    void onHelpPopup() {
        if (PermissionSingleton.getInstance().isAdmin()) {
            userInstructions.setVisible(false);
            adminInstructions.setVisible(true);
        } else {
            adminInstructions.setVisible(false);
            userInstructions.setVisible(true);
        }
        helpPane.setVisible(true);
    }

    @FXML
    void onCancelClose() {
        helpPane.setVisible(false);
    }


    //////////////////////////////////////////////
    //                                          //
    //     Search Service Request Functions     //
    //                                          //
    //////////////////////////////////////////////

    private ArrayList<User> autoCompleteUserSearch(String input) {
        ArrayList<User> autoCompleteUser = new ArrayList<>();
        if (input.length() > 0) {
            String sql = "SELECT * FROM HUser";
            try {
                ResultSet resultSet = DatabaseSingleton.getInstance().getDbHandler().runQuery(sql);
                while (resultSet.next()) {

                    String username = resultSet.getString(1);
                    String password = resultSet.getString(2);
                    String firstname = resultSet.getString(3);
                    String lastname = resultSet.getString(4);
                    String privilege = resultSet.getString(5);
                    String occupation = resultSet.getString(6);
                    User temp = new User(username, password, firstname, lastname, privilege, occupation);
                    String searchString = username + password + firstname + lastname + privilege + occupation;
                    if (searchString.toLowerCase().contains(input.toLowerCase())) {
                        autoCompleteUser.add(temp);
                    }

                }
                resultSet.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }

        }
        return autoCompleteUser;
    }


    /////////////////////////////
    //                         //
    //       Languages         //
    //                         //
    /////////////////////////////

    @FXML
    void onEnglish() {
        switcher.switchResource(ResourceBundle.getBundle("LanguageBundle", new Locale("en", "US")),
                Screens.Home);
    }

    @FXML
    void onFrench() {
        switcher.switchResource(ResourceBundle.getBundle("LanguageBundle", new Locale("fr", "FR")),
                Screens.Home);
    }

    @FXML
    void onSpanish() {
        switcher.switchResource(ResourceBundle.getBundle("LanguageBundle", new Locale("es", "ES")),
                Screens.Home);
    }

    @FXML
    void onChinese() {
        switcher.switchResource(ResourceBundle.getBundle("LanguageBundle", new Locale("zh", "CN"), new UTF8Control()),
                Screens.Home);
    }


    /////////////////////
    //                 //
    //       Map       //
    //                 //
    /////////////////////

    @FXML
    void on2D() {
        map.setIs2D(true);
        reloadMap();
        btn2D.setButtonType(JFXButton.ButtonType.RAISED);
        btn2D.setStyle("-fx-background-color:  #f2f5f7");
        btn3D.setButtonType(JFXButton.ButtonType.FLAT);
        btn3D.setStyle("-fx-background-color:  #b6b8b9");
    }


    @FXML
    void on3D() {
        map.setIs2D(false);
        reloadMap();
        btn2D.setButtonType(JFXButton.ButtonType.FLAT);
        btn2D.setStyle("-fx-background-color:  #b6b8b9");
        btn3D.setButtonType(JFXButton.ButtonType.RAISED);
        btn3D.setStyle("-fx-background-color:  #f2f5f7");
    }

    private void reloadMap() {
        int index;

    }


    //////////////////////////////
    //                          //
    //       Map Builder        //
    //                          //
    //////////////////////////////

    @FXML
    void onAddLocationConfirm() {
        String type = newNode_type.getSelectionModel().getSelectedItem();
        // TODO move into NodeBuilder

        if (!(type.equals("ELEV") || type.equals("STAI"))) {
            Node newNode = new NewNodeBuilder()
                    .setNodeType(type)
                    .setNumNodeType(map)
                    .setFloor(map.getFloor())
                    .setBuilding("New Building") // TODO set building
                    .setShortName(newNode_shortName.getText())
                    .setLongName(newNode_shortName.getText())
                    .setPosition(new Point2D(Double.parseDouble(newNode_x.getText()), Double.parseDouble(newNode_y.getText())))
                    .build();
            map.createNode(newNode);

        } else {
            if (newNode_shortName.getText().length() != 1
                    || !Character.isLetter(newNode_shortName.getText().charAt(0))
                    || map.getNodes(node -> node.getNodeID().contains(type + "00" + newNode_shortName.getText().charAt(0) + map.getFloor())).size() == 1) {
                addLocationPopup.setVisible(false);
                newNode_shortName.setText("");
            }

            Node newNode = new NewNodeBuilder()
                    .setNodeType(type)
                    .setLinkChar(newNode_shortName.getText().charAt(0))
                    .setFloor(map.getFloor())
                    .setBuilding("New Building") // TODO set building
                    .setShortName(newNode_shortName.getText())
                    .setLongName(newNode_shortName.getText())
                    .setPosition(new Point2D(Double.parseDouble(newNode_x.getText()), Double.parseDouble(newNode_y.getText())))
                    .build();
            map.createNode(newNode);
            for (Node node : map.getNodes(node -> node.getNodeID().contains(type + "00" + newNode_shortName.getText().charAt(0)))) {
                if (node == newNode) {
                    continue;
                }
                map.addEdge(node, newNode);
            }
        }

        addLocationPopup.setVisible(false);
        newNode_shortName.setText("");
    }

    @FXML
    void onAddLocationCancel() {
        addLocationPopup.setVisible(false);
        newNode_shortName.setText("");
    }

    @FXML
    void onModificationCancel() {
        gpaneNodeInfo.setVisible(false);
    }

    @FXML
    void onNodeModify() {

    }

    @FXML
    public void onMapEditor() {

        onCancelDirectionsEvent();
        setCancelMenuEvent();
    }


    ////////////////////////////////
    //                            //
    //       New/Edit User        //
    //                            //
    ////////////////////////////////

    @FXML
    private void onCancelEditUser() {
        editUserPane.setVisible(false);
        try {
            searchUserResultTable.getItems().clear();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onNewUserEvent() {
        userLabel.setText("New User");
        usernameField.setEditable(true);
        newUser = true;
        usernameField.clear();
        passwordField.clear();
        fnameField.clear();
        lnameField.clear();
        occupationField.clear();
        languageCheck.setSelected(false);
        religiousCheck.setSelected(false);
        securityCheck.setSelected(false);
        privilegeCombo.getSelectionModel().clearSelection();
        editUserPane.setVisible(false);
        newUserPane.setVisible(true);
    }

    private void displayInUserTable(ArrayList<User> users) {
        if (users.size() < 1) {
            //TODO: indicate to user that there are no results
            return;
        }

        ObservableList<User> listUsers = FXCollections.observableArrayList(users);

        searchUserResultTable.setEditable(false);

        usernameCol.setCellValueFactory(new PropertyValueFactory<User, String>("uname"));
        firstNameUserCol.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        lastNameUserCol.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        privilegeCol.setCellValueFactory(new PropertyValueFactory<User, String>("privilege"));
        occupationCol.setCellValueFactory(new PropertyValueFactory<User, String>("occupation"));
        chooseCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<User, String>, TableCell<User, String>> cellFactory
                = //
                new Callback<TableColumn<User, String>, TableCell<User, String>>() {
                    @Override
                    public TableCell call(final TableColumn<User, String> param) {
                        final TableCell<User, String> cell = new TableCell<User, String>() {

                            JFXButton btn = new JFXButton("Select");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        User e = getTableView().getItems().get(getIndex());
                                        onSelectUser(e);

                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };

        chooseCol.setCellFactory(cellFactory);

        searchUserResultTable.setItems(listUsers);
    }

    public void onSelectUser(User e) {
        usernameField.setEditable(false);
        userLabel.setText("Edit User");
        editedUser = e;
        newUser = false;
        usernameField.setText(e.getUname());
        //passwordField.setText(e.getPsword());
        fnameField.setText(e.getFirstName());
        lnameField.setText(e.getLastName());
        occupationField.setText(e.getOccupation());
        privilegeCombo.getSelectionModel().select(e.getPrivilege());
        if (ServiceRequestSingleton.getInstance().isInTable(e.getUname(), "LanguageInterpreter")) {
            languageCheck.setSelected(true);
        } else {
            languageCheck.setSelected(false);
        }
        if (ServiceRequestSingleton.getInstance().isInTable(e.getUname(), "ReligiousServices")) {
            religiousCheck.setSelected(true);
        } else {
            religiousCheck.setSelected(false);
        }
        if (ServiceRequestSingleton.getInstance().isInTable(e.getUname(), "SecurityRequest")) {
            securityCheck.setSelected(true);
        } else {
            securityCheck.setSelected(false);
        }

        editUserPane.setVisible(false);
        newUserPane.setVisible(true);
    }

    @FXML
    void onFilterType() {
        searchType = "none";
        filter = "none";
        try {
            if (filterType.getSelectionModel().getSelectedItem().equals("Priority")) {
                searchType = "Priority";
                availableTypes.setItems(priority);
                availableTypes.setVisible(true);
            } else if (filterType.getSelectionModel().getSelectedItem().equals("Status")) {
                searchType = "Status";
                availableTypes.setItems(status);
                availableTypes.setVisible(true);
            } else if (filterType.getSelectionModel().getSelectedItem().equals("Type")) {
                searchType = "Type";
                availableTypes.setItems(type);
                availableTypes.setVisible(true);
            }
        } catch (NullPointerException e) {
            searchType = "none";
        }
    }

    @FXML
    void onAvailableTypes() {
        try {
            filter = availableTypes.getSelectionModel().getSelectedItem().toString();
        } catch (NullPointerException e) {
            filter = "none";
        }
    }

    @FXML
    public void onEditUsers() {
        onCancelDirectionsEvent();
        setCancelMenuEvent();
        userTextField.clear();
        searchUserResultTable.getItems().clear();
        editUserPane.setVisible(true);
    }

    @FXML
    void onSearch() {
        ArrayList<ServiceRequest> requests = new ArrayList<>();
        try {
            if (filter.equalsIgnoreCase("none")) {
                ResultSet all = ServiceRequestSingleton.getInstance().getRequests();
                requests = ServiceRequestSingleton.getInstance().resultSetToServiceRequest(all);
                all.close();
            } else {
                switch (searchType) {
                    case "Priority":
                        ResultSet rp = ServiceRequestSingleton.getInstance().getRequestsOfPriority(Integer.parseInt(filter));
                        requests = ServiceRequestSingleton.getInstance().resultSetToServiceRequest(rp);
                        rp.close();
                        break;

                    case "Status":
                        ResultSet rs = ServiceRequestSingleton.getInstance().getRequestsOfStatus(filter);
                        requests = ServiceRequestSingleton.getInstance().resultSetToServiceRequest(rs);
                        rs.close();
                        break;

                    case "Type":
                        ResultSet rt = ServiceRequestSingleton.getInstance().getRequestsOfType(filter);
                        requests = ServiceRequestSingleton.getInstance().resultSetToServiceRequest(rt);
                        rt.close();
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            searchResultTable.getItems().clear();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //TODO: put result of search into table
        ObservableList<ServiceRequest> listRequests;
        if (requests.size() < 1) {
            //TODO: indicate to user that there are no results
            return;
        } else {
            listRequests = FXCollections.observableArrayList(requests);
        }

        searchResultTable.setEditable(false);

        idNumberCol.setCellValueFactory(new PropertyValueFactory<ServiceRequest, Integer>("id"));
        requestTypeCol.setCellValueFactory(new PropertyValueFactory<ServiceRequest, String>("type"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<ServiceRequest, String>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<ServiceRequest, String>("lastName"));
        destinationCol.setCellValueFactory(new PropertyValueFactory<ServiceRequest, String>("location"));
        requestPriorityCol.setCellValueFactory(new PropertyValueFactory<ServiceRequest, Integer>("priority"));
        theStatusCol.setCellValueFactory(new PropertyValueFactory<ServiceRequest, String>("status"));
        btnsCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<ServiceRequest, String>, TableCell<ServiceRequest, String>> cellFactory
                = //
                new Callback<TableColumn<ServiceRequest, String>, TableCell<ServiceRequest, String>>() {
                    @Override
                    public TableCell call(final TableColumn<ServiceRequest, String> param) {
                        final TableCell<ServiceRequest, String> cell = new TableCell<ServiceRequest, String>() {

                            JFXButton btn = new JFXButton("Select");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        ServiceRequest s = getTableView().getItems().get(getIndex());
                                        onSelect(s);
                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };

        btnsCol.setCellFactory(cellFactory);

        searchResultTable.setItems(listRequests);

        ServiceRequestSingleton.getInstance().setSearch(filter, searchType);
    }

    public void onSelect(ServiceRequest s) {
        ServiceRequestSingleton.getInstance().setPopUpRequest(s);
        serviceRequestPopUp = s;
        typeLabel.setText("Type: " + s.getType());
        idLabel.setText("Service Request #" + s.getId());
        firstNameLabel.setText("First Name: " + s.getFirstName());
        lastNameLabel.setText("Last Name: " + s.getLastName());
        locationLabel.setText(s.getLocation());
        statusLabel.setText(s.getStatus());
        instructionsTextArea.setText(s.getDescription());
        instructionsTextArea.setEditable(false);
        if (s.getStatus().equalsIgnoreCase("Complete")) {
            completeCheck.setSelected(true);
        } else {
            completeCheck.setSelected(false);
        }

        if (serviceRequestPopUp.getStatus().equals("Complete")) {
            completedByLabel.setVisible(true);
            usernameLabel.setVisible(true);
            usernameLabel.setText(serviceRequestPopUp.getCompletedBy());
        }
        searchPane.setVisible(false);
        editRequestPane.setVisible(true);
    }

    @FXML
    void onClear() {
        availableTypes.setVisible(false);
        availableTypes.valueProperty().set(null);
        filterType.valueProperty().set(null);
        searchType = "none";
        filter = "none";
        try {
            searchResultTable.getItems().clear();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        ServiceRequestSingleton.getInstance().setSearchNull();
    }

    @FXML
    void onCancelSearch() {
        searchPane.setVisible(false);
    }

    public void onCancelEdit() {
        editRequestPane.setVisible(false);
        searchPane.setVisible(true);
    }

    public void onSubmitEdit() {
        if (completeCheck.isSelected() && !serviceRequestPopUp.getStatus().equalsIgnoreCase("Complete")) {
            serviceRequestPopUp.setStatus("Complete");
            serviceRequestPopUp.setCompletedBy(PermissionSingleton.getInstance().getCurrUser());
            ServiceRequestSingleton.getInstance().updateCompletedBy(serviceRequestPopUp);
            ServiceRequestSingleton.getInstance().updateStatus(serviceRequestPopUp);
        }
        if (usernameSearch.getText() != null && !usernameSearch.getText().trim().isEmpty()) {
            ServiceRequestSingleton.getInstance().assignTo(usernameSearch.getText(), serviceRequestPopUp);
        }
        usernameSearch.setText("");
        editRequestPane.setVisible(false);
        searchPane.setVisible(true);
        onSearch();
    }


    @FXML
    private void onSubmitUser() {
        String username;
        if (newUser) {
            username = usernameField.getText();
        } else {
            username = editedUser.getUname();
        }
        String password = passwordField.getText();
        String firstName = fnameField.getText();
        String lastName = lnameField.getText();
        String occupation = occupationField.getText();
        boolean languageServices = languageCheck.isSelected();
        boolean religiousServices = religiousCheck.isSelected();
        boolean securityRequest = securityCheck.isSelected();

        User temp = new User(username, password, firstName, lastName, privilegeChoice, occupation);
        if (newUser) {
            PermissionSingleton.getInstance().addUser(temp);
        } else {
            PermissionSingleton.getInstance().updateUser(temp);
        }

        if (ServiceRequestSingleton.getInstance().isInTable(username, "LanguageInterpreter")) {
            if (!languageServices) {
                ServiceRequestSingleton.getInstance().removeUsernameLanguageInterpreter(username);
            }
        } else {
            if (languageServices) {
                ServiceRequestSingleton.getInstance().addUsernameLanguageInterpreter(username);
            }
        }

        if (ServiceRequestSingleton.getInstance().isInTable(username, "ReligiousServices")) {
            if (!religiousServices) {
                ServiceRequestSingleton.getInstance().removeUsernameReligiousServices(username);
            }
        } else {
            if (religiousServices) {
                ServiceRequestSingleton.getInstance().addUsernameReligiousServices(username);
            }
        }

        if (ServiceRequestSingleton.getInstance().isInTable(username, "SecurityRequest")) {
            if (!securityRequest) {
                ServiceRequestSingleton.getInstance().removeUsernameSecurityRequest(username);
            }
        } else {
            if (securityRequest) {
                ServiceRequestSingleton.getInstance().addUsernameSecurityRequest(username);
            }
        }
        newUserPane.setVisible(false);
        onEditUsers();
    }

    @FXML
    private void onPrivilegeBox() {
        try {
            privilegeChoice = privilegeCombo.getSelectionModel().getSelectedItem().toString();
        } catch (NullPointerException e) {
            privilegeChoice = "Guest";
        }
    }

    @FXML
    public void onCancelUser() {
        newUserPane.setVisible(false);
        editUserPane.setVisible(true);
        onEditUsers();
    }


    ////////////////////////////////////////
    //                                    //
    //       Language Interpreter         //
    //                                    //
    ////////////////////////////////////////

    @FXML
    private void onSearchServiceRequest() {
        filter = "none";
        searchType = "none";

        filterType.getItems().addAll(filterOptions);

        String lastSearch = ServiceRequestSingleton.getInstance().getLastSearch();
        String lastFilter = ServiceRequestSingleton.getInstance().getLastFilter();
        if (lastSearch != null && lastFilter != null) {
            searchType = lastSearch;
            filter = lastFilter;
        }
        onSearch();
        searchPane.setVisible(true);
        onCancelDirectionsEvent();
        setCancelMenuEvent();
    }

    @FXML
    private void onLanguageInterpreter() {
        onCancelDirectionsEvent();
        setCancelMenuEvent();
        languageInterpreterPane.setVisible(true);
        serviceRequestList.animateList(false);
    }

    @FXML
    private void onReligiousServices() {
        onCancelDirectionsEvent();
        setCancelMenuEvent();
        religiousServicesPane.setVisible(true);
        serviceRequestList.animateList(false);
    }

    @FXML
    private void onSecurityRequest() {
        onCancelDirectionsEvent();
        setCancelMenuEvent();
        securityPane.setVisible(true);
        serviceRequestList.animateList(false);
    }

    @FXML
    void onSubmitLI() {
        int requiredFieldsEmpty = 0;
        String l;
        String first_name;
        String last_name;
        String location;
        String description;
        if (languageField.getText() == null || languageField.getText().trim().isEmpty()) {
            languageRequiredLI.setVisible(true);
            requiredFieldsEmpty++;
        }
        if (firstNameLanguage.getText() == null || firstNameLanguage.getText().trim().isEmpty()) {
            firstNameRequiredLI.setVisible(true);
            requiredFieldsEmpty++;
        }
        if (lastNameLanguage.getText() == null || lastNameLanguage.getText().trim().isEmpty()) {
            lastNameRequiredLI.setVisible(true);
            requiredFieldsEmpty++;
        }
        if (destinationLanguage.getText() == null || destinationLanguage.getText().trim().isEmpty()) {
            locationRequiredLI.setVisible(true);
            requiredFieldsEmpty++;
        }
        if (requiredFieldsEmpty > 0) {
            return;
        }

        if (instructionsLanguage.getText() == null || instructionsLanguage.getText().trim().isEmpty()) {
            description = "N/A";
        } else {
            description = instructionsLanguage.getText();
        }
        l = languageField.getText();
        first_name = firstNameLanguage.getText();
        last_name = lastNameLanguage.getText();
        location = destinationLanguage.getText();
        String new_description = l + "/////" + description;
        ServiceRequest request = new LanguageInterpreter(first_name, last_name, location, new_description, "Incomplete", 1, l);
        ServiceRequestSingleton.getInstance().sendServiceRequest(request);
        ServiceRequestSingleton.getInstance().addServiceRequest(request);
        languageInterpreterPane.setVisible(false);

    }

    @FXML
    void onCancelLI() {
        languageInterpreterPane.setVisible(false);
    }


    @FXML
    void onSubmitRS() {
        int requiredFieldsEmpty = 0;
        String r;
        String first_name;
        String last_name;
        String location;
        String description;
        if (religionField.getText() == null || religionField.getText().trim().isEmpty()) {
            religionRequiredRS.setVisible(true);
            requiredFieldsEmpty++;
        }
        if (firstNameRS.getText() == null || firstNameRS.getText().trim().isEmpty()) {
            firstNameRequiredRS.setVisible(true);
            requiredFieldsEmpty++;
        }
        if (lastNameRS.getText() == null || lastNameRS.getText().trim().isEmpty()) {
            lastNameRequiredRS.setVisible(true);
            requiredFieldsEmpty++;
        }
        if (destinationRS.getText() == null || destinationRS.getText().trim().isEmpty()) {
            locationRequiredRS.setVisible(true);
            requiredFieldsEmpty++;
        }
        if (requiredFieldsEmpty > 0) {
            return;
        }

        if (instructionsRS.getText() == null || instructionsRS.getText().trim().isEmpty()) {
            description = "N/A";
        } else {
            description = instructionsRS.getText();
        }
        r = religionField.getText();
        first_name = firstNameRS.getText();
        last_name = lastNameRS.getText();
        location = destinationRS.getText();
        String new_description = r + "/////" + description + "\n";
        ServiceRequest request = new ReligiousServices(first_name, last_name, location, new_description, "Incomplete", 1, r);
        ServiceRequestSingleton.getInstance().sendServiceRequest(request);
        ServiceRequestSingleton.getInstance().addServiceRequest(request);
        religiousServicesPane.setVisible(false);
    }

    @FXML
    void onCancelRS() {
        religiousServicesPane.setVisible(false);
    }


    @FXML
    private void onSubmitSecurity() {
        if (securityLocationField.getText() == null || securityLocationField.getText().trim().isEmpty()) {
            securityLocationRequired.setVisible(true);
            return;
        }
        String location = securityLocationField.getText();
        String description = securityTextArea.getText();
        String status = "Incomplete";
        RadioButton selected = (RadioButton) securityToggle.getSelectedToggle();
        int priority = Integer.parseInt(selected.getText());
        SecurityRequest sec = new SecurityRequest(location, description, status, priority);

        ServiceRequestSingleton.getInstance().sendServiceRequest(sec);
        ServiceRequestSingleton.getInstance().addServiceRequest(sec);
        securityPane.setVisible(false);
    }

    @FXML
    private void onCancelSecurity() {
        securityPane.setVisible(false);
    }

    @Override
    public void onSrcNodeSelected(Node node) {

    }

    @Override
    public void onDstNodeSelected(Node node) {

    }
}