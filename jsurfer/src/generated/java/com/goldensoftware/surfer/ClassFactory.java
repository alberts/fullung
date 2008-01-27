package com.goldensoftware.surfer;

import com4j.COM4J;

/**
 * Defines methods to create COM objects
 */
public abstract class ClassFactory {
    private ClassFactory() {
    } // instanciation is not allowed

    /**
     * Application Class
     */
    public static com.goldensoftware.surfer.IApplication createApplication() {
        return COM4J.createInstance(com.goldensoftware.surfer.IApplication.class,
            "{B2933480-9788-11D2-9780-00104B6D9C80}");
    }
}
