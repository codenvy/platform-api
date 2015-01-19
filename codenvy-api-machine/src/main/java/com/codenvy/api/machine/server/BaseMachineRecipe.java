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

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

/**
 * @author andrew00x
 */
public abstract class BaseMachineRecipe implements MachineRecipe {
    @Override
    public String asString() {
        return null;
    }

    @Override
    public Reader asReader() {
        return null;
    }

    @Override
    public URL asURL() {
        return null;
    }

    @Override
    public File asFile() {
        return null;
    }

    @Override
    public InputStream asStream() {
        return null;
    }
}
