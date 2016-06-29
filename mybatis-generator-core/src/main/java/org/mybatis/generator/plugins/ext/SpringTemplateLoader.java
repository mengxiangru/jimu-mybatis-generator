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

import freemarker.cache.TemplateLoader;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class SpringTemplateLoader implements TemplateLoader {

    private final ResourceLoader resourceLoader;

    private final String templateLoaderPath;


    /**
     * Create a new SpringTemplateLoader.
     *
     * @param resourceLoader     the Spring ResourceLoader to use
     * @param templateLoaderPath the template loader path to use
     */
    public SpringTemplateLoader(ResourceLoader resourceLoader, String templateLoaderPath) {
        this.resourceLoader = resourceLoader;
        if (!templateLoaderPath.endsWith("/")) {
            templateLoaderPath += "/";
        }
        this.templateLoaderPath = templateLoaderPath;

    }


    @Override
    public Object findTemplateSource(String name) throws IOException {

        Resource resource = this.resourceLoader.getResource(this.templateLoaderPath + name);
        return (resource.exists() ? resource : null);
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        Resource resource = (Resource) templateSource;
        try {
            return new InputStreamReader(resource.getInputStream(), encoding);
        } catch (IOException ex) {
            throw ex;
        }
    }

    @Override
    public long getLastModified(Object templateSource) {
        Resource resource = (Resource) templateSource;
        try {
            return resource.lastModified();
        } catch (IOException ex) {
            return -1;
        }
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
    }

}

