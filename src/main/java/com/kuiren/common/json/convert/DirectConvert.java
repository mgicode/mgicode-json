package com.kuiren.common.json.convert;

public class DirectConvert {

	private Object orignObj;
	private String type;// 目前不用

	public DirectConvert(Object orignObj) {
		super();
		this.orignObj = orignObj;
	}

	public Object getOrignObj() {
		return orignObj;
	}

	public void setOrignObj(Object orignObj) {
		this.orignObj = orignObj;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
