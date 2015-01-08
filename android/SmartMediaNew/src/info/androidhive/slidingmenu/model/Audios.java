package info.androidhive.slidingmenu.model;

import java.util.ArrayList;

public class Audios {
	private ArrayList<String> listChansons = new ArrayList<String>();
	private ArrayList<String> listArtistes = new ArrayList<String>();
	private ArrayList<Integer> listId = new ArrayList<Integer>();
	
	public Audios(ArrayList<String> listChansons, ArrayList<String> listArtistes, ArrayList<Integer> listId) {
		this.listArtistes = listArtistes;
		this.listChansons = listChansons;
		this.listId = listId;
	}

	public ArrayList<String> getListChansons() {
		return listChansons;
	}

	public ArrayList<String> getListArtistes() {
		return listArtistes;
	}

	public ArrayList<Integer> getListId() {
		return listId;
	}
}
