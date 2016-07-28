<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/Views/Shared/Taglib.jsp" %>

<head>
    <c:set var="activeTab" value="1" scope="request"/>

    <script type="text/javascript">
        $(document).ready(function () {
            EDEN_UI_HELPER.HookDatePicker();
            EDEN_UI_HELPER.HookDateTimePicker();
            EDEN_UI_HELPER.HookDateTimeHourPicker();
        });
    </script>
</head>

<body>

<div class="page-header">
    <h2>编辑</h2>
</div>

<div class="panel panel-default">
    <div class="panel-heading">
        <button type="button" class="btn btn-danger" onclick="warningBack('未保存数据将丢失，确定返回？','')">
            取消
        </button>
        <button type="button" class="btn btn-success" onclick="submitForm('form1')">
            保存
        </button>
    </div>
    <div class="panel-body">
        <%=NotificationTips.showErrorWarnInfo(request)%>
        <sf:form id="form1" modelAttribute="vm${domainObjectName}" action="/${domainObjectNameWithLower}/edit${domainObjectName}" method="post" cssClass="form-horizontal" role="form">
            <!-- Hidden -->
            <input type="hidden" value="${r'${test}'}"><!-- 此处为解决IDEA报错问题 -->
            <!-- Hidden -->

            <div class="page-header" style="margin: 30px 0 20px">
                <h3>&nbsp;
                    <small>主键</small>
                </h3>
            </div>
        <#list keyColumnList as keyColumn>
            <div class="form-group">
                <label for="${keyColumn.javaProperty}" class="col-sm-2 control-label">${keyColumn.remarks}</label>

                <div class="col-sm-4">
                    <div class="input-group">
                        <span class="input-group-addon">*</span>
                        <#if keyColumn.fullyQualifiedJavaType == "java.util.Date">
                            <input class="js-datetimeHour-picker form-control" type="text" id="${keyColumn.javaProperty}" name="${keyColumn.javaProperty}" readonly/>
                        <#else>
                            <sf:input cssClass="form-control" path="${keyColumn.javaProperty}" id="${keyColumn.javaProperty}" placeholder="最长${keyColumn.length}" maxlength="${keyColumn.length}" readonly="true"/>
                        </#if>
                    </div>
                </div>
                <label for="${keyColumn.javaProperty}" class="col-sm-6 control-label inputError_GSM">
                    <sf:errors path="${keyColumn.javaProperty}"/>
                </label>
            </div>

        </#list>

            <div class="page-header" style="margin: 30px 0 20px">
                <h3>&nbsp;
                    <small>基本信息</small>
                </h3>
            </div>
        <#list baseColumnList as baseColumn>
            <div class="form-group">
                <label for="${baseColumn.javaProperty}" class="col-sm-2 control-label">${baseColumn.remarks}</label>

                <div class="col-sm-4">
                    <div class="input-group">
                        <span class="input-group-addon">*</span>
                        <#if baseColumn.fullyQualifiedJavaType == "java.util.Date">
                            <input class="js-datetimeHour-picker form-control" type="text" id="${baseColumn.javaProperty}" name="${baseColumn.javaProperty}" value='<fmt:formatDate value="${r'${vm'}${domainObjectName}.${baseColumn.javaProperty}}" pattern="yyyy-MM-dd HH:mm:ss"/>'/>
                        <#else>
                            <sf:input cssClass="form-control" path="${baseColumn.javaProperty}" id="${baseColumn.javaProperty}" placeholder="最长${baseColumn.length}" maxlength="${baseColumn.length}"/>
                        </#if>
                    </div>
                </div>
                <label for="${baseColumn.javaProperty}" class="col-sm-6 control-label inputError_GSM">
                    <sf:errors path="${baseColumn.javaProperty}"/>
                </label>
            </div>

        </#list>

        </sf:form>
    </div>
</div>
</body>