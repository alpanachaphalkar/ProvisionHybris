package com.hybris.provision;

public enum HybrisRecipe {

	B2B_Accelerator("b2b_acc", ":9001/yb2bacceleratorstorefront/?site=powertools"), 
	B2C_Accelerator("b2c_acc", ":9001/yacceleratorstorefront/?site=electronics");
	
	private String recipeId;
	private String defaultRecipeShopUrl;
	
	private HybrisRecipe(String recipeID, String defaultRecipeShop) {
		// TODO Auto-generated constructor stub
		this.recipeId = recipeID;
		this.defaultRecipeShopUrl = defaultRecipeShop;
	}
	
	public String getRecipeId(){
		return this.recipeId;
	}
	
	public String getDefaultRecipeShop(){
		return this.defaultRecipeShopUrl;
	}
	
}
