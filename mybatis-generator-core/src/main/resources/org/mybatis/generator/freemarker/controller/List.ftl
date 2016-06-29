
        PageHelper.startPage(page, PAGE_SIZE);
        List<${domainObjectName}> ${domainObjectNameWithLower}List = ${domainObjectNameWithLower}Service.get${domainObjectName}();
    
        List<Vm${domainObjectName}> vm${domainObjectName}List = ImmutableSet.copyOf(FluentIterable.from(${domainObjectNameWithLower}List).transform(new Function<${domainObjectName}, Vm${domainObjectName}>() {
            @Override
            public Vm${domainObjectName} apply(${domainObjectName} ${domainObjectNameWithLower}) {
            Vm${domainObjectName} vm${domainObjectName} = new Vm${domainObjectName}();
            vm${domainObjectName}.convertToVM(${domainObjectNameWithLower});
            return vm${domainObjectName};
            }
            }).toList()).asList();
    
        Map<String, Object> params = new HashMap<String, Object>();
                
        model.put("vm${domainObjectName}List", vm${domainObjectName}List);
        model.addAttribute("page", new ViewModelNumberPager(page, PAGE_SIZE, new Long(((Page) ${domainObjectNameWithLower}List).getTotal()).intValue(), "/${domainObjectNameWithLower}/list", params));
    
        return "/${domainObjectName}/List";