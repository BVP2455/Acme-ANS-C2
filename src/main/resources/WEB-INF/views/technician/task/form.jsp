<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
		<acme:input-select path="type" code="technician.task.form.label.type" choices="${types}" />
		<acme:input-textarea code="technician.task.form.label.description" path="description" />
		<acme:input-integer code="technician.task.form.label.priority" path="priority" placeholder="technician.task.form.placeholder.priority"/>
		<acme:input-integer code="technician.task.form.label.estimated-duration" path="estimatedDuration" placeholder="technician.task.form.placeholder.estimated-duration"/>
		


		<jstl:if test="${acme:anyOf(_command, 'show|update|publish|delete') && draftMode != false}">
			<acme:submit code="technician.task.form.button.update" action="/technician/task/update" />
			<acme:submit code="technician.task.form.button.delete" action="/technician/task/delete" />
			<acme:submit code="technician.task.form.button.publish" action="/technician/task/publish" />
			<acme:input-checkbox code="technician.task.form.button.confirmation" path="confirmation" />
		</jstl:if>
		<jstl:if test="${_command == 'create'}">
			<acme:submit code="technician.task.form.button.create" action="/technician/task/create" />
			<acme:input-checkbox code="technician.task.form.button.confirmation" path="confirmation" />
		</jstl:if>
		
		<jstl:if test="${! draftMode && _command != 'create' }">
			<acme:print code="technician.task.form.text.already-published"  />	
		</jstl:if>
		
		
	
	
	
	
	
	
	
	
</acme:form>