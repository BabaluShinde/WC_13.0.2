<%@ taglib uri="http://www.ptc.com/windchill/taglib/mvc" prefix="mvc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/changeWizards" prefix="cwiz"%>
<%@include file="/netmarkets/jsp/util/begin.jspf"%>

<cwiz:validateChangeTaskTypes/>
<jsp:include page="${mvc:getComponentURL('changeManagement.implementationPlan')}" />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>