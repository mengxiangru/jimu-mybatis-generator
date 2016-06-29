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

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by xiangru.meng on 2016/6/6.
 */
public class DeleteLogicByIdPlugin extends PluginAdapter {
    private static final String KEY_OF_DELETE_FLAG_COLUMN = "deleteFlagColumn";
    private static final String KEY_OF_DELETE_IS_SIMPLE = "isSimple";
    private String deleteFlagColumn;
    private boolean isSimple;


    public boolean validate(List<String> warnings) {
        deleteFlagColumn = properties.getProperty(KEY_OF_DELETE_FLAG_COLUMN);
        isSimple = properties.getProperty(KEY_OF_DELETE_IS_SIMPLE) == null ? true : Boolean.parseBoolean(properties.getProperty(KEY_OF_DELETE_IS_SIMPLE));
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();

        Method method = new Method("deleteLogicById");

        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), "deleteFlag", "@Param(\"deleteFlag\")"));

        if (!isSimple && introspectedTable.getRules().generatePrimaryKeyClass()) {
            FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
            importedTypes.add(type);
            method.addParameter(new Parameter(type, "key"));
        } else {
            List<IntrospectedColumn> introspectedColumns = introspectedTable.getPrimaryKeyColumns();
            boolean annotate = introspectedColumns.size() > 1;
            if (annotate) {
                importedTypes.add(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));
            }
            StringBuilder sb = new StringBuilder();
            for (IntrospectedColumn introspectedColumn : introspectedColumns) {
                FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
                importedTypes.add(type);
                Parameter parameter = new Parameter(type, introspectedColumn.getJavaProperty());
                if (annotate) {
                    sb.setLength(0);
                    sb.append("@Param(\"");
                    sb.append(introspectedColumn.getJavaProperty());
                    sb.append("\")");
                    parameter.addAnnotation(sb.toString());
                }
                method.addParameter(parameter);
            }
        }

        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);

        interfaze.addMethod(method);
        interfaze.addImportedTypes(importedTypes);

        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();//数据库表名

        XmlElement parentElement = document.getRootElement();

        // 产生分页语句前半部分
        XmlElement deleteLogicByIdsElement = new XmlElement("update");
        deleteLogicByIdsElement.addAttribute(new Attribute("id", "deleteLogicById"));

        deleteLogicByIdsElement.addElement(new TextElement("UPDATE " + tableName));
        deleteLogicByIdsElement.addElement(new TextElement("SET " + deleteFlagColumn + " = #{deleteFlag,jdbcType=INTEGER}"));

        StringBuilder sb = new StringBuilder();
        boolean and = false;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            sb.setLength(0);
            if (and) {
                sb.append("  AND "); 
            } else {
                sb.append("WHERE "); 
                and = true;
            }

            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = "); 
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
            deleteLogicByIdsElement.addElement(new TextElement(sb.toString()));
        }

        parentElement.addElement(deleteLogicByIdsElement);

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

}
