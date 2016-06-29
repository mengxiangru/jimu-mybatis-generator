
        ${domainObjectName} ${domainObjectNameWithLower} = ${domainObjectNameWithLower}Service.get${domainObjectName}ById(id);
        Vm${domainObjectName} vm${domainObjectName} = new Vm${domainObjectName}();
        vm${domainObjectName}.convertToVM(${domainObjectNameWithLower});

        model.addAttribute("vm${domainObjectName}", vm${domainObjectName});

        return "/${domainObjectName}/Edit${domainObjectName}";