package rh.integration;

import java.lang.reflect.Field;

public interface FieldResolver {

    public static FieldResolver NON_RESOLVABLE = new FieldResolver() {
        @Override
        public Object resolve(Object instance, Field field) {
            return null;
        }
    };

    Object resolve(Object instance, Field field);
}
