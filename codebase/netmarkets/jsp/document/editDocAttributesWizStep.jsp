<%@ page import="wt.content.ContentHolder" %>
<%@ page import="wt.access.AccessPermission" %>
<%@ page import="com.ptc.windchill.enterprise.attachments.server.AttachmentsHelper" %>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>

<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>

<%-- To display Object type, organization,etc --%>
<%@ include file="/netmarkets/jsp/components/setAttributesReadOnlyPropertyPanel.jspf"%>

<%-->Build a table descriptor and assign it to page variable td
This defines all of the attributes in the attributes step
<--%>
<jca:describeAttributesTable var="attributesTableDescriptor" id="editSetAttributes" mode="EDIT"
    componentType="WIZARD_ATTRIBUTES_TABLE" type="wt.doc.WTDocument" label="${attributesTableHeader}"
    scope="request">
   <jca:describeProperty id="name" mode="VIEW"/>
   <jca:describeProperty id="title"/>
   <jca:describeProperty id="description"/>
   <jca:describeProperty id="ALL_CUSTOM_HARD_ATTRIBUTES_FOR_INPUT_TYPE"/>
   <jca:describeProperty id="ALL_SOFT_NON_CLASSIFICATION_SCHEMA_ATTRIBUTES"/>
</jca:describeAttributesTable>

<%-- renders the read only attributes panel which includes information about Object type, organization,etc --%>
<%@ include file="/netmarkets/jsp/components/getSetAttributesWizStepModels.jspf"%>

<c:if test="${requestScope.attributesStepReadOnlyPanel != null}">
   <jca:renderPropertyPanel model="${attributesStepReadOnlyPanelModel}"/>
   <hr>
</c:if>

<%-- determine if we show the primary attachment component --%>
<%
    boolean bPermit = false;
    Object context = commandBean.getPageOid().getRef();
    bPermit = (context instanceof ContentHolder) && AttachmentsHelper.hasPermission((ContentHolder) context, AccessPermission.MODIFY_CONTENT);
%>

      <c:if test="<%= bPermit %>">
        <wctags:primaryAttachmentWithMSOI />
      </c:if>

<%-- renders attributes table defined above --%>

<c:if test="${requestScope.attributesTableDescriptor != null}">
   <jca:renderTable model="${tableModel}" />
</c:if>

<input type="hidden" name="createType" value="${nmcontext.context.pageType}">

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
