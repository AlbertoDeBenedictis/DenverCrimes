package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {

	private Graph<String, DefaultWeightedEdge> grafo;
	private EventsDao dao;
	private List<String> best;

	public Model() {
		dao = new EventsDao();
	}

	public void creaGrafo(String categoria, Integer mese) {

		/**
		 * NB: Non usiamo in questo caso un idMap per i vertici perchè sono
		 * rappresentati da stringhe, che hanno già un loro hashCode ed equals che non
		 * ci fa correre il rischio di considerare due elementi uguali come due diversi
		 * vertici
		 * 
		 */

		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

//		Mi faccio dare le adiacenze dal dao
		List<Adiacenza> adiacenze = this.dao.getAdiacenze(categoria, mese);
		for (Adiacenza a : adiacenze) {

//			Non sono sicuro che siano strettamente necessari gli IF

//			Se il primo vertice non è stato ancora aggiunto, lo aggiungo
			if (!this.grafo.containsVertex(a.getV1())) {
				this.grafo.addVertex(a.getV1());
			}
//			Faccio lo stesso per il secondo
			if (!this.grafo.containsVertex(a.getV2())) {
				this.grafo.addVertex(a.getV2());
			}
//			Stesso per l'arco (se il get mi dà null, vuol dire che non ce l'ho)
			if (this.grafo.getEdge(a.getV1(), a.getV2()) == null) {
				Graphs.addEdgeWithVertices(this.grafo, a.getV1(), a.getV2(), a.getPeso());
			}
		}

		System.out.println(String.format("Grafo creato con %d vertici e %d archi", this.grafo.vertexSet().size(),
				this.grafo.edgeSet().size()));

	}

	public List<Integer> getMesi() {
		return dao.getMesi();
	}

	public List<String> getCategorie() {
		return dao.getCategorie();
	}

	public List<Arco> getArchi() {

		double pesoMedio = 0;

		for (DefaultWeightedEdge arco : this.grafo.edgeSet()) {
			pesoMedio += this.grafo.getEdgeWeight(arco);
		}
		pesoMedio = pesoMedio / this.grafo.edgeSet().size();

		List<Arco> archi = new ArrayList<>();

		for (DefaultWeightedEdge arco : this.grafo.edgeSet()) {
			if (this.grafo.getEdgeWeight(arco) > pesoMedio) {
				archi.add(new Arco(this.grafo.getEdgeSource(arco), this.grafo.getEdgeTarget(arco),
						this.grafo.getEdgeWeight(arco)));
			}
		}

		Collections.sort(archi);

		return archi;
	}
	
	/**
	 * Trova il percorso tra sorgente e destinazione che tocca il numero
	 * max di vertici
	 * @param sorgente
	 * @param destinazione
	 * @return percorso con numero max vertici
	 */
	public List<String> trovaPercorso(String sorgente, String destinazione){
		
//		Inizializziamo soluzione parziale e finale
		List<String> parziale = new ArrayList<>();
		this.best = new ArrayList<>();
		
//		Aggiungiamo il nodo di partenza
		parziale.add(sorgente);
		
		trovaRicorsivo(destinazione, parziale, 0);
		
		return this.best;
	
	}

	private void trovaRicorsivo(String destinazione, List<String> parziale, int livello) {
		
//		CASO TERMINALE: l'ultimo vertice inserito in parziale è uguale alla destinazione
		if(parziale.get(parziale.size()-1).equals(destinazione)) {
			
//			Controllo se è migliore di best
			if(parziale.size()>best.size()) {
				this.best = new ArrayList<>(parziale);
//				Ovviamente la creo nuova altrimenti non aggiungo un bel niente
			}
			return;
		}
		
//		scorro i vicini dell'ultimo vertice inserito in parziale,
//		provo ad aggiungerlo e faccio backtracking
		for(String vicino: Graphs.neighborListOf(this.grafo, parziale.get(parziale.size()-1))) {
			
//			cammino aciclico --> controllo che il vertice non sia già in parziale
			if(!parziale.contains(vicino)) {
				
//				provo ad aggiungere
				parziale.add(vicino);
				
//				lancio la ricorsione
				trovaRicorsivo(destinazione,parziale,livello+1);
				
//				faccio backtracking
				parziale.remove(parziale.size()-1);
			}
			
		}
		
		
		
	}
	
	

}
