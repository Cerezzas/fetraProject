package az.coders.fera_project.config;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

public class GenericRelationBinder {

    public static void bind(Object parent) {
        if (parent == null) return;

        for (Method method : parent.getClass().getMethods()) {
            if (isCollectionGetter(method)) {
                try {
                    Object children = method.invoke(parent);
                    if (children instanceof Collection<?> collection) {
                        for (Object child : collection) {
                            setParentOnChild(child, parent);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }


    private static boolean isCollectionGetter(Method method) {
        return Modifier.isPublic(method.getModifiers())
                && method.getParameterCount() == 0
                && Collection.class.isAssignableFrom(method.getReturnType())
                && (method.getName().startsWith("get") || method.getName().startsWith("fetch"));
    }

    private static void setParentOnChild(Object child, Object parent) {
        for (Method childMethod : child.getClass().getMethods()) {
            if (childMethod.getName().startsWith("set")
                    && childMethod.getParameterCount() == 1
                    && childMethod.getParameterTypes()[0].isAssignableFrom(parent.getClass())) {
                try {
                    childMethod.invoke(child, parent);
                    return; // İlk uyğun method tapıldıqda dayandır
                } catch (Exception ignored) {
                }
            }
        }
    }
}