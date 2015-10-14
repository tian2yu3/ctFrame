package com.carltian.frame.config;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.carltian.frame.config.meta.ActionAttribute;
import com.carltian.frame.config.meta.DatabaseAttribute;
import com.carltian.frame.config.meta.ElementName;
import com.carltian.frame.config.meta.InitArgAttribute;
import com.carltian.frame.config.meta.LocalizationAttribute;
import com.carltian.frame.config.meta.PackageAttribute;
import com.carltian.frame.config.meta.RegistrationAttribute;
import com.carltian.frame.config.meta.RemoteAttribute;
import com.carltian.frame.config.meta.RootAttribute;
import com.carltian.frame.config.meta.TaskAttribute;
import com.carltian.frame.container.ResourceName;
import com.carltian.frame.container.reg.ArgInfo;
import com.carltian.frame.container.reg.Registrable;
import com.carltian.frame.container.reg.Registration;
import com.carltian.frame.container.reg.RegistrationType;
import com.carltian.frame.db.DatabaseManager;
import com.carltian.frame.local.LocalizationLoader;
import com.carltian.frame.remote.ActionConfig;
import com.carltian.frame.service.ServiceConfig;
import com.carltian.frame.task.TaskConfig;
import com.carltian.frame.util.FrameLogger;

/**
 * 配置解析器，解析系统框架的配置信息。负责解析XML中的配置，并与注解中的配置合并
 * 
 * @version 1.0
 * @author Carl Tian
 */
public class ConfigParser {
	/**
	 * 配置文件的命名空间
	 */
	static public final String NAMESPACE_URI = "http://www.carltian.com/xml/ns/frame";

	/**
	 * 配置文件的XSI命名空间
	 */
	static public final String XSI_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema-instance";

	/**
	 * 解析器支持的最小XML配置文件版本号
	 */
	static public final double MIN_VERSION = 1.0;

	/**
	 * 解析器支持的最大XML配置文件版本号
	 */
	static public final double MAX_VERSION = 1.0;

	/**
	 * 解析配置信息
	 * 
	 * @param configInputStream
	 *           XML配置文件的输入流
	 * @param config
	 *           已有的配置信息。该函数会将读入的配置覆盖到已有的配置中，如果为{@code null}则会新建一个配置信息
	 */
	static public void parse(InputStream configInputStream, FrameConfig config) {
		InputStream defaultInputStream = ConfigParser.class.getResourceAsStream("default.xml");
		// 纠正参数
		if (configInputStream == null) {
			// 需要打开新的流，同一文件流无法读取两次
			configInputStream = ConfigParser.class.getResourceAsStream("default.xml");
		}
		if (config == null) {
			config = new FrameConfig();
		}
		try {
			// 读取并校验xml
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			factory.setNamespaceAware(true);
			factory.setExpandEntityReferences(true);
			factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
					"http://www.w3.org/2001/XMLSchema");
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new ConfigSchemaResover());
			builder.setErrorHandler(new ConfigErrorHandler());
			Document configDoc = builder.parse(configInputStream);
			Document defaultDoc = builder.parse(defaultInputStream);
			// 加载根节点
			Element configRoot = configDoc.getDocumentElement();
			if (!nodeNameEquals(configRoot, ElementName.ROOT)) {
				// 在子节点中寻找根节点
				configRoot = getElement(configRoot, ElementName.ROOT, false);
			}
			if (configRoot == null) {
				throw new Exception("无法找到" + ElementName.ROOT + "配置节点！");
			}
			parseRoot(configRoot, defaultDoc.getDocumentElement(), config);
		} catch (Exception e) {
			FrameLogger.error("错误：框架无法初始化！XML配置有误。");
			throw new Error(e);
		}
	}

	/**
	 * 解析配置文件时的第1层私有函数，用于解析根节点
	 * 
	 * @param configRoot
	 *           配置文件根节点元素
	 * @param defaultRoot
	 *           默认配置根节点元素
	 * @param config
	 *           已有的需要进行覆盖填充的配置信息
	 * @throws Exception
	 *            当配置文件的版本不被支持或配置文件无法解析时抛出异常
	 */
	static private void parseRoot(Element configRoot, Element defaultRoot, FrameConfig config) throws Exception {
		// Required or Default Attr
		double version = Double.valueOf(configRoot.getAttributeNS(null, RootAttribute.VERSION));
		if (version > MAX_VERSION) {
			throw new Exception("当前运行库不支持版本大于" + MAX_VERSION + "的配置文件！请到官网下载新版Frame运行库。");
		} else if (version < MIN_VERSION) {
			throw new Exception("当前运行库不支持版本小于" + MIN_VERSION + "的配置文件！请先更新配置文件。");
		}
		// Other Attr
		// Child
		// 解析Database
		parseDatabase(getElement(configRoot, defaultRoot, ElementName.DATABASE, true), config);
		// 解析Localization
		parseLocalization(getElement(configRoot, defaultRoot, ElementName.LOCALIZATION, true), config);
		// 解析Remote
		parseRemote(getElement(configRoot, defaultRoot, ElementName.REMOTE, true), config);
		// 解析Services
		parseServices(getElement(configRoot, defaultRoot, ElementName.SERVICES, true), config);
		// 解析Tasks
		parseTasks(getElement(configRoot, defaultRoot, ElementName.TASKS, true), config);
	}

	/**
	 * 解析配置文件时的第2层私有函数，用于解析数据库(database)相关配置
	 * 
	 * @param element
	 *           配置文件中的数据库(database)节点元素
	 * @param config
	 *           已有的需要进行覆盖填充的配置信息
	 * @throws ClassNotFoundException
	 *            无法找到配置中指定的{@link DatabaseManager}类
	 */
	@SuppressWarnings("unchecked")
	static private void parseDatabase(Element element, FrameConfig config) throws ClassNotFoundException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		// Required or Default Attr
		// Other Attr
		Attr attr = element.getAttributeNodeNS(null, DatabaseAttribute.CLASS);
		if (attr == null) {
			return;
		}
		Class<? extends DatabaseManager> clazz = (Class<? extends DatabaseManager>) Class.forName(attr.getValue(), true,
				classLoader);
		Registration reg = ConfigAnnotationParser.parseRegistration(RegistrationType.Resource,
				ResourceName.DatabaseManager, clazz, true);
		// Child
		parseInitArg(getElements(element, ElementName.INIT_ARG, true), reg.getInitArgMap());
		config.setDatabaseManagerRegistration(reg);
	}

	/**
	 * 解析配置文件时的第2层私有函数，用于解析本地化(localization)相关配置
	 * 
	 * @param element
	 *           配置文件中的本地化(localization)节点元素
	 * @param config
	 *           已有的需要进行覆盖填充的配置信息
	 * @throws ClassNotFoundException
	 *            无法找到配置中指定的{@link LocalizationLoader}类
	 */
	@SuppressWarnings("unchecked")
	static private void parseLocalization(Element element, FrameConfig config) throws ClassNotFoundException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		// Required or Default Attr
		config.setLocalizationLoaderClass((Class<? extends LocalizationLoader>) Class.forName(
				element.getAttributeNS(null, LocalizationAttribute.LOADER), true, classLoader));
		// Other Attr
		Attr attr = element.getAttributeNodeNS(null, LocalizationAttribute.DEFAULT);
		if (attr != null) {
			// 解析默认区域设置
			Locale defaultLocale = Locale.getDefault();
			String defaultLocaleStr = attr.getValue();
			if (defaultLocaleStr != null && !defaultLocaleStr.isEmpty()) {
				String[] localeParams = defaultLocaleStr.split("_");
				if (localeParams.length == 1) {
					defaultLocale = new Locale(localeParams[0]);
				} else if (localeParams.length == 2) {
					defaultLocale = new Locale(localeParams[0], localeParams[1]);
				} else if (localeParams.length == 3) {
					defaultLocale = new Locale(localeParams[0], localeParams[1], localeParams[2]);
				}
			}
			config.setDefaultLocale(defaultLocale);
		}
		// Child
	}

	/**
	 * 解析配置文件时的第2层私有函数，用于解析远程接口(remote)相关配置
	 * 
	 * @param element
	 *           配置文件中的远程接口(remote)节点元素
	 * @param config
	 *           已有的需要进行覆盖填充的配置信息
	 * @throws ClassNotFoundException
	 *            解析动作配置时可能会抛出该异常，详见{@link #parseAction}
	 */
	static private void parseRemote(Element element, FrameConfig config) throws ClassNotFoundException {
		// Required or Default Attr
		// Other Attr
		Attr attr = element.getAttributeNodeNS(null, RemoteAttribute.PATH_NAME);
		if (attr != null) {
			config.setRemotePathName(attr.getValue());
		}
		// Child
		List<String> actionPkgList = parsePkgList(getElements(element, ElementName.PACKAGE, true));
		List<ActionConfig> actionConfigList = parseAction(getElements(element, ElementName.ACTION, true));
		config.setActionConfigList(ConfigAnnotationParser.parseActionPkg(actionConfigList, actionPkgList));
	}

	/**
	 * 解析配置文件时的第2层私有函数，用于解析服务(services)相关配置
	 * 
	 * @param element
	 *           配置文件中的服务(services)节点元素
	 * @param config
	 *           已有的需要进行覆盖填充的配置信息
	 * @throws ClassNotFoundException
	 *            解析服务配置时可能会抛出该异常，详见{@link #parseService}
	 */
	private static void parseServices(Element element, FrameConfig config) throws ClassNotFoundException {
		// Required or Default Attr
		// Other Attr
		// Child
		List<String> servicePkgList = parsePkgList(getElements(element, ElementName.PACKAGE, true));
		List<ServiceConfig> serviceConfigList = parseService(getElements(element, ElementName.SERVICE, true));
		config.setServiceConfigList(ConfigAnnotationParser.parseServicePkg(serviceConfigList, servicePkgList));
	}

	/**
	 * 解析配置文件时的第2层私有函数，用于解析定时任务(tasks)相关配置
	 * 
	 * @param element
	 *           配置文件中的定时任务(tasks)节点元素
	 * @param config
	 *           已有的需要进行覆盖填充的配置信息
	 * @throws ClassNotFoundException
	 *            解析任务配置时可能会抛出该异常，详见{@link #parseTask}
	 */
	private static void parseTasks(Element element, FrameConfig config) throws ClassNotFoundException {
		// Required or Default Attr
		// Other Attr
		// Child
		List<String> taskPkgList = parsePkgList(getElements(element, ElementName.PACKAGE, true));
		List<TaskConfig> taskConfigList = parseTask(getElements(element, ElementName.TASK, true));
		config.setTaskConfigList(ConfigAnnotationParser.parseTaskPkg(taskConfigList, taskPkgList));
	}

	/**
	 * 解析配置文件时的第3层私有函数，用于解析动作(action)相关配置
	 * 
	 * @param elements
	 *           配置文件中的动作(action)节点元素列表
	 * @return 解析完成的全部动作(action)配置列表
	 * @throws ClassNotFoundException
	 *            无法找到配置中指定的动作(action)类
	 */
	private static List<ActionConfig> parseAction(List<Element> elements) throws ClassNotFoundException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		List<ActionConfig> actionConfigList = new LinkedList<ActionConfig>();
		for (Element element : elements) {
			// Required or Default Attr
			Class<?> clazz = Class.forName(element.getAttributeNS(null, RegistrationAttribute.CLASS), true, classLoader);
			ActionConfig actionConfig = ConfigAnnotationParser.parseActionClass(clazz);
			if (actionConfig == null) {
				actionConfig = new ActionConfig();
			}
			// Other Attr
			Attr attr = element.getAttributeNodeNS(null, ActionAttribute.MODULE);
			if (attr != null) {
				actionConfig.setModule(attr.getValue());
			}
			// Child
			parseRegistration(RegistrationType.Action, clazz, element, actionConfig);
			actionConfigList.add(actionConfig);
		}
		return actionConfigList;
	}

	/**
	 * 解析配置文件时的第3层私有函数，用于解析服务(service)相关配置
	 * 
	 * @param elements
	 *           配置文件中的服务(service)节点元素列表
	 * @return 解析完成的全部服务(service)配置列表
	 * @throws ClassNotFoundException
	 *            无法找到配置中指定的服务(service)类
	 */
	private static List<ServiceConfig> parseService(List<Element> elements) throws ClassNotFoundException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		List<ServiceConfig> serviceConfigList = new LinkedList<ServiceConfig>();
		for (Element element : elements) {
			// Required or Default Attr
			Class<?> clazz = Class.forName(element.getAttributeNS(null, RegistrationAttribute.CLASS), true, classLoader);
			ServiceConfig serviceConfig = ConfigAnnotationParser.parseServiceClass(clazz);
			if (serviceConfig == null) {
				serviceConfig = new ServiceConfig();
			}
			// Other Attr
			// Child
			parseRegistration(RegistrationType.Service, clazz, element, serviceConfig);
			serviceConfigList.add(serviceConfig);
		}
		return serviceConfigList;
	}

	/**
	 * 解析配置文件时的第3层私有函数，用于解析任务(task)相关配置
	 * 
	 * @param elements
	 *           配置文件中的任务(task)节点元素列表
	 * @return 解析完成的全部任务(task)配置列表
	 * @throws ClassNotFoundException
	 *            无法找到配置中指定的任务(task)类
	 */
	private static List<TaskConfig> parseTask(List<Element> elements) throws ClassNotFoundException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		List<TaskConfig> taskConfigList = new LinkedList<TaskConfig>();
		for (Element element : elements) {
			// Required or Default Attr
			@SuppressWarnings("unchecked") Class<? extends TimerTask> clazz = (Class<? extends TimerTask>) Class.forName(
					element.getAttributeNS(null, RegistrationAttribute.CLASS), true, classLoader);
			TaskConfig taskConfig = ConfigAnnotationParser.parseTaskClass(clazz);
			if (taskConfig == null) {
				taskConfig = new TaskConfig();
			}
			// Other Attr
			Attr attr = element.getAttributeNodeNS(null, TaskAttribute.DELAY);
			if (attr != null) {
				taskConfig.setDelay(Long.valueOf(attr.getValue()));
			}
			attr = element.getAttributeNodeNS(null, TaskAttribute.PERIOD);
			if (attr != null) {
				taskConfig.setPeriod(Long.valueOf(attr.getValue()));
			}
			// Child
			parseRegistration(RegistrationType.Task, clazz, element, taskConfig);
			taskConfigList.add(taskConfig);
		}
		return taskConfigList;
	}

	/**
	 * 解析配置文件时的第3层私有函数，用于解析包路径(package)相关配置
	 * 
	 * @param elements
	 *           配置文件中的包路径(package)节点元素列表
	 * @return 解析完成的全部包路径(package)文本列表
	 */
	private static List<String> parsePkgList(List<Element> elements) {
		List<String> pkgList = new LinkedList<String>();
		for (Element element : elements) {
			// Required or Default Attr
			pkgList.add(element.getAttributeNS(null, PackageAttribute.NAME));
			// Other Attr
			// Child
		}
		return pkgList;
	}

	/**
	 * 解析配置文件时的通用私有函数，用于解析通用注册信息(registration)配置
	 * 
	 * @param type
	 *           注册类型
	 * @param clazz
	 *           需要解析的类
	 * @param element
	 *           配置文件中的各种包含注册信息(registration)的元素
	 * @param regItem
	 *           已有的需要进行覆盖填充的注册信息(registration)配置
	 */
	private static void parseRegistration(RegistrationType type, Class<?> clazz, Element element, Registrable regItem) {
		// 生成注册信息
		Registration reg = regItem.getRegistration();
		if (reg == null) {
			// 类内没有注册注解，则使用默认配置生成注册信息，注入信息使用类内注入注解配置
			reg = ConfigAnnotationParser.parseRegistration(type, "", clazz, true);
			regItem.setRegistration(reg);
		}
		// Required or Default Attr
		// Other Attr
		// 使用XML配置覆盖注册注解或默认配置，如果没有指定则不覆盖
		Attr attr = element.getAttributeNodeNS(null, RegistrationAttribute.SINGLETON);
		if (attr != null) {
			reg.setSingleton(getXmlBool(attr.getValue()));
		}
		attr = element.getAttributeNodeNS(null, RegistrationAttribute.NAME);
		if (attr != null) {
			reg.setName(attr.getValue());
		}
		// Child
		// 生成初始化参数列表
		parseInitArg(getElements(element, ElementName.INIT_ARG, true), reg.getInitArgMap());
	}

	/**
	 * 解析配置文件时的通用私有函数，用于解析实例的初始化参数(init-arg)配置
	 * 
	 * @param elements
	 *           配置文件中的初始化参数(init-arg)元素列表
	 * @param initArgMap
	 *           已有的需要进行覆盖填充的初始化参数(init-arg)映射
	 */
	private static void parseInitArg(List<Element> elements, Map<String, ArgInfo> initArgMap) {
		for (Element arg : elements) {
			ArgInfo argInfo = new ArgInfo();
			// Required or Default Attr
			initArgMap.put(arg.getAttributeNS(null, InitArgAttribute.NAME), argInfo);
			if (!getXmlBool(arg.getAttributeNS(XSI_NAMESPACE_URI, InitArgAttribute.NIL))) {
				argInfo.setValue(arg.getTextContent());
			}
			// Other Attr
			Attr typeAttr = arg.getAttributeNodeNS(null, InitArgAttribute.TYPE);
			Attr extTypeAttr = arg.getAttributeNodeNS(null, InitArgAttribute.EXT_TYPE);
			if (typeAttr != null) {
				argInfo.setType(RegistrationType.get(typeAttr.getValue()));
			} else if (typeAttr == null && extTypeAttr != null) {
				argInfo.setType(RegistrationType.Extension);
			}
			if (extTypeAttr != null) {
				argInfo.setExtType(extTypeAttr.getValue());
			}
			// Child
		}
	}

	private static boolean nodeNameEquals(Node node, String localName) {
		return node != null && NAMESPACE_URI.equals(node.getNamespaceURI()) && localName != null
				&& localName.equals(node.getLocalName());
	}

	private static List<Element> getElements(Element parent, String localName, boolean childOnly) {
		List<Element> elements = new LinkedList<Element>();
		if (parent != null && localName != null) {
			NodeList nodeList;
			if (childOnly) {
				nodeList = parent.getChildNodes();
			} else {
				nodeList = parent.getElementsByTagNameNS(NAMESPACE_URI, localName);
			}
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node instanceof Element) {
					if (childOnly && !nodeNameEquals(node, localName)) {
						// tag名称不符
						continue;
					}
					elements.add((Element) node);
				}
			}
		}
		return elements;
	}

	private static Element getElement(Element parent, String localName, boolean childOnly) {
		List<Element> elements = getElements(parent, localName, childOnly);
		return elements.isEmpty() ? null : elements.get(0);
	}

	private static Element getElement(Element parent, Element defaultParent, String localName, boolean childOnly) {
		Element element = getElement(parent, localName, childOnly);
		if (element == null) {
			element = getElement(defaultParent, localName, childOnly);
		}
		return element;
	}

	private static boolean getXmlBool(String value, boolean defaultValue) {
		if ("true".equals(value) || "1".equals(value)) {
			return true;
		} else if ("false".equals(value) || "0".equals(value)) {
			return false;
		} else {
			return defaultValue;
		}
	}

	private static boolean getXmlBool(String value) {
		return getXmlBool(value, false);
	}
}
