package com.kuiren.common.json;

import com.kuiren.common.json.convert.INameConvert;

/**
 * todo:加上带条件删除及条件add
 * 
 * @author 彭仁夔 于2014年10月28日下午7:47:33创建
 * 
 */
public class JsonConfig {

	private String expr;// 名称或查找的表达式xxx.yyyy

	// 当前操作属性的作用域，即@之后的对象，操作属性所在实体，可能为*,点串形式等
	private String targetName;

	// 增加时使用addName保存属性名
	// 删除 时使用delName保存属性名
	// replace时使用addName保存修改之后的属性名，使用delName保存原来的名称
	// private String addName;
	private Object addName;// 支持INameConvert

	private String delName;

	// 对于add来讲可以为真实的值，也可以是解析后的实现自定义接口的类型
	// 对于replace来讲，是解析后的实现自定义接口的类型
	// 对del来讲，其没有作用
	private Object value;

	// 0:把后一个实体中属性替换到前一个实体中去，1：把当前的实体替换到当前实体的其它属性
	// 采用点串的话需要这样去标明其为1
	// 0: add,1:common replace,2:
	private String type = null;

	private Class<?> delCls;

	// 目前没有用到
	private String actualName;

	private String remain1;

	public JsonConfig() {

	}

	public JsonConfig(String name) {
		this.setExpr(name);
	}

	public JsonConfig(String name, Object value) {
		this.setExpr(name);
		this.value = value;
	}

	public JsonConfig(String name, Object value, String targetName) {
		super();
		this.setExpr(name);
		this.value = value;
		this.targetName = targetName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getDelName() {
		return delName;
	}

	public void setDelName(String delName) {
		this.delName = delName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}

	public String getActualName() {
		return actualName;
	}

	public void setActualName(String actualName) {
		this.actualName = actualName;
	}

	//2015.12.10 add by pengrk,for the dync add/replace name
	public String getAddName(Object obj, String name, Object value) {
		if (addName instanceof INameConvert) {
			return (String) ((INameConvert) addName).convert(obj, name, value);

		} else {
			return (String) addName;
		}
	}

	public Object getAddName() {
		return addName;
	}

	public void setAddName(Object addName) {
		this.addName = addName;
	}

	// public String getAddName() {
	// return addName;
	// }
	//
	// public void setAddName(String addName) {
	// this.addName = addName;
	// }

	/********************************************/
	
	public JsonConfig clone() {
		JsonConfig jc1 = new JsonConfig();
		jc1.setAddName(this.getAddName());
		jc1.setDelName(this.getDelName());
		jc1.setExpr(this.getExpr());
		jc1.setTargetName(this.getTargetName());
		jc1.setType(this.getType());
		jc1.setValue(this.getValue());
		jc1.setActualName(this.getActualName());
		jc1.setDelCls(this.getDelCls());
		return jc1;
	}

	public String getRemain1() {
		return remain1;
	}

	public void setRemain1(String remain1) {
		this.remain1 = remain1;
	}

	public Class<?> getDelCls() {
		return delCls;
	}

	public void setDelCls(Class<?> delCls) {
		this.delCls = delCls;
	}

}
