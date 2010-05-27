package com.jme.util;

/**
 * ErrorNotifier interface defines the process for communicating errors
 * to a specified recipient.
 * @author Mark Powell
 *
 */
public interface ErrorNotifier {
    /**
     * sends a message to recipients.
     * @param message the message to send.
     */
    public void send(String message, String trace);
}
