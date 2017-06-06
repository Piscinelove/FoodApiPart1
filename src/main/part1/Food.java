package com.example.part1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
public class Food {
	
	//VARIABLES
	@Id
	private String _id;
	private String name;
	private String ingredients_translations;
	private int quantity;
	private String unit;
	private int portion_quantity;
	private String portion_unit;
	private Map<String, NutrientsElements> nutrients;
	
	//GETTERS & SETTERS
	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIngredients_translations() {
		return ingredients_translations;
	}

	public void setIngredients_translations(String ingredients_translations) {
		this.ingredients_translations = ingredients_translations;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getPortion_quantity() {
		return portion_quantity;
	}

	public void setPortion_quantity(int portion_quantity) {
		this.portion_quantity = portion_quantity;
	}

	public String getPortion_unit() {
		return portion_unit;
	}

	public void setPortion_unit(String portion_unit) {
		this.portion_unit = portion_unit;
	}

	public Map<String, NutrientsElements> getNutrients() {
		return nutrients;
	}

	public void setNutrients(Map<String, NutrientsElements> nutrients) {
		this.nutrients = nutrients;
	}
	//TO STRING

	@Override
	public String toString() {
		return "Food [_id=" + _id + ", name=" + name + ", ingredients_translations=" + ingredients_translations
				+ ", quantity=" + quantity + ", unit=" + unit + ", portion_quantity=" + portion_quantity
				+ ", portion_unit=" + portion_unit + ", nutrients=" + nutrients + "]";
	}
	
	
	
	
	
	
	
	

}
