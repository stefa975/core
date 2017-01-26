/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jboss.as.console.client.core;

/**
 * @author Heiko Braun
 * @date 4/12/11
 */
public interface ApplicationProperties {
    String INITIAL_TOKEN = "initial_token";
    String STANDALONE = "standalone_usage";
    String DOMAIN_API = "domain-api";
    String DEPLOYMENT_API = "add-content";
    String PATCH_API = "management-upload";
    String LOGOUT_API = "logout";
    
    void setProperty(String key, String value);

    String getProperty(String key);

    boolean hasProperty(String key);

    void removeProperty(String key);

    boolean isStandalone();
}
