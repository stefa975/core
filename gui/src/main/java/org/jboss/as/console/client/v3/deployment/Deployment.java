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
package org.jboss.as.console.client.v3.deployment;

import java.util.ArrayList;
import java.util.List;

import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;

/**
 * A deployed and assigned content on a specific server.
 *
 * @author Harald Pehl
 */
public class Deployment extends Content {

    enum Status {
        OK, FAILED, STOPPED, UNDEFINED
    }


    /**
     * Expects a "subsystem" child resource. Modeled as a static helper method to make it usable from both
     * deployments and subdeployments.
     */
    static void parseSubsystems(ModelNode node, List<Subsystem> subsystems) {
        List<Property> properties = node.get("subsystem").asPropertyList();
        for (Property property : properties) {
            Subsystem subsystem = new Subsystem(property.getName(), property.getValue());
            subsystems.add(subsystem);
        }
    }

    private final ReferenceServer referenceServer;
    private final List<Subdeployment> subdeployments;
    private final List<Subsystem> subsystems;

    public Deployment(final ReferenceServer referenceServer, final ModelNode node) {
        super(node);
        this.referenceServer = referenceServer;
        this.subdeployments = new ArrayList<>();
        this.subsystems = new ArrayList<>();

        if (node.hasDefined("subsystem")) {
            parseSubsystems(node, subsystems);
        }
        if (node.hasDefined("subdeployment")) {
            List<Property> properties = node.get("subdeployment").asPropertyList();
            for (Property property : properties) {
                Subdeployment subdeployment = new Subdeployment(this, property.getName(), property.getValue());
                subdeployments.add(subdeployment);
            }
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Deployment)) { return false; }
        if (!super.equals(o)) { return false; }

        Deployment that = (Deployment) o;
        //noinspection SimplifiableIfStatement
        if (!referenceServer.equals(that.referenceServer)) { return false; }
        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + referenceServer.hashCode();
        result = 31 * result + getName().hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Deployment{").append(getName());
        if (!isStandalone()) {
            builder.append("@").append(referenceServer.getHost()).append("/").append(referenceServer.getServer());
        }
        builder.append(", ")
                .append((isEnabled() ? "enabled" : "disabled"))
                .append(", ")
                .append(getStatus());
        builder.append("}");
        return builder.toString();
    }

    public boolean isStandalone() {
        return referenceServer.isStandalone();
    }

    public ReferenceServer getReferenceServer() {
        return referenceServer;
    }

    public boolean isEnabled() {
        ModelNode enabled = get("enabled");
        //noinspection SimplifiableConditionalExpression
        return enabled.isDefined() ? enabled.asBoolean() : false;
    }

    public boolean isManaged() {
        ModelNode managed = get("managed");
        //noinspection SimplifiableConditionalExpression
        return managed.isDefined() ? managed.asBoolean() : false;
    }

    public boolean isArchive() {
        ModelNode content = get("content");
        boolean archive = true;
        // sometimes the deployment archive attribute is undefined
        if (content.get(0).hasDefined("archive")) {
            archive = content.get(0).get("archive").asBoolean();
        }
        //noinspection SimplifiableConditionalExpression
        return archive;
    }

    public Status getStatus() {
        Status status = Status.UNDEFINED;
        ModelNode statusNode = get("status");
        if (statusNode.isDefined()) {
            try {
                status = Status.valueOf(statusNode.asString());
            } catch (IllegalArgumentException e) {
                // returns UNDEFINED
            }
        }
        return status;
    }

    public String getEnabledTime() {
        ModelNode node = get("enabled-timestamp");
        if (node.isDefined()) {
            return node.asString();
        }
        return null;
    }

    public String getDisabledTime() {
        ModelNode node = get("disabled-timestamp");
        if (node.isDefined()) {
            return node.asString();
        }
        return null;
    }

    public boolean hasSubdeployments() {
        return !subdeployments.isEmpty();
    }

    public List<Subdeployment> getSubdeployments() {
        return subdeployments;
    }

    public List<Subsystem> getSubsystems() {
        return subsystems;
    }
}
