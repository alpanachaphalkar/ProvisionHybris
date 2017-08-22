package com.hybris;

public enum HybrisRecipe {

	B2B_Accelerator("b2b_acc", ":9001/yb2bacceleratorstorefront/?site=powertools", "powertools"), 
	B2C_Accelerator("b2c_acc", ":9001/yacceleratorstorefront/?site=electronics", "electronics");
	
	private String recipeId;
	private String defaultRecipeShopUrl;
	private String defaultShop;
	
	private HybrisRecipe(String recipeID, String defaultRecipeShop, String defaultShop) {
		// TODO Auto-generated constructor stub
		this.setRecipeId(recipeID);
		this.setDefaultRecipeShopUrl(defaultRecipeShop);
		this.setDefaultShop(defaultShop);
	}

	public String getRecipeId() {
		return recipeId;
	}

	public void setRecipeId(String recipeId) {
		this.recipeId = recipeId;
	}

	public String getDefaultRecipeShopUrl() {
		return defaultRecipeShopUrl;
	}

	public void setDefaultRecipeShopUrl(String defaultRecipeShopUrl) {
		this.defaultRecipeShopUrl = defaultRecipeShopUrl;
	}

	public String getDefaultShop() {
		return defaultShop;
	}

	public void setDefaultShop(String defaultShop) {
		this.defaultShop = defaultShop;
	}
	
}
