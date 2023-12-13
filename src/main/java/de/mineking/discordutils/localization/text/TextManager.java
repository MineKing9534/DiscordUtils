package de.mineking.discordutils.localization.text;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class TextManager {
	public static String prefix = null;
	public static Set<String> ignoreStackElement = new HashSet<>();

	static {
		ignoreStackElement.add(TextManager.class.getName());
		ignoreStackElement.add(ITextContainer.class.getName());
		ignoreStackElement.add(IReplyHandler.class.getName());
		ignoreStackElement.add(TextFile.class.getName());
	}

	public TextManager() {
		if(prefix == null) throw new IllegalStateException("You have to set your package prefix first!");
	}

	@NotNull
	public static String getPath(@NotNull String name) {
		return name.replace('.', '/');
	}

	@NotNull
	public static String getClassPath(@NotNull Class<?> clazz) {
		return getClassPath(clazz.getName());
	}

	@NotNull
	public static String getClassPath(@NotNull String name) {
		return name
				.replace(prefix + ".", "")
				.replaceAll("\\$.*$", "")
				.replace(".", "/");
	}

	@NotNull
	public static String getCurrentPath() {
		for(var e : new Exception().getStackTrace()) {
			var file = e.getFileName().replace(".java", "");
			var clazz = e.getClassName().replaceAll(file + ".*$", "") + file;

			if(!ignoreStackElement.contains(clazz) && clazz.startsWith(prefix)) return getClassPath(clazz);
		}

		throw new IllegalStateException();
	}
}
