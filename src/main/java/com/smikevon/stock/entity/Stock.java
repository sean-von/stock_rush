package com.smikevon.stock.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="stock")
public class Stock {
	
	@Id
	private String id;
	
	private String name;
	
	@Indexed
	private int capital;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCapital() {
		return capital;
	}

	public void setCapital(int capital) {
		this.capital = capital;
	}

}
