package com.kuiren.common.json.util;

import org.apache.commons.lang3.ArrayUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author pengrk
 * @email:sjkjs155@126.com
 * @wetsite:www.mgicode.com
 * @license:GPL
 */
public class StringUtil {

    //protected final static Logger logger = LoggerFactory.getLogger(StringUtil.class);

	public static String NEWLINE = "\n";
	protected static Random random = new Random();
	private static Pattern commentPattern = Pattern.compile("/\\*.*?(\\*/|$)",
			Pattern.DOTALL);

	private static Pattern lineCommentPattern = Pattern
			.compile("--.*?(\r|\n|$)");

	private static Pattern leftTrimPattern = Pattern.compile("^(　| )+");

	private static Pattern rightTrimPattern = Pattern.compile("(　| )+$");

	public static String mergeIds(String oldIds, String newIds) {
		if (isNullOrEmpty(newIds))
			return oldIds;
		if (isNullOrEmpty(oldIds))
			return newIds;
		String ids[] = oldIds.split(",");
		String newidss[] = newIds.split(",");
		String[] both = (String[]) ArrayUtils.addAll(ids, newidss);
		return StringUtil.join(Arrays.asList(both), ",");
	}

	public static String joinIds(String oldIds, String newIds) {
		return mergeIds(oldIds, newIds);
	}

	public static String delIds(String oldIds, String removeIds) {
		return removeIds(oldIds, removeIds);
	}

	public static String removeIds(String oldIds, String removeIds) {
		if (isNullOrEmpty(removeIds))
			return oldIds;
		if (isNullOrEmpty(oldIds))
			return "";
		String ids[] = oldIds.split(",");
		String newidss[] = removeIds.split(",");

		List<String> list = new ArrayList<String>();
		for (String oid : ids) {
			for (String did : newidss) {
				if (oid != null && did != null && oid.trim().equals(did.trim())) {
				} else {
					list.add(oid);
				}

			}
		}
		return StringUtil.join(list, ",");
	}

	/**
	 * 在内容的每行前加上前缀，特别用来log的内容上
	 * 
	 * @param pre
	 *            每行前后的显示的内容，如时间，-
	 * @param c
	 *            可以包括\n分隔的多行的内容
	 * @return
	 */
	public static String addPreRow(String pre, String c) {
		if (c == null) {
			return "";
		}
		String strs[] = c.split("\\n");
		StringBuilder sb = new StringBuilder();
		for (String s : strs) {
			sb.append(NEWLINE);
			sb.append(pre + " " + s);

		}
		return sb.toString();
	}

	public static String incFillZero(String code, Integer len, String c) {
		if (StringUtil.IsNullOrEmpty(code))
			code = "1";
		Integer intHao = Integer.parseInt(code);
		intHao++;
		String strHao = code + "";
		if (len == null) {
			return strHao;
		}
		if (c == null) {
			c = "0";
		}
		while (strHao.length() < len) {

			strHao = c + strHao;
		}
		return strHao;
	}

	/**
	 * 根据当前时间生成保存的文件名称
	 * 
	 * @param fileName
	 * @return
	 */
	public static String genFileName(String fileName) {
		return genFileName(fileName, "yyMMddHHmmssSSS");
	}

	public static String genFileName(String fileName, String pattern) {
		return genFileName(fileName, pattern, false);
	}

	public static String genFileName(String fileName, String pattern,
			boolean need) {

		DateFormat format = new SimpleDateFormat(pattern);
		String formatDate = format.format(new Date());

		int random1 = random.nextInt(10000);

		int position = fileName.lastIndexOf(".");
		String extension = fileName.substring(position);
		String preFileName = fileName.substring(0, position);
		if (need == true) {
			return preFileName + formatDate + random1 + extension;
		} else {
			return formatDate + random1 + extension;
		}
	}

	public static String incFillZero(String code, Integer len) {

		return incFillZero(code, len, "0");
	}

	public static String join(Collection cs, String sep) {

		// StringBuffer sb = new StringBuffer();
		// for (Object o : cs) {
		//
		// sb.append(o.toString() + sep);
		// }
		//
		// if (sb.length() > 1) {
		//
		// sb.deleteCharAt(sb.length() - 1);
		// }
		//
		// return sb.toString();

		return join(cs, sep, 0);
	}

	public static String joinBySingleQuote(Collection cs, String sep) {
		return join(cs, sep, 1);
	}

	public static String joinByQuote(Collection cs, String sep) {
		return join(cs, sep, 2);
	}

	/**
	 * 
	 * @param cs
	 * @param sep
	 * @param type
	 *            0:无，1：‘，2：“
	 * @return 彭仁夔 于 2016年5月10日 下午4:31:01 创建
	 */
	public static String join(Collection cs, String sep, int type) {

		StringBuffer sb = new StringBuffer();
		for (Object o : cs) {

			String s = o.toString() + sep;
			if (type == 1) {
				s = StringUtil.singleQuote(o.toString()) + sep;
			} else if (type == 2) {
				s = StringUtil.quote(o.toString()) + sep;
			}
			sb.append(s);
		}

		if (sb.length() > 1) {

			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}



	/**
	 * split("dde,seem;dss","[,;]")
	 * 
	 * @author 彭仁夔于2015年8月14日下午3:54:41创建
	 * @param str
	 * @param regex
	 * @return
	 * 
	 */
	public static List<String> split(String str, String regex) {
		List<String> strlist = new ArrayList<String>();
		if (StringUtil.IsNotNullOrEmpty(str)) {
			String[] as = str.split(regex);
			if (as != null) {
				for (String s : as) {
					strlist.add(s);
				}
			}
		}
		return strlist;
	}

	/**
	 * <pre>
	 * 通过ch分隔字符把字符串s分隔，
	 * 从开始向后数，取第count个之后的所有内容并返回
	 * 如果没有找到分隔字符，返回所有内容
	 * 	String str = "instruction/radiodialog/singleselectlist.ftl";
	 * 		str = StrUtils.afterStr(str, "/", 1);
	 * 		返回radiodialog/singleselectlist.ftl
	 * </pre>
	 * 
	 * @param s 内容
	 * @param ch 分隔字符
	 * @param count
	 *            取第count个之后
	 * @return
	 */
	public static String afterStr(String s, String ch, int count) {
		if (IsNullOrEmpty(s)) {
			return s;
		}
		String temp = s;
		for (int i = 0; i < count; i++) {
			int index = temp.indexOf(ch);
			if (index == -1) {
				return temp;
			}
			temp = temp.substring(index + 1);
		}
		return temp;
	}

	/**
	 * <pre>
	 * 通过ch分隔字符把字符串s分隔成几部分，
	 * 从末尾向前数，取第count个的内容并返回，
	 * 如果没有找到分隔字符，返回所有内容
	 * 如 afterStrSub(getPath(), "/", 1) 取得全路径中文件名
	 * </pre>
	 * 
	 * @param s 内容
	 * @param ch
	 *            分隔字符
	 * @param count
	 *            末尾向前数，取第count个
	 * @return
	 */
	public static String afterStrSub(String s, String ch, int count) {
		if (IsNullOrEmpty(s)) {
			return s;
		}
		String temp = s;
		String temp1 = "";
		for (int i = 0; i < count; i++) {
			int index = temp.lastIndexOf(ch);
			if (index == -1) {
				return temp;
			}

			if (temp1.equals("")) {
				temp1 = temp.substring(index + 1);
			} else {
				temp1 = temp.substring(index + 1) + "/" + temp1;
			}
			temp = temp.substring(0, index);
		}
		return temp1;
	}

	public static String frontStr(String s, String ch, int count) {
		if (IsNullOrEmpty(s)) {
			return s;
		}

		String temp = s;
		for (int i = 0; i < count; i++) {
			int index = temp.lastIndexOf(ch);
			if (index == -1) {
				return null;
			}
			temp = temp.substring(0, index);
		}
		return temp;
	}

	public static String frontStrPre(String s, String ch, int count) {
		if (IsNullOrEmpty(s)) {
			return s;
		}
		String temp = s;
		String temp1 = "";
		for (int i = 0; i < count; i++) {
			int index = temp.indexOf(ch);
			if (index == -1) {
				return null;
			}
			if (temp1.equals("")) {
				temp1 = temp.substring(0, index);
			} else {
				temp1 = temp.substring(0, index) + "/" + temp1;
			}
			temp = temp.substring(index + 1);
		}
		return temp1;
	}

	/**
	 * 把路径中/,//,\,\\等都转换为/， 如果路径中第一个字符是/,去掉它
	 * 
	 * @param path
	 * @return
	 */
	public static String buildPath(String path) {
		return buildPath(path, false);
	}

	/**
	 * 把路径中/,//,\,\\等都转换为/，
	 * 
	 * @param path
	 * @param needhead
	 *            false 如果路径中第一个字符是/,去掉它
	 * @return
	 */
	public static String buildPath(String path, boolean needhead) {
		if (IsNullOrEmpty(path)) {
			return path;
		} else {
			path = path.replace("\\\\", "/").replace("\\", "/")
					.replace("///", "/").replace("//", "/");
			if (needhead == false) {
				if (path.charAt(0) == '\\' | path.charAt(0) == '/') {
					path = path.substring(1);

				}
			}

			path = path.replaceFirst(":/", "://");
			// path = StringUtil.replaceString(path, "[:][/]", "://");
			return path;
		}

	}

	/**
	 * 对路径进行处理把路径中/,//,\,\\等都转换为seperate，
	 * 
	 * @param path
	 * @param needhead
	 *            false 如果路径中第一个字符是seperate,去掉它
	 * @param needfoot
	 *            true 如果路径中最后一个字符不是seperate,加上它
	 * @param seperate 分隔字符
	 *            ，一般为/,当然可以指定
	 * @return
	 */
	public static String buildPath(String path, boolean needhead,
			boolean needfoot, String seperate) {
		if (IsNullOrEmpty(path)) {
			return path;
		} else {
			path = path.replace("\\\\", seperate).replace("\\", seperate)
					.replace("///", seperate).replace("//", seperate);
			if (needhead == false) {
				if (path.charAt(0) == '\\' || path.charAt(0) == '/') {
					path = path.substring(1);

				}
			}
			if (needfoot == true) {
				int lastindex = path.length() - 1;
				if (path.charAt(lastindex) == '\\'
						|| path.charAt(lastindex) == '/') {

				} else {
					path = path + seperate;
				}
			}

			return path;
		}

	}

	/**
	 * List中包括给定的字符串，区分大小写
	 * 
	 * @param arr
	 * @param s
	 * @return
	 */
	public static boolean contain(List<String> arr, String s) {
		if (arr == null || arr.size() < 1 || IsNullOrEmpty(s))
			return false;
		for (String str : arr) {
			if (str.equals(s)) {
				return true;

			}
		}
		return false;
	}

	/**
	 * Arr中包括给定的字符串，区分大小写
	 * 
	 * @param arr
	 * @param s
	 * @return
	 */
	public static boolean contain(String[] arr, String s) {
		if (arr == null || arr.length < 1 || IsNullOrEmpty(s))
			return false;
		for (String str : arr) {
			if (str.equals(s)) {
				return true;

			}
		}
		return false;
	}

	/**
	 * s字符串包含 arr中的某个字符串
	 * 
	 * @param arr
	 * @param s
	 * @return
	 */
	public static boolean containPre(String[] arr, String s) {
		if (arr == null || arr.length < 1 || isNullOrEmpty(s))
			return false;
		for (String str : arr) {
			if (s.startsWith(str)) {
				return true;

			}
		}
		return false;
	}

	/**
	 * arr中的某个字符串包含s字符串
	 * 
	 * @param arr
	 * @param s
	 * @return
	 */
	public static boolean containStart(List<String> arr, String s) {
		if (arr == null || arr.size() < 1 || IsNullOrEmpty(s))
			return false;
		for (String str : arr) {
			if (str.startsWith(s)) {
				return true;

			}
		}
		return false;
	}

	/**
	 * arr中的某个字符串包含s字符串
	 * 
	 * @param arr
	 * @param s
	 * @return
	 */
	public static boolean containStart(String[] arr, String s) {
		if (arr == null || arr.length < 1 || IsNullOrEmpty(s))
			return false;
		for (String str : arr) {
			if (s.startsWith(str)) {
				return true;

			}
		}
		return false;
	}

	/**
	 * 把字符串中的\\r\\n|\\r|\\n换成\n
	 * 
	 * @param text
	 * @return
	 */
	public static String convertLineSep(String text) {
		return text.replaceAll("\\r\\n|\\r|\\n", "\n");

	}

	/**
	 * 把字符串中的\\r\\n|\\r|\\n换成demiliter
	 * 
	 * @param text
	 * @param demiliter
	 * @return
	 */
	public static String convertLineSep(String text, String demiliter) {
		return text.replaceAll("\\r\\n|\\r|\\n", demiliter);

	}

	/**
	 * 替换字符串中特殊字符,主要用于html转换为纯文本
	 */
	public static String encodeString(String strData) {
		if (strData == null) {
			return "";
		}
		strData = replaceString(strData, "&", "&amp;");
		strData = replaceString(strData, "<", "&lt;");
		strData = replaceString(strData, ">", "&gt;");
		strData = replaceString(strData, "&apos;", "&apos;");
		strData = replaceString(strData, "\"", "&quot;");
		return strData;
	}

	/**
	 * 还原字符串中特殊字符，主要用于纯文本转换为html
	 */
	public static String decodeString(String strData) {
		strData = replaceString(strData, "&lt;", "<");
		strData = replaceString(strData, "&gt;", ">");
		strData = replaceString(strData, "&apos;", "&apos;");
		strData = replaceString(strData, "&quot;", "\"");
		strData = replaceString(strData, "&amp;", "&");
		return strData;
	}

	public static String defaultValue(String str) {
		return defaultValue(str, "");
	}

	public static String defaultValue(String str, String defalutv) {
		if (str == null)
			return defalutv;
		else
			return str;
	}

	/**
	 * 将异常信息转化成字符串*
	 * 
	 * @param t
	 * @return
	 * @throws IOException
	 * 
	 * 
	 */

	public static String exception(Throwable t) {
		try {
			if (t == null)
				return null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				t.printStackTrace(new PrintStream(baos));
			} finally {
				baos.close();
			}
			return baos.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取异常的全部信息
	 * 
	 * @param e
	 * @return
	 */
	public static String getFullErrorMessage(Throwable e) {
		StringBuffer buffer = new StringBuffer();
		StackTraceElement[] stacktrace = e.getStackTrace();
		buffer.append("Caused by: " + e + "\n");
		for (StackTraceElement tmp : stacktrace) {
			buffer.append("\tat " + tmp.toString() + "\n");
		}
		return buffer.toString();
	}

	/**
	 * 取得文件名的后缀
	 * 
	 * @param filename
	 * @return
	 */
	public static String getExt(String filename) {
		if (filename == null)
			return "";
		int index = filename.lastIndexOf(".");
		if (index > 0) {
			String ext = filename.substring(index + 1);
			return ext;
		} else {
			return "";
		}

	}

	public static String getExtDot(String filename) {
		if (filename == null)
			return "";
		int index = filename.lastIndexOf(".");
		if (index > -1) {
			String ext = filename.substring(index);
			return ext;
		} else {
			return "";
		}

	}

	/**
	 * s在Arr数组中第几个
	 * 
	 * @param arr
	 * @param s
	 * @return
	 */
	public static int indexOfArr(String[] arr, String s) {
		if (arr == null || arr.length < 1 || IsNullOrEmpty(s))
			return -1;
		for (int i = 0; i < arr.length; i++) {
			if (s.equals(arr[i])) {
				return i;
			}
		}
		return -1;
	}

	// 2. InputStream --> String
	public static String inputStream2NewLineString(InputStream is) {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		try {
			while ((line = in.readLine()) != null) {
				buffer.append(line);
				buffer.append(NEWLINE);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buffer.toString();
	}

	// 2. InputStream --> String
	public static String inputStream2NewLineString(InputStream is,
			String encoding) {
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(is, encoding);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		BufferedReader in = new BufferedReader(isr);
		StringBuffer buffer = new StringBuffer();
		String line = "";
		try {
			while ((line = in.readLine()) != null) {
				buffer.append(line);
				buffer.append(NEWLINE);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buffer.toString();
	}

	// 2. InputStream --> String
	public static String inputStream2String(InputStream is) {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		try {
			while ((line = in.readLine()) != null) {
				buffer.append(line);
				// buffer.append(NEWLINE);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}

	public static boolean isChanged(Object o1, Object o2) {
		String s1 = "";
		String s2 = "";
		if (o1 != null) {
			s1 = o1.toString();
		}
		if (o2 != null) {
			s2 = o2.toString();
		}
		return isChanged(s1, s2);
	}

	@Deprecated
	public static boolean IsNotNullOrEmpty(String str) {
		return !IsNullOrEmpty(str);

	}

	public static boolean isNotNullOrEmpty(String str) {
		return !isNullOrEmpty(str);
	}

	@Deprecated
	public static boolean IsNullOrEmpty(String str) {

		// if (str == null) {
		// return true;
		// }
		// if ("".equals(str.trim())) {
		// return true;
		// }
		// return false;
		return isNullOrEmpty(str);
	}

	public static boolean isNullOrEmpty(String str) {

		if (str == null) {
			return true;
		}
		if ("".equals(str.trim())) {
			return true;
		}
		return false;
	}

	public static boolean isNum(String str) {
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}

	public static boolean isNumeric(String text) {
		for (int i = 0; i < text.length(); i++) {
			char chr = text.charAt(i);
			if (!(chr >= '0' && chr <= '9')) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 去掉left边的指定的trimChar
	 * 
	 * @param str
	 * @param trimChar
	 * @return
	 */
	public static String lTrim(String str, char trimChar) {
		int cnt = str.length();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c != trimChar) {
				cnt = i;
				break;
			}
		}
		return str.substring(cnt);
	}

	/**
	 * 去掉right边的指定的trimChar
	 * 
	 * @param str
	 * @param trimChar
	 * @return
	 */
	public static String rTrim(String str, char trimChar) {
		int cnt = 0;
		for (int i = str.length() - 1; i >= 0; i--) {
			char c = str.charAt(i);
			if (c != trimChar) {
				cnt = i + 1;
				break;
			}
		}
		return str.substring(0, cnt);
	}

//	public static void main(String[] args) {
//		// utils.subStrUnCamel(entity.name,2,true)
//		String p = "http://pathd\\//de//dessss";
//		String s = StringUtil.buildPath(p, false);
//		System.out.println(s);
//
//		// String str = "instruction/radiodialog/singleselectlist.ftl";
//		// str = StringUtil.frontStr(StringUtil.buildPath(str), "/", 1);
//		// System.out.println(str);
//	}
//
//	

	/**
	 * 把o转换为字符串，并加上引号
	 * 
	 * @param o
	 * @return
	 */
	public static String quote(Object o) {
		if (o == null) {
			return "\"\"";
		}
		return "\"" + o.toString() + "\"";

	}

	public static String singleQuote(Object o) {
		if (o == null) {
			return "\'\'";
		}
		return "\'" + o.toString() + "\'";

	}

	public static String unQuote(Object o) {
		if (o == null) {
			return null;
		}
		String vString = o.toString();
		if (StringUtil.IsNullOrEmpty(vString))
			return null;
		String vString2 = vString.replaceAll("\"", "").replaceAll("'", "");
		return vString2;

	}

	/**
	 * 替换一个字符串中的某些指定字符
	 * 
	 * @param strData
	 *            String 原始字符串
	 * @param regex
	 *            String 要替换的字符串
	 * @param replacement
	 *            String 替代字符串
	 * @return String 替换后的字符串
	 */
	public static String replaceString(String strData, String regex,
			String replacement) {
		// if (strData == null || "".equals(strData) || regex == null
		// || "".equals(regex)) {
		// return null;
		// }
		// int index = strData.indexOf(regex);
		// // String strNew = "";
		// StringBuilder sBuilder = new StringBuilder();
		// if (index >= 0) {
		// while (index >= 0) {
		// sBuilder.append(strData.substring(0, index));
		// sBuilder.append(replacement);
		// // strNew += strData.substring(0, index) + replacement;
		// strData = strData.substring(index + regex.length());
		// index = strData.indexOf(regex);
		// }
		// sBuilder.append(strData);
		// return sBuilder.toString();
		// // strNew += strData;
		// // return strNew;
		// }
		// return strData;
		return replace(strData, regex, replacement);
	}

	public static String replace(String strData, String regex,
			String replacement) {
		if (strData == null || "".equals(strData) || regex == null
				|| "".equals(regex)) {
			return null;
		}
		int index = strData.indexOf(regex);
		// String strNew = "";
		StringBuilder sBuilder = new StringBuilder();
		if (index >= 0) {
			while (index >= 0) {
				sBuilder.append(strData.substring(0, index));
				sBuilder.append(replacement);
				// strNew += strData.substring(0, index) + replacement;
				strData = strData.substring(index + regex.length());
				index = strData.indexOf(regex);
			}
			sBuilder.append(strData);
			return sBuilder.toString();
			// strNew += strData;
			// return strNew;
		}
		return strData;
	}

	public static String replace(String strData, String regex,
			String replacement, int pos) {
		if (strData == null || "".equals(strData) || regex == null
				|| "".equals(regex)) {
			return strData;
		}
		int index = getPos(strData, regex, pos);
		if (index < 0) {
			return strData;
		}
		StringBuilder sb = new StringBuilder();
		// 前一部分
		sb.append(strData.substring(0, index));
		// 中间替换的
		sb.append(replacement);
		// 后面
		strData = strData.substring(index + regex.length());
		sb.append(strData);
		return sb.toString();
	}

	/**
	 * 取得指定regex出现的位置
	 * 
	 * @param str
	 * @param regex
	 * @param count
	 *            从0开始
	 * @return 彭仁夔 于 2016年5月29日 上午8:42:18 创建
	 */
	public static int getPos(String str, String regex, int count) {
		Matcher slashMatcher = Pattern.compile(regex).matcher(str);
		int mIdx = 0;
		while (slashMatcher.find()) {
			if (mIdx == count) {// 当regex第count次出现的位置
				break;
			}
			mIdx++;
		}
		return slashMatcher.start();
	}

	public static List<Integer> getPos(String str, String regex) {
		Matcher slashMatcher = Pattern.compile(regex).matcher(str);
		List<Integer> list = new ArrayList<Integer>();
		while (slashMatcher.find()) {
			list.add(slashMatcher.start());
		}
		return list;
	}

	public static String replace(String strData, String regex,
			String replacement, List<Integer> poss) {
		if (strData == null || "".equals(strData) || regex == null
				|| "".equals(regex)) {
			return strData;
		}
		int revomeLen = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < poss.size(); i++) {
			Integer p = poss.get(i);
			int index = p - revomeLen;
			int subIndex = index + regex.length();
			// 前一部分
			sb.append(strData.substring(0, index));
			// 中间替换的
			sb.append(replacement);
			// 后面
			strData = strData.substring(subIndex);
			revomeLen = revomeLen + subIndex;
		}
		sb.append(strData);

		return sb.toString();
	}

	/**
	 * 把fullpath通过"/"进行分隔
	 * 
	 * @param fullpath
	 * @return
	 */
	public static String[] splitPath(String fullpath) {
		return splitPath(fullpath, "/");
	}

	/**
	 * 把fullpath通过split进行分隔
	 * 
	 * @param fullpath
	 * @param split
	 * @return
	 */
	public static String[] splitPath(String fullpath, String split) {
		if (StringUtil.IsNullOrEmpty(fullpath))
			return null;
		fullpath = StringUtil.buildPath(fullpath);
		String[] ps = fullpath.split(split);

		List<String> psList = new ArrayList<String>();
		for (String s : ps) {
			if (StringUtil.IsNullOrEmpty(s)) {
			} else {
				psList.add(s);
			}
		}

		String[] ps1 = new String[psList.size()];
		psList.toArray(ps1);
		return ps1;
	}



	/**
	 * 把单词转为复数
	 * 
	 * @param word
	 * @return
	 */
	public static String pluralize(String word) {
		String newword = Inflector.getInstance().pluralize(word);
		if (word.equals(newword)) {
			return word + "s";
		} else {
			return newword;
		}
	}

	/**
	 * 把单词转为单数
	 * 
	 * @param word
	 * @return
	 */
	public static String singularize(String word) {
		String newword = Inflector.getInstance().singularize(word);
		return newword;

	}
}

class Inflector {

	// Pfft, can't think of a better name, but this is needed to avoid the price
	// of initializing the pattern on each call.
	private static final Pattern UNDERSCORE_PATTERN_1 = Pattern
			.compile("([A-Z]+)([A-Z][a-z])");
	private static final Pattern UNDERSCORE_PATTERN_2 = Pattern
			.compile("([a-z\\d])([A-Z])");

	private static List<RuleAndReplacement> plurals = new ArrayList<RuleAndReplacement>();
	private static List<RuleAndReplacement> singulars = new ArrayList<RuleAndReplacement>();
	private static List<String> uncountables = new ArrayList<String>();

	private static Inflector instance; // (Pseudo-)Singleton instance.

	public static Inflector getInstance() {
		if (instance == null) {
			instance = new Inflector();
		}
		return instance;
	}

	public static void irregular(String singular, String plural) {
		plural(singular, plural);
		singular(plural, singular);
	}

	public static void plural(String rule, String replacement) {
		plurals.add(0, new RuleAndReplacement(rule, replacement));
	}

	public static void singular(String rule, String replacement) {
		singulars.add(0, new RuleAndReplacement(rule, replacement));
	}

	public static void uncountable(String... words) {
		for (String word : words) {
			uncountables.add(word);
		}
	}

	private Inflector() {
		// Woo, you can't touch me.

		initialize();
	}

	private void initialize() {
		plural("$", "s");
		plural("s$", "s");
		plural("(ax|test)is$", "$1es");
		plural("(octop|vir)us$", "$1i");
		plural("(alias|status)$", "$1es");
		plural("(bu)s$", "$1es");
		plural("(buffal|tomat)o$", "$1oes");
		plural("([ti])um$", "$1a");
		plural("sis$", "ses");
		plural("(?:([^f])fe|([lr])f)$", "$1$2ves");
		plural("(hive)$", "$1s");
		plural("([^aeiouy]|qu)y$", "$1ies");
		plural("([^aeiouy]|qu)ies$", "$1y");
		plural("(x|ch|ss|sh)$", "$1es");
		plural("(matr|vert|ind)ix|ex$", "$1ices");
		plural("([m|l])ouse$", "$1ice");
		plural("(ox)$", "$1en");
		plural("(quiz)$", "$1zes");

		singular("s$", "");
		singular("(n)ews$", "$1ews");
		singular("([ti])a$", "$1um");
		singular(
				"((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$",
				"$1$2sis");
		singular("(^analy)ses$", "$1sis");
		singular("([^f])ves$", "$1fe");
		singular("(hive)s$", "$1");
		singular("(tive)s$", "$1");
		singular("([lr])ves$", "$1f");
		singular("([^aeiouy]|qu)ies$", "$1y");
		singular("(s)eries$", "$1eries");
		singular("(m)ovies$", "$1ovie");
		singular("(x|ch|ss|sh)es$", "$1");
		singular("([m|l])ice$", "$1ouse");
		singular("(bus)es$", "$1");
		singular("(o)es$", "$1");
		singular("(shoe)s$", "$1");
		singular("(cris|ax|test)es$", "$1is");
		singular("([octop|vir])i$", "$1us");
		singular("(alias|status)es$", "$1");
		singular("^(ox)en", "$1");
		singular("(vert|ind)ices$", "$1ex");
		singular("(matr)ices$", "$1ix");
		singular("(quiz)zes$", "$1");

		irregular("person", "people");
		irregular("man", "men");
		irregular("child", "children");
		irregular("sex", "sexes");
		irregular("move", "moves");

		uncountable(new String[] { "equipment", "information", "rice", "money",
				"species", "series", "fish", "sheep" });
	}

	/**
	 * 变复数
	 * 
	 * @param word
	 * @return
	 */
	public String pluralize(String word) {
		if (uncountables.contains(word.toLowerCase())) {
			return word;
		}
		return replaceWithFirstRule(word, plurals);
	}

	private String replaceWithFirstRule(String word,
			List<RuleAndReplacement> ruleAndReplacements) {

		for (RuleAndReplacement rar : ruleAndReplacements) {
			String rule = rar.getRule();
			String replacement = rar.getReplacement();

			// Return if we find a match.
			Matcher matcher = Pattern.compile(rule, Pattern.CASE_INSENSITIVE)
					.matcher(word);
			if (matcher.find()) {
				return matcher.replaceAll(replacement);
			}
		}
		return word;
	}

	/**
	 * 变单数
	 * 
	 * @param word
	 * @return
	 */
	public String singularize(String word) {
		if (uncountables.contains(word.toLowerCase())) {
			return word;
		}
		return replaceWithFirstRule(word, singulars);
	}

	@SuppressWarnings("unchecked")
	public String tableize(Class klass) {
		// Strip away package name - we only want the 'base' class name.
		String className = klass.getName().replace(
				klass.getPackage().getName() + ".", "");
		return tableize(className);
	}

	public String tableize(String className) {
		return pluralize(underscore(className));
	}

	public String underscore(String camelCasedWord) {

		// Regexes in Java are fucking stupid...
		String underscoredWord = UNDERSCORE_PATTERN_1.matcher(camelCasedWord)
				.replaceAll("$1_$2");
		underscoredWord = UNDERSCORE_PATTERN_2.matcher(underscoredWord)
				.replaceAll("$1_$2");
		underscoredWord = underscoredWord.replace('-', '_').toLowerCase();

		return underscoredWord;
	}

//	public static void main(String[] args) {
//		// utils.subStrUnCamel(entity.name,2,true)
//
//		String str = "instruction/radiodialog/singleselectlist.ftl";
//		str = StringUtil.afterStrSub(StringUtil.buildPath(str), "/", 1);
//		System.out.println(str);
//	}
}

// Ugh, no open structs in Java (not-natively at least).
class RuleAndReplacement {
	private String rule;
	private String replacement;

	public RuleAndReplacement(String rule, String replacement) {
		this.rule = rule;
		this.replacement = replacement;
	}

	public String getReplacement() {
		return replacement;
	}

	public String getRule() {
		return rule;
	}

	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}
}

class StringEncoding {
	public static final class Encoding {
		private String name;
		private String encoding;

		public Encoding(String name, String encoding) {
			this.name = name;
			this.encoding = encoding;
		}

		public String getEncoding() {
			return encoding;
		}

		public String getName() {
			return name;
		}
	}

	private final static int GB2312 = 0;

	private final static int GBK = 1;

	private final static int BIG5 = 2;

	private final static int UTF8 = 3;

	private final static int UNICODE = 4;

	private final static int EUC_KR = 5;

	private final static int SJIS = 6;

	private final static int EUC_JP = 7;

	private final static int ASCII = 8;

	private final static int UNKNOWN = 9;

	private final static int TOTALT = 10;

	private static Encoding[] encodings;

	private static Encoding check(final int result) {
		if (result == -1) {
			return encodings[UNKNOWN];
		}
		return encodings[result];

	}

	/** */
	/**
	 * 检查为ascii的或然率
	 * 
	 * @param content
	 * @return
	 */
	private static int getProbabilityByASCIIEncoding(byte[] content) {
		int score = 75;
		int i, rawtextlen;

		rawtextlen = content.length;

		for (i = 0; i < rawtextlen; i++) {
			if (content[i] < 0) {
				score = score - 5;
			} else if (content[i] == (byte) 0x1B) { // ESC (used by ISO 2022)
				score = score - 5;
			}
			if (score <= 0) {
				return 0;
			}
		}
		return score;
	}

	private int[][] GB2312format;
	private int[][] GBKformat;
	private int[][] Big5format;

	private int[][] EUC_KRformat;

	private int[][] JPformat;

	static {
		initEncodings();
	}

	private static void initEncodings() {

		encodings = new Encoding[TOTALT];

		int i = 0;
		encodings[i++] = new Encoding("GB2312", "GB2312");
		encodings[i++] = new Encoding("GBK", "GBK");
		encodings[i++] = new Encoding("BIG5", "BIG5");
		encodings[i++] = new Encoding("UTF8", "UTF-8");
		encodings[i++] = new Encoding("UNICODE(UTF-16)", "UTF-16");
		encodings[i++] = new Encoding("EUC-KR", "EUC-KR");
		encodings[i++] = new Encoding("Shift-JIS", "Shift_JIS");
		encodings[i++] = new Encoding("EUC-JP", "EUC-JP");
		encodings[i++] = new Encoding("ASCII", "ASCII");
		encodings[i++] = new Encoding("ISO8859-1", "ISO8859-1");

	}

	public StringEncoding() {
		init();
	}

	public Encoding getEncoding(final byte[] data) {
		return check(getEncodingValue(data));
	}

	private int getEncodingValue(byte[] content) {
		if (content == null)
			return -1;
		int[] scores;
		int index, maxscore = 0;
		int encoding = UNKNOWN;
		scores = new int[TOTALT];
		// 分配或然率
		scores[GB2312] = getProbabilityByGB2312Encoding(content);
		scores[GBK] = getProbabilityByGBKEncoding(content);
		scores[BIG5] = getProbabilityByBIG5Encoding(content);
		scores[UTF8] = getProbabilityByUTF8Encoding(content);
		scores[UNICODE] = getProbabilityByUTF16Encoding(content);
		scores[EUC_KR] = getProbabilityByEUC_KREncoding(content);
		scores[ASCII] = getProbabilityByASCIIEncoding(content);
		scores[SJIS] = getProbabilityBySJISEncoding(content);
		scores[EUC_JP] = getProbabilityByEUC_JPEncoding(content);
		scores[UNKNOWN] = 0;

		// 概率比较
		for (index = 0; index < TOTALT; index++) {
			if (scores[index] > maxscore) {
				// 索引
				encoding = index;
				// 最大几率
				maxscore = scores[index];
			}
		}
		// 返回或然率大于50%的数据
		if (maxscore <= 50) {
			encoding = UNKNOWN;
		}
		return encoding;
	}

	/** */
	/**
	 * 解析为big5的或然率
	 * 
	 * @param content
	 * @return
	 */
	private int getProbabilityByBIG5Encoding(byte[] content) {
		int i, rawtextlen = 0;
		int dbchars = 1, bfchars = 1;
		float rangeval = 0, formatval = 0;
		long bfformat = 0, totalformat = 1;
		int row, column;
		rawtextlen = content.length;
		for (i = 0; i < rawtextlen - 1; i++) {
			if (content[i] >= 0) {
			} else {
				dbchars++;
				if ((byte) 0xA1 <= content[i]
						&& content[i] <= (byte) 0xF9
						&& (((byte) 0x40 <= content[i + 1] && content[i + 1] <= (byte) 0x7E) || ((byte) 0xA1 <= content[i + 1] && content[i + 1] <= (byte) 0xFE))) {
					bfchars++;
					totalformat += 500;
					row = content[i] + 256 - 0xA1;
					if (0x40 <= content[i + 1] && content[i + 1] <= 0x7E) {
						column = content[i + 1] - 0x40;
					} else {
						column = content[i + 1] + 256 - 0x61;
					}
					if (Big5format[row][column] != 0) {
						bfformat += Big5format[row][column];
					} else if (3 <= row && row <= 37) {
						bfformat += 200;
					}
				}
				i++;
			}
		}
		rangeval = 50 * ((float) bfchars / (float) dbchars);
		formatval = 50 * ((float) bfformat / (float) totalformat);

		return (int) (rangeval + formatval);
	}

	private int getProbabilityByEUC_JPEncoding(byte[] content) {
		int i, rawtextlen = 0;

		int dbchars = 1, jpchars = 1;
		long jpformat = 0, totalformat = 1;
		float rangeval = 0, formatval = 0;
		int row, column;

		rawtextlen = content.length;
		for (i = 0; i < rawtextlen - 1; i++) {
			if (content[i] >= 0) {
			} else {
				dbchars++;
				if ((byte) 0xA1 <= content[i] && content[i] <= (byte) 0xFE
						&& (byte) 0xA1 <= content[i + 1]
						&& content[i + 1] <= (byte) 0xFE) {
					jpchars++;
					totalformat += 500;
					row = content[i] + 256 - 0xA1;
					column = content[i + 1] + 256 - 0xA1;
					if (JPformat[row][column] != 0) {
						jpformat += JPformat[row][column];
					} else if (15 <= row && row < 55) {
						jpformat += 0;
					}

				}
				i++;
			}
		}
		rangeval = 50 * ((float) jpchars / (float) dbchars);
		formatval = 50 * ((float) jpformat / (float) totalformat);

		return (int) (rangeval + formatval);
	}

	/** */
	/**
	 * 检查为euc_kr的或然率
	 * 
	 * @param content
	 * @return
	 */
	private int getProbabilityByEUC_KREncoding(byte[] content) {
		int i, rawtextlen = 0;

		int dbchars = 1, krchars = 1;
		long krformat = 0, totalformat = 1;
		float rangeval = 0, formatval = 0;
		int row, column;
		rawtextlen = content.length;
		for (i = 0; i < rawtextlen - 1; i++) {
			if (content[i] >= 0) {
			} else {
				dbchars++;
				if ((byte) 0xA1 <= content[i] && content[i] <= (byte) 0xFE
						&& (byte) 0xA1 <= content[i + 1]
						&& content[i + 1] <= (byte) 0xFE) {
					krchars++;
					totalformat += 500;
					row = content[i] + 256 - 0xA1;
					column = content[i + 1] + 256 - 0xA1;
					if (EUC_KRformat[row][column] != 0) {
						krformat += EUC_KRformat[row][column];
					} else if (15 <= row && row < 55) {
						krformat += 0;
					}

				}
				i++;
			}
		}
		rangeval = 50 * ((float) krchars / (float) dbchars);
		formatval = 50 * ((float) krformat / (float) totalformat);

		return (int) (rangeval + formatval);
	}

	/** */
	/**
	 * gb2312数据或然率计算
	 * 
	 * @param content
	 * @return
	 */
	private int getProbabilityByGB2312Encoding(byte[] content) {
		int i, rawtextlen = 0;

		int dbchars = 1, gbchars = 1;
		long gbformat = 0, totalformat = 1;
		float rangeval = 0, formatval = 0;
		int row, column;

		// 检查是否在亚洲汉字范围内
		rawtextlen = content.length;
		for (i = 0; i < rawtextlen - 1; i++) {
			if (content[i] >= 0) {
			} else {
				dbchars++;
				// 汉字GB码由两个字节组成，每个字节的范围是0xA1 ~ 0xFE
				if ((byte) 0xA1 <= content[i] && content[i] <= (byte) 0xF7
						&& (byte) 0xA1 <= content[i + 1]
						&& content[i + 1] <= (byte) 0xFE) {
					gbchars++;
					totalformat += 500;
					row = content[i] + 256 - 0xA1;
					column = content[i + 1] + 256 - 0xA1;
					if (GB2312format[row][column] != 0) {
						gbformat += GB2312format[row][column];
					} else if (15 <= row && row < 55) {
						// 在gb编码范围
						gbformat += 200;
					}

				}
				i++;
			}
		}
		rangeval = 50 * ((float) gbchars / (float) dbchars);
		formatval = 50 * ((float) gbformat / (float) totalformat);

		return (int) (rangeval + formatval);
	}

	/** */
	/**
	 * gb2312或然率计算
	 * 
	 * @param content
	 * @return
	 */
	private int getProbabilityByGBKEncoding(byte[] content) {
		int i, rawtextlen = 0;

		int dbchars = 1, gbchars = 1;
		long gbformat = 0, totalformat = 1;
		float rangeval = 0, formatval = 0;
		int row, column;
		rawtextlen = content.length;
		for (i = 0; i < rawtextlen - 1; i++) {
			if (content[i] >= 0) {
			} else {
				dbchars++;
				if ((byte) 0xA1 <= content[i] && content[i] <= (byte) 0xF7
						&& // gb范围
						(byte) 0xA1 <= content[i + 1]
						&& content[i + 1] <= (byte) 0xFE) {
					gbchars++;
					totalformat += 500;
					row = content[i] + 256 - 0xA1;
					column = content[i + 1] + 256 - 0xA1;
					if (GB2312format[row][column] != 0) {
						gbformat += GB2312format[row][column];
					} else if (15 <= row && row < 55) {
						gbformat += 200;
					}

				} else if ((byte) 0x81 <= content[i]
						&& content[i] <= (byte) 0xFE && // gb扩展区域
						(((byte) 0x80 <= content[i + 1] && content[i + 1] <= (byte) 0xFE) || ((byte) 0x40 <= content[i + 1] && content[i + 1] <= (byte) 0x7E))) {
					gbchars++;
					totalformat += 500;
					row = content[i] + 256 - 0x81;
					if (0x40 <= content[i + 1] && content[i + 1] <= 0x7E) {
						column = content[i + 1] - 0x40;
					} else {
						column = content[i + 1] + 256 - 0x40;
					}
					if (GBKformat[row][column] != 0) {
						gbformat += GBKformat[row][column];
					}
				}
				i++;
			}
		}
		rangeval = 50 * ((float) gbchars / (float) dbchars);
		formatval = 50 * ((float) gbformat / (float) totalformat);
		return (int) (rangeval + formatval) - 1;
	}

	private int getProbabilityBySJISEncoding(byte[] content) {
		int i, rawtextlen = 0;

		int dbchars = 1, jpchars = 1;
		long jpformat = 0, totalformat = 1;
		float rangeval = 0, formatval = 0;
		int row, column, adjust;

		rawtextlen = content.length;
		for (i = 0; i < rawtextlen - 1; i++) {
			if (content[i] >= 0) {
			} else {
				dbchars++;
				if (i + 1 < content.length
						&& (((byte) 0x81 <= content[i] && content[i] <= (byte) 0x9F) || ((byte) 0xE0 <= content[i] && content[i] <= (byte) 0xEF))
						&& (((byte) 0x40 <= content[i + 1] && content[i + 1] <= (byte) 0x7E) || ((byte) 0x80 <= content[i + 1] && content[i + 1] <= (byte) 0xFC))) {
					jpchars++;
					totalformat += 500;
					row = content[i] + 256;
					column = content[i + 1] + 256;
					if (column < 0x9f) {
						adjust = 1;
						if (column > 0x7f) {
							column -= 0x20;
						} else {
							column -= 0x19;
						}
					} else {
						adjust = 0;
						column -= 0x7e;
					}
					if (row < 0xa0) {
						row = ((row - 0x70) << 1) - adjust;
					} else {
						row = ((row - 0xb0) << 1) - adjust;
					}

					row -= 0x20;
					column = 0x20;
					if (row < JPformat.length && column < JPformat[row].length
							&& JPformat[row][column] != 0) {
						jpformat += JPformat[row][column];
					}
					i++;
				} else if ((byte) 0xA1 <= content[i]
						&& content[i] <= (byte) 0xDF) {
				}

			}
		}
		rangeval = 50 * ((float) jpchars / (float) dbchars);
		formatval = 50 * ((float) jpformat / (float) totalformat);

		return (int) (rangeval + formatval) - 1;
	}

	/** */
	/**
	 * 检查为utf-16的或然率
	 * 
	 * @param content
	 * @return
	 */
	private int getProbabilityByUTF16Encoding(byte[] content) {

		if (content.length > 1
				&& ((byte) 0xFE == content[0] && (byte) 0xFF == content[1])
				|| ((byte) 0xFF == content[0] && (byte) 0xFE == content[1])) {
			return 100;
		}
		return 0;
	}

	/** */
	/**
	 * 在utf-8中的或然率
	 * 
	 * @param content
	 * @return
	 */
	private int getProbabilityByUTF8Encoding(byte[] content) {
		int score = 0;
		int i, rawtextlen = 0;
		int goodbytes = 0, asciibytes = 0;
		// 检查是否为汉字可接受范围
		rawtextlen = content.length;
		for (i = 0; i < rawtextlen; i++) {
			if ((content[i] & (byte) 0x7F) == content[i]) {
				asciibytes++;
			} else if (-64 <= content[i] && content[i] <= -33
					&& i + 1 < rawtextlen && -128 <= content[i + 1]
					&& content[i + 1] <= -65) {
				goodbytes += 2;
				i++;
			} else if (-32 <= content[i] && content[i] <= -17
					&& i + 2 < rawtextlen && -128 <= content[i + 1]
					&& content[i + 1] <= -65 && -128 <= content[i + 2]
					&& content[i + 2] <= -65) {
				goodbytes += 3;
				i += 2;
			}
		}

		if (asciibytes == rawtextlen) {
			return 0;
		}

		score = (int) (100 * ((float) goodbytes / (float) (rawtextlen - asciibytes)));
		// 如果不高于98则减少到零
		if (score > 98) {
			return score;
		} else if (score > 95 && goodbytes > 30) {
			return score;
		} else {
			return 0;
		}

	}

	private void init() {
		GB2312format = new int[94][94];
		GBKformat = new int[126][191];
		Big5format = new int[94][158];
		EUC_KRformat = new int[94][94];
		JPformat = new int[94][94];
	}

}

// public class StringUtil {
//
// public static boolean IsNullOrEmpty(String str) {
//
// if (str == null) {
// return true;
// }
// if (str.trim().equalsIgnoreCase("")) {
// return true;
// }
// return false;
// }
//
//
// public static String defaultValue(String v, String d) {
// if (StringUtil.IsNullOrEmpty(v)) {
// return d;
// } else {
// return v;
// }
// }
// }
