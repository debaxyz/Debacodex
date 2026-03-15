package com.github.debacodex.model;

public class Item {
	private String title;
	private String description;
	private String imageUrl;
	private String village;
	private String rice;
	private String money;
	private String things;
	private String status;
	private int id;

	public Item(int id, String title, String description, String imageUrl, String village, String rice, String money, String things,String status) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.imageUrl = imageUrl;
		this.village = village;
		this.rice = rice;
		this.money = money;
		this.things = things;
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getVillage() {
		return village;
	}

	public String getRice() {
		return rice;
	}

	public String getMoney() {
		return money;
	}
	
	public String getThing() {
		return things;
	}
	
	public String getStatus() {
		return status;
	}

	public int getId() {
		return id;
	}
}