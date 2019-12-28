package com.octopro.meetndev;

public class Cards {
	private String userId;
	private String name;

	public Cards(String userId, String name){
		this.userId = userId;
		this.name = name;
	}

	//provide Getter and Setter

	public String getUserId(){
		return userId;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}
}
