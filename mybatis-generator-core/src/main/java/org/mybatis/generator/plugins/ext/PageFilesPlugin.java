/**
 * Copyright 2006-2015 the original author or authors.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mybatis.generator.plugins.ext;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.plugins.freemarker.FreemarkerDocument;
import org.mybatis.generator.plugins.freemarker.FreemarkerElement;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.*;
import java.sql.Types;
import java.util.*;

/**
 * Created by xiangru.meng on 2016/6/6.
 */
public class PageFilesPlugin extends PluginAdapter {
    private static final String KEY_OF_TARGET_PROJECT = "targetProject";
    private static final String KEY_OF_TARGET_PACKAGE = "targetPackage";
    String targetProject;
    String targetPackage;

    @Override
    public boolean validate(List<String> warnings) {
        targetProject = properties.getProperty(KEY_OF_TARGET_PROJECT);
        targetPackage = properties.getProperty(KEY_OF_TARGET_PACKAGE);

        return true;
    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {

        String domainObjectName = introspectedTable.getTableConfiguration().getDomainObjectName();
        String domainObjectNameWithLower = new StringBuilder().append(Character.toLowerCase(domainObjectName.charAt(0))).append(domainObjectName.substring(1)).toString();

        Configuration cfg = new Configuration();
        cfg.setEncoding(Locale.CHINA, "UTF-8");
        SpringTemplateLoader springTemplateLoader = new SpringTemplateLoader(new DefaultResourceLoader(), "classpath:/org/mybatis/generator/freemarker/page/");
        cfg.setTemplateLoader(springTemplateLoader);

        Map param = new HashMap();
        param.put("domainObjectName", domainObjectName);
        param.put("domainObjectNameWithLower", domainObjectNameWithLower);
        param.put("baseColumnList", introspectedTable.getBaseColumns());
        param.put("keyColumnList", introspectedTable.getPrimaryKeyColumns());

        StringBuffer sb = new StringBuffer();
        boolean splitFlag = false;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getBaseColumns()) {
            if (introspectedColumn.getJdbcType() == Types.VARCHAR) {
                if(splitFlag){
                    sb.append("/");
                }else {
                    splitFlag =true;
                }
                sb.append(introspectedColumn.getRemarks() );
            }
        }
        param.put("placeholder", sb.toString());

        sb.setLength(0);
        boolean andFlag = false;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            if(andFlag){
                sb.append("&");
            }else {
                andFlag =true;
            }
            sb.append(introspectedColumn.getJavaProperty() + "=${item." + introspectedColumn.getJavaProperty() + "}");
        }
        param.put("primaryKey", sb.toString());

        List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>(3);
        answer.add(getListPage(cfg, param));
        answer.add(getAddPage(cfg, param));
        answer.add(getEditPage(cfg, param));

        return answer;
    }

    private GeneratedXmlFile getListPage(Configuration cfg, Map param) {
        String domainObjectName = (String) param.get("domainObjectName");

        FreemarkerDocument document = new FreemarkerDocument();

        FreemarkerElement root = new FreemarkerElement("ListPage"); //$NON-NLS-1$
        document.setRootElement(root);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(out, "UTF-8");

            Template template = cfg.getTemplate("List.ftl");
            template.process(param, writer);
            root.addElement(new TextElement(out.toString("UTF-8")));

        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        GeneratedXmlFile gxf = new GeneratedXmlFile(document, properties.getProperty("fileName", "List.jsp"), targetPackage + "/" + domainObjectName, targetProject, false, context.getXmlFormatter());

        return gxf;
    }

    private GeneratedXmlFile getAddPage(Configuration cfg, Map param) {
        String domainObjectName = (String) param.get("domainObjectName");

        FreemarkerDocument document = new FreemarkerDocument();

        FreemarkerElement root = new FreemarkerElement("AddPage"); //$NON-NLS-1$
        document.setRootElement(root);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(out, "UTF-8");

            Template template = cfg.getTemplate("Add.ftl");
            template.process(param, writer);
            root.addElement(new TextElement(out.toString("UTF-8")));

        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        GeneratedXmlFile gxf = new GeneratedXmlFile(document, properties.getProperty("fileName", String.format("Add%s.jsp", domainObjectName)), targetPackage + "/" + domainObjectName, targetProject, false, context.getXmlFormatter());

        return gxf;
    }

    private GeneratedXmlFile getEditPage(Configuration cfg, Map param) {
        String domainObjectName = (String) param.get("domainObjectName");

        FreemarkerDocument document = new FreemarkerDocument();

        FreemarkerElement root = new FreemarkerElement("EditPage"); //$NON-NLS-1$
        document.setRootElement(root);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(out, "UTF-8");

            Template template = cfg.getTemplate("Edit.ftl");
            template.process(param, writer);
            root.addElement(new TextElement(out.toString("UTF-8")));

        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        GeneratedXmlFile gxf = new GeneratedXmlFile(document, properties.getProperty("fileName", String.format("Edit%s.jsp", domainObjectName)), targetPackage + "/" + domainObjectName, targetProject, false, context.getXmlFormatter());

        return gxf;
    }
}