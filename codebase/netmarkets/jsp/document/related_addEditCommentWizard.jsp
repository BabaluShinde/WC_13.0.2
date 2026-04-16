<%@ include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/docmgnt" prefix="docmgnt"%>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>
<%@ page import="com.ptc.windchill.enterprise.doc.documentResource" %>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource" />

<fmt:message var="addComment"    key="ADD_COMMENT_TEXTAREA_HEADER" />
<fmt:message var="editComment"   key="EDIT_COMMENT_TEXTAREA_HEADER" />

<c:choose>
	<c:when test="${docmgnt:getLengthAddEditComment(commandBean) == 0}">
		<c:set var="title" value="${addComment}" scope="page"/>
	</c:when>
	<c:otherwise>
		<c:set var="title" value="${editComment}" scope="page"/>	
	</c:otherwise>
</c:choose>
	
<jca:wizard buttonList="addEditCommentWizardButtons" title="${title}" >		
    <jca:wizardStep action="related_addEditCommentWizardStep" type="document"/> 
</jca:wizard>


<%@ include file="/netmarkets/jsp/util/end.jspf"%>