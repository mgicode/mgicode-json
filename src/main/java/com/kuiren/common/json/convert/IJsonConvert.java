package com.kuiren.common.json.convert;

import com.kuiren.common.json.JsonCreator;

public abstract class IJsonConvert {

	private JsonCreator jsonCreator;

	public void setJsonCreator(JsonCreator jc) {

		this.jsonCreator = jc;
	}

	public JsonCreator getJsonCreator() {
		return this.jsonCreator;
	}

	public abstract Object convert(Object obj, String name, Object value);
}
