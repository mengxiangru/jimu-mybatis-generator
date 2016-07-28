/**
 *    Copyright 2006-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.plugins.ext;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.internal.PluginAggregator;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangru.meng on 2016/6/6.
 */
public class ControllerJavaFilesPlugin extends PluginAdapter {
    private static final String KEY_OF_ROOT_CLASS = "rootClass";
    private static final String KEY_OF_TARGET_PROJECT = "targetProject";
    private static final String KEY_OF_TARGET_PACKAGE = "targetPackage";
    String rootClass;
    String targetProject;
    String targetPackage;

    @Override
    public boolean validate(List<String> warnings) {
        rootClass = properties.getProperty(KEY_OF_ROOT_CLASS);
        targetProject = properties.getProperty(KEY_OF_TARGET_PROJECT);
        targetPackage = properties.getProperty(KEY_OF_TARGET_PACKAGE);

        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

        ArrayList<GeneratedJavaFile> retn = new ArrayList<GeneratedJavaFile>();

        String daoBasePackage = this.getContext().getJavaClientGeneratorConfiguration().getTargetPackage();
        String domainObjectName = introspectedTable.getTableConfiguration().getDomainObjectName();
        String DomainObjectNameWithLower = new StringBuilder().append(Character.toLowerCase(domainObjectName.charAt(0))).append(domainObjectName.substring(1)).toString();

        String serviceBasePackage = daoBasePackage;
        String vmmodelBasePackage = daoBasePackage;
        for (Plugin plugin : ((PluginAggregator) this.getContext().getPlugins()).getPlugins()) {
            if (plugin instanceof ServiceJavaFilesPlugin) {
                serviceBasePackage = ((ServiceJavaFilesPlugin) plugin).targetPackage;
            }
            if (plugin instanceof VmModelJavaFilesPlugin) {
                vmmodelBasePackage = ((VmModelJavaFilesPlugin) plugin).targetPackage;
            }
        }

        //controller
        TopLevelClass controllerClass = getControllerClass(introspectedTable, targetPackage, serviceBasePackage, vmmodelBasePackage, domainObjectName, DomainObjectNameWithLower);
        GeneratedJavaFile controllerClassJavaFile = new GeneratedJavaFile(controllerClass, targetProject, new DefaultJavaFormatter());
        retn.add(controllerClassJavaFile);

        return retn;
    }

    private TopLevelClass getControllerClass(IntrospectedTable introspectedTable, String targetPackage, String serviceBasePackage, String vmmodelBasePackage, String domainObjectName, String domainObjectNameWithLower) {
        String domainBasePackage = this.getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
        //class
        TopLevelClass cls = new TopLevelClass(targetPackage + "." + domainObjectName + "Controller");
        cls.setVisibility(JavaVisibility.PUBLIC);
        cls.addAnnotation("@Controller");
        cls.addAnnotation(String.format("@RequestMapping(value=\"/%s\")", domainObjectNameWithLower));
        cls.addImportedType("org.springframework.stereotype.Controller");
        cls.addImportedType("javax.annotation.Resource");
        cls.addImportedType("org.springframework.web.bind.annotation.RequestMapping");
        cls.addImportedType("org.springframework.web.bind.annotation.RequestMethod");
        cls.addImportedType("org.springframework.web.bind.annotation.RequestParam");
        cls.addImportedType("org.springframework.web.bind.annotation.PathVariable");
        cls.addImportedType("org.springframework.validation.BindingResult");
        cls.addImportedType("org.springframework.ui.ModelMap");
        cls.addImportedType("org.springframework.ui.Model");
        cls.addImportedType("com.github.pagehelper.Page");
        cls.addImportedType("com.github.pagehelper.PageHelper");
        cls.addImportedType("com.google.common.base.Function");
        cls.addImportedType("com.google.common.collect.FluentIterable");
        cls.addImportedType("com.google.common.collect.ImmutableSet");
        cls.addImportedType("java.util.HashMap");
        cls.addImportedType("java.util.List");
        cls.addImportedType("java.util.Map");
        cls.addImportedType("javax.validation.Valid");
        cls.addImportedType("com.jimubox.auth.shiro.ext.CurrentUser");
        cls.addImportedType("com.jimubox.auth.shiro.model.InnerUser");
        cls.addImportedType("com.jimubox.tools.view.NotificationTips");
        cls.addImportedType("com.jimubox.tools.view.ViewModelNumberPager");

        cls.addImportedType(String.format("%s.%sService", serviceBasePackage, domainObjectName));
        cls.addImportedType(String.format("%s.Vm%s", vmmodelBasePackage, domainObjectName));
        cls.addImportedType(String.format("%s.%s", domainBasePackage, domainObjectName));

        if (rootClass != null) {
            FullyQualifiedJavaType superClass = new FullyQualifiedJavaType(rootClass);
            cls.setSuperClass(superClass);
            cls.addImportedType(superClass);
        }

        //fields
        Field serviceField = new Field();
        serviceField.addAnnotation(String.format("@Resource(name=\"%sService\")", domainObjectNameWithLower));
        serviceField.setVisibility(JavaVisibility.PRIVATE);
        serviceField.setType(new FullyQualifiedJavaType(String.format("%sService", domainObjectName)));
        serviceField.setName(String.format("%sService", domainObjectNameWithLower));
        cls.addField(serviceField);

        Configuration cfg = new Configuration();

        Map root = new HashMap();
        root.put("domainObjectName", domainObjectName);
        root.put("domainObjectNameWithLower", domainObjectNameWithLower);

        SpringTemplateLoader springTemplateLoader = new SpringTemplateLoader(new DefaultResourceLoader(), "classpath:/org/mybatis/generator/freemarker/controller/");
        cfg.setTemplateLoader(springTemplateLoader);


        //search method
        Method search = new Method();
        search.addAnnotation("@RequestMapping(value = \"/search\")");
        search.setVisibility(JavaVisibility.PUBLIC);
        search.setName(String.format("search%s", domainObjectName));
        search.setReturnType(FullyQualifiedJavaType.getStringInstance());
        search.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), "page", "@RequestParam(value = \"page\", defaultValue = \"1\")"));
        search.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "parameter", "@RequestParam(value = \"parameter\")"));
        search.addParameter(new Parameter(new FullyQualifiedJavaType("ModelMap"), "model"));
        search.addParameter(new Parameter(new FullyQualifiedJavaType("InnerUser"), "innerUser", "@CurrentUser"));

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(out, "UTF-8");

            Template template = cfg.getTemplate("Search.ftl");
            template.process(root, writer);
            search.addBodyLine(out.toString("UTF-8"));

        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        cls.addMethod(search);

        //list method
        Method list = new Method();
        list.addAnnotation("@RequestMapping(value = \"/list\")");
        list.setVisibility(JavaVisibility.PUBLIC);
        list.setName(String.format("%sList", domainObjectNameWithLower));
        list.setReturnType(FullyQualifiedJavaType.getStringInstance());
        list.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), "page", "@RequestParam(value = \"page\", defaultValue = \"1\")"));
        list.addParameter(new Parameter(new FullyQualifiedJavaType("ModelMap"), "model"));
        list.addParameter(new Parameter(new FullyQualifiedJavaType("InnerUser"), "innerUser", "@CurrentUser"));

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(out, "UTF-8");

            Template template = cfg.getTemplate("List.ftl");
            template.process(root, writer);
            list.addBodyLine(out.toString("UTF-8"));
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        cls.addMethod(list);

        //addGet method
        Method addGet = new Method();
        addGet.addAnnotation(String.format("@RequestMapping(value = \"/add%s\", method = RequestMethod.GET)", domainObjectName));
        addGet.setVisibility(JavaVisibility.PUBLIC);
        addGet.setName(String.format("add%s", domainObjectName));
        addGet.setReturnType(FullyQualifiedJavaType.getStringInstance());
        addGet.addParameter(new Parameter(new FullyQualifiedJavaType("ModelMap"), "model"));
        addGet.addParameter(new Parameter(new FullyQualifiedJavaType("InnerUser"), "innerUser", "@CurrentUser"));

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(out, "UTF-8");

            Template template = cfg.getTemplate("AddGet.ftl");
            template.process(root, writer);
            addGet.addBodyLine(out.toString("UTF-8"));
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        cls.addMethod(addGet);

        //addPost method
        Method addPost = new Method();
        addPost.addAnnotation(String.format("@RequestMapping(value = \"/add%s\", method = RequestMethod.POST)", domainObjectName));
        addPost.setVisibility(JavaVisibility.PUBLIC);
        addPost.setName(String.format("add%s", domainObjectName));
        addPost.setReturnType(FullyQualifiedJavaType.getStringInstance());
        addPost.addParameter(new Parameter(new FullyQualifiedJavaType(String.format("Vm%s", domainObjectName)), String.format("vm%s", domainObjectName), "@Valid"));
        addPost.addParameter(new Parameter(new FullyQualifiedJavaType("BindingResult"), "result"));
        addPost.addParameter(new Parameter(new FullyQualifiedJavaType("InnerUser"), "innerUser", "@CurrentUser"));

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(out, "UTF-8");

            Template template = cfg.getTemplate("AddPost.ftl");
            template.process(root, writer);
            addPost.addBodyLine(out.toString("UTF-8"));
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        cls.addMethod(addPost);

        //editGet method
        Method editGet = new Method();
        editGet.addAnnotation(String.format("@RequestMapping(value = \"/edit%s\", method = RequestMethod.GET)", domainObjectName));
        editGet.setVisibility(JavaVisibility.PUBLIC);
        editGet.setName(String.format("edit%s", domainObjectName));
        editGet.setReturnType(FullyQualifiedJavaType.getStringInstance());

        if (introspectedTable.hasPrimaryKeyColumns()) {
            List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();

            if (primaryKeyColumns.size() > 1) {
                editGet.addParameter(new Parameter(new FullyQualifiedJavaType(new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType()).getShortName()), "key"));

                cls.addImportedType(introspectedTable.getPrimaryKeyType());
            } else {
                editGet.addParameter(new Parameter(new FullyQualifiedJavaType(primaryKeyColumns.get(0).getFullyQualifiedJavaType().getShortName()), "key", "@RequestParam(name = \"" + primaryKeyColumns.get(0).getJavaProperty() + "\")"));
            }
        }

        editGet.addParameter(new Parameter(new FullyQualifiedJavaType("ModelMap"), "model"));
        editGet.addParameter(new Parameter(new FullyQualifiedJavaType("InnerUser"), "innerUser", "@CurrentUser"));

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(out, "UTF-8");

            Template template = cfg.getTemplate("EditGet.ftl");
            template.process(root, writer);
            editGet.addBodyLine(out.toString("UTF-8"));
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        cls.addMethod(editGet);


        //editPost method
        Method editPost = new Method();
        editPost.addAnnotation(String.format("@RequestMapping(value = \"/edit%s\", method = RequestMethod.POST)", domainObjectName));
        editPost.setVisibility(JavaVisibility.PUBLIC);
        editPost.setName(String.format("edit%s", domainObjectName));
        editPost.setReturnType(FullyQualifiedJavaType.getStringInstance());
        editPost.addParameter(new Parameter(new FullyQualifiedJavaType(String.format("Vm%s", domainObjectName)), String.format("vm%s", domainObjectName), "@Valid"));
        if (introspectedTable.hasPrimaryKeyColumns()) {
            List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();

            if (primaryKeyColumns.size() > 1) {
                editPost.addParameter(new Parameter(new FullyQualifiedJavaType(new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType()).getShortName()), "key"));
            } else {
                editPost.addParameter(new Parameter(new FullyQualifiedJavaType(primaryKeyColumns.get(0).getFullyQualifiedJavaType().getShortName()), "key", "@RequestParam(name = \"" + primaryKeyColumns.get(0).getJavaProperty() + "\")"));
            }
        }
        editPost.addParameter(new Parameter(new FullyQualifiedJavaType("BindingResult"), "result"));
        editPost.addParameter(new Parameter(new FullyQualifiedJavaType("InnerUser"), "innerUser", "@CurrentUser"));

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(out, "UTF-8");

            Template template = cfg.getTemplate("EditPost.ftl");
            template.process(root, writer);
            editPost.addBodyLine(out.toString("UTF-8"));
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        cls.addMethod(editPost);

        //delete method
        Method delete = new Method();
        delete.addAnnotation(String.format("@RequestMapping(value = \"/delete%s\", method = RequestMethod.GET)", domainObjectName));
        delete.setVisibility(JavaVisibility.PUBLIC);
        delete.setName(String.format("delete%s", domainObjectName));
        delete.setReturnType(FullyQualifiedJavaType.getStringInstance());

        if (introspectedTable.hasPrimaryKeyColumns()) {
            List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();

            if (primaryKeyColumns.size() > 1) {
                delete.addParameter(new Parameter(new FullyQualifiedJavaType(new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType()).getShortName()), "key"));
            } else {
                delete.addParameter(new Parameter(new FullyQualifiedJavaType(primaryKeyColumns.get(0).getFullyQualifiedJavaType().getShortName()), "key", "@RequestParam(name = \"" + primaryKeyColumns.get(0).getJavaProperty() + "\")"));
            }
        }

        delete.addParameter(new Parameter(new FullyQualifiedJavaType("ModelMap"), "model"));
        delete.addParameter(new Parameter(new FullyQualifiedJavaType("InnerUser"), "innerUser", "@CurrentUser"));

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(out, "UTF-8");

            Template template = cfg.getTemplate("Delete.ftl");
            template.process(root, writer);
            delete.addBodyLine(out.toString("UTF-8"));
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        cls.addMethod(delete);

        return cls;
    }

}