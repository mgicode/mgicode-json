package com.kuiren.common.json.util;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author pengrk
 * @email:sjkjs155@126.com
 * @wetsite:www.mgicode.com
 * @license:GPL
 */
class ReadWriteMethod {

	public Method readMethod;
	public Method writeMethod;

	public Method getReadMethod() {
		return readMethod;
	}

	public void setReadMethod(Method readMethod) {
		this.readMethod = readMethod;
	}

	public Method getWriteMethod() {
		return writeMethod;
	}

	public void setWriteMethod(Method writeMethod) {
		this.writeMethod = writeMethod;
	}

	public ReadWriteMethod(Method readMethod, Method writeMethod) {
		super();
		this.readMethod = readMethod;
		this.writeMethod = writeMethod;
	}

}

public class BeanUtil {
	//protected final static Log logger = LogFactory.getLog(BeanUtil.class);

	private static Map<String, Field> fieldMap = new HashMap<String, Field>();
	private static Map<String, Method> methodMap = new HashMap<String, Method>();

	private static Map<String, List<ReadWriteMethod>> clzPropDesc = new HashMap<String, List<ReadWriteMethod>>();

	/**
	 * 是否简单类型，如Int，long，float等
	 * 
	 * @param property
	 *            任意对象
	 * @return
	 */
	public static boolean isSimpleType(Object property) {

		Set<Class<?>> simpleTypes = new HashSet<Class<?>>();

		simpleTypes.add(Byte.TYPE);
		simpleTypes.add(Short.TYPE);
		simpleTypes.add(Integer.TYPE);
		simpleTypes.add(Long.TYPE);
		simpleTypes.add(Float.TYPE);
		simpleTypes.add(Double.TYPE);
		simpleTypes.add(Boolean.TYPE);

		if (property == null) {
			return true;
		}
		Class type = property.getClass();
		return simpleTypes.contains(type) || Number.class.isAssignableFrom(type) || String.class.isAssignableFrom(type)
				|| Boolean.class.isAssignableFrom(type) || Date.class.isAssignableFrom(type);
	}

	public static boolean isBoolean(Object property) {
		Set<Class<?>> simpleTypes = new HashSet<Class<?>>();
		simpleTypes.add(Boolean.TYPE);
		if (property == null) {
			return true;
		}
		Class type = property.getClass();
		return simpleTypes.contains(type) || Boolean.class.isAssignableFrom(type);
	}

	public static boolean isInt(Object property) {
		Set<Class<?>> simpleTypes = new HashSet<Class<?>>();
		simpleTypes.add(Integer.TYPE);
		if (property == null) {
			return true;
		}
		Class type = property.getClass();
		return simpleTypes.contains(type) || Integer.class.isAssignableFrom(type);
	}

	public static boolean isLong(Object property) {
		Set<Class<?>> simpleTypes = new HashSet<Class<?>>();
		simpleTypes.add(Long.TYPE);
		if (property == null) {
			return true;
		}
		Class type = property.getClass();
		return simpleTypes.contains(type) || Long.class.isAssignableFrom(type);
	}

	public static boolean isFloat(Object property) {
		Set<Class<?>> simpleTypes = new HashSet<Class<?>>();
		simpleTypes.add(Float.TYPE);
		if (property == null) {
			return true;
		}
		Class type = property.getClass();
		return simpleTypes.contains(type) || Float.class.isAssignableFrom(type);
	}

	public static Object readFieldValue(Object object, String fieldName) {

		return getFieldValue(object, fieldName);
	}

	/**
	 * 指定对象，指定属性名，取得其属性值
	 * 
	 * @param object
	 *            对象
	 * @param fieldName
	 *            属性名
	 * @return 属性值
	 */
	public static Object getFieldValue(Object object, String fieldName) {
		if (object == null)
			return null;
		Object result = null;

		try {
			Field field = getDeclaredField(object, fieldName);
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			// 直接通过其属性字段来读
			result = field.get(object);
			if (result == null) {
				PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, object.getClass());
				Method method = propertyDescriptor.getReadMethod();
				result = method.invoke(object, null);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static void writeFieldValue(Object object, String fieldName, Object value) throws NoSuchFieldException {

		setFieldValue(object, fieldName, value);
	}

	/**
	 * 根据对象，属性名来指定属性值
	 * 
	 * @param object
	 * @param fieldName
	 * @param value
	 * @throws NoSuchFieldException
	 */
	public static void setFieldValue(Object object, String fieldName, Object value) throws NoSuchFieldException {
		Field field = getDeclaredField(object, fieldName);
		if (!field.isAccessible()) {
			field.setAccessible(true);
		}
		try {
			field.set(object, value);
		} catch (IllegalAccessException e) {
//			if (logger.isErrorEnabled()) {
//				logger.error("不可能抛出的异常:{}", e);
//			}

			e.printStackTrace();

		}
	}

	/**
	 * 循环向上转型,获取对象的DeclaredField.
	 * 
	 * @throws NoSuchFieldException
	 *             如果没有该Field时抛出.
	 */
	public static Field getDeclaredField(Object object, String propertyName) throws NoSuchFieldException {

		return getDeclaredField(object.getClass(), propertyName);
	}

	/**
	 * 循环向上转型,获取类的DeclaredField.
	 */
	@SuppressWarnings("unchecked")
	public static Field getDeclaredField(Class clazz, String fieldName) throws NoSuchFieldException {

		String name = clazz.getName() + '.' + fieldName;

		if (fieldMap.containsKey(name)) {
			return fieldMap.get(name);
		} else {

			Field field = null;
			for (Class superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
				try {
					field = superClass.getDeclaredField(fieldName);
					if (field != null)
						break;
				} catch (NoSuchFieldException e) {
					// Field不在当前类，继续向上转型
				}
			}
			if (field != null) {
				fieldMap.put(name, field);
				return field;
			} else {
				throw new NoSuchFieldException("没有此字段 " + clazz.getName() + '.' + fieldName);
			}
		}

	}

	@SuppressWarnings("unchecked")
	public static Method getDeclaredMethod(Class clazz, String methodName, Class<?>... parameterTypes)
			throws NoSuchFieldException {

		String name = clazz.getName() + '.' + methodName;

		if (methodMap.containsKey(name)) {
			return methodMap.get(name);
		} else {

			Method field = null;
			for (Class superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
				try {
					field = superClass.getDeclaredMethod(methodName, parameterTypes);
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
				if (field != null)
					break;
			}
			if (field != null) {
				methodMap.put(name, field);
				return field;
			} else {
				throw new NoSuchFieldException("没有此方法 " + clazz.getName() + '.' + methodName);
			}
		}

	}

	/**
	 * 获得超类的泛型
	 * 
	 * @param clazz
	 *            当前类
	 * @param index
	 *            泛型的索引
	 * @return
	 */
	public static Class getSuperClassGenricType(Class clazz, int index) {

		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			//logger.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			//logger.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
				//	+ params.length);
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			//logger.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
			return Object.class;
		}
		return (Class) params[index];
	}

	/**
	 * 获得超类的泛型
	 * 
	 * @param clazz
	 * @return
	 */
	public static Class getSuperClassGenricType(Class clazz) {
		return getSuperClassGenricType(clazz, 0);
	}

	/**
	 * 调用指定对象的私有方法
	 * 
	 * @param object
	 * @param methodName
	 * @param params
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static Object invokePrivateMethod(Object object, String methodName, Object... params)
			throws NoSuchMethodException {

		Class[] types = new Class[params.length];
		for (int i = 0; i < params.length; i++) {
			types[i] = params[i].getClass();
//			if (logger.isDebugEnabled()) {
//				logger.debug("参数类型：" + types[i].toString());
//			}
		}

		Class clazz = object.getClass();

		Method method = null;
		try {
			method = clazz.getMethod(methodName, types);
		} catch (Exception e) {

		}
		if (method == null) {
			for (Class superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
//				if (logger.isDebugEnabled()) {
//					logger.debug(clazz.toString() + "," + methodName + "");
//				}
				try {
					method = superClass.getDeclaredMethod(methodName, types);
					break;
				} catch (NoSuchMethodException e) {
					// method=superClass.get.getDeclaredMethod(methodName);
				}
			}
		}
		// if (method == null){
		//
		// clazz.get
		// }
		if (method == null) {
			for (Method m : clazz.getMethods()) {
				if (m.getName().equals(methodName)) {
//					if (logger.isDebugEnabled()) {
//						logger.debug("通过getMethods的循环找到了的");
//					}
					method = m;
					break;
				}
			}
		}
		if (method == null) {

			throw new NoSuchMethodException(clazz.getSimpleName() + " No Such Method:" + methodName);
		}
		boolean accessible = method.isAccessible();
		method.setAccessible(true);
		Object result = null;
		try {
			result = method.invoke(object, params);
		} catch (Exception e) {
			// ReflectionUtils.handleReflectionException(e);
		}
		method.setAccessible(accessible);
		return result;
	}

	/**
	 * 将list转换为数组
	 * 
	 * @param list
	 * @return
	 */
	public static Object[] toArray(List list) {
		Object[] result = new Object[list.size()];
		for (int i = 0; i < list.size(); i++) {
			result[i] = list.get(i);
		}
		return result;
	}

	/**
	 * 从包package中获取所有的Class
	 * 
	 * @param pack
	 * @return
	 */
	public static Set<Class<?>> getClasses(String pack) {

		// 第一个class类的集合
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();

		boolean recursive = true; // 是否循环迭代
		String packageName = pack; // 获取包的名字 并进行替换
		String packageDirName = packageName.replace('.', '/');
		Enumeration<URL> dirs; // 定义一个枚举的集合 并进行循环来处理这个目录下的things
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);

			while (dirs.hasMoreElements()) { // 循环迭代下去
				URL url = dirs.nextElement(); // 获取下一个元素
				String protocol = url.getProtocol();// 得到协议的名称
				if ("file".equals(protocol)) {// 如果是以文件的形式保存在服务器上
					// System.err.println("file类型的扫描");// 获取包的物理路径
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					// 以文件的方式扫描整个包下的文件 并添加到集合中
					findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
				} else if ("jar".equals(protocol)) {// 如果是jar包文件 定义一个JarFile
					// System.err.println("jar类型的扫描");
					JarFile jar;
					try {// 获取jar
						jar = ((JarURLConnection) url.openConnection()).getJarFile();
						// 从此jar包 得到一个枚举类
						Enumeration<JarEntry> entries = jar.entries();
						while (entries.hasMoreElements()) {// 同样的进行循环迭代
							// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
							JarEntry entry = entries.nextElement();
							try {
								String name = entry.getName();
								if (name.charAt(0) == '/') {// 如果是以/开头的
									name = name.substring(1);// 获取后面的字符串
								}
								// 如果前半部分和定义的包名相同
								if (name.startsWith(packageDirName)) {
									int idx = name.lastIndexOf('/');
									if (idx != -1) {// 如果以"/"结尾 是一个包,获取包名
													// 把"/"替换成"."
										packageName = name.substring(0, idx).replace('/', '.');
									}
									if ((idx != -1) || recursive) {// 如果可以迭代下去
																	// 并且是一个包
										// 如果是一个.class文件 而且不是目录
										if (name.endsWith(".class") && !entry.isDirectory()) {
											// 去掉后面的".class" 获取真正的类名
											String className = name.substring(packageName.length() + 1,
													name.length() - 6);
											try { // 添加到classes
												classes.add(Class.forName(packageName + '.' + className));
											} catch (ClassNotFoundException e) {
												e.printStackTrace();
											}
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();

							}

						}
					} catch (IOException e) {
						// log.error("在扫描用户定义视图时从jar包获取文件出错");
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return classes;
	}

	private static List<ReadWriteMethod> getPropertyDescriptors(Class<?> clz) {

		String clzname = clz.getName();
		if (!clzPropDesc.containsKey(clzname)) {
			BeanInfo beanInfo = null;
			try {
				beanInfo = Introspector.getBeanInfo(clz);
			} catch (IntrospectionException e) {
				e.printStackTrace();
			}

			PropertyDescriptor propertyDescriptors[] = beanInfo.getPropertyDescriptors();
			List<ReadWriteMethod> list = new ArrayList<ReadWriteMethod>();
			for (PropertyDescriptor pd : propertyDescriptors) {

				list.add(new ReadWriteMethod(pd.getReadMethod(), pd.getWriteMethod()));
			}
			clzPropDesc.put(clzname, list);
		}

		return clzPropDesc.get(clzname);

	}

	/**
	 * 复制一个对象
	 * 
	 * @param clz
	 *            该对象的类型
	 * @param o
	 *            对象
	 * @return
	 */
	public static Object clone(Class<?> clz, Object o) {

		Object object = null;
		try {
			object = clz.newInstance();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// BeanInfo beanInfo = null;
		// try {
		// beanInfo = Introspector.getBeanInfo(clz);
		// } catch (IntrospectionException e) {
		// e.printStackTrace();
		// }
		//
		// PropertyDescriptor propertyDescriptors[] = beanInfo
		// .getPropertyDescriptors();

		List<ReadWriteMethod> pdlist = getPropertyDescriptors(clz);

		// for (int i = 0; i < propertyDescriptors.length; i++) {
		// PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
		for (ReadWriteMethod readWriteMethod : pdlist) {
			Method method = readWriteMethod.getReadMethod();
			Method write = readWriteMethod.getWriteMethod();
			if (method != null && write != null) {
				try {
					Object o1 = method.invoke(o);
					if (o1 != null) {
						write.invoke(object, o1);
					}
				} catch (IllegalArgumentException e) {

					e.printStackTrace();
				} catch (IllegalAccessException e) {

					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return object;
	}

	/**
	 * 以文件的形式来获取包下的所有Class
	 * 
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 */
	public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive,
			Set<Class<?>> classes) {
		// 获取此包的目录 建立一个File
		File dir = new File(packagePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
//			if (logger.isDebugEnabled()) {
//				logger.debug("用户定义包名 " + packageName + " 下没有任何文件");
//			}
			// log.warn("用户定义包名 " + packageName + " 下没有任何文件");
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		// 循环所有文件
		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
						classes);
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					// 添加到集合中去
					// classes.add(Class.forName(packageName + '.' +
					// className));
					// 经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
					classes.add(
							Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
				} catch (ClassNotFoundException e) {
//					if (logger.isErrorEnabled()) {
//						logger.error("添加用户自定义视图类错误 找不到此类的.class文件");
//					}
					e.printStackTrace();
				}
			}
		}
	}

	public static Object newInstance(Class persistentObjectClass) {
		try {
			return persistentObjectClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object invoke(Object o, Method m) {
		try {
			return m.invoke(o);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断方法有多少个参数
	 * 
	 * @param m
	 * @return
	 */
	public static int hasParamCount(Method m) {
		Class[] paramsClazz = m.getParameterTypes();
		return paramsClazz.length;
	}

}
