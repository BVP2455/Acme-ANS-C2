<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form> 
	<acme:input-select code="technician.maintenance-task.form.label.task" path="task" readonly="${_command != 'create'}" choices="${tasks}"/>
	<jstl:if test="${_command == 'show'}">	
		<acme:input-textbox code="technician.maintenance-task.form.label.priority" path="priority" readonly="true" />
		<acme:input-textbox code="technician.maintenance-task.form.label.type" path="type" readonly="true"/>
		<acme:input-textbox code="technician.maintenance-task.form.label.technician" path="technician" readonly="true"/>
		<acme:input-textbox code="technician.maintenance-task.form.label.maintenance-record" path="maintenanceRecord" readonly="true"/>
		<acme:input-textbox code="technician.maintenance-task.form.label.aircraft" path="aircraft" readonly="true"/>
	</jstl:if>
	<jstl:choose>	 
		<jstl:when test="${acme:anyOf(_command, 'show|delete') && draftMode}">
			<acme:submit code="technician.maintenance-task.form.button.delete" action="/technician/maintenance-task/delete?id=${id}"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="technician.maintenance-task.form.button.create" action="/technician/maintenance-task/create?mrId=${mrId}"/>
		</jstl:when>		
	</jstl:choose>
	<jstl:if test="${_command != 'create'}">
			<acme:button code="technician.maintenance-task.form.button.view-task" action="/technician/task/show?id=${taskId}"/>
	</jstl:if>
</acme:form>