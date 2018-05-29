package org.mbari.m3.corelib;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Initializer
 */
public class Initializer {

    private static Injector injector;

    public static Injector getInjector() {
        if (injector == null) {
            Config config = ConfigFactory.load();
            String moduleName = config.getString("app.injector.module.class");
            try {
                Class clazz = Class.forName(moduleName);
                // TODO in java 9 use clazz.getDeclaredConstructor().newInstance()
                // You'll have to find one where constructor.getParameterCount == 0
                Module module = (Module) clazz.newInstance();
                injector = Guice.createInjector(module);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create dependency injector", e);
            }
        }
        return injector;
    }
    
}