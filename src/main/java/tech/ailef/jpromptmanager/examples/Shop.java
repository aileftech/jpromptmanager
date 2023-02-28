package tech.ailef.jpromptmanager.examples;

import java.util.List;

public class Shop {
	private String name;
	
	private String tagline;
	
	private List<String> owners;

	private String shopHistory;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTagline() {
		return tagline;
	}

	public void setTagline(String tagline) {
		this.tagline = tagline;
	}

	public List<String> getOwners() {
		return owners;
	}

	public void setOwners(List<String> owners) {
		this.owners = owners;
	}
	
	public String getShopHistory() {
		return shopHistory;
	}
	
	public void setShopHistory(String shopHistory) {
		this.shopHistory = shopHistory;
	}

	@Override
	public String toString() {
		return "Shop [name=" + name + ", tagline=" + tagline + ", owners=" + owners + ", shopHistory=" + shopHistory
				+ "]";
	}
	
}
