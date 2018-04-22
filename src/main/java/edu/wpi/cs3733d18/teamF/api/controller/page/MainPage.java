package edu.wpi.cs3733d18.teamF.api.controller.page;

import com.jfoenix.controls.*;
import com.twilio.Twilio;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import edu.wpi.cs3733d18.teamF.api.controller.PaneSwitcher;
import edu.wpi.cs3733d18.teamF.api.controller.UserSingleton;
import edu.wpi.cs3733d18.teamF.api.controller.SwitchableController;
import edu.wpi.cs3733d18.teamF.api.controller.User;
import edu.wpi.cs3733d18.teamF.api.db.DatabaseSingleton;
import edu.wpi.cs3733d18.teamF.api.gfx.PaneVoiceController;
import edu.wpi.cs3733d18.teamF.api.notifications.TwilioHandlerSingleton;
import edu.wpi.cs3733d18.teamF.api.sr.*;
import edu.wpi.cs3733d18.teamF.api.voice.VoiceCommandVerification;
import edu.wpi.cs3733d18.teamF.api.voice.VoiceLauncher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MainPage implements SwitchableController, Observer {
    private final ObservableList<String> priority = FXCollections.observableArrayList("0", "1", "2", "3", "4", "5");
    private final ObservableList<String> status = FXCollections.observableArrayList("Incomplete", "In Progress", "Complete");
    private final ObservableList<String> type = FXCollections.observableArrayList("Language Interpreter", "Religious Services", "Security Request");
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
    public Label typeLabel, idLabel, fullNameLabel, locationLabel, statusLabel, completedByLabel, usernameLabel;
    @FXML
    public TextArea instructionsTextArea;
    PaneSwitcher switcher;
    @FXML
    AnchorPane languageInterpreterPane;
    @FXML
    JFXTextField languageField;
    @FXML
    JFXTextField firstNameLanguage;
    @FXML
    JFXTextField lastNameLanguage;
    @FXML
    JFXTextField destinationLanguage;
    @FXML
    JFXTextArea instructionsLanguage;
    @FXML
    Label languageRequiredLI;
    @FXML
    Label lastNameRequiredLI;
    @FXML
    Label firstNameRequiredLI;
    @FXML
    Label locationRequiredLI;
    @FXML
    ToggleGroup securityToggle;
    @FXML
    JFXTextField securityLocationField;
    @FXML
    JFXTextArea securityTextArea;
    @FXML
    AnchorPane securityPane;
    @FXML
    GridPane serviceRequestPane;
    @FXML
    JFXTabPane serviceRequestTabPane;
    @FXML
    TextField religionField, firstNameRS, lastNameRS, destinationRS;
    @FXML
    TextArea instructionsRS;
    @FXML
    Label religionRequiredRS, firstNameRequiredRS, lastNameRequiredRS, locationRequiredRS;
    String lastSearch = ServiceRequestSingleton.getInstance().getLastSearch();
    String lastFilter = ServiceRequestSingleton.getInstance().getLastFilter();
    /////////////////////////////////////////
    //                                     //
    //           Service Request           //
    //                                     //
    /////////////////////////////////////////
    private ServiceRequest serviceRequestPopUp;
    @FXML
    private AnchorPane editRequestPane;
    /////////////////////////////////
    //       Search Services       //
    /////////////////////////////////
    private String searchType;
    private String filter;
    @FXML
    private AnchorPane searchPane;
    @FXML
    private JFXTextField usernameSearch;
    @FXML
    private JFXCheckBox completeCheck;
    @FXML
    private AnchorPane religiousServicesPane;
    @FXML
    private Label securityLocationRequired;

    @FXML
    private FontAwesomeIconView closeBtn;
    @FXML
    private HBox mainPane;
    @FXML
    private JFXButton languageInterpreterBtn;
    @FXML
    private JFXButton religiousServicesBtn;
    @FXML
    private JFXButton securityRequestBtn;

    private VoiceCommandVerification voice;
    private PaneVoiceController paneVoiceController;

    @FXML
    private Pane voicePane;


    ////////////////////////
    //                    //
    //   User management  //
    //                    //
    ////////////////////////
    @FXML
    private JFXListView usernameList;

    @Override
    public void initialize(PaneSwitcher switcher) {
        this.switcher = switcher;
        VoiceCommandVerification voice = new VoiceCommandVerification();
        voice.addObserver(this);
        VoiceLauncher.getInstance().addObserver(voice);

        paneVoiceController = new PaneVoiceController(voicePane);

        serviceRequestPane.setPrefSize(ServiceRequestSingleton.getInstance().getPrefWidth(), ServiceRequestSingleton.getInstance().getPrefLength());

        languageInterpreterBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            languageInterpreterPane.toFront();
            if(ServiceRequestSingleton.getInstance().getDestNodeID() != null){
                destinationLanguage.setText(ServiceRequestSingleton.getInstance().getDestNodeID());
            }
        });

        religiousServicesBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            religiousServicesPane.toFront();
            if(ServiceRequestSingleton.getInstance().getDestNodeID() != null){
                destinationRS.setText(ServiceRequestSingleton.getInstance().getDestNodeID());
            }
        });
        securityRequestBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            securityPane.toFront();
            if(ServiceRequestSingleton.getInstance().getDestNodeID() != null){
                securityLocationField.setText(ServiceRequestSingleton.getInstance().getDestNodeID());
            }
        });


        closeBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            VoiceLauncher.getInstance().terminate();
            switcher.terminate();
        });

        filter = "none";
        searchType = "none";
        if (lastSearch != null && lastFilter != null) {
            searchType = lastSearch;
            filter = lastFilter;
        }

        filterType.getItems().addAll(filterOptions);

        usernameSearch.setOnKeyTyped((KeyEvent e) -> {
            String input = usernameSearch.getText();
            input = input.concat("" + e.getCharacter());
            autoComplete(input, usernameList, "HUser", "username");
        });

        onSearch();
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

    @FXML
    private void setAssignTo(){
        String selection = usernameList.getSelectionModel().getSelectedItem().toString();
        usernameSearch.setText(selection);
        usernameList.setVisible(false);
    }

    private ArrayList<User> autoCompleteUserSearch(String input) {
        ArrayList<User> autoCompleteUser = new ArrayList<>();
        if (input.length() >= 0) {
            String sql = "SELECT * FROM HUser";
            try {
                ResultSet resultSet = DatabaseSingleton.getInstance().getDbHandler().runQuery(sql);
                while (resultSet.next()) {

                    String username = resultSet.getString(1);
                    String firstname = resultSet.getString(2);
                    String lastname = resultSet.getString(3);
                    String occupation = resultSet.getString(4);
                    User temp = new User(username, firstname, lastname, occupation);
                    String searchString = username + firstname + lastname +  occupation;
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
        fullNameLabel.setText(s.getFirstName() + " " + s.getLastName());
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
        editRequestPane.toFront();
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
        onSearch();
    }

    @FXML
    public void onSubmitEdit() {
        if (usernameSearch.getText() != null && !usernameSearch.getText().trim().isEmpty()) {
            ServiceRequestSingleton.getInstance().assignTo(usernameSearch.getText(), serviceRequestPopUp);
        }
        if (completeCheck.isSelected() && !serviceRequestPopUp.getStatus().equalsIgnoreCase("Complete")) {
            serviceRequestPopUp.setStatus("Complete");
            serviceRequestPopUp.setCompletedBy(UserSingleton.getInstance().getCurrUser());
            ServiceRequestSingleton.getInstance().updateCompletedBy(serviceRequestPopUp);
            ServiceRequestSingleton.getInstance().updateStatus(serviceRequestPopUp);
        }
        usernameSearch.setText("");
        editRequestPane.toBack();
        onSearch();
    }

    @FXML
    private void onCancelEdit(){
        editRequestPane.toBack();
    }

    ////////////////////////
    //                    //
    //       LI           //
    //                    //
    ////////////////////////

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
        TwilioHandlerSingleton.getInstance().sendMessage("\n" + first_name + " " + last_name + " needs a " + l + " interpreter at " + location + ".\nAdditional Details: " + description);
        languageInterpreterPane.toBack();
        clearLanguage();
    }

    @FXML
    void onCancelLI() {
        languageInterpreterPane.toBack();
        clearLanguage();
    }

    private void clearLanguage() {
        languageField.clear();
        if (languageRequiredLI.isVisible()) {
            languageRequiredLI.setVisible(false);
        }
        firstNameLanguage.clear();
        if (firstNameRequiredLI.isVisible()) {
            firstNameRequiredLI.setVisible(false);
        }
        lastNameLanguage.clear();
        if (lastNameRequiredLI.isVisible()) {
            lastNameRequiredLI.setVisible(false);
        }
        destinationLanguage.clear();
        if (locationRequiredLI.isVisible()) {
            locationRequiredLI.setVisible(false);
        }
        instructionsLanguage.clear();
    }


    ////////////////////////
    //                    //
    //       RS           //
    //                    //
    ////////////////////////
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
        TwilioHandlerSingleton.getInstance().sendMessage("\n" + first_name + " " + last_name + " needs " + r + " services at " + location + ".\nAdditional Details: " + description);
        religiousServicesPane.toBack();
        clearReligious();
    }

    @FXML
    void onCancelRS() {
        religiousServicesPane.toBack();
        clearReligious();
    }

    private void clearReligious() {
        religionField.clear();
        if (religionRequiredRS.isVisible()) {
            religionRequiredRS.setVisible(false);
        }
        firstNameRS.clear();
        if (firstNameRequiredRS.isVisible()) {
            firstNameRequiredRS.setVisible(false);
        }
        lastNameRS.clear();
        if (lastNameRequiredRS.isVisible()) {
            lastNameRequiredRS.setVisible(false);
        }
        destinationRS.clear();
        if (locationRequiredRS.isVisible()) {
            locationRequiredRS.setVisible(false);
        }
        instructionsRS.clear();
    }

    ////////////////////////
    //                    //
    //       SR           //
    //                    //
    ////////////////////////

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
        TwilioHandlerSingleton.getInstance().sendMessage("\nSecurity is required at " + location + ".\nAdditional Details: " + description);
        securityPane.toBack();
        clearSecurity();
    }

    @FXML
    private void onCancelSecurity() {
        securityPane.toBack();
        clearSecurity();
    }

    private void clearSecurity() {
        securityLocationField.clear();
        securityTextArea.clear();
        if (securityLocationRequired.isVisible()) {
            securityLocationRequired.setVisible(false);
        }
    }


    @FXML
    private void onCreateNewServiceRequest() {
        mainPane.toFront();
        clearLanguage();
        clearReligious();
        clearSecurity();
    }

    @FXML
    private void onSearchServiceRequest() {
        onClear();
        onSearch();
    }


    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Observer caught");
        if (!(o instanceof VoiceCommandVerification)) {
            return;
        }
        System.out.println("Observer Passed");
        System.out.println("arg = " + arg);

        if (arg instanceof String) {
            if(arg.toString().equalsIgnoreCase("ACTIVATE")) {
                paneVoiceController.setVisibility(true);
            }
            else if(arg.toString().equalsIgnoreCase("Language")){
                languageInterpreterPane.toFront();
            }else if(arg.toString().equalsIgnoreCase("Religious")){
                religiousServicesPane.toFront();
            }else if(arg.toString().equalsIgnoreCase("Security")){
                securityPane.toFront();
            }else if(arg.toString().equalsIgnoreCase("Create")){
                SingleSelectionModel<Tab> selectionModel = serviceRequestTabPane.getSelectionModel();
                selectionModel.select(0);
            }else if(arg.toString().equalsIgnoreCase("Search")){
                SingleSelectionModel<Tab> selectionModel = serviceRequestTabPane.getSelectionModel();
                selectionModel.select(1);
            }else if(arg.toString().equalsIgnoreCase("User")){
                SingleSelectionModel<Tab> selectionModel = serviceRequestTabPane.getSelectionModel();
                selectionModel.select(2);
            }
        }
    }
}
