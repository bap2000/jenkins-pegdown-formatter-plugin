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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.CodeSource;
import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class IsolatingClassLoader extends SecureClassLoader {

    private final Map<String, byte[]> classes = new HashMap<String, byte[]>();

    public IsolatingClassLoader(final InputStream bundle) throws IOException {
        final ZipInputStream bundleZIS = new ZipInputStream(bundle);
        ZipEntry library;
        while ((library = bundleZIS.getNextEntry()) != null) {
            if (library.isDirectory()) continue;
            final byte[] jar = readEntry(bundleZIS);
            final ZipInputStream libraryZIS = new ZipInputStream(new ByteArrayInputStream(jar));
            ZipEntry classDef;
            while ((classDef = libraryZIS.getNextEntry()) != null) {
                if (classDef.isDirectory()) continue;
                classes.put(classDef.getName(), readEntry(libraryZIS));
            }
        }
    }

    private byte[] readEntry(final ZipInputStream zipInputStream) throws IOException {
        byte[] buffer = new byte[1024];
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        int cnt;
        while ((cnt = zipInputStream.read(buffer,0, buffer.length)) != -1) {
            baos.write(buffer, 0, cnt);
        }
        return baos.toByteArray();
    }

    public Class loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        // will Jenkins even run with a security manager?
		final SecurityManager securityManager = System.getSecurityManager();
		Class clazz = findLoadedClass(name);
		if (clazz == null) {
			if (securityManager != null) {
				final int index = name.lastIndexOf('.');
				if (index > 0)
					securityManager.checkPackageAccess(name.substring(0, index));
			}
			try {
				clazz = findClass(name);
			} catch (ClassNotFoundException cnfe) {
                clazz = findSystemClass(name);
            }
            // can clazz be null here?
			if (clazz == null)
				throw new ClassNotFoundException(name);
		}
		if (resolve) resolveClass(clazz);
		return clazz;
	}

	protected Class findClass(final String name) throws ClassNotFoundException {
        // calling before system classloader, so only check package access if we have something to define
		final String mapKey = name.replace('.', '/');
		byte[] classBytes = classes.get(mapKey + ".class");
		if (classBytes == null )
			throw new ClassNotFoundException(name);
		final SecurityManager securityManager = System.getSecurityManager();
		if (securityManager != null) {
			int index = name.lastIndexOf('.');
			if (index > 0)
				securityManager.checkPackageDefinition(name.substring(0, index));
		}
		return super.defineClass(name, classBytes, 0, classBytes.length, (CodeSource) null);
	}

	public InputStream getResourceAsStream(final String id) {
		if (id == null) return null;
        final String mapKey = id.charAt(0) == '/' ? id.substring(1) : id;
		byte[] resource = classes.get(mapKey);
		if (resource == null) return null;
		return new ByteArrayInputStream(resource);
	}

}
