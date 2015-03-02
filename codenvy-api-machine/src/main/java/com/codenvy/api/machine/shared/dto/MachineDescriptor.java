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
package com.codenvy.api.machine.shared.dto;

import com.codenvy.api.core.rest.shared.dto.Hyperlinks;
import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.machine.shared.Machine;
import com.codenvy.api.machine.shared.MachineState;
import com.codenvy.dto.shared.DTO;

import java.util.List;
import java.util.Set;

/**
 * @author andrew00x
 */
@DTO
public interface MachineDescriptor extends Machine, Hyperlinks {
    void setId(String id);

    MachineDescriptor withId(String id);

    void setType(String type);

    MachineDescriptor withType(String type);

    MachineState getState();

    void setState(MachineState state);

    MachineDescriptor withState(MachineState state);

    void setOwner(String owner);

    MachineDescriptor withOwner(String owner);

    String getWorkspaceId();

    void setWorkspaceId(String workspaceId);

    MachineDescriptor withWorkspaceId(String workspaceId);

    Set<ProjectBindingDescriptor> getProjects();

    void setProjects(List<ProjectBindingDescriptor> projects);

    MachineDescriptor withProjects(List<ProjectBindingDescriptor> projects);

    MachineDescriptor withLinks(List<Link> links);
}
