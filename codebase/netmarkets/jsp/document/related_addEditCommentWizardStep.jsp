<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/docmgnt" prefix="docmgnt"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/wrappers" prefix="w"%>

<%@ page import="com.ptc.windchill.enterprise.doc.documentResource" %>
<%@ page import="com.ptc.windchill.enterprise.doc.commands.RelatedObjectsCommand"%>
 
<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource" />

<fmt:message var="commentLabel"   key="COMMENT_TEXTAREA_HEADER" />

<table border="0">
	<font class="wizardlabel">
	<tr>
		<td align="right" valign="top"> 
			<w:label value="${commentLabel} : "/>
		</td>
		<td align="left" valign="top"> 
			<w:textArea name="newComment" value="${docmgnt:getAddEditComment(commandBean)}" 
			   cols="30" rows="10" maxLength="${docmgnt:getCommentUpperLimit()}" />
		</td>
	</tr> 
	</font>    
</table>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
