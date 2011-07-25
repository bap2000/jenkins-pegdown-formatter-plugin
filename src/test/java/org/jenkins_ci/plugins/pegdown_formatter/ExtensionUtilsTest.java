/*
 * The MIT License
 *
 * Copyright (C) 2010-2011 by Anthony Robinson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkins_ci.plugins.pegdown_formatter;

import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class ExtensionUtilsTest {

    private static final IsolatingClassLoader pegDownClassLoader;

    static {
        try {
            pegDownClassLoader = new IsolatingClassLoader(PegDownFormatter.class.getResourceAsStream(PegDownFormatter.PEG_DOWN_BUNDLE));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    } 

    @Test
    public void testEnsureAllFlagsCorrect() throws Exception {
        for (PegDownExtension extension : ExtensionUtils.DEFAULTS) {
            assertExtensionFlag(extension);
        }
        for (PegDownExtension advancedExtension : ExtensionUtils.ADVANCED_DEFAULTS) {
            assertExtensionFlag(advancedExtension);
        }
    }

    private void assertExtensionFlag(final PegDownExtension extension) throws Exception {
        final Class<?> extensions = pegDownClassLoader.loadClass("org.pegdown.Extensions");
        final Field field = extensions.getField(extension.getName());
        assertEquals(extension.getName(), field.getInt(null), extension.getFlag());
    }

}
