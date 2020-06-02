package it.polito.tdp.crimes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.crimes.model.Adiacenza;
import it.polito.tdp.crimes.model.Event;

public class EventsDao {

	/**
	 * Ritorna la lista dei mesi distinti in cui si sono verificati i diversi
	 * crimini
	 * 
	 * @return
	 */
	public List<Integer> getMesi() {

//		Occhio al distinct nella query
		String sql = "SELECT DISTINCT Month(reported_date) as mese FROM events";

		List<Integer> mesi = new LinkedList<>();

		try {

			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

//			Mi prendo l'intero che rappresenta il mese
			while (rs.next()) {
				mesi.add(rs.getInt("mese"));
			}
			conn.close();

//			Prima li ordiniamo
			Collections.sort(mesi);

			return mesi;

		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}

	}

	/**
	 * Ritorna le categorie di crimini
	 * 
	 * @return
	 */
	public List<String> getCategorie() {

		String sql = "Select distinct offense_category_id as categoria from events";

		List<String> categorie = new LinkedList<>();

		try {

			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

//			Mi prendo l'intero che rappresenta il mese
			while (rs.next()) {
				categorie.add(rs.getString("categoria"));
			}
			conn.close();

			return categorie;

		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}

	}

	public List<Event> listAllEvents() {
		String sql = "SELECT * FROM events";
		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			List<Event> list = new ArrayList<>();

			ResultSet res = st.executeQuery();

			while (res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"), res.getInt("offense_code"),
							res.getInt("offense_code_extension"), res.getString("offense_type_id"),
							res.getString("offense_category_id"), res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"), res.getDouble("geo_lon"), res.getDouble("geo_lat"),
							res.getInt("district_id"), res.getInt("precinct_id"), res.getString("neighborhood_id"),
							res.getInt("is_crime"), res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}

			conn.close();
			return list;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public List<Adiacenza> getAdiacenze(String categoria, Integer mese) {

		String sql = "select e1.offense_type_id as v1, e2.offense_type_id as v2, count(distinct(e1.neighborhood_id)) as peso from events as e1, events as e2 where e1.offense_category_id = ? and e2.offense_category_id=? and Month(e1.reported_date) = ? and Month(e2.reported_date) = ? and e1.offense_type_id != e2.offense_type_id and e1.neighborhood_id = e2.neighborhood_id group by e1.offense_type_id, e2.offense_type_id ";

		List<Adiacenza> adiacenze = new LinkedList<>();

		try {

			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

//			Set parametri
			st.setString(1, categoria);
			st.setString(2, categoria);
			st.setInt(3, mese);
			st.setInt(4, mese);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				adiacenze.add(new Adiacenza(rs.getString("v1"),rs.getString("v2"),rs.getDouble("peso")));
			}
			conn.close();

			return adiacenze;

		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}

	}

}
