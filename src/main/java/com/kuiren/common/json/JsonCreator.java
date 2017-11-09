package com.kuiren.common.json;

import com.kuiren.common.json.convert.DirectConvert;
import com.kuiren.common.json.convert.IJsonConvert;
import com.kuiren.common.json.convert.INameConvert;
import com.kuiren.common.json.util.BeanUtil;
import com.kuiren.common.json.util.JsonUtil;
import com.kuiren.common.json.util.StringUtil;


import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.sql.Clob;
import java.util.*;

/**
 * 非常方便的Json转换类，示例：
 * <p>
 * <pre>
 * String subStr = new JsonCreator().exclude(&quot;checkCompType&quot;, &quot;plan&quot;, &quot;indicator&quot;).replace(&quot;pid&quot;, &quot;_parentId&quot;)
 * 	.replace(&quot;indexType.name&quot;, &quot;tname&quot;).add(&quot;rows&quot;, ctsiList).add(&quot;total&quot;, 3).build(plan);
 * </pre>
 *
 * @author 彭仁夔 于2012年10月21日下午6:05:14创建
 * @email:546711211@qq.com
 * @wetsite:www.mgicode.com
 * @license:GPL
 */

public class JsonCreator {
   // protected final static Logger logger = LoggerFactory.getLogger(JsonCreator.class);

    private Map<String, List<JsonConfig>> configs = new HashMap<String, List<JsonConfig>>();
    private boolean isRemoveNullProp = true;
    private JsonMatch jsonMatch;

    private String dataFormat = "yyyy-MM-dd";
    // 0:不处理，1：大写，2 小写
    private Integer upperOrLower = 0;

    private List<String> onlyNameList = new ArrayList<String>();

    private boolean onlyFlag = false;

    private Map<String, String> loopMap = new HashMap<String, String>();

    // 使用JsonConvert中需要使用限制的Class，一般为当前实体
    private Class limitClz = null;

    public JsonCreator() {
        this(true);
    }

    // public JsonCreator exclude
    public String getClob(Clob c) {
        Reader reader;
        StringBuffer sb = new StringBuffer();
        try {
            reader = c.getCharacterStream();
            BufferedReader br = new BufferedReader(reader);
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public JsonCreator(boolean removeNullProp) {
        jsonMatch = new JsonMatch();
        this.isRemoveNullProp = removeNullProp;
        configs.put("add", new ArrayList<JsonConfig>());
        configs.put("del", new ArrayList<JsonConfig>());
        configs.put("rep", new ArrayList<JsonConfig>());
        initDelconfig(Class.class);
        onlyNameList.add("actionbean");
    }

    public JsonCreator setDateFormat(String format) {
        if (format != null && !"".equals(format)) {
            dataFormat = format;
        }
        return this;
    }

    /**
     * 实体字段过多，一个一人地排除（exclude）比较麻烦，<br>
     * 使用该函数，把需要的几个字段指定即可。<br>
     * 一般用于不超过5个字段左右的使用该函数比较合适。<br>
     *
     * @param name
     * @return
     */
    public JsonCreator only(String... name) {
        for (String n : name) {
            onlyNameList.add(n);
            onlyFlag = true;
        }
        return this;
    }

    /**
     * @param flag 0:不处理，1：大写，2 小写
     * @return
     * @author:彭仁夔 于2014年12月19日上午9:26:17创建
     */
    public JsonCreator setLowerUpper(Integer flag) {
        this.upperOrLower = flag;
        return this;
    }

    /**
     * 把expr的查找的字段转换为replacedName替换的名称<br>
     *
     * @param expr         查找表达式
     * @param replacedName 替换的名称
     * @return
     */
    public JsonCreator replace(String expr, String replacedName) {
        // JsonConfig jc = jsonMatch.parse(expr);
        // jc.setAddName(replacedName);
        // jc.setType("10");
        // configs.get("rep").add(jc);
        //
        // return this;
        return replace(expr, replacedName, null);
    }

    /**
     * 把expr的查找的字段转换为INameConvert回调函数运行结果值<br>
     *
     * @param expr         查找表达式
     * @param replacedName INameConvert 转换名称的结果值
     * @return *
     */
    public JsonCreator replace(String expr, INameConvert replacedName) {
        return replace(expr, replacedName, null);
    }

    /**
     * // todo:采用ongl表达式来改造INameConvert String str = new
     * JsonCreator().replace("type", "typeidName", new IJsonConvert() {
     *
     * @Override public String convert(Object object, String name, Object val) { if
     *           (object instanceof TCommonType) { String dname = dictService
     *           .getDictNameById(val + ""); return dname; } return null; }
     *           }).build(retMsg);
     * @param expr
     * @param replacedName
     * @param value
     *
     */
    // public JsonCreator replace(String expr, String replacedName,
    // final String servername, final String methodname, final Class clz) {
    //
    // IJsonConvert jsonConvert = new IJsonConvert() {
    // @Override
    // public Object convert(Object obj, String name, Object value) {
    // if (obj.getClass() == clz) {
    // return invoke(servername, methodname, obj, name, value);
    // }
    // return null;
    //
    // }
    // };
    //
    // replace(expr, replacedName, jsonConvert);
    //
    // return this;
    // }

    /**
     * 把expr表达式查找到字段采用replacedName的名称来替换字段名，<br>
     * 采用value采用替换其字段值
     *
     * @param expr         查找字段表达式
     * @param replacedName 替换的名称
     * @param value        查找字段的值需要进行转换，一般使用IJSonConvert
     */
    public JsonCreator replace(String expr, String replacedName, Object value) {
        JsonConfig jc = jsonMatch.parse(expr);
        jc.setAddName(replacedName);
        jc.setType("10");
        jc.setValue(value);
        configs.get("rep").add(jc);

        return this;
    }

    /**
     * 既可以动态定义其转换的字段名称和值
     *
     * @param expr         查找表达式
     * @param replacedName 名称转换对象
     * @param value        值转换对象，使用使用基本类型和IJsonConvert
     * @return
     */
    public JsonCreator replace(String expr, INameConvert replacedName, Object value) {
        JsonConfig jc = jsonMatch.parse(expr);
        jc.setAddName(replacedName);
        jc.setType("10");
        jc.setValue(value);
        configs.get("rep").add(jc);

        return this;
    }

    /**
     * 把不需要的字段排除
     *
     * @param objs
     * @return
     */
    public JsonCreator exclude(Object... objs) {
        for (Object obj : objs) {
            if (obj instanceof String) {
                String s = (String) obj;
                if (s != null && !s.equals("")) {
                    JsonConfig jc = jsonMatch.parse(s);
                    jc.setType("20");
                    configs.get("del").add(jc);
                }
            } else if (obj instanceof Class) {
                if (obj != null) {
                    configs.get("del").add(initDelconfig((Class) obj));
                }
            }
        }

        return this;
    }

    private JsonConfig initDelconfig(Class<?> cls) {
        JsonConfig jc = new JsonConfig();
        jc.setTargetName("*");
        jc.setDelCls(cls);
        jc.setType("21");

        return jc;

    }

    /**
     * 在转换对象中第一层上加上指定name的指定值value
     *
     * @param name
     * @param value 基本类型的值和IJsonConvert
     * @return
     */
    public JsonCreator add(String name, Object value) {
        return add(name, name + "@^", value);
    }

    /**
     * propname只有在使用IJSonConvert回调时使用 .add("data", "children", new IJsonConvert() {
     *
     * @param name
     * @param value 需要进行转换的名称（如id采用回调函数转为name）
     * @return
     * @Override public Object convert(Object obj, String name, Object value) {
     * <p>
     * if (obj instanceof TInstance) { KeyValue keyValue=new
     * KeyValue("aa","bbb"); // o.setProcessTime(((TInstance) //
     * obj).getProcessTime()); //o.setTaskFeature("deedd"); return
     * keyValue; } return null; // return "{'xx':1,'yy':2}"; } })
     * <p>
     * add第二个参数表达式只能指定，不能采用泛指，上面采用泛指，会出现一直执行的问题， 而add(String name, Object
     * value) 则不会
     * @author:彭仁夔 于2014年10月25日上午7:17:54创建
     */
    public JsonCreator add(String name, String expr, Object value) {
        JsonConfig js = jsonMatch.parse(expr);
        js.setAddName(name);
        js.setDelName(name);
        js.setRemain1(js.getExpr());
        js.setValue(value);
        js.setType("00");// 增加中的最基本

        configs.get("add").add(js);
        return this;
    }

    /**
     * 增加属性，原来的IJsonConvert的代码太多，采用servername和methodname来代替
     *
     * @param name
     * @param expr
     * @param servername
     *            spring bean 的名称
     * @param methodname
     *            方法名
     * @return 彭仁夔 于 2016年3月28日 下午3:01:08 创建
     */
    // public JsonCreator add(String name, String expr, final String servername,
    // final String methodname, final Class clz) {
    //
    // IJsonConvert jsonConvert = new IJsonConvert() {
    // @Override
    // public Object convert(Object obj, String name, Object value) {
    // if (obj.getClass() == clz) {
    // return invoke(servername, methodname, obj, name, value);
    // }
    // return null;
    // }
    // };
    //
    // add(name, expr, jsonConvert);
    //
    // return this;
    // }

    /**
     * 把对象转换Json字符串
     *
     * @param o 对象
     * @return
     */
    public String build(Object o) {
        try {
            return buildNode("actionbean", "actionbean", o, new StringBuilder(), false, new ArrayList<String>());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 把对象转换Json数组字符串<br>
     * 有时前台需要数组,保证返回的为数组
     *
     * @param o
     * @return
     */
    public String buildArr(Object o) {
        if (o == null) {
            return "[]";
        }
        String str = build(o);
        if (str == null)
            return "";
        if (str.startsWith("["))
            return str;
        else
            return "[" + str + "]";
    }

    /**
     * 和buildArr类似，为空时返回{}
     *
     * @param o
     * @return
     */
    public String buildObj(Object o) {
        if (o == null) {
            return "{}";
        }
        String str = build(o);
        if (str == null) {
            return "{}";
        } else
            return str;
    }

    /**
     * 当o为空时，返回指定clz中字段格式<br>
     * 减少为空时的手动转换
     *
     * @param o   转换对象
     * @param clz 一般为AppRetData或RetData等相关的对象
     * @return 彭仁夔 于 2016年4月14日 上午9:09:46 创建
     */
    public String build(Object o, Class clz) {
        if (o == null) {
            try {
                o = clz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
                //logger.error(e.toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
               // logger.error(e.toString());
            }
        }
        String str = build(o);
        return str;
    }

    private boolean needBuild(String targetName) {
        return ((onlyFlag == false) || (onlyFlag == true && StringUtil.contain(onlyNameList, targetName)));
    }

    public String buildNode(String dottargetName, String targetName, Object in, StringBuilder out, boolean incollect,
                            List<String> parents) throws Exception {

        // String name = in.toString();

        // pengrk add at 2016.3.21,to solve the loop reference error
        // if (logger.isDebugEnabled()) {
        // logger.debug(name);
        // }
        // if (loopMap.containsKey(name)) {
        // 循环引用
        // } else

        if (needBuild(targetName)) {
            // pengrk add at 2016.3.21
            // loopMap.put(name, name);

            if (incollect) {
                if (targetName.startsWith("\"")) {
                    out.append("" + keyNameConvert(targetName) + "");
                } else {
                    out.append("\"" + keyNameConvert(targetName) + "\"");
                }
                out.append(":");
            }
            if (Collection.class.isAssignableFrom(in.getClass())) {

                buildCollection(dottargetName, targetName, in, out, incollect, parents);

            } else if (in.getClass().isArray()) {

                buildArray(dottargetName, targetName, in, out, incollect, parents);

            } else if (Map.class.isAssignableFrom(in.getClass())) {
                buildMap(dottargetName, targetName, in, out, incollect, parents);
            } else {

                buildObject(dottargetName, targetName, in, out, incollect, parents);
            }
        }

        return out.toString();
    }

    /**
     * @param property
     * @param flag     0：加引号，1不加引号
     * @return
     */
    private String getSimpleTypeStr(Object property, int flag) {
        if (property == null)
            return "null";
        Class<?> type = property.getClass();
        if (String.class.isAssignableFrom(type)) {
            if (flag == 1) {
                return (String) property;
            } else
                return JsonUtil.quote((String) property);
        } else if (Date.class.isAssignableFrom(type)) {
            if (this.dataFormat != null) {

                return JsonUtil.quote(JsonUtil.getDateFormat((Date) property, dataFormat));

            } else
                return "new Date(" + ((Date) property).getTime() + ")";
        } else {
            return property.toString();
        }
    }

    private void buildCollection(String dottargetName, String targetName, Object in, StringBuilder out,
                                 boolean incollect, List<String> parents) throws Exception {

        int length = ((Collection<?>) in).size(), i = 0;

        out.append("[");
        for (Object value : (Collection<?>) in) {
            if (JsonUtil.isSimpleType(value)) {
                out.append(getSimpleTypeStr(value, 0));
            } else {
                buildNode(dottargetName, targetName, value, out, false, parents);
            }
            if (i++ != (length - 1)) {
                out.append(", ");
            }
        }

        out.append("]");
    }

    private void buildArray(String dottargetName, String targetName, Object in, StringBuilder out, boolean incollect,
                            List<String> parents) throws Exception {

        int length = Array.getLength(in);

        out.append("[");
        for (int i = 0; i < length; i++) {
            Object value = Array.get(in, i);
            if (JsonUtil.isSimpleType(value)) {
                out.append(getSimpleTypeStr(value, 0));
            } else {
                buildNode(dottargetName, targetName, value, out, false, parents);
            }
            if (i != length - 1) {
                out.append(", ");
            }
        }

        out.append("]");

    }

    private Method getReadMethod(PropertyDescriptor property, Class clazz) {

        if (property == null) {
            return null;
        }
        Method m = property.getReadMethod();
        Method readMethod = null;

        if (clazz != null && clazz.getName().indexOf("$$") > -1) { // 如果是CGLIB动态生成的类
            try {
                String name = clazz.getName().substring(0, clazz.getName().indexOf("$$"));
                // hibernate 3.3 com.kuiren.dicts.domain.TXtDictType_$$_
                if (name.endsWith("_")) {
                    name = name.substring(0, name.length() - 1);
                }
                if (m == null) {
                } else if (m.getName() == null) {
                } else if (m.getName().contains("getHibernateLazyInitializer")) {

                } else if (m.getName().equals("getHandler")) {
                    // Hibernate 3.6
                    // org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer@4a9a775

                } else {
                    readMethod = BeanUtil.getDeclaredMethod(Class.forName(name), m.getName(), m.getParameterTypes());
                }

                // if (m != null
                // && (m.getName() != null)
                // && (!m.getName()
                // .contains("getHibernateLazyInitializer") || !m
                // .getName().contains("JavassistLazyInitializer")))
                // // readMethod = Class.forName(name).getDeclaredMethod(
                // // m.getName(), m.getParameterTypes());
                // readMethod = BeanUtil.getDeclaredMethod(
                // Class.forName(name), m.getName(),
                // m.getParameterTypes());

                // todo:去掉 getHibernateLazyInitializer方法
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            // 若不是CGLib生成的类，那么要序列化的属性的accessor方法就是该类中的方法。
            readMethod = m;
        }

        return readMethod;
    }

    // public static Method getDeclaredMethod(Object object, String methodName,
    // Class<?> ... parameterTypes){
    // Method method = null ;
    // for(Class<?> clazz = object.getClass() ; clazz != Object.class ; clazz =
    // clazz.getSuperclass()) {
    // try {
    // method = clazz.getDeclaredMethod(methodName, parameterTypes) ;
    // return method ;
    // } catch (Exception e) {
    // }
    // }
    // return null;
    // }
    private PropertyDescriptor[] getPropertyDescriptors(Class clazz, Object in) throws Exception {

        PropertyDescriptor[] props;
        if (clazz.getName().contains("$")) {
            props = Introspector.getBeanInfo(clazz, clazz.getSuperclass()).getPropertyDescriptors();
        } else {
            props = Introspector.getBeanInfo(in.getClass()).getPropertyDescriptors();
        }

        return props;

    }

    private void buildMap(String dottargetName, String targetName, Object in, StringBuilder out, boolean incollect,
                          List<String> parents) throws Exception {
        out.append("{");
        int oldLength = out.length();

        List<JsonConfig> addlist = new ArrayList<JsonConfig>();
        Map<?, ?> pdmap = initMapConfig(dottargetName, targetName, in, (Map<?, ?>) in, addlist);

        for (Map.Entry<?, ?> entry : ((Map<?, ?>) pdmap).entrySet()) {
            String propertyName = getSimpleTypeStr(entry.getKey(), 1);

            Object value = entry.getValue();

            if (value != null && value.getClass().getName().trim().equals("java.sql.Clob")) {
                value = getClob((Clob) value);
                // 对oracle的clob进行处理
                // cpmap.put(key.toString().toLowerCase(),
                // getClob((SerializableClob)value));

            }
            if (JsonUtil.isSimpleType(value)) {
                if (needBuild(propertyName)) {
                    if (out.length() > oldLength) {
                        out.append(", ");
                    }

                    out.append(JsonUtil.quote(keyNameConvert(propertyName)));
                    out.append(":");
                    out.append(getSimpleTypeStr(value, 0));
                }
            } else {
                if (needBuild(propertyName)) {
                    if (out.length() > oldLength) {
                        out.append(", ");
                    }

                    buildNode(dottargetName, propertyName, value, out, true, parents);
                }
            }
        }

        // 加上add
        configPropDeal(addlist, in, dottargetName, out, oldLength, parents);
        out.append("}");
    }

    private String keyNameConvert(String key) {
        String name = key;
        if (name != null && name != "") {
            // // 0:不处理，1：大写，2 小写
            if (upperOrLower == 1) {
                name = name.toUpperCase();

            } else if (upperOrLower == 2) {
                name = name.toLowerCase();
            }
        }

        return name;

    }

    private void buildObject(String dottargetName, String targetName, Object in, StringBuilder out, boolean incollect,
                             List<String> parents) throws Exception {

        // System.out.println(targetName + ":" + dottargetName + "\n");
        out.append("{");

        if (parents != null && StringUtil.contain(parents, in.toString())) {
            out.append("}");
            return;
        }
        Class<?> clazz = in.getClass();
        int oldLength = out.length();
        // 取得对象的所有属性描述
        PropertyDescriptor[] props = getPropertyDescriptors(clazz, in);
        // 初始化增加列
        List<JsonConfig> addlist = new ArrayList<JsonConfig>();
        List<PropertyDescriptor> pdlist = initObjConfig(dottargetName, targetName, in, props, addlist);

        for (PropertyDescriptor property : pdlist) {
            String key = property.getName();
            // 默认去掉class的属性，这个是java对象本身的
            if (JsonUtil.isExcludedType(property.getPropertyType(), Class.class)) {
                continue;
            }
            try {
                Method readMethod = getReadMethod(property, clazz);
                if (readMethod != null) {
                    Object value = property.getReadMethod().invoke(in);
                    buildProp(dottargetName, key, value, out, oldLength, 0, parents, in);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 加上add
        configPropDeal(addlist, in, dottargetName, out, oldLength, parents);

        out.append("}");

    }

    private boolean buildProp(String dottargetName, String key, Object value, StringBuilder out, int oldLength,
                              int flag, List<String> parents, Object in) throws Exception {

        // modefy by pengrk at 2015/12/10,if the list is empty return null
        if (isRemoveNullProp == true) {
            if (value instanceof Collection && (value != null)) {
                if (((Collection) value).size() < 1) {
                    return false;
                }
            } else if (value == null) {
                return false;
            }

        }

        if (value == null) {
            return false;
        }
        // if (value == null && isRemoveNullProp == true) {
        // return false;
        // }
        /********************************************/

        if (value.getClass().getName().trim().equals("java.sql.Clob")) {
            value = getClob((Clob) value);
            // 对oracle的clob进行处理
            // cpmap.put(key.toString().toLowerCase(),
            // getClob((SerializableClob)value));

        }

        //
        // if (value.getClass().getName().trim()
        // .equals("org.hibernate.lob.SerializableClob")) {
        // value = getClob((SerializableClob) value);
        // // 对oracle的clob进行处理
        // // cpmap.put(key.toString().toLowerCase(),
        // // getClob((SerializableClob)value));
        //
        // }
        if (JsonUtil.isSimpleType(value)) {
            if (needBuild(key)) {
                if (out.length() > oldLength) {
                    out.append(", ");
                }
                out.append("\"" + keyNameConvert(key) + "\"");
                out.append(":");
                out.append(getSimpleTypeStr(value, flag));
            }

        } else {
            if (needBuild(key)) {
                if (out.length() > oldLength) {
                    out.append(", ");
                }

                List<String> parentList = JsonUtil.copy(parents);
                parentList.add(in.toString());
                buildNode(dottargetName + "." + key, key, value, out, true, parentList);
            }
        }

        return true;
    }

    private void configPropDeal(List<JsonConfig> thisAddList, Object in, String dottargetName, StringBuilder out,
                                int oldLength, List<String> parents) throws Exception {
        for (JsonConfig kv : thisAddList) {
            int flag = 0;
            Object value = kv.getValue();
            if (value instanceof IJsonConvert) {// add支持回调函数
                // 在转换器中加上IJsonConvert
                ((IJsonConvert) value).setJsonCreator(this);

                if ("10".equals(kv.getType())) { // replace
                    value = ((IJsonConvert) value).convert(in, kv.getActualName(),
                            JsonUtil.evalByOgnl(kv.getExpr(), in));
                    if (value == null) {
                        continue;
                    }
                } else { // add
                    value = ((IJsonConvert) value).convert(in, kv.getRemain1(),
                            JsonUtil.evalByOgnl(kv.getRemain1(), in));
                    if (value instanceof DirectConvert) {

                        // ...
                        value = new JsonCreator().build(value);
                        flag = 1;
                    }

                }

            }

            // 2015/12/10:add by pengrk，用来支持其增加或修改的名称进行动态计算的
            String addName = calAddName(kv, in);
            // *************************************

            buildProp(dottargetName, addName, value, out, oldLength, flag, parents, in);
        }
    }

    // 2015/12/10:add by pengrk，用来支持其增加或修改的名称进行动态计算的
    private String calAddName(JsonConfig kv, Object in) {

        String addName = null;
        if (kv.getAddName() instanceof INameConvert) {
            addName = kv.getAddName(in, kv.getActualName(), JsonUtil.evalByOgnl(kv.getExpr(), in));
        } else {
            addName = (String) kv.getAddName();
        }

        return addName;

    }

    private List<PropertyDescriptor> initObjConfig(String dottargetName, String targetName, Object in,
                                                   PropertyDescriptor[] props, List<JsonConfig> addlist) {

        // 把属性描述和其属性名对应起来进行Map,方便代码的的操作
        Map<String, PropertyDescriptor> pdMap = new HashMap<String, PropertyDescriptor>();
        for (PropertyDescriptor prop : props) {
            pdMap.put(prop.getName(), prop);
        }

        for (JsonConfig c : configs.get("add")) {
            if (jsonMatch.matchAdd(dottargetName, targetName, in, c)) {
                addlist.add(c);
                if (c.getDelName() != null && c.getDelName() != "") {
                    pdMap.remove(c.getDelName());
                }
            }
        }
        for (PropertyDescriptor prop : props) {
            for (JsonConfig jc : configs.get("rep")) {
                if (jsonMatch.matchUpdate(dottargetName, targetName, in, prop, jc)) {
                    Object value = jc.getValue();
                    if (value == null) { // 计算点串方式的属性值
                        value = JsonUtil.evalByOgnl(jc.getExpr(), in);
                    }

                    JsonConfig jc1 = jc.clone();
                    jc1.setActualName(prop.getName());

                    jc1.setValue(value);
                    addlist.add(jc1);
                    if (jc1.getDelName() != null && jc1.getDelName() != "") {
                        pdMap.remove(jc1.getDelName());
                    }
                }
            }

            for (JsonConfig jc : configs.get("del")) {
                if (jc.getDelCls() != null && JsonUtil.isExcludedType(prop.getPropertyType(), jc.getDelCls())) {
                    pdMap.remove(prop.getName());
                } else if (jsonMatch.matchDel(dottargetName, targetName, in, prop, jc)) {
                    if (jc.getDelName() != null && jc.getDelName() != "") {
                        pdMap.remove(jc.getDelName());
                    }
                }
            }

        }

        return JsonUtil.collect2List(pdMap.values());
    }

    private Map initMapConfig(String dottargetName, String targetName, Object in, Map<?, ?> props,
                              List<JsonConfig> addlist) {

        Map<Object, Object> pdMap = new HashMap<Object, Object>();
        for (Object prop : props.keySet()) {
            pdMap.put(prop, props.get(prop));
        }

        for (JsonConfig c : configs.get("add")) {
            if (jsonMatch.matchAdd(dottargetName, targetName, in, c)) {
                addlist.add(c);
                if (c.getDelName() != null && c.getDelName() != "") {
                    pdMap.remove(c.getDelName());
                }
            }
        }
        for (Object prop : props.keySet()) {

            Object vObj = props.get(prop);

            for (JsonConfig jc : configs.get("rep")) {
                if (jsonMatch.matchUpdate(dottargetName, targetName, in, (String) prop, jc)) {
                    Object value = jc.getValue();
                    if (value == null) {
                        value = JsonUtil.evalByOgnl(jc.getExpr(), in);
                    }

                    JsonConfig jc1 = jc.clone();
                    jc1.setActualName((String) prop);
                    jc1.setValue(value);
                    addlist.add(jc1);
                    if (jc1.getDelName() != null && jc1.getDelName() != "") {
                        pdMap.remove(jc1.getDelName());
                    }
                }
            }

            for (JsonConfig jc : configs.get("del")) {
                if (jc.getDelCls() != null && JsonUtil.isExcludedType(vObj.getClass(), jc.getDelCls())) {
                    pdMap.remove((String) prop);
                } else if (jsonMatch.matchDel(dottargetName, targetName, in, (String) prop, jc)) {
                    if (jc.getDelName() != null && jc.getDelName() != "") {
                        pdMap.remove(jc.getDelName());
                    }
                }
            }

        }

        return pdMap;
    }

    public Class getLimitClz() {
        return limitClz;
    }

    public JsonCreator setLimitClz(Class limitClz) {
        this.limitClz = limitClz;
        return this;
    }

}
