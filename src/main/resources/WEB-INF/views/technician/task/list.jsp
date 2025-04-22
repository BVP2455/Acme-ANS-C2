<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="technician.task.list.label.description" path="description" width="60%"/>
	<acme:list-column code="technician.task.list.label.draft-mode" path="draftMode" width="10%"/>
	<acme:list-column code="technician.task.list.label.type" path="type" width="10%"/>
	<acme:list-column code="technician.task.list.label.priority" path="priority" width="10%"/>
	<acme:list-column code="technician.task.list.label.estimated-duration" path="estimatedDuration" width="10%"/>
	
	<acme:list-payload path="tasks"/>
</acme:list>

<jstl:if test="${_command == 'list'}">
	<acme:button code="technician.task.list.button.create" action="/technician/task/create"/>

</jstl:if>	
