<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
		<acme:input-select code="technician.maintenance-record.form.label.aircraft" path="aircraft" choices="${aircrafts}" />
		<acme:input-moment code="technician.maintenance-record.form.label.maintenance-moment" path="maintenanceMoment" readonly="true" />
<%-- 		<jstl:if test="${_command != 'create'}"> --%>
<%-- 			<acme:input-select path="status" code="technician.maintenance-record.form.label.status" choices="${statuses}" /> --%>
<%-- 		</jstl:if> --%>
		<acme:input-select path="status" code="technician.maintenance-record.form.label.status" choices="${statuses}" />
		<acme:input-moment code="technician.maintenance-record.form.label.next-inspection-due" path="nextInspectionDue" placeholder="technician.maintenance-record.form.placeholder.next-inspection-due"/>
		<acme:input-money code="technician.maintenance-record.form.label.estimated-cost" path="estimatedCost" placeholder="technician.maintenance-record.form.placeholder.estimated-cost" />
		<acme:input-textarea code="technician.maintenance-record.form.label.notes" path="notes" />
		

		<jstl:if test="${acme:anyOf(_command, 'show|update|publish|delete') && draftMode != false}">
			<acme:submit code="technician.maintenance-record.form.button.update" action="/technician/maintenance-record/update" />
			<acme:submit code="technician.maintenance-record.form.button.delete" action="/technician/maintenance-record/delete" />
			<acme:input-checkbox code="technician.maintenance-record.form.button.confirmation" path="confirmation" />
		</jstl:if>
		<jstl:if test="${_command != 'create'}">
			<acme:button code="technician.maintenance-record.form.button.maintenance-tasks" action="/technician/maintenance-task/list?mrId=${id}"  />
		</jstl:if>
		<jstl:if test="${_command == 'create'}">
			<acme:submit code="technician.maintenance-record.form.button.create" action="/technician/maintenance-record/create" />
			<acme:input-checkbox code="technician.maintenance-record.form.button.confirmation" path="confirmation" />
		</jstl:if>
		<jstl:if test="${canBePublished && draftMode}">
			<acme:submit code="technician.maintenance-record.form.button.publish" action="/technician/maintenance-record/publish" />			
		</jstl:if>
		<jstl:if test="${ ! canBePublished  && _command != 'create' }">
			<jstl:if test="${ ! draftMode}">
				<acme:print code="technician.maintenance-record.form.text.already-published"  />
			</jstl:if>
			<jstl:if test="${ draftMode}">
				<acme:print code="technician.maintenance-record.form.text.can-not-be-published"  />
			</jstl:if>
		</jstl:if>
		
		
	
	
	
	
	
	
	
	
</acme:form>