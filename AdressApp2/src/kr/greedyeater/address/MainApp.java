package kr.greedyeater.address;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kr.greedyeater.address.model.Person;
import kr.greedyeater.address.model.PersonListWrapper;
import kr.greedyeater.address.view.BirthdayStatisticsController;
import kr.greedyeater.address.view.PersonEditDialogController;
import kr.greedyeater.address.view.PersonOverviewController;
import kr.greedyeater.address.view.RootLayoutController;

public class MainApp extends Application {
	
    private Stage primaryStage;
    private BorderPane rootLayout;
    /**
     * The data as an observable list of Persons.
     */
    private ObservableList<Person> personData = FXCollections.observableArrayList();

    /**
     * Constructor
     * 
     */
    public MainApp() {
        // Add some sample data
        personData.add(new Person("Hans", "Muster"));
        personData.add(new Person("Ruth", "Mueller"));
        personData.add(new Person("Heinz", "Kurz"));
        personData.add(new Person("Cornelia", "Meier"));
        personData.add(new Person("Werner", "Meyer"));
        personData.add(new Person("Lydia", "Kunz"));
        personData.add(new Person("Anna", "Best"));
        personData.add(new Person("Stefan", "Meier"));
        personData.add(new Person("Martin", "Mueller"));
    }
    
    /**
     * Returns the data as an observable list of Persons. 
     * @return
     */
    public ObservableList<Person> getPersonData() {
        return personData;
    }
    
    @Override
	public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AddressApp");
        
        initRootLayout();

        showPersonOverview();		
	}
	
    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);
            
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Try to load last opened person file.
        File file = getPersonFilePath();
        if (file != null) {
            loadPersonDataFromFile(file);
        }        
    }
    
    /**
     * Shows the person overview inside the root layout.
     */
    public void showPersonOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/PersonOverview.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(personOverview);
            
            // Give the controller access to the main app.
            PersonOverviewController controller = loader.getController();
            controller.setMainApp(this);
            
        } catch (IOException e) {
        	
            e.printStackTrace();
        }
    }
    /**
     * Opens a dialog to edit details for the specified person. If the user
     * clicks OK, the changes are saved into the provided person object and true
     * is returned.
     * 
     * @param person the person object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showPersonEditDialog(Person person) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/PersonEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            
            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Person");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            
            // Set the person into the controller.
            PersonEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPerson(person);
            
            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
            
            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }   
    
	public static void main(String[] args) {
		launch(args);
	}
	/**
	 * Returns the person file preference, i.e. the file that was last opened.
	 * The preference is read from the OS specific registry. If no such
	 * preference can be found, null is returned.
	 * 
	 * @return
	 */
	public File getPersonFilePath() {
		// Returns the preference node from the calling user's preference tree 
		// that is associated (by convention) with the specified class's package.
		// -> 클래스와 관련된 Preference 노드를 리턴한다. (Preference Tree를 내부적으로 가지고 있음)
	    Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    
	    // Returns the value associated with the specified key in this preference node. 
	    // Preference Node 의 get 메서드를 이용해서 filePath 라는 이름의  속성의 데이터를 반환한다.
	    String filePath = prefs.get("filePath", null);
	    if (filePath != null) {
	        return new File(filePath);
	    } else {
	        return null;
	    }
	}
	
	/**
	 * Sets the file path of the currently loaded file. The path is persisted in
	 * the OS specific registry.
	 * 
	 * @param file the file or null to remove the path
	 */
	public void setPersonFilePath(File file) {
	    Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    if (file != null) {
	        prefs.put("filePath", file.getPath());

	        // Update the stage title.
	        primaryStage.setTitle("AddressApp - " + file.getName());
	    } else {
	        prefs.remove("filePath");

	        // Update the stage title.
	        primaryStage.setTitle("AddressApp");
	    }
	}
	/**
	 * Loads person data from the specified file. The current person data will
	 * be replaced.
	 * 
	 * @param file
	 */
	public void loadPersonDataFromFile(File file) {
	    try {
	    	// The JAXBContext class provides the client's entry point to the JAXB API.
	    	// client 에게 entry point 를 제공한다. 
	        JAXBContext context = JAXBContext
	        		// The client application must supply a list of classes that the new context object needs to recognize.
	        		/*
	        		 * @XmlRootElement(name ="persons") // Defines the name of the root element.
	        		 * public class PersonListWrapper {
	        		 * 	private List<Person> persons;
	        		 * 		@XmlElement(name = "person") // is an optional name we can specify for the element
	        		 * 		public List<Person> getPersons(){
	        		 * 			return persons;
	        		 * 		}
	        		 * 		
	        		 * 		public void setPersons(List<Person> persons){
	        		 * 			this.persons = persons;
	        		 * 		}
	        		 * }
	        		 * 위와 같은 형태로 정의를 한 뒤에 아래의 구문에서 정의한 클래스의 Definition 을 전달한다.  
	        		 */
	                .newInstance(PersonListWrapper.class);
	        // 추출한 context 를 통해 Unmarshaller 객체를 생성한다. 
	        // Create an Unmarshaller object that can be used to convert XML data into a java content tree.
	        Unmarshaller um = context.createUnmarshaller();
	        
	        // Reading XML from the file and unmarshalling.
	        PersonListWrapper wrapper = (PersonListWrapper) um.unmarshal(file);
	        
	        // Removes all of the elements from this list (optional operation). 
	        // The list will be empty after this call returns.
	        personData.clear();
	        // Appends all of the elements in the specified collection to the end of this list, 
	        // in the order that they are returned by the specified collection's iterator (optional operation). 
	        personData.addAll(wrapper.getPersons());
	        
	        // Save the file path to the registry.
	        setPersonFilePath(file);
	        
	    } catch (Exception e) { // catches ANY exception
	        Alert alert = new Alert(AlertType.ERROR);
	        alert.setTitle("Error");
	        alert.setHeaderText("Could not load data");
	        alert.setContentText("Could not load data from file:\n" + file.getPath());

	        alert.showAndWait();
	    }
	}

	/**
	 * Saves the current person data to the specified file.
	 * 
	 * @param file
	 */
	public void savePersonDataToFile(File file) {
	    try {
	        JAXBContext context = JAXBContext
	                .newInstance(PersonListWrapper.class);
	        // Appends all of the elements in the specified collection to the end of this list, 
	        // in the order that they are returned by the specified collection's iterator (optional operation). 
	        Marshaller m = context.createMarshaller();
	        
	        // Set the particular property in the underlying implementation of Marshaller.
	        // This method can only be used to set one of the standard JAXB defined properties 
	        // above or a provider specific property.
	        // Marshaller 객체의 Property 를 설정하는 구문이고, 현재 설정을 추가하는 부분은 
	        // linefeed 와 indentation 을 Marshalling 시에 반영하라는 의미이다. 
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	        // Wrapping our person data.
	        PersonListWrapper wrapper = new PersonListWrapper();
	        wrapper.setPersons(personData);
	        
	        // Marshalling and saving XML to the file.
	        m.marshal(wrapper, file);

	        // Save the file path to the registry.
	        setPersonFilePath(file);
	    } catch (Exception e) { // catches ANY exception
	        Alert alert = new Alert(AlertType.ERROR);
	        alert.setTitle("Error");
	        alert.setHeaderText("Could not save data");
	        alert.setContentText("Could not save data to file:\n" + file.getPath());

	        alert.showAndWait();
	    }
	}
	/**
	 * Opens a dialog to show birthday statistics.
	 */
	public void showBirthdayStatistics() {
	    try {
	        // Load the fxml file and create a new stage for the popup.
	        FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(MainApp.class.getResource("view/BirthdayStatistics.fxml"));
	        AnchorPane page = (AnchorPane) loader.load();
	        Stage dialogStage = new Stage();
	        dialogStage.setTitle("Birthday Statistics");
	        dialogStage.initModality(Modality.WINDOW_MODAL);
	        dialogStage.initOwner(primaryStage);
	        Scene scene = new Scene(page);
	        dialogStage.setScene(scene);

	        // Set the persons into the controller.
	        BirthdayStatisticsController controller = loader.getController();
	        controller.setPersonData(personData);

	        dialogStage.show();

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
