/**
 * @author Shaun Parkison
 * Simple driver program for turning 
 * Philips Hue light(s) on/off
 */

package simpleHue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonObject;

import simpleHue.util.VarFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SimpleHue extends Application {

	/*
	 * Class Variables
	 */
	private VarFactory vf = VarFactory.getInstance(); // Holds application variables
	private HashMap<Integer, List<Double>> lightConfig = new HashMap<Integer, List<Double>>();
	private String bridgeUrl = "";

	public static void main(String args[]){
		SimpleHue app = new SimpleHue();
		if(!app.vf.getConfig().exists()) {
			Application.launch(SimpleHue.class, (java.lang.String[])null);
		} else {
			try {
				app.processConfig();
			} catch (IOException e) {
				System.out.println("Error processing config file: ");
				e.printStackTrace();
				System.exit(0);
			}
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			/*
			 * Build main stage
			 */
			AnchorPane page = (AnchorPane) FXMLLoader.load(SimpleHue.class.getResource("assets/simple_hue.fxml"));
			Scene main_scene = new Scene(page);
			primaryStage.setScene(main_scene);
			primaryStage.setTitle("simple hue v" + vf.getVersion());
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch (Exception ex) {
			Logger.getLogger(SimpleHue.class.getName()).log(Level.SEVERE, null, ex);
		}
		// Ensure application terminates on window close
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});
	}

	private void processConfig() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(vf.getConfig()));
	    try {
	        String line = br.readLine();
	        int count = 0;
	        while (line != null) {
	        	if(count == 0)
	        		bridgeUrl = line;
	        	else{
	        		String[] split = line.split("\t");
	        		List<Double> xy = new ArrayList<Double>();
	        		xy.add(Double.valueOf(split[1]));
	        		xy.add(Double.valueOf(split[2]));
	        		lightConfig.put(Integer.valueOf(split[0]), xy);
	        	}
	        	count++;
	            line = br.readLine();
	        }
	    } finally {
	        br.close();
	    }
	    // Toggle lights in the config file...
	    for(Integer lightId : lightConfig.keySet()){
	    	if(isOn(lightId)){
	    		// Turn it off
	    		turnEmOff(lightId);
	    	} else {
	    		// Turn it on
	    		turnEmOn(lightId);
	    	}
		}
	    // All done, shut 'er down
	    System.exit(0);
	}

	/************************************************************
	 * Hue specific Actions
	 ************************************************************/

	/**
	 * Turn off lights
	 * @param lightId
	 */
	private void turnEmOn(int lightId){
		String body = "{\"on\": true,\"xy\":[" + lightConfig.get(lightId).get(0) + "," + lightConfig.get(lightId).get(1) + "]}";
		try {
			HueRequest.PUT("http://" + bridgeUrl + "/api/" + vf.getUsername() + "/lights/" + lightId + "/state/", body);
		} catch (Exception e) {
			System.out.println("Error turning light on! ");
			e.printStackTrace();
		}
	}
	
	/**
	 * Turn on lights
	 * @param lightId
	 */
	private void turnEmOff(int lightId){
		String body = "{\"on\":false}";
		try {
			HueRequest.PUT("http://" + bridgeUrl + "/api/" + vf.getUsername() + "/lights/" + lightId + "/state/", body);
		} catch (Exception e) {
			System.out.println("Error turning light off! ");
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns true/false based on whether light is on/off
	 * 
	 * @param id of the light querying
	 * @return boolean
	 * @throws Exception
	 */
	public boolean isOn(int lightId){
		JsonObject response;
		boolean result = false;
		try {
			response = HueRequest.GET("http://" + bridgeUrl + "/api/" + vf.getUsername() + "/lights/" + lightId);
			result = response.get("state").getAsJsonObject().get("on").getAsBoolean();
		} catch (Exception e) {
			System.out.println("Error polling light! ");
			e.printStackTrace();
		}
		return result;
	}

}
