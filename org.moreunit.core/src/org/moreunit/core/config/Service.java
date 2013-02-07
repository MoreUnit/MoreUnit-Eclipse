package org.moreunit.core.config;

/**
 * A service that can be started and stopped.
 */
public interface Service
{
    void start();

    void stop();
}
