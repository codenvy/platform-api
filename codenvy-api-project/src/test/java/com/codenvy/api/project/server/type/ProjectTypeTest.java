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
package com.codenvy.api.project.server.type;


import com.codenvy.api.project.server.*;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.multibindings.Multibinder;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;


/**
 * @author gazarenkov
 */
public class ProjectTypeTest {

    Injector injector;

    @Before
    public void setUp() throws Exception {

        //MockitoAnnotations.initMocks(this);
        // Bind components
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {

                install(new ProjectApiModule());

                Multibinder<ValueProviderFactory> valueProviderMultibinder = Multibinder.newSetBinder(binder(), ValueProviderFactory.class);
                valueProviderMultibinder.addBinding().to(MyVPFactory.class);

                Multibinder<ProjectType> projectTypesMultibinder = Multibinder.newSetBinder(binder(), ProjectType.class);
                projectTypesMultibinder.addBinding().to(MyProjectType.class);

                bind(ProjectTypeRegistry.class);

            }
        });

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testProjectTypeService() throws Exception {


        ProjectTypeRegistry registry = injector.getInstance(ProjectTypeRegistry.class);

        ProjectTypeService service = new ProjectTypeService(registry);

        Assert.assertEquals(2, service.getProjectTypes().size());

    }

    @Test
    public void testProjectTypeDefinition() throws Exception {


        ProjectTypeRegistry registry = injector.getInstance(ProjectTypeRegistry.class);


        ProjectType type = registry.getProjectType("my");

        Assert.assertNotNull(type);
        Assert.assertEquals(1, type.getParents().size());
        Assert.assertEquals(BaseProjectType.ID, type.getParents().get(0).getId());
        Assert.assertNotNull(((Variable) type.getAttribute("var")).getValueProviderFactory());
        Assert.assertNull(type.getAttribute("var").getValue());
        Assert.assertEquals(4, type.getAttributes().size());
        Assert.assertNotNull(type.getAttribute("const"));
        Assert.assertEquals(new AttributeValue("const_value"), type.getAttribute("const").getValue());
        Assert.assertEquals(new AttributeValue("value"), type.getAttribute("var1").getValue());
        Assert.assertTrue(type.getAttribute("var1").isRequired());
        Assert.assertTrue(type.getAttribute("var1").isVariable());
        Assert.assertFalse(type.getAttribute("const").isVariable());


    }

    @Test
    public void testInvalidPTDefinition() throws Exception {

        ProjectType pt = new ProjectType("my", "second") {};

        Set<ProjectType> pts = new HashSet<>();
        pts.add(new MyProjectType(null));
        pts.add(pt);
        ProjectTypeRegistry reg = new ProjectTypeRegistry(pts);

        // BASE and MY (
        Assert.assertEquals(2, reg.getProjectTypes().size());

        // Invalid names
        pts.clear();
        pts.add(new ProjectType(null, "null id") {});
        pts.add(new ProjectType("", "empty id") {});
        pts.add(new ProjectType("invalid id", "invalid id") {});
        pts.add(new ProjectType("id1", null) {});
        pts.add(new ProjectType("id2", "") {});
        reg = new ProjectTypeRegistry(pts);
        // BASE only
        Assert.assertEquals(1, reg.getProjectTypes().size());

        // Invalid parent
        final ProjectType invalidParent = new ProjectType("parent", "parent") { };
        pts.add(new ProjectType("notRegParent", "not reg parent") {
            {
                addParent(invalidParent);
            }
        });
        reg = new ProjectTypeRegistry(pts);
        // BASE only
        Assert.assertEquals(1, reg.getProjectTypes().size());

    }

    @Test
    public void testPTInheritance() throws Exception {

        Set<ProjectType> pts = new HashSet<>();
        final ProjectType parent = new ProjectType("parent", "parent") {
            {
                addConstantDefinition("parent_const", "Constant", "const_value");
            }

        };
        final ProjectType child = new ProjectType("child", "child") {
            {
                addParent(parent);
                addConstantDefinition("child_const", "Constant", "const_value");
            }
        };

        pts.add(child);
        pts.add(parent);

        ProjectTypeRegistry reg = new ProjectTypeRegistry(pts);
        Assert.assertEquals(3, reg.getProjectTypes().size());
        Assert.assertEquals(1, child.getParents().size());
        Assert.assertEquals(3, reg.getProjectType("child").getAttributes().size());
        Assert.assertEquals(2, reg.getProjectType("parent").getAttributes().size());
        Assert.assertTrue(reg.getProjectType("child").isTypeOf("parent"));

    }

    @Test
    public void testAttributeNameConflict() throws Exception {

        Set<ProjectType> pts = new HashSet<>();
        final ProjectType parent = new ProjectType("parent", "parent") {
            {
                addConstantDefinition("parent_const", "Constant", "const_value");
            }

        };
        final ProjectType child = new ProjectType("child", "child") {
            {
                addParent(parent);
                addConstantDefinition("parent_const", "Constant", "const_value");
            }
        };

        pts.add(child);
        pts.add(parent);

        ProjectTypeRegistry reg = new ProjectTypeRegistry(pts);

        Assert.assertNotNull(reg.getProjectType("parent"));
        Assert.assertNull(reg.getProjectType("child"));
        Assert.assertEquals(2, reg.getProjectTypes().size());

    }

    @Test
    public void testMultiInheritance() throws Exception {

        Set<ProjectType> pts = new HashSet<>();
        final ProjectType parent1 = new ProjectType("parent1", "parent") {
            {
                addConstantDefinition("parent1_const", "Constant", "const_value");
            }

        };
        final ProjectType parent2 = new ProjectType("parent2", "parent") {
            {
                addConstantDefinition("parent2_const", "Constant", "const_value");
            }

        };
        final ProjectType child = new ProjectType("child", "child") {
            {
                addParent(parent1);
                addParent(parent2);
                addConstantDefinition("child_const", "Constant", "const_value");
            }
        };

        pts.add(child);
        pts.add(parent1);
        pts.add(parent2);

        ProjectTypeRegistry reg = new ProjectTypeRegistry(pts);

        Assert.assertEquals(2, child.getParents().size());
        Assert.assertEquals(4, reg.getProjectType("child").getAttributes().size());

    }

    @Test
    public void testMultiInheritanceAttributeConflict() throws Exception {

        Set<ProjectType> pts = new HashSet<>();
        final ProjectType parent1 = new ProjectType("parent1", "parent") {
            {
                addConstantDefinition("parent_const", "Constant", "const_value");
            }

        };
        final ProjectType parent2 = new ProjectType("parent2", "parent") {
            {
                addConstantDefinition("parent_const", "Constant", "const_value");
            }

        };
        final ProjectType child = new ProjectType("child", "child") {
            {
                addParent(parent1);
                addParent(parent2);
                addConstantDefinition("child_const", "Constant", "const_value");
            }
        };

        pts.add(child);
        pts.add(parent1);
        pts.add(parent2);

        ProjectTypeRegistry reg = new ProjectTypeRegistry(pts);

        Assert.assertNotNull(reg.getProjectType("parent1"));
        Assert.assertNotNull(reg.getProjectType("parent2"));
        Assert.assertNull(reg.getProjectType("child"));


    }

    @Test
    public void testWithDefaultBuilderAndRunner() throws Exception {

        Set<ProjectType> pts = new HashSet<>();
        final ProjectType type = new ProjectType("testWithDefaultBuilderAndRunner", "testWithDefaultBuilderAndRunner") {
            {
                addConstantDefinition("parent_const", "Constant", "const_value");
                setDefaultBuilder("builder1");
                setDefaultRunner("system:/runner1/myRunner");
            }

        };


        pts.add(type);
        ProjectTypeRegistry reg = new ProjectTypeRegistry(pts);

        Assert.assertNotNull(reg.getProjectType("testWithDefaultBuilderAndRunner"));

        Assert.assertEquals("builder1", reg.getProjectType("testWithDefaultBuilderAndRunner").getDefaultBuilder());
        Assert.assertEquals("system:/runner1/myRunner", reg.getProjectType("testWithDefaultBuilderAndRunner").getDefaultRunner());

    }


    @Test
    public void testTypeOf() throws Exception {

        Set<ProjectType> pts = new HashSet<>();
        final ProjectType parent = new ProjectType("parent", "parent") { };

        final ProjectType parent1 = new ProjectType("parent1", "parent") {};

        final ProjectType parent2 = new ProjectType("parent2", "parent") {};

        final ProjectType child = new ProjectType("child", "child") {
            {
                addParent(parent);
                addParent(parent2);
            }
        };

        final ProjectType child2 = new ProjectType("child2", "child2") {
            {
                addParent(child);
            }
        };

        pts.add(child);
        pts.add(parent);
        pts.add(child2);
        pts.add(parent1);
        pts.add(parent2);

        ProjectTypeRegistry reg = new ProjectTypeRegistry(pts);

        ProjectType t1 = reg.getProjectType("child2");

        Assert.assertTrue(t1.isTypeOf("parent"));
        Assert.assertTrue(t1.isTypeOf("parent2"));
        Assert.assertTrue(t1.isTypeOf("blank"));
        Assert.assertFalse(t1.isTypeOf("parent1"));


    }


    @Test
    public void testSortPTs() throws Exception {

        Set<ProjectType> pts = new HashSet<>();
        final ProjectType parent = new ProjectType("parent", "parent") { };

        final ProjectType child = new ProjectType("child", "child") {
            {
                addParent(parent);
            }
        };

        final ProjectType child2 = new ProjectType("child2", "child2") {
            {
                addParent(child);
            }
        };

        pts.add(child);
        pts.add(parent);
        pts.add(child2);

        ProjectTypeRegistry reg = new ProjectTypeRegistry(pts);
        List<ProjectType> list = reg.getProjectTypes(new ProjectTypeRegistry.ChildToParentComparator());

        Assert.assertEquals(list.get(0).getId(), "child2");
        Assert.assertEquals(list.get(1).getId(), "child");
        Assert.assertEquals(list.get(2).getId(), "parent");
        Assert.assertEquals(list.get(3).getId(), "blank");

    }


    /**
         * @author gazarenkov
         */
    @Singleton
    public static class MyVPFactory implements ValueProviderFactory {

        @Override
        public ValueProvider newInstance(FolderEntry projectFolder) {
            return new MyValueProvider();

        }

        public static class MyValueProvider implements ValueProvider {

            @Override
            public List<String> getValues(String attributeName) throws ValueStorageException {
                return Arrays.asList("gena");
            }

            @Override
            public void setValues(String attributeName, List<String> value) throws ValueStorageException, InvalidValueException {

            }

        }
    }

    /**
     * @author gazarenkov
     */
    @Singleton
    public static class MyProjectType extends ProjectType {

        @Inject
        public MyProjectType(MyVPFactory myVPFactory) {

            super("my", "my type");

            addConstantDefinition("const", "Constant", "const_value");
            addVariableDefinition("var", "Variable", false, myVPFactory);
            addVariableDefinition("var1", "var", true, new AttributeValue("value"));

        }

    }
}
