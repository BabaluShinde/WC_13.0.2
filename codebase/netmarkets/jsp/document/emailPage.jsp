<%@ taglib prefix="fmt" uri="http://www.ptc.com/windchill/taglib/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@ taglib prefix="docmgnt" uri="http://www.ptc.com/windchill/taglib/docmgnt" %>
<%@ page import="com.ptc.windchill.enterprise.doc.documentResource"%>
<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource" />
<fmt:message var="emailDocTitle" key="EMAIL_DOCUMENT_TITLE" />

<c:set var="actionName" value="<%=HTMLEncoder.encodeForHTMLAttribute(commandBean.getRequest().getParameter(\"actionName\"))%>" scope="request"/> 

<jca:wizard buttonList="DefaultWizardButtonsNoApply" title="${emailDocTitle}" helpSelectorKey="TeamEmailTeamMember">
    <jca:wizardStep action="emailPage_step" type="document" />
</jca:wizard>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
