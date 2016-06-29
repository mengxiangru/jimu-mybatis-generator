
        if (result.hasErrors()) {
            NotificationTips.setFormError(getRequest());
            return "/${domainObjectName}/Add${domainObjectName}";
        }

        ${domainObjectName} ${domainObjectNameWithLower} = vm${domainObjectName}.convertToModel();

        ${domainObjectNameWithLower}Service.insert(${domainObjectNameWithLower});

        NotificationTips.setInfo("保存成功", getRequest());
        return "redirect:/${domainObjectNameWithLower}/list";