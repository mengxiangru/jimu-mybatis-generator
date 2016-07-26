<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/Views/Shared/Taglib.jsp" %>

<head>
    <c:set var="activeTab" value="1" scope="request"/>
</head>

<body>

<div class="page-header">
    <h2>列表</h2>
</div>
<%=NotificationTips.showErrorWarnInfo(request)%>
<div class="panel panel-default">
    <!-- Default panel contents -->
    <div class="panel-heading">
        <form action="/${domainObjectNameWithLower}/search" method="post">
            <button type="button" class="btn btn-success" onclick="submitAction('/${domainObjectNameWithLower}/add${domainObjectName}/')">
                <span class="glyphicon glyphicon-plus" aria-hidden="true"></span>添加
            </button>

            <!-- 搜索框 -->
            <div class="input-group navbar-right" style="width: 250px;margin-right: 20px;">
                <input type="text" name="parameter" id="parameter" class="form-control" placeholder="名称.." value="${r'${parameter}'}"/>
                    <span class="input-group-btn">
                        <button class="btn btn-info" type="submit">
                            <span class="glyphicon glyphicon-search" aria-hidden="true"></span>
                        </button>
                    </span>
            </div>
        </form>
    </div>

    <table class="table table-striped table-hover">
        <thead>
        <tr>
        <#list baseColumnList as baseColumn>
            <th>${baseColumn.remarks}</th>
        </#list>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${r'${vm'}${domainObjectName}List}">
            <tr>
            <#list baseColumnList as baseColumn>
                <#if baseColumn.fullyQualifiedJavaType == "java.util.Date">
                    <td>
                        <fmt:formatDate value='${r'${item.'}${baseColumn.javaProperty}}' pattern='yyyy-MM-dd HH:mm:ss'/>
                    </td>
                <#else>
                    <td>${r'${item.'}${baseColumn.javaProperty}}</td>
                </#if>
            </#list>
                <td>
                    <button type="button" class="btn btn-info btn-xs" onclick="submitAction('/${domainObjectNameWithLower}/edit${domainObjectName}/${r'${item.id}'}')">
                        <span class="glyphicon glyphicon-edit" aria-hidden="true"></span> 编辑
                    </button>
                    <button type="button" class="btn btn-info btn-xs" onclick="warningAction('确认删除？','/${domainObjectNameWithLower}/delete${domainObjectName}/${r'${item.id}'}')">
                        <span class="glyphicon glyphicon-remove" aria-hidden="true"></span> 删除
                    </button>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
${r'${page}'}
</body>


