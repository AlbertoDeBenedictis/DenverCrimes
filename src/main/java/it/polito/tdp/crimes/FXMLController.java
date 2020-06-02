/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.crimes;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.crimes.model.Arco;
import it.polito.tdp.crimes.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class FXMLController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxCategoria"
    private ComboBox<String> boxCategoria; // Value injected by FXMLLoader

    @FXML // fx:id="boxMese"
    private ComboBox<Integer> boxMese; // Value injected by FXMLLoader

    @FXML // fx:id="btnAnalisi"
    private Button btnAnalisi; // Value injected by FXMLLoader

    @FXML // fx:id="boxArco"
    private ComboBox<Arco> boxArco; // Value injected by FXMLLoader

    @FXML // fx:id="btnPercorso"
    private Button btnPercorso; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCalcolaPercorso(ActionEvent event) {

    	txtResult.clear();
    	
    	Arco a = this.boxArco.getValue();
    	if(a == null) {
    		txtResult.setText("ERRORE: seleziona arco!");
    		return;
    	}
    	
//    	Prendo il percorso
    	List<String> percorso = this.model.trovaPercorso(a.getV1(), a.getV2());
    	
//    	Lo stampo
    	txtResult.appendText("Percorso migliore:\n\n");
    	for(String v: percorso) {
    		txtResult.appendText(v+"\n");
    	}
    	
    	
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {

    	txtResult.clear();
    	
    	String categoria = boxCategoria.getValue();
    	if(categoria== null) {
    		txtResult.setText("Seleziona categoria!");
    		return;
    	}
    	Integer mese = boxMese.getValue();
    	if(mese == null) {
    		txtResult.setText("Seleziona mese!");
    		return;
    	}
    	
    	this.model.creaGrafo(categoria, mese);
    	
    	
//    	Mi prendo gli archi e stampo la lista (maggior del peso medio)
    	List<Arco> archi = this.model.getArchi();
    	
//    	Aggiungo gli archi nel menu a tendina
    	boxArco.getItems().addAll(archi);
    	
    	txtResult.appendText("ARCHI > PESO MEDIO:\n");
    	for(Arco a: archi) {
    		txtResult.appendText(a.toString()+"\n");
    	}
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxCategoria != null : "fx:id=\"boxCategoria\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnAnalisi != null : "fx:id=\"btnAnalisi\" was not injected: check your FXML file 'Scene.fxml'.";
        assert boxArco != null : "fx:id=\"boxArco\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnPercorso != null : "fx:id=\"btnPercorso\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	
//    	Riempiamo le due tendine
    	this.boxCategoria.getItems().addAll(this.model.getCategorie());
    	this.boxMese.getItems().addAll(this.model.getMesi());
//    	Freghiamocene che i mesi sono numeri e non li vedo con il loro nome
    	
    }
}
