/*
 * @author: Miffy Chen & James Yu
 * @date:   2018/11/20
 * 
 * SimulationController.java
 * 
 */

package cpuscheduler;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


/**
 * Simulation Controller
 */
public class SimulationController implements Initializable {
	
	@FXML
	private Button backButton;
	
	@FXML
	private ScrollPane scroll;
	
	private static long lastUpdate = 0;
	private VBox subroot1;
	private AnimationTimer at;
	private ArrayList<Scheduler> lvls = new ArrayList<>();
	private Map<Integer, Integer> processBarsTable = new HashMap<>();
	private ArrayList<ProgressBar> processBars = new ArrayList<>();
	private ArrayList<Label> labels = new ArrayList<>();
	
	// Back button
	@FXML
	private void handleBackButtonAction(ActionEvent event) {
		
		// clear everything on simulation page
		at.stop();
		processBars.clear();
		processBarsTable.clear();
		labels.clear();
		lvls.clear();
		FXMLDocumentController.getCpu().resetSimData();
		FXMLDocumentController.getCpu().resetReport();
		FXMLDocumentController.getCpu().resetAll();
		
		// reload main page
		try {
			Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
			Scene scene = new Scene(root);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(scene);
			stage.show();
		}
		catch (IOException ex) {
			Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
		}
		
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
		VBox root = new VBox();
		root.setSpacing(5);
		root.setAlignment(Pos.CENTER);
		scroll.setContent(root);
		
		Scanner scanner = new Scanner(FXMLDocumentController.getCpu().getSimData());
		
		DecimalFormat df = new DecimalFormat("#.#");
		df.setRoundingMode(RoundingMode.FLOOR);
		
		at = new AnimationTimer() {
			
			@Override
			public void handle(long now) {
				if (now - lastUpdate >= 28_000_000 * (1 / FXMLDocumentController.getSpeed())) {
					ProgressBar tmp;
					String strtmp = "";
					if (scanner.hasNextLine()) {
						String line = scanner.nextLine();
						String[] splt = line.split("\\s+");
						if (processBarsTable.containsKey(Integer.valueOf(splt[0]))) {
							strtmp = "";
                            strtmp += "" + splt[0] + ": ";
                            strtmp += "Wait Time: " + String.format("%.1f", Double.valueOf(splt[7])) + " ";
                            strtmp += "Turn Around Time: " + String.format("%.1f", Double.valueOf(splt[8])) + " - ";
                            strtmp += "Priority: " + splt[3] + " ";
                            strtmp += "Arrival Time: " + String.format("%.1f", Double.valueOf(splt[4])) + " ";
                            strtmp += "Start Time: " + String.format("%.1f", Double.valueOf(splt[5])) + " ";
                            strtmp += "Finish Time: " + String.format("%.1f", Double.valueOf(splt[6]));
							
							processBars.get(processBarsTable.get(Integer.valueOf(splt[0])))
							        .setProgress((Double.valueOf(df.format(Double.valueOf(splt[2])))
							                - (Double.valueOf(df.format(Double.valueOf(splt[1])))))
							                / Double.valueOf(df.format(Double.valueOf(splt[2]))));
							labels.get(processBarsTable.get(Integer.valueOf(splt[0]))).setText(strtmp);
							strtmp = "";
						}
						else {
							subroot1 = new VBox();
							tmp = new ProgressBar((Double.valueOf(splt[2]) - Double.valueOf(splt[1])) / Double.valueOf(splt[2]));
							tmp.setPrefWidth(200);
							processBars.add(tmp);
							processBarsTable.put(Integer.valueOf(splt[0]), processBars.size() - 1);
							subroot1.getChildren().add(processBars.get(processBars.size() - 1));
							strtmp = "";
                            strtmp += " " + splt[0] + ": ";
                            strtmp += "Wait Time: " + String.format("%.1f", Double.valueOf(splt[7])) + " ";
                            strtmp += "Turn Around Time: " + String.format("%.1f", Double.valueOf(splt[8])) + " - ";
                            strtmp += "Priority: " + splt[3] + " ";
                            strtmp += "Arrival Time: " + String.format("%.1f", Double.valueOf(splt[4])) + " ";
                            strtmp += "Start Time: " + String.format("%.1f", Double.valueOf(splt[5])) + " ";
                            strtmp += "Finish Time: " + String.format("%.1f", Double.valueOf(splt[6]));
							labels.add(new Label(strtmp));
							subroot1.getChildren().add(labels.get(processBars.size() - 1));
							strtmp = "";
							subroot1.setBorder(new Border(new BorderStroke(Color.BLACK,
									BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
							subroot1.setAlignment(Pos.CENTER);
							root.getChildren().add(subroot1);
						}
					}
					else {
						subroot1 = new VBox();
						subroot1.getChildren().add(new Label("\nResults:\n" + FXMLDocumentController.getCpu().getReport()));
						root.getChildren().add(subroot1);
						scanner.close();
						at.stop();
					}
					lastUpdate = now;
				}
			}
		};
		at.start();
	}
	
}
