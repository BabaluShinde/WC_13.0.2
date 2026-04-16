 <%@ page errorPage="/netmarkets/jsp/util/error.jsp"%>
 
 <%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
 <%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
 <%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
 
 <fmt:setLocale value="${localeBean.locale}"/>
 <fmt:setBundle basename="com.ptc.windchill.enterprise.attachments.attachmentsResource" />
 <fmt:message var="title" key="ACK_CONFRIM_WIZARD_TITLE" />
 
 <jca:wizard buttonList="AckConfrimWizardButtons" title="${title}" > 
     <jca:wizardStep action="acknowledgeConfirm_step" type="attachments" />
 </jca:wizard>
 
 
<%@ include file="/netmarkets/jsp/util/end.jspf" %>
