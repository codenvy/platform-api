/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.api.workspace.shared.dto;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.dto.shared.DTO;

/**
 * @author Eugene Voevodin
 */
@DTO
public interface WorkspaceRef {

    String getName();

    void setName(String name);

    WorkspaceRef withName(String name);

    boolean isTemporary();

    void setTemporary(boolean isTemporary);

    WorkspaceRef withTemporary(boolean isTemporary);

    Link getWorkspaceLink();

    void setWorkspaceLink(Link link);

    WorkspaceRef withWorkspaceLink(Link link);

    Link getProjectsLink();

    void setProjectsLink(Link link);

    WorkspaceRef withProjectsLink(Link link);
}
