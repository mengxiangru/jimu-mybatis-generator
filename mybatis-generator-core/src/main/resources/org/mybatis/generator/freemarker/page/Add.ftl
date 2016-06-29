
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/Views/Shared/Taglib.jsp" %>

<layout:override name="<%=Blocks.BLOCK_HEADER_CSS%>">
</layout:override>

<layout:override name="<%=Blocks.BLOCK_HEADER_SCRIPTS%>">
    <script type="text/javascript">
        $(document).ready(function () {
            EDEN_UI_HELPER.HookDatePicker();
            EDEN_UI_HELPER.HookDateTimePicker();
            EDEN_UI_HELPER.HookDateTimeHourPicker();
        });
    </script>
</layout:override>

<layout:override name="<%=Blocks.BLOCK_HEADER%>">
    <c:import url="/Views/Shared/Header.jsp">
        <c:param name="title" value="ActiveMenu"/>
    </c:import>
</layout:override>

<layout:override name="<%=Blocks.BLOCK_BODY%>">
    <div class="container">

        <div class="page-header">
            <h2>添加</h2>
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
                <sf:form id="form1" modelAttribute="vm${domainObjectName}" action="/${domainObjectNameWithLower}/add${domainObjectName}" method="post" cssClass="form-horizontal" role="form">
                    <!-- Hidden -->
                    <input type="hidden" value="${r'${test}'}"><!-- 此处为解决IDEA报错问题 -->
                    <!-- Hidden -->

                <#list baseColumnList as baseColumn>
                    <div class="form-group">
                        <label for="${baseColumn.javaProperty}" class="col-sm-2 control-label">${baseColumn.remarks}</label>

                        <div class="col-sm-4">
                            <div class="input-group">
                                <span class="input-group-addon">*</span>
                                <#if baseColumn.fullyQualifiedJavaType == "java.util.Date">
                                 <input class="js-datetimeHour-picker form-control" type="text" id="${baseColumn.javaProperty}" name="${baseColumn.javaProperty}"/>
                                <#else>
                                 <sf:input cssClass="form-control" path="${baseColumn.javaProperty}" id="${baseColumn.javaProperty}"/>
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
    </div>
</layout:override>

<c:import url="/Views/Shared/Layout.jsp">
    <c:param name="title" value="添加"/>
</c:import>