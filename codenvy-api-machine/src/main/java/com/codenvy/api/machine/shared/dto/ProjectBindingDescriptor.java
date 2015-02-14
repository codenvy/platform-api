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
import com.codenvy.api.machine.shared.ProjectBinding;
import com.codenvy.dto.shared.DTO;

import java.util.List;

/**
 * @author andrew00x
 */
@DTO
public interface ProjectBindingDescriptor extends ProjectBinding, Hyperlinks {
    void setWorkspaceId(String workspaceId);

    ProjectBindingDescriptor withWorkspaceId(String workspaceId);

    void setPath(String path);

    ProjectBindingDescriptor withPath(String path);

    ProjectBindingDescriptor withLinks(List<Link> links);
}