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

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiangru.meng on 2016/6/6.
 */
public class ServiceJavaFilesPlugin extends PluginAdapter {
    private static final String KEY_OF_SERVICE_PROJECT = "targetProject";
    private static final String KEY_OF_SERVICE_PACKAGE = "targetPackage";
    String targetProject;
    String targetPackage;

    @Override
    public boolean validate(List<String> warnings) {
        targetProject = properties.getProperty(KEY_OF_SERVICE_PROJECT);
        targetPackage = properties.getProperty(KEY_OF_SERVICE_PACKAGE);

        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

        ArrayList<GeneratedJavaFile> retn = new ArrayList<GeneratedJavaFile>();

        String daoBasePackage = this.getContext().getJavaClientGeneratorConfiguration().getTargetPackage();
        String domainObjectName = introspectedTable.getTableConfiguration().getDomainObjectName();
        String DomainObjectNameWithLowerFirstChar = new StringBuilder().append(Character.toLowerCase(domainObjectName.charAt(0))).append(domainObjectName.substring(1)).toString();

        //service
        TopLevelClass serviceClass = getServiceClass(introspectedTable, targetPackage, daoBasePackage, domainObjectName, DomainObjectNameWithLowerFirstChar);
        GeneratedJavaFile serviceImplClassJavaFile = new GeneratedJavaFile(serviceClass, targetProject, new DefaultJavaFormatter());
        retn.add(serviceImplClassJavaFile);

        return retn;
    }

    private Interface getDaoInterface(IntrospectedTable introspectedTable, String daoBasePackage, String domainObjectName, String DomainObjectNameWithLowerFirstChar) {
        String domainBasePackage = this.getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
        //class
        Interface cls = new Interface(daoBasePackage + "." + domainObjectName + "Dao");
        cls.setVisibility(JavaVisibility.PUBLIC);
        cls.addSuperInterface(new FullyQualifiedJavaType(String.format("BaseDao<%s>", domainObjectName)));

        cls.addImportedType(new FullyQualifiedJavaType(String.format("%s.base.BaseDao", daoBasePackage)));
        cls.addImportedType(new FullyQualifiedJavaType(String.format("%s.%s", domainBasePackage, domainObjectName)));
        return cls;
    }

    private TopLevelClass getDaoImplClass(IntrospectedTable introspectedTable, String daoBasePackage, String domainObjectName, String DomainObjectNameWithLowerFirstChar) {
        String domainBasePackage = this.getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
        //class
        TopLevelClass cls = new TopLevelClass(daoBasePackage + ".impl." + domainObjectName + "DaoImpl");
        cls.addAnnotation(String.format("@Repository(value=\"%sDao\")", DomainObjectNameWithLowerFirstChar));
        cls.setSuperClass(String.format("BaseDaoImpl<%s>", domainObjectName));
        cls.addSuperInterface(new FullyQualifiedJavaType(String.format("%sDao", domainObjectName)));
        cls.addImportedType("org.springframework.stereotype.Repository");
        cls.addImportedType(String.format("%s.%sDao", daoBasePackage, domainObjectName));
        cls.addImportedType(String.format("%s.base.impl.BaseDaoImpl", daoBasePackage));
        cls.addImportedType(String.format("%s.%s", domainBasePackage, domainObjectName));

        //fields
        Field nameSpaceField = new Field();
        nameSpaceField.setVisibility(JavaVisibility.PRIVATE);
        nameSpaceField.setStatic(true);
        nameSpaceField.setFinal(true);
        nameSpaceField.setType(FullyQualifiedJavaType.getStringInstance());
        nameSpaceField.setName("NAME_SPACE");
        nameSpaceField.setInitializationString("\"" + daoBasePackage + "." +
                introspectedTable.getTableConfiguration().getDomainObjectName() + "Mapper\"");
        cls.addField(nameSpaceField);

        //methods
        Method getNameSpace = new Method();
        getNameSpace.addAnnotation("@Override");
        getNameSpace.setVisibility(JavaVisibility.PROTECTED);
        getNameSpace.setName("getNameSpace");
        getNameSpace.setReturnType(FullyQualifiedJavaType.getStringInstance());
        getNameSpace.addBodyLine("return NAME_SPACE;");
        cls.addMethod(getNameSpace);
        return cls;
    }

    private Interface getServiceInterface(IntrospectedTable introspectedTable, String serviceBasePackage, String domainObjectName, String DomainObjectNameWithLowerFirstChar) {
        String domainBasePackage = this.getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
        //class
        Interface cls = new Interface(serviceBasePackage + "." + domainObjectName + "Service");
        cls.setVisibility(JavaVisibility.PUBLIC);
        cls.addSuperInterface(new FullyQualifiedJavaType(String.format("BaseService<%s>", domainObjectName)));

        cls.addImportedType(new FullyQualifiedJavaType(String.format("%s.base.BaseService", serviceBasePackage)));
        cls.addImportedType(new FullyQualifiedJavaType(String.format("%s.%s", domainBasePackage, domainObjectName)));
        return cls;
    }

    private TopLevelClass getServiceImplClass(IntrospectedTable introspectedTable, String serviceBasePackage, String daoBasePackage, String domainObjectName, String DomainObjectNameWithLowerFirstChar) {
        String domainBasePackage = this.getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
        //class
        TopLevelClass cls = new TopLevelClass(serviceBasePackage + ".impl." + domainObjectName + "ServiceImpl");
        cls.setVisibility(JavaVisibility.PUBLIC);
        cls.addAnnotation(String.format("@Service(value=\"%sService\")", DomainObjectNameWithLowerFirstChar));
        cls.addAnnotation("@Transactional(rollbackFor=Exception.class)");
        cls.setSuperClass(String.format("BaseServiceImpl<%s>", domainObjectName));
        cls.addSuperInterface(new FullyQualifiedJavaType(String.format("%sService", domainObjectName)));
        cls.addImportedType("javax.annotation.Resource");
        cls.addImportedType("org.springframework.stereotype.Service");
        cls.addImportedType("org.springframework.transaction.annotation.Transactional");
        cls.addImportedType(String.format("%s.%sDao", daoBasePackage, domainObjectName));
        cls.addImportedType(String.format("%s.base.BaseDao", daoBasePackage));
        cls.addImportedType(String.format("%s.base.impl.BaseServiceImpl", serviceBasePackage));
        cls.addImportedType(String.format("%s.%sService", serviceBasePackage, domainObjectName));
        cls.addImportedType(String.format("%s.%s", domainBasePackage, domainObjectName));
        //fields
        Field daoField = new Field();
        daoField.addAnnotation("@Resource");
        daoField.setVisibility(JavaVisibility.PRIVATE);
        daoField.setType(new FullyQualifiedJavaType(String.format("%sDao", domainObjectName)));
        daoField.setName(String.format("%sDao", DomainObjectNameWithLowerFirstChar));
        cls.addField(daoField);

        //methods
        Method getDao = new Method();
        getDao.addAnnotation("@Override");
        getDao.setVisibility(JavaVisibility.PROTECTED);
        getDao.setName("getDao");
        getDao.setReturnType(new FullyQualifiedJavaType(String.format("BaseDao<%s>", domainObjectName)));
        getDao.addBodyLine(String.format("return %sDao;", DomainObjectNameWithLowerFirstChar));
        cls.addMethod(getDao);

        return cls;
    }

    private TopLevelClass getServiceClass(IntrospectedTable introspectedTable, String targetPackage, String daoBasePackage, String domainObjectName, String domainObjectNameWithLowerFirstChar) {
        String domainBasePackage = this.getContext().getJavaModelGeneratorConfiguration().getTargetPackage();
        //class
        TopLevelClass cls = new TopLevelClass(targetPackage + "." + domainObjectName + "Service");
        cls.setVisibility(JavaVisibility.PUBLIC);
        cls.addAnnotation(String.format("@Service(value=\"%sService\")", domainObjectNameWithLowerFirstChar));
        cls.addImportedType("javax.annotation.Resource");
        cls.addImportedType("org.springframework.stereotype.Service");
        cls.addImportedType(String.format("%s.%sRepository", daoBasePackage, domainObjectName));
        cls.addImportedType(String.format("%s.%s", domainBasePackage, domainObjectName));
        cls.addImportedType(FullyQualifiedJavaType.getNewListInstance());
        cls.addImportedType(FullyQualifiedJavaType.getNewHashMapInstance());


        //fields
        Field field = new Field();
        field.addAnnotation(String.format("@Resource(name=\"%sRepository\")", domainObjectNameWithLowerFirstChar));
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(new FullyQualifiedJavaType(String.format("%sRepository", domainObjectName)));
        field.setName(String.format("%sRepository", domainObjectNameWithLowerFirstChar));
        cls.addField(field);

        //insert methods
        Method insert = new Method();
        insert.setVisibility(JavaVisibility.PUBLIC);
        insert.setName("insert");
        insert.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
        insert.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName), domainObjectNameWithLowerFirstChar));
        insert.addBodyLine(String.format("return %sRepository.insert(%s) > 0;", domainObjectNameWithLowerFirstChar, domainObjectNameWithLowerFirstChar));
        cls.addMethod(insert);

        //delete methods
        Method delete = new Method();
        delete.setVisibility(JavaVisibility.PUBLIC);
        delete.setName("deleteById");
        delete.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
        String deleteParam = "";
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
            cls.addImportedType(type);
            deleteParam = "key";
            delete.addParameter(new Parameter(type, deleteParam)); //$NON-NLS-1$
        } else {
            List<IntrospectedColumn> introspectedColumns = introspectedTable.getPrimaryKeyColumns();
            boolean annotate = introspectedColumns.size() > 1;
            if (annotate) {
                cls.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param")); //$NON-NLS-1$
            }
            StringBuilder sb = new StringBuilder();
            for (IntrospectedColumn introspectedColumn : introspectedColumns) {
                FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
                cls.addImportedType(type);
                deleteParam = introspectedColumn.getJavaProperty();
                Parameter parameter = new Parameter(type, deleteParam);
                if (annotate) {
                    sb.setLength(0);
                    sb.append("@Param(\""); //$NON-NLS-1$
                    sb.append(introspectedColumn.getJavaProperty());
                    sb.append("\")"); //$NON-NLS-1$
                    parameter.addAnnotation(sb.toString());
                }
                delete.addParameter(parameter);
            }
        }
        delete.addBodyLine(String.format("return %sRepository.deleteById(%s) > 0;", domainObjectNameWithLowerFirstChar, deleteParam));
        cls.addMethod(delete);

        //update methods
        Method update = new Method();
        update.setVisibility(JavaVisibility.PUBLIC);
        update.setName("updateById");
        update.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
        update.addParameter(new Parameter(new FullyQualifiedJavaType(domainObjectName), domainObjectNameWithLowerFirstChar));
        update.addBodyLine(String.format("return %sRepository.updateById(%s) > 0;", domainObjectNameWithLowerFirstChar, domainObjectNameWithLowerFirstChar));
        cls.addMethod(update);

        //getById methods
        Method getById = new Method();
        getById.setVisibility(JavaVisibility.PUBLIC);
        getById.setName(String.format("get%sById", domainObjectName));
        getById.setReturnType(new FullyQualifiedJavaType(domainObjectName));

        String getByIdParam = "";
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
            cls.addImportedType(type);
            getByIdParam = "key";
            getById.addParameter(new Parameter(type, getByIdParam)); //$NON-NLS-1$
        } else {
            List<IntrospectedColumn> introspectedColumns = introspectedTable.getPrimaryKeyColumns();
            boolean annotate = introspectedColumns.size() > 1;
            if (annotate) {
                cls.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param")); //$NON-NLS-1$
            }
            StringBuilder sb = new StringBuilder();
            for (IntrospectedColumn introspectedColumn : introspectedColumns) {
                FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
                cls.addImportedType(type);
                getByIdParam = introspectedColumn.getJavaProperty();
                Parameter parameter = new Parameter(type, getByIdParam);
                if (annotate) {
                    sb.setLength(0);
                    sb.append("@Param(\""); //$NON-NLS-1$
                    sb.append(introspectedColumn.getJavaProperty());
                    sb.append("\")"); //$NON-NLS-1$
                    parameter.addAnnotation(sb.toString());
                }
                getById.addParameter(parameter);
            }
        }
        getById.addBodyLine(String.format("return %sRepository.selectById(%s);", domainObjectNameWithLowerFirstChar, getByIdParam));
        cls.addMethod(getById);

        //search methods
        Method search = new Method();
        search.setVisibility(JavaVisibility.PUBLIC);
        search.setName(String.format("search%s", domainObjectName));
        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("List");
        FullyQualifiedJavaType listType;
        if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
            listType = new FullyQualifiedJavaType(introspectedTable.getRecordWithBLOBsType());
        } else {
            listType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        }
        returnType.addTypeArgument(listType);
        search.setReturnType(returnType);
        search.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "parameter"));
        search.addBodyLine("HashMap<String, Object> paramMap = new HashMap<>();");
        search.addBodyLine("paramMap.put(\"parameter\", parameter);");
        search.addBodyLine(String.format("return %sRepository.search%s(paramMap);", domainObjectNameWithLowerFirstChar, domainObjectName));
        cls.addMethod(search);

        //get methods
        Method get = new Method();
        get.setVisibility(JavaVisibility.PUBLIC);
        get.setName(String.format("get%s", domainObjectName));
        get.setReturnType(returnType);
        get.addBodyLine("HashMap<String, Object> paramMap = new HashMap<>();");
        get.addBodyLine(String.format("return %sRepository.select%s(paramMap);", domainObjectNameWithLowerFirstChar, domainObjectName));
        cls.addMethod(get);

        return cls;
    }

}