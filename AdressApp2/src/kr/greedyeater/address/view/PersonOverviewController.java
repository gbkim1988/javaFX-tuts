package kr.greedyeater.address.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import kr.greedyeater.address.MainApp;
import kr.greedyeater.address.model.Person;
import kr.greedyeater.util.DateUtil;

public class PersonOverviewController {
	  @FXML
	    private TableView<Person> personTable;
	    @FXML
	    private TableColumn<Person, String> firstNameColumn;
	    @FXML
	    private TableColumn<Person, String> lastNameColumn;

	    @FXML
	    private Label firstNameLabel;
	    @FXML
	    private Label lastNameLabel;
	    @FXML
	    private Label streetLabel;
	    @FXML
	    private Label postalCodeLabel;
	    @FXML
	    private Label cityLabel;
	    @FXML
	    private Label birthdayLabel;

	    // Reference to the main application.
	    private MainApp mainApp;
	    
	    /**
	     * The constructor.
	     * The constructor is called before the initialize() method.
	     */
	    public PersonOverviewController() {
	    }
	    
	    /**
	     * Initializes the controller class. This method is automatically called
	     * after the fxml file has been loaded.
	     */
	    @FXML
	    private void initialize() {
	    	
	        // Initialize the person table with the two columns.
	        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
	        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
	        
	        // Clear person details.
	        showPersonDetails(null);

	        // Listen for selection changes and show the person details when changed.
	        personTable.getSelectionModel().selectedItemProperty().addListener(
	                (observable, oldValue, newValue) -> showPersonDetails(newValue) );
	    }
	    
	    /**
	     * Is called by the main application to give a reference back to itself.
	     * 
	     * @param mainApp
	     */
	    public void setMainApp(MainApp mainApp) {
	        this.mainApp = mainApp;

	        // Add observable list data to the table
	        personTable.setItems(mainApp.getPersonData());
	    }	    
	    
	    /**
	     * Fills all text fields to show details about the person.
	     * If the specified person is null, all text fields are cleared.
	     * 
	     * @param person the person or null
	     */
	    private void showPersonDetails(Person person) {
	        if (person != null) {
	            // Fill the labels with info from the person object.
	            firstNameLabel.setText(person.getFirstName());
	            lastNameLabel.setText(person.getLastName());
	            streetLabel.setText(person.getStreet());
	            postalCodeLabel.setText(Integer.toString(person.getPostalCode()));
	            cityLabel.setText(person.getCity());

	            // TODO: We need a way to convert the birthday into a String! 
	            // birthdayLabel.setText(...);
	            birthdayLabel.setText(DateUtil.format(person.getBirthday()));
	        } else {
	            // Person is null, remove all the text.
	            firstNameLabel.setText("");
	            lastNameLabel.setText("");
	            streetLabel.setText("");
	            postalCodeLabel.setText("");
	            cityLabel.setText("");
	            birthdayLabel.setText("");
	        }
	    }	    
	    /**
	     * Called when the user clicks on the delete button.
	     */
	    @FXML
	    private void handleDeletePerson() {
	    	/**
	    	 *  아래의 코드는 문제를 일으킬 소지가 있다. selectedIndex 가 -1 인 경우이다. 
	    	 *  이러한 에러를 처리하는 방법은 -1 가 아닌 경우 정해진 행위를 하는 것이다. 
	    	 * */
	        int selectedIndex = personTable.getSelectionModel().getSelectedIndex();
	        // if ( selectedIndex != -1 ) 
	        if (selectedIndex >= 0 )
	        	personTable.getItems().remove(selectedIndex);
	        else{
	            // Nothing selected.
	            Alert alert = new Alert(AlertType.WARNING);
	            alert.initOwner(mainApp.getPrimaryStage());
	            alert.setTitle("No Selection");
	            alert.setHeaderText("No Person Selected");
	            alert.setContentText("Please select a person in the table.");
	            
	            alert.showAndWait();
	        }
	    }
	    /**
	     * Called when the user clicks on the new button.
	     */
	    @FXML
	    private void handleNewPerson() {
	    	Person tempPerson = new Person();
	    	boolean okClicked = mainApp.showPersonEditDialog(tempPerson);
	    	if ( okClicked ){
	    		mainApp.getPersonData().add(tempPerson);
	    	}
	    }
	    
	    /**
	     * Called when the user clicks on the edit button.
	     */
	    @FXML
	    private void handleEditPerson() {
	    	Person tempPerson = personTable.getSelectionModel().getSelectedItem();
	    	if ( tempPerson != null ){
	    		boolean okClicked = mainApp.showPersonEditDialog(tempPerson);
	    		if (okClicked) {
	    			showPersonDetails(tempPerson);
	    		}
	    	}else{
	            // Nothing selected.
	            Alert alert = new Alert(AlertType.WARNING);
	            alert.initOwner(mainApp.getPrimaryStage());
	            alert.setTitle("No Selection");
	            alert.setHeaderText("No Person Selected");
	            alert.setContentText("Please select a person in the table.");

	            alert.showAndWait();
	    	}
	    }
}
