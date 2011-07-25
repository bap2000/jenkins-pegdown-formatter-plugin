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

import hudson.Extension;
import hudson.Util;
import hudson.markup.MarkupFormatter;
import hudson.markup.MarkupFormatterDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class PegDownFormatter extends MarkupFormatter {

    public static final String PEG_DOWN_BUNDLE = "/pegdown/plugin-pegdown-if.zip";
    private static final String FORMATTER_CLASS_NAME = "org.jenkins_ci.plugins.pegdown_formatter.impl.Formatter";
    private static final String FORMATTER_METHOD_NAME = "markdownToHtml";
    private static Method formatter;

    private final int flags;
    private final List<PegDownExtension> extensions;
    private final List<PegDownExtension> advancedExtensions;

    @DataBoundConstructor
    public PegDownFormatter(final List<PegDownExtension> extensions, final List<PegDownExtension> advancedExtensions) throws IOException {
        initFormatter();
        this.extensions = extensions;
        this.advancedExtensions = advancedExtensions;
        this.flags = ExtensionUtils.toFlags(extensions, advancedExtensions);
    }

    public List<PegDownExtension> getExtensions() {
        return extensions;
    }

    public List<PegDownExtension> getAdvancedExtensions() {
        return advancedExtensions;
    }

    private final void initFormatter() {
        try {
            synchronized (PegDownFormatter.class) {
                if (formatter == null) {
                    final InputStream pegDownBundle = IsolatingClassLoader.class.getResourceAsStream(PEG_DOWN_BUNDLE);
                    final IsolatingClassLoader pegDownClassLoader = new IsolatingClassLoader(pegDownBundle);
                    final Class<?> formatterClass = pegDownClassLoader.loadClass(FORMATTER_CLASS_NAME);
                    formatter = formatterClass.getMethod(FORMATTER_METHOD_NAME, String.class, int.class);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void translate(final String pegDown, final Writer writer) throws IOException {
        if (Util.fixEmptyAndTrim(pegDown) == null) return;
        String html = null;
        try {
            html = (String) formatter.invoke(null, pegDown, flags);
        } catch (IllegalAccessException iae) {
            throw new RuntimeException(iae);
        } catch (InvocationTargetException ite) {
            throw new RuntimeException(ite.getCause());
        }
        writer.write(html);
    }

    public Object readResolve() {
        initFormatter();
        return this;
    }

    @Extension
    public static class PegDownFormatterDescriptor extends MarkupFormatterDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.pegDownFormatter();
        }

        public String getHelpUrl() {
            return super.getHelpFile("syntax");
        }

        public List<PegDownExtension> getDefaultExtensions() {
            return ExtensionUtils.DEFAULTS;
        }

        public List<PegDownExtension> getDefaultAdvancedExtensions() {
            return ExtensionUtils.ADVANCED_DEFAULTS;
        }

    }

}
