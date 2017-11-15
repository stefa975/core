/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.console.client.v3.dmr;

import org.jboss.dmr.client.ModelNode;

/**
 * Represents a fully qualified DMR address ready to be put into a DMR operation.
 *
 * @author Harald Pehl
 */
public class ResourceAddress extends ModelNode {

    /**
     * Constant for the root address. Do <strong>not</strong> use this constants to chain calls like {@code add("foo",
     * "bar")}. This will change the resource address for {@code ROOT} and will lead to very interesting errors!
     */
    public static final ResourceAddress ROOT = new ResourceAddress() {
        static final String NOT_SUPPORTED = "Not supported for ROOT address";

        @Override
        public ResourceAddress add(final String propertyName, final String propertyValue) {
            throw new UnsupportedOperationException(NOT_SUPPORTED);
        }
    };

    public ResourceAddress() {
        super();
    }

    public ResourceAddress(ModelNode address) {
        set(address);
    }

    public ResourceAddress add(final String propertyName, final String propertyValue) {
        add().set(propertyName, propertyValue);
        return this;
    }
}
