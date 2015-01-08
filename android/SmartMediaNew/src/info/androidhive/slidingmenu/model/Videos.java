package info.androidhive.slidingmenu.model;

import java.util.ArrayList;

public class Videos {

	private ArrayList<String> listTitle = new ArrayList<String>();
	private ArrayList<Integer> listId = new ArrayList<Integer>();
	
	public Videos(ArrayList<String> listTitle, ArrayList<Integer> listId) {
		this.listTitle = listTitle;
		this.listId = listId;
	}

	public ArrayList<String> getListTitles() {
		return listTitle;
	}

	public ArrayList<Integer> getListIds() {
		return listId;
	}
}
