package com.example.geocuratedphotolibrary.model;

public enum PhotoTag {

	ARTS("arts"), FOOD("food"), SHOPS("shops"), MALLS("malls"), MULTIPLEX(
			"multiplex"), RELIGIOUS("relegious"), CULTURAL("cultural");

	private String mTag;

	private PhotoTag(String tag) {
		this.mTag = tag;
	}
	
	public String getTag() {
		return mTag;
	}
	
	public static PhotoTag fromString(String text) {
		if (text != null) {
			for (PhotoTag type : PhotoTag.values()) {
				if (text.equalsIgnoreCase(type.mTag)) {
					return type;
				}
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return mTag;
	}

}
