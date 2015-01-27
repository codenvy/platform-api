/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.api.machine.server;

import com.codenvy.api.core.ForbiddenException;
import com.codenvy.api.core.ServerException;
import com.codenvy.api.core.util.LineConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author andrew00x
 */
public abstract class MachineBuilder {
    private final String machineId;

    private Set<File>           files;
    private Map<String, Object> parameters;
    private MachineRecipe       recipe;

    protected MachineBuilder(String machineId) {
        this.machineId = machineId;
    }

    /**
     * Builds machine using supplied configuration
     *
     * @throws ForbiddenException if machine can't be built due to misconfiguration
     * @throws ServerException if internal error occurs
     */
    public Machine buildMachine() throws ServerException, ForbiddenException {
        return buildMachine(LineConsumer.DEV_NULL);
    }

    /**
     * Builds machine using supplied configuration. Puts logs to given line consumer.
     *
     * @throws ForbiddenException if machine can't be built due to misconfiguration
     * @throws ServerException if internal error occurs
     */
    public abstract Machine buildMachine(LineConsumer lineConsumer) throws ServerException, ForbiddenException;

    public MachineBuilder setRecipe(MachineRecipe recipe) {
        this.recipe = recipe;
        return this;
    }

    public MachineBuilder addFile(File file) {
        getFiles().add(file);
        return this;
    }

    public MachineBuilder setParameters(Map<String, Object> parameters) {
        getParameters().putAll(parameters);
        return this;
    }

    public MachineBuilder setParameter(String name, Object value) {
        getParameters().put(name, value);
        return this;
    }

    protected MachineRecipe getRecipe() {
        return recipe;
    }

    protected Map<String, Object> getParameters() {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
        }
        return this.parameters;
    }

    protected Set<File> getFiles() {
        if (files == null) {
            files = new LinkedHashSet<>();
        }
        return this.files;
    }

    protected String getMachineId() {
        return machineId;
    }
}