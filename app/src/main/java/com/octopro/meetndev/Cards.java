package com.octopro.meetndev;

public class Cards {
	private String userId;
	private String name;
	private String userImageUrl;
	private String skills;

	public Cards(String userId, String name, String userImageUrl, String skills){
		this.userId = userId;
		this.name = name;
		this.userImageUrl = userImageUrl;
		this.skills = skills;
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

	public String getUserImageUrl(){
		return userImageUrl;
	}

	public void setUserImageUrl(String userImageUrl){
		this.userImageUrl = userImageUrl;
	}

	public String getUserSkills(){
		return skills;
	}

	public void setUserSkills(String skills){
		this.skills = skills;
	}
}
