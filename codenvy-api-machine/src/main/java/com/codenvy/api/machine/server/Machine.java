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

import com.codenvy.api.core.ServerException;
import com.codenvy.api.machine.shared.dto.SnapshotDescriptor;

import java.util.List;

/**
 * @author andrew00x
 */
public interface Machine {
    public enum State {
        CREATING, ACTIVE, INACTIVE, DESTROYED
    }

    /** Get id of machine */
    String getId();

    /** Get state of machine */
    State getState();

    /**
     * Start machine
     *
     * @throws ServerException
     *         if internal error occurs
     */
    void start() throws ServerException;

    /**
     * Suspend machine
     *
     * @throws ServerException
     *         if internal error occurs
     */
    void suspend() throws ServerException;

    /**
     * Resume machine
     *
     * @throws ServerException
     *         if internal error occurs
     */
    void resume() throws ServerException;

    /**
     * Destroy machine
     *
     * @throws ServerException
     *         if internal error occurs
     */
    void destroy() throws ServerException;

    /** Saves the machine's current state and returns id of created snapshot. */
    String takeSnapshot(String description) throws ServerException;

    void removeSnapshot(String snapshotId) throws ServerException;

    List<SnapshotDescriptor> getSnapshots() throws ServerException;

    CommandProcess newCommandProcess(String command);

    /**
     * Get list of processes that are running in the machine
     *
     * @return list of running processes
     * @throws ServerException
     *         if internal error occurs
     */
    List<CommandProcess> getRunningProcesses() throws ServerException;
}