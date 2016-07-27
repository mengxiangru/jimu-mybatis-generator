
        ${domainObjectName} ${domainObjectNameWithLower} = ${domainObjectNameWithLower}Service.get${domainObjectName}ById(key);

        if(${domainObjectNameWithLower} != null){
                ${domainObjectNameWithLower}Service.deleteById(key);
        }

        NotificationTips.setInfo("删除成功", getRequest());
        return "redirect:/${domainObjectNameWithLower}/list";