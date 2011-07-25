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

import java.util.ArrayList;
import java.util.List;

public class ExtensionUtils {

    public static final List<PegDownExtension> DEFAULTS;
    public static final List<PegDownExtension> ADVANCED_DEFAULTS;

    static {
        DEFAULTS = new ArrayList<PegDownExtension>();
        DEFAULTS.add(new PegDownExtension("SUPPRESS_ALL_HTML", 196608, false));
        ADVANCED_DEFAULTS = new ArrayList<PegDownExtension>();
        ADVANCED_DEFAULTS.add(new PegDownExtension("AUTOLINKS", 16, true));
        ADVANCED_DEFAULTS.add(new PegDownExtension("DEFINITIONS", 64, true));
        ADVANCED_DEFAULTS.add(new PegDownExtension("FENCED_CODE_BLOCKS", 128, true));
        ADVANCED_DEFAULTS.add(new PegDownExtension("HARDWRAPS", 8, true));
        ADVANCED_DEFAULTS.add(new PegDownExtension("NO_FOLLOW_LINKS", 262144, true));
        ADVANCED_DEFAULTS.add(new PegDownExtension("SMARTYPANTS", 3, true));
        ADVANCED_DEFAULTS.add(new PegDownExtension("TABLES", 32, true));
    }

    public static int toFlags(final List<PegDownExtension> extensions, final List<PegDownExtension> advancedExtensions) {
        int flags = combineFlags(0, extensions);
        return combineFlags(flags, advancedExtensions);
    }

    public static int combineFlags(final int currentFlags, final List<PegDownExtension> extensions) {
        int flags = currentFlags;
        for (PegDownExtension extension : extensions)
            if (extension.isSelected())
                flags |= extension.getFlag();
        return flags;
    }

}
