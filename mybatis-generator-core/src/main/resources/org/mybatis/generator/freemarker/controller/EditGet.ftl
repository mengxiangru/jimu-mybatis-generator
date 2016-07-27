
        ${domainObjectName} ${domainObjectNameWithLower} = ${domainObjectNameWithLower}Service.get${domainObjectName}ById(key);
        Vm${domainObjectName} vm${domainObjectName} = new Vm${domainObjectName}();
        vm${domainObjectName}.convertToVM(${domainObjectNameWithLower});

        model.addAttribute("vm${domainObjectName}", vm${domainObjectName});

        return "/${domainObjectName}/Edit${domainObjectName}";