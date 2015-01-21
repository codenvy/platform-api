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
package com.codenvy.api.factory.dto;

import com.codenvy.api.core.factory.FactoryParameter;
import com.codenvy.dto.shared.DTO;

import static com.codenvy.api.core.factory.FactoryParameter.Obligation.OPTIONAL;

/**
 * Describes parameters of the workspace that should be used for factory
 *
 * @author Alexander Garagatyi
 * @author Sergii Leschenko
 */
@DTO
public interface Workspace {
    @FactoryParameter(obligation = OPTIONAL, queryParameterName = "type")
    Boolean getType();

    void setType(Boolean type);

    Workspace withType(Boolean type);
}

