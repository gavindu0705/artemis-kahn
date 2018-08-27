package com.artemis.kahn.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

public class InvokeShell {
	public static GroovyClassLoader LOADER = new GroovyClassLoader();

	private final static LoadingCache<String, GroovyObject> SCRIPT_CACHE = CacheBuilder.newBuilder().maximumSize(10000).expireAfterWrite(300, TimeUnit.SECONDS)
			.build(new CacheLoader<String, GroovyObject>() {

				@Override
				public GroovyObject load(String scriptText){
					String key = DigestUtils.md5Hex(scriptText) + "#" + scriptText.hashCode();
					if (SCRIPT_CACHE.asMap().containsKey(scriptText)) {
						return SCRIPT_CACHE.getUnchecked(key);
					}
					try {
						Class<?> groovyClass = LOADER.parseClass(scriptText);
						SCRIPT_CACHE.put(key, (GroovyObject) groovyClass.newInstance());
					} catch (Exception e) {
						SCRIPT_CACHE.put(key, null);
					}
					return SCRIPT_CACHE.getUnchecked(key);
             }

			});

	public static Object invoke(String scriptText, Object arg) throws InstantiationException, IllegalAccessException,
			NoSuchMethodException, SecurityException {
		GroovyObject newInstance = SCRIPT_CACHE.getUnchecked(scriptText);
		if (newInstance == null) {
			throw new NoSuchMethodException("script parse error");
		}

		Class clazz = newInstance.getClass();
		Method method = clazz.getMethod("invoke", String.class);
		if (method == null) {
			method = clazz.getMethod("invoke", Object.class);
			if(method == null) {
				throw new NoSuchMethodException("no such method or args error");
			}
		}

		Object ret = newInstance.invokeMethod("invoke", arg);
		return ret;
	}

	/**
	 *
	 * @param scriptText
	 * @return
	 */
	public static boolean precompiler(String scriptText) {
		if (StringUtils.isBlank(scriptText)) {
			return false;
		}

		Class<?> groovyClass = null;
		try {
			groovyClass = LOADER.parseClass(scriptText);
		} catch (Exception e) {
			return false;
		}

		if (!"G".equals(groovyClass.getSimpleName())) {
			return false;
		}

		Method method = null;
		try {
			GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
			method = groovyObject.getClass().getMethod("invoke", String.class);
		} catch (Exception e) {
			return false;
		}

		if (method == null) {
			return false;
		}

		Type[] types = method.getParameterTypes();
		if (types == null || types.length != 1) {
			return false;
		}

		if (types[0].toString().indexOf("java.lang.String") == -1) {
			return false;
		}

		return true;
	}



}
