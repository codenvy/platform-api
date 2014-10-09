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
package com.codenvy.api.project.server;

import com.codenvy.api.core.ConflictException;
import com.codenvy.api.core.ForbiddenException;
import com.codenvy.api.core.ServerException;
import com.codenvy.api.vfs.shared.dto.AccessControlEntry;
import com.codenvy.api.vfs.shared.dto.Principal;
import com.codenvy.commons.json.JsonHelper;
import com.codenvy.commons.json.JsonParseException;
import com.codenvy.dto.server.DtoFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Part of project meta-data that is stored in file &lt;project folder&gt;/.codenvy/project.json.
 *
 * @author andrew00x
 */
public class ProjectJson2 {

    /**
     * Checks whether the Project's meta information is readable
     *
     * @param project
     *         project to check
     * @return true if project meta-information is readable (it exists, there are appropriate permissions etc)
     * otherwise returns false
     */
    public static boolean isReadable(Project project) {
        final VirtualFileEntry projectFile;
        try {
            projectFile = project.getBaseFolder().getChild(Constants.CODENVY_PROJECT_FILE_RELATIVE_PATH);
            if (projectFile == null || !projectFile.isFile()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static ProjectJson2 load(Project project) throws ServerException {
        final VirtualFileEntry projectFile;
        try {
            projectFile = project.getBaseFolder().getChild(Constants.CODENVY_PROJECT_FILE_RELATIVE_PATH);
        } catch (ForbiddenException e) {
            // If have access to the project then must have access to its meta-information. If don't have access then treat that as server error.
            throw new ServerException(e.getServiceError());
        }
        if (projectFile == null || !projectFile.isFile()) {
            return new ProjectJson2();
        }
        try (InputStream inputStream = ((FileEntry)projectFile).getInputStream()) {
            return load(inputStream);
        } catch (IOException e) {
            throw new ServerException(e.getMessage(), e);
        }
    }

    public static ProjectJson2 load(InputStream inputStream) throws IOException {
        try {
            return JsonHelper.fromJson(inputStream, ProjectJson2.class, null);
        } catch (JsonParseException e) {
            throw new IOException("Unable to parse the project's property file. " +
                                  "Check the project.json file for corruption or modification. Consider reloading the project. " +
                                  e.getMessage());
        }
    }

    public void save(Project project) throws ServerException {
        try {
            final FolderEntry baseFolder = project.getBaseFolder();
            VirtualFileEntry projectFile = baseFolder.getChild(Constants.CODENVY_PROJECT_FILE_RELATIVE_PATH);
            if (projectFile != null) {
                if (!projectFile.isFile()) {
                    throw new ServerException(String.format(
                            "Unable to save the project's properties to the file system. Path %s/%s exists but is not a file.",
                            baseFolder.getPath(), Constants.CODENVY_PROJECT_FILE_RELATIVE_PATH));
                }
                ((FileEntry)projectFile).updateContent(JsonHelper.toJson(this).getBytes());
            } else {
                VirtualFileEntry codenvyDir = baseFolder.getChild(Constants.CODENVY_FOLDER);
                if (codenvyDir == null) {
                    try {
                        codenvyDir = baseFolder.createFolder(Constants.CODENVY_FOLDER);
                    } catch (ConflictException e) {
                        // Already checked existence of folder ".codenvy".
                        throw new ServerException(e.getServiceError());
                    }
                    // Need to be able update files in .codenvy folder independently to user actions.
                    final List<AccessControlEntry> acl = new ArrayList<>(1);
                    final DtoFactory dtoFactory = DtoFactory.getInstance();
                    acl.add(dtoFactory.createDto(AccessControlEntry.class)
                                      .withPrincipal(dtoFactory.createDto(Principal.class).withName("any").withType(Principal.Type.USER))
                                      .withPermissions(Arrays.asList("all")));
                    codenvyDir.getVirtualFile().updateACL(acl, true, null);
                } else if (!codenvyDir.isFolder()) {
                    throw new ServerException(String.format(
                            "Unable to save the project's properties to the file system. Path %s/%s exists but is not a folder.",
                            baseFolder.getPath(), Constants.CODENVY_FOLDER));
                }
                try {
                    ((FolderEntry)codenvyDir)
                            .createFile(Constants.CODENVY_PROJECT_FILE, JsonHelper.toJson(this).getBytes(), "application/json");
                } catch (ConflictException e) {
                    // Already checked existence of file ".codenvy/project.json".
                    throw new ServerException(e.getServiceError());
                }
            }
        } catch (ForbiddenException e) {
            // If have access to the project then must have access to its meta-information. If don't have access then treat that as server error.
            throw new ServerException(e.getServiceError());
        }
    }

    private String                    type;
    private Builders                  builders;
    private Runners                   runners;
    private String                    description;
    private Map<String, List<String>> attributes;

    public ProjectJson2() {
    }

    public ProjectJson2(String type, Map<String, List<String>> attributes, Builders builders, Runners runners, String description) {
        this.type = type;
        this.builders = builders;
        this.runners = runners;
        this.description = description;
        this.attributes = attributes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ProjectJson2 withType(String type) {
        this.type = type;
        return this;
    }

    public Builders getBuilders() {
        return builders;
    }

    public ProjectJson2 withBuilders(Builders builders) {
        this.builders = builders;
        return this;
    }

    public void setBuilders(Builders builders) {
        this.builders = builders;
    }

    public Runners getRunners() {
        return runners;
    }

    public ProjectJson2 withRunners(Runners runners) {
        this.runners = runners;
        return this;
    }

    public void setRunners(Runners runners) {
        this.runners = runners;
    }

    public Map<String, List<String>> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        return attributes;
    }

    public void setAttributes(Map<String, List<String>> attributes) {
        this.attributes = attributes;
    }

    public ProjectJson2 withAttributes(Map<String, List<String>> attributes) {
        this.attributes = attributes;
        return this;
    }

    public String getAttributeValue(String name) {
        if (attributes != null) {
            final List<String> value = attributes.get(name);
            if (value != null && !value.isEmpty()) {
                return value.get(0);
            }
        }
        return null;
    }

    public List<String> getAttributeValues(String name) {
        if (attributes != null) {
            final List<String> value = attributes.get(name);
            if (value != null) {
                return new ArrayList<>(value);
            }
        }
        return null;
    }

    public void removeAttribute(String name) {
        if (attributes != null) {
            attributes.remove(name);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectJson2 withDescription(String description) {
        this.description = description;
        return this;
    }
}