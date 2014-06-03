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
package com.codenvy.api.factory.parameter;

import com.codenvy.api.factory.FactoryUrlException;
import com.codenvy.api.factory.dto.Factory;
import com.codenvy.api.factory.dto.ProjectAttributes;
import com.codenvy.dto.server.DtoFactory;

/**
 * Move 'ptype' parameter into projectattributes object.
 *
 * @author Alexander Garagatyi
 */
public class ProjectTypeConverter implements LegacyConverter {
    @Override
    public void convert(Factory factory) throws FactoryUrlException {
        if (factory.getPtype() != null) {
            ProjectAttributes attributes = factory.getProjectattributes();
            if (null == attributes || attributes.getPtype() == null) {
                attributes =
                        attributes == null ? DtoFactory.getInstance().createDto(ProjectAttributes.class) : attributes;
                attributes.setPtype(factory.getPtype());
                factory.setPtype(null);
                factory.setProjectattributes(attributes);
            } else if (attributes.getPtype() != null) {
                throw new FactoryUrlException(
                        "Parameters 'ptype' and 'projectsttributes.ptype' are mutually exclusive.");
            }
        }
    }
}
