
        if (result.hasErrors()) {
                NotificationTips.setFormError(getRequest());
                return "/${domainObjectName}/Edit${domainObjectName}";
        }

        ${domainObjectName} ${domainObjectNameWithLower} = ${domainObjectNameWithLower}Service.get${domainObjectName}ById(key);
        ${domainObjectNameWithLower} = vm${domainObjectName}.updateModel(${domainObjectNameWithLower});

        ${domainObjectNameWithLower}Service.updateById(${domainObjectNameWithLower});

        NotificationTips.setInfo("修改成功", getRequest());
        return "redirect:/${domainObjectNameWithLower}/list";