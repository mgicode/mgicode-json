package com.kuiren.common.json;

import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;

public class JsonMatch {

	/**
	 * todo:表达式中加上格式转换，主要是为日期格式转换
	 * 
	 * @author:彭仁夔 于2014年10月27日下午2:47:18创建
	 * @param expr
	 * @return
	 */
	public JsonConfig parse(String expr) {
		JsonConfig jc = new JsonConfig();
		if (expr.startsWith(".")) {
			//.代表当前的对象属性上，是一种简化操作，其等于xxxx@^
			if (!expr.contains("@")) {
				expr = expr.substring(1) + "@^";
			} else {
				expr = expr.substring(1);
			}
		} else if (!expr.contains("@")) {
			//对表达式找到所有的对象属性
			expr = expr + "@*";
		}

		String arr[] = expr.split("@");

		jc.setExpr(arr[0]);
		if (arr[1].startsWith("^")) {
			// ^actionbean
			if (arr[1].length() == 1) {
				arr[1] = "^actionbean";
			} else {
				arr[1] = "^actionbean." + arr[1].substring(1);
			}
		}
		jc.setTargetName(arr[1]);

		if (!arr[0].contains(".")) {
			jc.setDelName(arr[0]);
		}

		return jc;

	}

	public boolean commonMatch(String dottargetName, String targetName,
			Object in, JsonConfig jc) {
		if (jc.getTargetName() == null) {
			System.out.println(jc.getExpr());
		}
		if ("*".equals(jc.getTargetName())) {
			return true;

		} else if (jc.getTargetName().startsWith("^")) {
			if (dottargetName.equals(jc.getTargetName().substring(1))) {
				return true;
			}
		} else {
			if (StringUtils.endsWith(dottargetName, jc.getTargetName())) {
				return true;
			}
		}

		return false;
	}

	public boolean matchAdd(String dottargetName, String targetName, Object in,
			JsonConfig jc) {
		return commonMatch(dottargetName, targetName, in, jc);
	}

	public boolean matchUpdate(String dottargetName, String targetName,
			Object in, PropertyDescriptor prop, JsonConfig jc) {
		// boolean flag = commonMatch(dottargetName, targetName, in, jc);
		// String name = prop.getName();
		// String jcExpr = jc.getExpr();
		// boolean flag1 = false;
		// String name1 = jcExpr;
		// if (jcExpr.contains(".")) {
		// name1 = jcExpr.split("[.]")[0];
		// }
		//
		// if (name.equals(name1)) {
		// flag = flag && true;
		// } else {
		// flag = flag && false;
		// }
		// return flag;

		return matchUpdate(dottargetName, targetName, in, prop.getName(), jc);
	}

	public boolean matchUpdate(String dottargetName, String targetName,
			Object in, String name, JsonConfig jc) {
		boolean flag = commonMatch(dottargetName, targetName, in, jc);
		// String name = prop.getName();
		String jcExpr = jc.getExpr();
		boolean flag1 = false;
		String name1 = jcExpr;
		if (jcExpr.contains(".")) {
			name1 = jcExpr.split("[.]")[0];
		}

		if (name.equals(name1)) {
			flag = flag && true;
		} else {
			flag = flag && false;
		}
		return flag;
	}

	public boolean matchDel(String dottargetName, String targetName, Object in,
			PropertyDescriptor prop, JsonConfig jc) {

		// boolean flag = commonMatch(dottargetName, targetName, in, jc);
		// String name = prop.getName();
		// String jcExpr = jc.getExpr();
		//
		// String name1 = jcExpr;
		//
		// if (name.equals(name1)) {
		// flag = flag && true;
		// } else {
		// flag = flag && false;
		// }
		// return flag;

		return matchDel(dottargetName, dottargetName, in, prop.getName(), jc);
	}

	public boolean matchDel(String dottargetName, String targetName, Object in,
			String name, JsonConfig jc) {

		boolean flag = commonMatch(dottargetName, targetName, in, jc);
		// String name = prop.getName();
		String jcExpr = jc.getExpr();

		String name1 = jcExpr;

		if (name.equals(name1)) {
			flag = flag && true;
		} else {
			flag = flag && false;
		}
		return flag;
	}
}
