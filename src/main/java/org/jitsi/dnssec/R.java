/*
 * dnssecjava - a DNSSEC validating stub resolver for Java
 * Copyright (c) 2013-2015 Ingo Bauersachs
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jitsi.dnssec;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Utility class to retrieve messages from {@link ResourceBundle}s.
 */
public final class R {
    private static ResourceBundle rb = ResourceBundle.getBundle("messages");

    private R() {
    }

    /**
     * Gets a translated message.
     * @param key The message key to retrieve.
     * @param values The values that fill placeholders in the message.
     * @return The formatted message.
     */
    public static String get(String key, Object... values) {
        return MessageFormat.format(rb.getString(key), values);
    }
}
