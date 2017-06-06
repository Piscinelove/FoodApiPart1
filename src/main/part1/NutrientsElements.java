package com.example.part1;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class NutrientsElements {
	
	//VARIABLES
	private String name_translations;
	private String unit;
	private double per_hundred;
	private double per_portion;
	private double per_day;
	
	//GETTERS & SETTERS

	

	public String getUnit() {
		return unit;
	}

	public String getName_translations() {
		return name_translations;
	}

	public void setName_translations(String name_translations) {
		this.name_translations = name_translations;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public double getPer_hundred() {
		return per_hundred;
	}

	public void setPer_hundred(double per_hundred) {
		this.per_hundred = per_hundred;
	}

	public double getPer_portion() {
		return per_portion;
	}

	public void setPer_portion(double per_portion) {
		this.per_portion = per_portion;
	}

	public double getPer_day() {
		return per_day;
	}

	public void setPer_day(double per_day) {
		this.per_day = per_day;
	}


	

	
	//TO STRING
	@Override
	public String toString() {
		return "NutrientsElements [name_translations=" + name_translations + ", unit=" + unit + ", per_hundred="
				+ per_hundred + ", per_portion=" + per_portion + ", per_day=" + per_day + "]";
	}

	

}
