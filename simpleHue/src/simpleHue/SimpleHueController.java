/**
 * @author Shaun Parkison
 * Simple driver program for turning 
 * Philips Hue light(s) on/off
 * 
 * Controller class for the app GUI
 */

package simpleHue;

import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import simpleHue.util.ColorToXY;
import simpleHue.util.VarFactory;

import com.google.gson.*;

public class SimpleHueController implements Initializable{

	/*
	 * Class Variables
	 */
	private static final boolean debug = false; // Set debugging (outputs to console currently, need to save to log file...)
	private ObservableList<HueLight> lights = FXCollections.observableArrayList();
	private VarFactory vf = VarFactory.getInstance(); // Holds application variables
	private static String bridgeUrl; // The discovered Hue bridge URL

	/*
	 * JavaFX Variables
	 */
	@FXML
	private AnchorPane simple_hue; // Main AnchorPane
	@FXML
	private AnchorPane simple_hue_about; // About AnchorPane
	@FXML
	private AnchorPane start_panel; // Start pane. Hide/Show depending on logged in status 
	@FXML
	private AnchorPane about_panel; // Start pane. Hide/Show depending on logged in status
	@FXML
	private AnchorPane connecting_panel; // Logging in pane. Hide/Show depending on logged in status
	@FXML
	private AnchorPane logged_in; // Logged in pane. Displayed when successfully logged into Hue bridge
	@FXML
	private TextArea message_box; // Text area to display login status
	@FXML
	private TextArea error_panel; // Text area to display login status
	@FXML
	private Button start_button; // Button to start discovery and registration of simple hue client
	@FXML
	private Button save_config_button; // Button to save the configuration file
	@FXML
	private Label save_config_button_label; // Label to display config save status
	@FXML
	private Button about_button; // Button to show info window
	@FXML
	private Button about_close_button; // Button to hide info window
	@FXML
	private TableView<HueLight> lights_table; // Table for the discovered lights and their associated properties
	@FXML
	private TableColumn<HueLight, Integer> light_id;
	@FXML
	private TableColumn<HueLight, String> light_name;
	@FXML
	private TableColumn<HueLight, Boolean> light_enable_toggle;
	@FXML
	private TableColumn<HueLight, ColorPicker> light_on_color;

	@Override // This method is called by the FXMLLoader when initialization is complete
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		// Assert our elements are in place
		assert simple_hue != null : "fx:id=\"simple_hue\" was not injected: check your FXML file 'simple_hue.fxml'.";
		assert simple_hue_about != null : "fx:id=\"simple_hue_about\" was not injected: check your FXML file 'simple_hue.fxml'.";
		assert start_panel != null : "fx:id=\"start_panel\" was not injected: check your FXML file 'simple_hue.fxml'.";
		assert about_panel != null : "fx:id=\"about_panel\" was not injected: check your FXML file 'simple_hue.fxml'.";
		assert start_button != null : "fx:id=\"start_button\" was not injected: check your FXML file 'simple_hue.fxml'.";
		assert save_config_button != null : "fx:id=\"save_config_button\" was not injected: check your FXML file 'simple_hue.fxml'.";
		assert about_button != null : "fx:id=\"about_button\" was not injected: check your FXML file 'simple_hue.fxml'.";
		assert about_close_button != null : "fx:id=\"about_button\" was not injected: check your FXML file 'simple_hue.fxml'.";
		assert connecting_panel != null : "fx:id=\"connecting_panel\" was not injected: check your FXML file 'simple_hue.fxml'.";
		assert message_box != null : "fx:id=\"message_box\" was not injected: check your FXML file 'simple_hue.fxml'.";
		assert error_panel != null : "fx:id=\"error_panel\" was not injected: check your FXML file 'simple_hue.fxml'.";
		assert lights_table != null : "fx:id=\"lights_table\" was not injected: check your FXML file 'simple_hue.fxml'.";
		assert light_name != null : "fx:id=\"light_name\" was not injected: check your FXML file 'simple_hue.fxml'.";
		assert light_id != null : "fx:id=\"light_id\" was not injected: check your FXML file 'simple_hue.fxml'.";
		assert light_enable_toggle != null : "fx:id=\"light_enable_toggle\" was not injected: check your FXML file 'simple_hue.fxml'.";
		assert light_on_color != null : "fx:id=\"light_on_color\" was not injected: check your FXML file 'simple_hue.fxml'.";
		
		// Build the lights table view
		light_id.setCellValueFactory(new PropertyValueFactory<HueLight, Integer>("id"));
		light_name.setCellValueFactory(new PropertyValueFactory<HueLight, String>("name"));
		light_enable_toggle.setCellValueFactory(new PropertyValueFactory<HueLight, Boolean>("toggle"));
		light_on_color.setCellValueFactory(new PropertyValueFactory<HueLight, ColorPicker>("onColor"));
		
		// Attach the lights array to the table
		lights_table.setItems(lights);
	}

	/************************************************************
	 * JavaFX specific Actions
	 ************************************************************/

	/**
	 * Start button clicked action
	 * @param event
	 */
	public void getStartedClicked(ActionEvent event){

		if(debug)
			System.out.println("Start button pressed, beginning setup.");

		connecting_panel.setVisible(true);
		try {
			newConnect();
		} catch (Exception e) {
			message_box.clear();
			error_panel.clear();
			error_panel.setVisible(true);
			error_panel.setText("Error setting up connection to Hue bridge: " + e.getMessage());
			connecting_panel.setVisible(false);

			if(debug)
				System.out.println("Error setting up connection to Hue bridge");
		}
	}

	/**
	 * Show/Hide the about window
	 * @param event
	 * @throws IOException
	 */
	public void aboutButtonClicked(ActionEvent event) throws IOException{
		if(event.getSource() == about_button){
			if(about_panel.isVisible())
				about_panel.setVisible(false);
			else
				about_panel.setVisible(true);
		} else {
			about_panel.setVisible(false);
		}
	}

	/**
	 * Used to build out our configuration file
	 * after successful connection to Hue bridge
	 */
	public void saveConfigClicked(ActionEvent event){
		
		save_config_button_label.setText("");
		save_config_button.setDisable(true);
		
		if(debug)
			System.out.println("Light configuration:");
		
		HashMap<Integer, List<Double>> lightConfig = new HashMap<Integer, List<Double>>();
		
		for (HueLight light : lights_table.getItems()) {
			if(debug)
				System.out.println("Name: " + light.getName() + ", Toggled: " + light.getToggle().isSelected() + ", Color: " + light.getOnColor().getValue());
			if(light.getToggle().isSelected()){
				Color lightColor = Color.web(light.getOnColor().getValue().toString());
				lightConfig.put(light.getId(), ColorToXY.getRGBtoXY(lightColor));
			}
		}

		// Create the configuration file
		try {
			vf.getConfig().createNewFile();
			PrintWriter outFile = new PrintWriter(vf.getConfig());
			// Write the bridge URL out first
			outFile.println(bridgeUrl);
			// Write the desired lights, and their desired color out
			for(Integer lightId : lightConfig.keySet()){
				outFile.println(lightId + "\t" + lightConfig.get(lightId).get(0) + "\t" + lightConfig.get(lightId).get(1));
			}
			outFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Pause for a couple seconds to allow button transition to be visible
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		save_config_button.setDisable(false);
		save_config_button_label.setText("Configuration filed created successfully!");
		
	}

	/************************************************************
	 * Hue specific Actions
	 ************************************************************/

	/**
	 * Find new bridge
	 * @throws Exception
	 */
	private void newConnect() throws Exception{

		message_box.setText("Discovering your Hue bridge ID now...");
		message_box.appendText("\n*********************************************************");

		if(debug)
			System.out.println("Discovering your Hue bridge ID now...");

		final Timer timer = new Timer();
		TimerTask addUserLoop = new TimerTask(){
			int tries = 0;
			public void run(){
				try {					
					JsonObject response = HueRequest.GET("https://www.meethue.com/api/nupnp");

					if (response != null){
						timer.cancel();
						timer.purge();
						bridgeUrl = response.get("internalipaddress").getAsString();

						Platform.runLater(new Runnable(){
							@Override
							public void run(){
								message_box.appendText("\nDiscovery successful! Your bridge IP address is: " + bridgeUrl);
							}
						});

						if(debug)
							System.out.println("Hue bridge discovery success. Bridge IP: " + bridgeUrl);

						login();
					}
				} catch (Exception e) {

					Platform.runLater(new Runnable(){
						@Override
						public void run(){
							message_box.clear();
							error_panel.clear();
							error_panel.setVisible(true);
							error_panel.setText("Error attempting to discover Hue bridge. Please try again.");
							connecting_panel.setVisible(false);
						}
					});

					if(debug){
						System.out.println("Error attempting to discover Hue bridge: " + e.getMessage());
						e.printStackTrace();
					}

				}

				if (tries > 6){
					try {	
						timer.cancel();
						timer.purge();

						Platform.runLater(new Runnable(){
							@Override
							public void run(){
								message_box.clear();
								error_panel.clear();
								error_panel.setVisible(true);
								error_panel.setText("Connection to bridge timed out. Please try again.");
								connecting_panel.setVisible(false);
							}
						});

						if(debug)
							System.out.println("Connection to bridge timed out. Please try again.");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				tries++;
			}
		};
		timer.scheduleAtFixedRate(addUserLoop, 0, 1500);
	}

	/**
	 * Register new simple hue user
	 */
	private void registerUser(){

		Platform.runLater(new Runnable(){
			@Override
			public void run(){
				message_box.appendText("\nAttempting to register new \"simple hue\" user, please press the center button on your Hue bridge now.");
			}
		});

		if(debug)
			System.out.println("Attempting to register new \"simple hue\" user, please press the center button on your Hue bridge now");

		final Timer timer = new Timer();
		TimerTask addUserLoop = new TimerTask(){

			String body = "{\"vf.getDevicetype()\": \"" + vf.getDevicetype() + "\", \"vf.getUsername()\": \"" + vf.getUsername() + "\"}";
			int tries = 0;
			public void run(){
				// to register a new bridge user (user must press the link button)
				try {
					tries++;
					JsonObject response = HueRequest.POST("http://" + bridgeUrl + "/api/", body);

					Platform.runLater(new Runnable(){
						@Override
						public void run(){
							message_box.appendText(".");
						}
					});

					if (HueRequest.responseCheck(response) == "success"){

						Platform.runLater(new Runnable(){
							@Override
							public void run(){
								message_box.appendText("\nUser registration successful!");
							}
						});

						if(debug)
							System.out.println("User registration successful!");

						timer.cancel();
						timer.purge();
						login();
					} else if (tries > 20){
						// abort after several tries

						Platform.runLater(new Runnable(){
							@Override
							public void run(){
								message_box.clear();
								error_panel.clear();
								error_panel.setVisible(true);
								error_panel.setText("Creating user timed out. Please try again.");
								connecting_panel.setVisible(false);
							}
						});

						if(debug)
							System.out.println("Creating user timed out. Please try again.");

						timer.cancel();
						timer.purge();
					}
				} catch (Exception e) {
					Platform.runLater(new Runnable(){
						@Override
						public void run(){
							message_box.clear();
							error_panel.clear();
							error_panel.setVisible(true);
							error_panel.setText("Error creating user.");
							connecting_panel.setVisible(false);
						}
					});
					if(debug)
						System.out.println("Error creating user: " + e.getMessage());
				}
			}
		};
		timer.scheduleAtFixedRate(addUserLoop, 1500, 1500);
	}

	/**
	 * Login to the Hue bridge
	 * @throws Exception
	 */
	public void login() throws Exception{

		JsonObject response = HueRequest.GET("http://" + bridgeUrl + "/api/" + vf.getUsername());
		if (HueRequest.responseCheck(response) == "data"){
			getLights();
		}
		else if (HueRequest.responseCheck(response) == "error"){
			registerUser();
		}
	}

	/**
	 * Get info for the lights
	 * @throws Exception
	 */
	private void getLights() throws Exception{

		Platform.runLater(new Runnable(){
			@Override
			public void run(){
				message_box.appendText("\nHue bridge login successful, discovering lights now...");
			}
		});

		if(debug)
			System.out.println("Hue bridge login successful, discovering lights now...");

		JsonObject response = HueRequest.GET("http://" + bridgeUrl + "/api/" + vf.getUsername() + "/lights/");
		for (int i = 1; i < 50; i++){
			if (response.has(String.valueOf(i))){
				JsonObject state = response.getAsJsonObject(String.valueOf(i)).getAsJsonObject("state");
				if (state.has("on") && state.has("hue") && state.has("sat") && state.has("bri")){
					String lightName = response.getAsJsonObject(String.valueOf(i)).getAsJsonPrimitive("name").getAsString();
					HueLight light = new HueLight(i, lightName);
					lights.add(light);
					if(debug)
						System.out.println("Hue light discovered: " + light.toString());
				}
			}
		}

		Platform.runLater(new Runnable(){
			@Override
			public void run(){
				message_box.appendText("\nsimple hue setup complete!");
			}
		});

		if(debug)
			System.out.println("simple hue setup complete!");

		// Call logged in to display config screen
		loggedIn();

	}

	/**
	 * Called after successful login and light retrieval
	 */
	public void loggedIn(){
		Platform.runLater(new Runnable(){
			@Override
			public void run(){
				logged_in.setVisible(true);
			}
		});
	}

}
