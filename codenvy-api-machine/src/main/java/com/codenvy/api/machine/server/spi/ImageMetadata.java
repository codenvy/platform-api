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
package com.codenvy.api.machine.server.spi;

import com.codenvy.api.machine.shared.Recipe;

/**
 * @author andrew00x
 */
public interface ImageMetadata extends ImageKey {
    /**
     * Get recipe that was used for creation this image. If image was created as snapshot of {@code Instance} this method returns {@code
     * null}.
     */
    Recipe getRecipe();
}