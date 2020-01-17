package com.rspsi.util;

import java.util.List;
import java.util.function.BiConsumer;

import org.apache.commons.compress.utils.Lists;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;

public class ChangeListenerUtil {
	
	private static List<ChangeListener<?>> weakListeners = Lists.newArrayList();
	
	public static ChangeListener<Boolean> addRunOnceListener(boolean onSelected, Runnable r) {
		ChangeListener<Boolean> changeListener = (observable, oldVal, newVal) -> {
			if (onSelected && newVal) {
				r.run();
			} else if (!onSelected && !newVal) {
				r.run();
			}
		};
		WeakChangeListener<Boolean> weakChangeListener = new WeakChangeListener<Boolean>(changeListener);
		add(weakChangeListener);
		return weakChangeListener;
	}
	
	public static void add(ChangeListener<Boolean> listener) {
		weakListeners.add(listener);
	}
	
	public static void remove(ChangeListener<Boolean> listener) {
		weakListeners.remove(listener);
	}

	public static void addListener(boolean onSelected, Runnable r, ReadOnlyBooleanProperty... nodes) {
		ChangeListener<Boolean> changeListener = (observable, oldVal, newVal) -> {
			if (onSelected && newVal) {
				r.run();
			} else if (!onSelected && !newVal) {
				r.run();
			}
		};
		for(ReadOnlyBooleanProperty node : nodes)
			node.addListener(changeListener);
	}

	public static void addListener(Runnable r, ReadOnlyBooleanProperty... nodes) {
		for(ReadOnlyBooleanProperty node : nodes)
			node.addListener((observable, oldVal, newVal) -> r.run());
	}

	public static void addListener(Runnable r, ReadOnlyIntegerProperty... nodes) {
		for(ReadOnlyIntegerProperty node : nodes)
			node.addListener((observable, oldVal, newVal) -> r.run());
	}

	public static void addListener(BiConsumer<Integer, Integer> consumer, ReadOnlyIntegerProperty... nodes) {
		for(ReadOnlyIntegerProperty node : nodes)
			node.addListener((observable, oldVal, newVal) -> consumer.accept(oldVal.intValue(), newVal.intValue()));
	}
	
	public static void addListener(Runnable r, ReadOnlyDoubleProperty... nodes) {
		for(ReadOnlyDoubleProperty node : nodes)
			node.addListener((observable, oldVal, newVal) -> {
				r.run();
			});
	}

	public static void addRangeListener(IntegerProperty node, int minimum, int maximum, boolean loopAround) {
		node.addListener((observable, oldVal, newVal) -> {
			if (newVal.intValue() < minimum) {
				node.setValue(loopAround ? maximum : minimum);
			} else if (newVal.intValue() > maximum) {
				node.setValue(loopAround ? minimum : maximum);
			}
		});
	}

	public static void addListener(Runnable r, ObjectProperty<?> objectProperty) {
		objectProperty.addListener((ChangeListener<Object>) (observable, oldVal, newVal) -> {
			r.run();
		});
	}

	public static void addListener(Runnable r, ReadOnlyObjectProperty<Integer>... nodes) {
		for(ReadOnlyObjectProperty<Integer> node : nodes)
			node.addListener((ChangeListener<Number>) (observable, oldVal, newVal) -> {
				r.run();
			});
	}
	


}
