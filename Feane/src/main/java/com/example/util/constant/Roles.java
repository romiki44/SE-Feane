package com.example.util.constant;

//toto je strasne divne enum...stale nechapem`...akoze vlastenn je tam rola USER,ADMIN a EDITOR
//a ten USER=ROLE_USER, ADMIN=ROLE_EDMIN, EDITOR=ROLE_EDITOR, co sa dosiahlo tym privatnym konstruktorom asi
//ak ked chcem niekde vediet hodnotu zavolam si getRole()..aha, tak asi uz torchu chapem, vid stranku nizsie :(
//https://howtodoinjava.com/java/enum/enum-tutorial/
public enum Roles {
	USER("ROLE_USER"),ADMIN("ROLE_ADMIN"),EDITOR("ROLE_EDITOR");
	
	private String role;	
	private Roles(String role) {
		this.role = role;
	}

	public String getRole() {
		return role;
	}
}
