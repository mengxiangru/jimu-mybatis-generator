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

import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.internal.PluginAggregator;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.JavaBeansUtil.*;

/**
 * Created by xiangru.meng on 2016/6/6.
 */
public class VmModelJavaFilesPlugin extends PluginAdapter {
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
        String DomainObjectNameWithLowerFirstChar = new StringBuilder().append(Character.toLowerCase(domainObjectName.charAt(0))).append(domainObjectName.substring(1)).toString();

        String serviceBasePackage = daoBasePackage;
        for (Plugin plugin : ((PluginAggregator) this.getContext().getPlugins()).getPlugins()) {
            if (plugin instanceof ServiceJavaFilesPlugin) {
                serviceBasePackage = ((ServiceJavaFilesPlugin) plugin).targetPackage;
            }
        }

        //vmmodel
        TopLevelClass vmModelClass = getVmModelClass(introspectedTable, domainObjectName, DomainObjectNameWithLowerFirstChar);
        GeneratedJavaFile vmModelClassJavaFile = new GeneratedJavaFile(vmModelClass, targetProject, new DefaultJavaFormatter());
        retn.add(vmModelClassJavaFile);

        return retn;
    }

    private TopLevelClass getVmModelClass(IntrospectedTable introspectedTable, String domainObjectName, String domainObjectNameWithLower) {
        Plugin plugins = context.getPlugins();

        String domainBasePackage = this.getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
        //class
        TopLevelClass cls = new TopLevelClass(targetPackage + ".Vm" + domainObjectName);
        cls.setVisibility(JavaVisibility.PUBLIC);
        cls.addImportedType(String.format("%s.%s", domainBasePackage, domainObjectName));

        List<IntrospectedColumn> introspectedColumns = introspectedTable.getAllColumns();
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            Field field = getJavaBeansField(introspectedColumn, context, introspectedTable);
            if (plugins.modelFieldGenerated(field, cls, introspectedColumn, introspectedTable, ModelClassType.BASE_RECORD)) {
                cls.addField(field);
                cls.addImportedType(field.getType());
            }

            Method method = getJavaBeansGetter(introspectedColumn, context, introspectedTable);
            if (plugins.modelGetterMethodGenerated(method, cls, introspectedColumn, introspectedTable, ModelClassType.BASE_RECORD)) {
                cls.addMethod(method);
            }

            if (!introspectedTable.isImmutable()) {
                method = getJavaBeansSetter(introspectedColumn, context, introspectedTable);
                if (plugins.modelSetterMethodGenerated(method, cls, introspectedColumn, introspectedTable, ModelClassType.BASE_RECORD)) {
                    cls.addMethod(method);
                }
            }
        }

        //convertToVM method
        Method convertToVM = new Method();
        convertToVM.setVisibility(JavaVisibility.PUBLIC);
        convertToVM.setName("convertToVM");
        convertToVM.addParameter(new Parameter(new FullyQualifiedJavaType(String.format("%s", domainObjectName)), String.format("%s", domainObjectNameWithLower)));

        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            Method method = getJavaBeansGetter(introspectedColumn, context, introspectedTable);

            convertToVM.addBodyLine("this." + introspectedColumn.getJavaProperty() + " = " + String.format("%s", domainObjectNameWithLower) + "." + method.getName() + "();");
        }

        cls.addMethod(convertToVM);

        //convertToModel method
        Method convertToModel = new Method();
        convertToModel.setVisibility(JavaVisibility.PUBLIC);
        convertToModel.setName("convertToModel");
        convertToModel.setReturnType(new FullyQualifiedJavaType(String.format("%s", domainObjectName)));

        convertToModel.addBodyLine(String.format("%s %s = new %s();", domainObjectName, domainObjectNameWithLower, domainObjectName));
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            Method method = getJavaBeansSetter(introspectedColumn, context, introspectedTable);

            convertToModel.addBodyLine(String.format("%s", domainObjectNameWithLower) + "." + method.getName() + "(" + introspectedColumn.getJavaProperty() + ");");
        }
        convertToModel.addBodyLine(String.format("return %s;", domainObjectNameWithLower));

        cls.addMethod(convertToModel);

        //updateModel method
        Method updateModel = new Method();
        updateModel.setVisibility(JavaVisibility.PUBLIC);
        updateModel.setName("updateModel");
        updateModel.addParameter(new Parameter(new FullyQualifiedJavaType(String.format("%s", domainObjectName)), String.format("%s", domainObjectNameWithLower)));
        updateModel.setReturnType(new FullyQualifiedJavaType(String.format("%s", domainObjectName)));

        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            Method method = getJavaBeansSetter(introspectedColumn, context, introspectedTable);

            updateModel.addBodyLine(String.format("%s", domainObjectNameWithLower) + "." + method.getName() + "(" + introspectedColumn.getJavaProperty() + ");");
        }
        updateModel.addBodyLine(String.format("return %s;", domainObjectNameWithLower));

        cls.addMethod(updateModel);

        return cls;
    }
}