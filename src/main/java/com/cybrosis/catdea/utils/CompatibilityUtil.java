/*
 * Copyright 2019 Cybrosis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cybrosis.catdea.utils;

import com.android.ddmlib.logcat.LogCatHeader;
import com.android.ddmlib.logcat.LogCatTimestamp;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;

/**
 * @author cybrosis
 */
public class CompatibilityUtil {
    public static class LogcatHeader {
        private static final ZoneId ZONE = ZoneId.systemDefault();
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSS");

        public static String getTimestamp(LogCatHeader header) {
            Instant instant;

            try {
                final Method method = header.getClass().getDeclaredMethod("getTimestampInstant");
                instant = (Instant) method.invoke(header);
            } catch (ReflectiveOperationException ignore) {
                // Compatibility issue with IU-182.5262.2, IU-183.6156.11
                try {
                    final Method method = header.getClass().getDeclaredMethod("getTimestamp");
                    final LogCatTimestamp timestamp = (LogCatTimestamp) method.invoke(header);

                    return timestamp.toString();
                } catch (ReflectiveOperationException | NullPointerException e) {
                    instant = Instant.now();
                }
            }

            return LocalDateTime.ofInstant(instant, ZONE).format(FORMATTER);
        }
    }

    public static class EdtExecutorService {
        public static Executor getInstance() {
            try {
                final Class<?> clazz = Class.forName("com.intellij.util.concurrency.EdtExecutorService");
                final Method method = clazz.getDeclaredMethod("getInstance");
                return (Executor) method.invoke(null);
            } catch (ReflectiveOperationException ignored) {
                try {
                    final Class<?> clazz = Class.forName("com.android.tools.idea.concurrent.EdtExecutor");
                    final Field field = clazz.getDeclaredField("INSTANCE");
                    return (Executor) field.get(null);
                } catch (ReflectiveOperationException e) {
                    final NoClassDefFoundError error = new NoClassDefFoundError("Either com.intellij.util.concurrency.EdtExecutorService or com.android.tools.idea.concurrent.EdtExecutor should exist");
                    error.initCause(e);
                    throw error;
                }
            }
        }
    }
}
