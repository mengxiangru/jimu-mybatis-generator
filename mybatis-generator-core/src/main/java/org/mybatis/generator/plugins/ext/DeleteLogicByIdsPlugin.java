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

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * Created by xiangru.meng on 2016/6/6.
 */
public class DeleteLogicByIdsPlugin extends PluginAdapter {
    private static final String KEY_OF_DELETE_FLAG_COLUMN = "deleteFlagColumn";
    String deleteFlagColumn;


    public boolean validate(List<String> warnings) {
        deleteFlagColumn = properties.getProperty(KEY_OF_DELETE_FLAG_COLUMN);

        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        interfaze.addMethod(generateDeleteLogicByIds(introspectedTable));

        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();//数据库表名

        XmlElement parentElement = document.getRootElement();

        // 产生分页语句前半部分
        XmlElement deleteLogicByIdsElement = new XmlElement("update");
        deleteLogicByIdsElement.addAttribute(new Attribute("id", "deleteLogicByIds"));

        deleteLogicByIdsElement.addElement(new TextElement("UPDATE " + tableName));
        deleteLogicByIdsElement.addElement(new TextElement("SET " + deleteFlagColumn + " = #{deleteFlag,jdbcType=INTEGER}"));
        deleteLogicByIdsElement.addElement(new TextElement("WHERE id IN " + " <foreach item=\"item\" index=\"index\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\">#{item}</foreach> "));

        parentElement.addElement(deleteLogicByIdsElement);

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    private Method generateDeleteLogicByIds(IntrospectedTable introspectedTable) {

        Method m = new Method("deleteLogicByIds");

        m.setVisibility(JavaVisibility.PUBLIC);

        m.setReturnType(FullyQualifiedJavaType.getIntInstance());

        m.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), "deleteFlag", "@Param(\"deleteFlag\")"));
        m.addParameter(new Parameter(new FullyQualifiedJavaType("Integer[]"), "ids", "@Param(\"ids\")"));

        context.getCommentGenerator().addGeneralMethodComment(m, introspectedTable);
        return m;
    }

}
