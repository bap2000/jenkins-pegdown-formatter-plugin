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

import hudson.model.Describable;
import hudson.model.Hudson;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.kohsuke.stapler.DataBoundConstructor;

public class PegDownExtension implements Describable<PegDownExtension> {

    private final String name;
    private final int flag;
    private final boolean selected;

    @DataBoundConstructor
    public PegDownExtension(final String name, final int flag, final boolean selected) {
        this.name = name;
        this.flag = flag;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public int getFlag() {
        return flag;
    }

    public boolean isSelected() {
        return selected;
    }

    public PegDownExtensionDescriptor getDescriptor() {
        return Hudson.getInstance().getDescriptorByType(PegDownExtensionDescriptor.class);
    }

    public boolean equals(final Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;

        final PegDownExtension thatExtn = (PegDownExtension) that;
        return new EqualsBuilder()
                .append(name, thatExtn.name)
                .append(flag, thatExtn.flag)
                .append(selected, thatExtn.selected)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append(name).append(flag).append(selected).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("flag", flag)
                .append("selected", selected)
                .toString();
    }

}
