package org.pardus.manager.model;

import java.util.List;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class NetworkItemList {

	@Expose
	private List<NetworkItem> data;
//	private ObservableList<NetworkItem> data;

	public List<NetworkItem> getData() {
		return data;
	}

	public NetworkItemList(List<NetworkItem> list) {
		this.data = list;

	}

	public String toJson(boolean pretty) {
		if (pretty) {
			return new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create().toJson(this);
		} else {
			return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(this);
		}

	}

	public static <T> T fromJson(String json, Class<T> referenceClass) {

		return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(json, referenceClass);
	}

	

}
