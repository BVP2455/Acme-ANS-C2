<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="technician.maintenance-task.list.label.priority" path="priority"/>
	<acme:list-column code="technician.maintenance-task.list.label.task-draft-mode" path="taskDraftMode"/>
	<acme:list-column code="technician.maintenance-task.list.label.description" path="description"/>
	
</acme:list>	
	
<jstl:if test="${draftMode}">
	<acme:button code="technician.maintenance-task.list.button.create" action="/technician/maintenance-task/create?mrId=${mrId}"/>
</jstl:if>