package de.mineking.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class ReflectionUtils {
	public static List<Field> getFields(Class<?> clazz, Predicate<Field> filter) {
		List<Field> fields = new LinkedList<>();
		
		do {
			fields.addAll(
					Arrays.stream(clazz.getDeclaredFields())
					.filter(filter)
					.toList()
			);
			
			clazz = clazz.getSuperclass();
		} while(clazz != null);
		
		return fields;
	}
	
	public static List<Field> getFields(Class<?> clazz) {
		return getFields(clazz, f ->
			!Modifier.isStatic(f.getModifiers()) &&
			!Modifier.isTransient(f.getModifiers()) &&
			!Modifier.isFinal(f.getModifiers())
		);
	}
	
	public static List<Field> getAllFields(Class<?> clazz) {
		return getFields(clazz, f -> true);
	}
}
