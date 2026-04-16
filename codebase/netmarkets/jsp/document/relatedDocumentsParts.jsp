<% request.setAttribute(NmAction.SHOW_CONTEXT_INFO, "false"); %>

<%@ page import="wt.doc.WTDocument,
                 com.ptc.netmarkets.util.beans.NmCommandBean,
				 wt.part.PartDocHelper,
				 com.ptc.windchill.enterprise.util.PartManagementHelper
"%>

<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>

<%@ page import="com.ptc.windchill.enterprise.part.partResource" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt" %>

<fmt:setLocale value="${localeBean.locale}"/>

<fmt:message var="describesPartTableHeader"  key="DESCRIBES_PARTS_TABLE_HEADER"     bundle="com.ptc.windchill.enterprise.part.partResource" />
<fmt:message var="referencesPartTableHeader" key="REFERENCED_BY_PARTS_TABLE_HEADER" bundle="com.ptc.windchill.enterprise.part.partResource" />

<%-->*****************************************************************************************************<--%>
<%//

    WTDocument document = (WTDocument) commandBean.getPrimaryOid().getWtRef().getObject();

    boolean isPDMMethod = PartManagementHelper.getWcPDMMethodPref();
    boolean isRefDoc = PartDocHelper.isReferenceDocument(document);

	String objectType = "wt.part.WTPart";

	if (isPDMMethod) {

		String tableLabelRef  = (String) pageContext.findAttribute("referencesPartTableHeader");
		String tableLabelDesc = (String) pageContext.findAttribute("describesPartTableHeader");
		String methodDesc = "getAssociatedDescParts";
		String methodRef  = "getAssociatedRefParts";
		String tableIdDesc = "part.relatedDocumentsDescribesParts.list";
		String tableIdRef  = "part.relatedDocumentsReferencesParts.list";
		String helpRef = "part_reference_by";
		String helpDesc = "part_describe_ref";

    	request.setAttribute("tableLabelRef",tableLabelRef);
    	request.setAttribute("tableLabelDesc",tableLabelDesc);
    	request.setAttribute("methodDesc",methodDesc);
    	request.setAttribute("methodRef",methodRef);
    	request.setAttribute("tableIdDesc",tableIdDesc);
    	request.setAttribute("tableIdRef",tableIdRef);
    	request.setAttribute("helpDesc",helpDesc);
    	request.setAttribute("helpRef",helpRef);
	}
	else {

		String tableId = "";
		String tableLabel = "";
		String toolBar = "";
		String method = "";
		String help = "";

    	if (isRefDoc) {
			tableLabel  = (String) pageContext.findAttribute("referencesPartTableHeader");
    		method = "getAssociatedRefParts";
    		tableId = "part.relatedDocumentsReferencesParts.list";
    		help = "part_reference_by";
    		toolBar = "relatedDocPartReferencesToolBar";
		}
		else {
			tableLabel = (String) pageContext.findAttribute("describesPartTableHeader");
    		method = "getAssociatedDescParts";
    		tableId = "part.relatedDocumentsDescribesParts.list";
    		help = "part_describe_ref";
    		toolBar = "relatedDocPartDescribesToolBar";
		}
    	request.setAttribute("tableLabel",tableLabel);
    	request.setAttribute("tableId",tableId);
    	request.setAttribute("toolBar",toolBar);
    	request.setAttribute("method",method);
    	request.setAttribute("help",help);

    }
    request.setAttribute("objectType",objectType);
%>
<%-->Build a table descriptor and assign it to page variable td<--%>
<%
  	if (isPDMMethod) {
  	// WC PCM method has 2 tables, References and Described By
%>

<jca:describeTable var="describes" id="${tableIdDesc}" type="${objectType}"
                   label="${tableLabelDesc}" configurable="true">
  <jca:setComponentProperty key="actionModel" value="relatedDocPartDescribesToolBar"/>
  <jca:describeColumn id="type_icon"/>
  <jca:describeColumn id="number"/>
  <jca:describeColumn id="version">
      <jca:setComponentProperty key="display" value="VERSIONSANSVIEW"/>
  </jca:describeColumn>
  <jca:describeColumn id="infoPageAction" />
  <jca:describeColumn id="name"/>
  <jca:describeColumn id="containerName"/>
  <jca:describeColumn id="state"/>
  <jca:describeColumn id="lastModified"/>
</jca:describeTable>

<c:set target="${describes.properties}" property="selectable" value="true"/>

<%-->Get a component model for our table<--%>
<jca:getModel var="tableModelDesc" descriptor="${describes}"
              serviceName="com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand"
              methodName="${methodDesc}">
<jca:addServiceArgument type="wt.doc.WTDocument" value="${param.oid}"/>
</jca:getModel>

<%-->Get the NmHTMLTable from the command<--%>
<jca:renderTable model="${tableModelDesc}"  pageLimit="0" showCount="true" helpContext="${helpDesc}"/>

<jca:describeTable var="references" id="${tableIdRef}" type="${objectType}"
                   label="${tableLabelRef}" configurable="true">
  <jca:setComponentProperty key="actionModel" value="relatedDocPartReferencesToolBar"/>
  <jca:describeColumn id="type_icon"/>
  <jca:describeColumn id="number"/>
  <jca:describeColumn id="version">
      <jca:setComponentProperty key="display" value="VERSIONSANSVIEW"/>
  </jca:describeColumn>
  <jca:describeColumn id="infoPageAction" />
  <jca:describeColumn id="name"/>
  <jca:describeColumn id="containerName"/>
  <jca:describeColumn id="state"/>
  <jca:describeColumn id="lastModified"/>
</jca:describeTable>

<c:set target="${references.properties}" property="selectable" value="true"/>

<%-->Get a component model for our table<--%>
<jca:getModel var="tableModelRef" descriptor="${references}"
              serviceName="com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand"
              methodName="${methodRef}">
<jca:addServiceArgument type="wt.doc.WTDocument" value="${param.oid}"/>
</jca:getModel>

<%-->Get the NmHTMLTable from the command<--%>
<jca:renderTable model="${tableModelRef}"  pageLimit="0" showCount="true" helpContext="${helpRef}"/>

<%} else {
  	// WC PDMLink method has 1 tables, either References or Described By table
  	// depending on the details page document
    	%>
<%-->*****************************************************************************************************<--%>

<jca:describeTable var="referencesDescribes" id="${tableId}" type="${objectType}"
                   label="${tableLabel}" configurable="true">
  <jca:setComponentProperty key="actionModel" value="${toolBar}"/>
  <jca:describeColumn id="type_icon"/>
  <jca:describeColumn id="number"/>
  <jca:describeColumn id="version">
      <jca:setComponentProperty key="display" value="VERSIONSANSVIEW"/>
  </jca:describeColumn>
  <jca:describeColumn id="infoPageAction" />
  <jca:describeColumn id="name"/>
  <jca:describeColumn id="containerName"/>
  <jca:describeColumn id="state"/>
  <jca:describeColumn id="lastModified"/>
</jca:describeTable>

<c:set target="${referencesDescribes.properties}" property="selectable" value="true"/>

<%-->Get a component model for our table<--%>
<jca:getModel var="tableModelRef" descriptor="${referencesDescribes}"
              serviceName="com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand"
              methodName="${method}">
<jca:addServiceArgument type="wt.doc.WTDocument" value="${param.oid}"/>
</jca:getModel>

<%-->Get the NmHTMLTable from the command<--%>
<jca:renderTable model="${tableModelRef}"  pageLimit="0" showCount="true" helpContext="${help}"/>

<%-->*****************************************************************************************************<--%>

<%} %>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>