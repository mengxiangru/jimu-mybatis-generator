
        ${domainObjectName} ${domainObjectNameWithLower} = ${domainObjectNameWithLower}Service.get${domainObjectName}ById(id);

        ${domainObjectNameWithLower}Service.deleteById(${domainObjectNameWithLower}.getId());

        NotificationTips.setInfo("删除成功", getRequest());
        return "redirect:/${domainObjectNameWithLower}/list";