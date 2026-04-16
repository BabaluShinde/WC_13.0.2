<%@ include file="/netmarkets/jsp/util/context.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ page import="com.ptc.netmarkets.model.NmOid"%>
<%@ page import="wt.inf.library.WTLibrary"%>
<%@ page import="wt.pdmlink.PDMLinkProduct"%>
<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource"/> 
<fmt:message var="pickerTitle" key="ADD_CHILDREN"/> 
	
<%  NmOid coid = NmOid.newNmOid (request.getParameter("rootContainer"));
    boolean isPDMContext = coid.isA(PDMLinkProduct.class) || coid.isA(WTLibrary.class);
%>

<c:choose>    
       <c:when test="<%=isPDMContext%>">
          <wctags:itemMasterPicker id="add_children_doc" multiSelect="true" inline="true"
	          pickerCallback="processPickedItems" pickerTitle="${pickerTitle}"
	          typeComponentId="PDMLink.structSearch"
	          objectType="wt.doc.WTDocument"
	          editable="false" showTypePicker="false"
	          baseWhereClause="(checkoutInfo.state!='wrk')&(latestIteration='1')"
	          customAccessController="com.ptc.windchill.enterprise.search.server.LatestVersionAccessController"
	          searchResultsViewId="addDocumentsSearchResult.table.id"
	          helpSelectorKey="DocMgmtDocStructureAdd" />
       </c:when>  
       <c:otherwise>
          <wctags:itemMasterPicker id="add_children_doc" multiSelect="true" inline="true"
	          pickerCallback="processPickedItems" pickerTitle="${pickerTitle}"
	          objectType="wt.doc.WTDocument" showTypePicker="false"
	          editable="false" containerRef="<%=request.getParameter(\"rootContainer\")%>"
	          componentId="DocStructureAdd"
	          baseWhereClause="(checkoutInfo.state!='sb c/o')&(checkoutInfo.state!='wrk')&(latestIteration='1')"
	          customAccessController="com.ptc.windchill.enterprise.search.server.LatestVersionAccessController"
	          searchResultsViewId="addDocumentsSearchResult.table.id"
	          helpSelectorKey="DocMgmtDocStructureAdd" />  
       </c:otherwise>  
</c:choose>
   
<script type="text/javascript">	

	function removeButtons() {
		var okButton = document.getElementById("PJL_wizard_ok");
		var cancelButton = document.getElementById("PJL_wizard_cancel");
		var td = okButton.parentNode;
		td.removeChild(okButton);
		td = cancelButton.parentNode;
		td.removeChild(cancelButton);
	}

	removeButtons();
</script>


