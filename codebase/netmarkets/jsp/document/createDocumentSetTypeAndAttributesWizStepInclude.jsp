<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/picker" prefix="p"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/partRelDoc" prefix="partRelDoc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/configlinkui" prefix="clui"%>
<%@ taglib prefix="w" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ taglib prefix="docmgnt" uri="http://www.ptc.com/windchill/taglib/docmgnt" %>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<%@ page import="com.ptc.windchill.enterprise.workSet.util.WorkSetManagementHelper" %>


<c:set var="pickerParamFilterTypes" value="<%=request.getAttribute(\"pickerParamFilterTypes\")%>" />
<c:choose>

  <c:when test='${param.invokedfrom == "docsb"}' >
    <!-- If New Document wizard is invoked from Edit Structure, association constraints
         need to be enforced. (Please see the Javadoc for DefaultAssociationConstraintIT
         for more details). The list of creatable types needs to be filtered out to
         inlcude only the types allowed by association constrains. This is achieved by
         finding the list of valid (allowable) types using the tag class
         getValidRoleBTypesForSelectedDocument below and then setting the type picker's
         'type' parameter to 'ROOT_TYPES'-->
      <docmgnt:getValidRoleBTypesForSelectedDocument var="roleBDocTypes" />
      <jca:configureTypePicker>
          <c:forEach var="item" items="${roleBDocTypes}">
              <p:pickerParam name="seedType" value="${item}"/>
          </c:forEach>
          <c:choose>
              <c:when test='${not empty pickerParamFilterTypes}'>
                  <c:forEach var="item" items="${pickerParamFilterTypes}">
                      <p:pickerParam name="filterType" value="${item}"/>
                  </c:forEach>
              </c:when>
              <c:otherwise>
                  <p:pickerParam name="filterType" value="wt.federation.ProxyDocument"/>
                  <p:pickerParam name="filterType" value="wt.doc.WTDocument|com.ptc.InterferenceDetectionDefinition"/>
              </c:otherwise>
          </c:choose>
          <p:pickerParam name="type" value="ROOT_TYPES"/>
      </jca:configureTypePicker>
   </c:when>

  <c:when test='${param.invokedfrom == "config_link_table"}' >
        <%-->   The action to create a document is from a configurable link table,
        so the types need to be selected based on the association constraints
        on the configurable link. <--%>
      <clui:getRoleBTypesForNewAction var="roleBObjectTypes" roleBBaseType="wt.doc.WTDocument"/>

       <jca:configureTypePicker>
           <c:forEach var="item" items="${roleBObjectTypes}">
             <p:pickerParam name="seedType" value="${item}"/>
           </c:forEach>
    </jca:configureTypePicker>
   </c:when>

   <c:when test='${param.invokedfrom == "workSet"}' >
        <%-->   The action to create a document is from a work set reference document table,
        so the types need to be selected based on the association constraints
        on the configurable link. <--%>
        <c:set var="validTypesForWorkSet" value="<%=WorkSetManagementHelper.getValidReferenceDocumentList(commandBean)%>" />
       <jca:configureTypePicker>
           <c:forEach var="item" items="${validTypesForWorkSet}">
             <p:pickerParam name="seedType" value="${item}"/>
           </c:forEach>
      </jca:configureTypePicker>
   </c:when>

   <c:when test="${param.noRefDoc == null}">
    <%-->   The action to create a document is not from a Part Details or Part Instance
        details page, 3rd level nav References Document table or the wcPDMMethod
        is true (any type of document can be created from the References Document
        table) don't filter out Reference Documents and its sub types. <--%>

    <c:choose>
        <c:when test='${not empty pickerParamFilterTypes}'>
            <jca:configureTypePicker>
                <c:forEach var="item" items="${pickerParamFilterTypes}">
                    <p:pickerParam name="filterType" value="${item}"/>
                </c:forEach>
                <c:choose>
                  <%--> Set the seedType if its available. This is set for Reference Documents for Parts  <--%>
                  <c:when test="${param.typePickerSeedObj != null}">
                      <p:pickerParam name="seedType" value="${param.typePickerSeedObj}"/>
                  </c:when>
                </c:choose>
            </jca:configureTypePicker>
        </c:when>
        <c:otherwise>
            <jca:configureTypePicker>
                <p:pickerParam name="filterType"  value="wt.doc.WTDocument|com.ptc.InterferenceDetectionDefinition"/>
                <p:pickerParam name="filterType" value="wt.federation.ProxyDocument"/>
                <c:choose>
                  <%--> Set the seedType if its available. This is set for Reference Documents for Parts  <--%>
                  <c:when test="${validRefTypeRoleBObjectTypeList !=null &&  validRefTypeRoleBObjectTypeList.size()>0}">
					     <c:forEach var="item" items="${validRefTypeRoleBObjectTypeList}">
                           <p:pickerParam name="seedType" value="${item}"/>
                         </c:forEach>
				  </c:when>

				 <c:otherwise>
				    <c:choose>
					   <c:when test="${param.typePickerSeedObj != null}">
                         <p:pickerParam name="seedType" value="${param.typePickerSeedObj}"/>
                       </c:when>
				    </c:choose>
				 </c:otherwise>
                </c:choose>
            </jca:configureTypePicker>
        </c:otherwise>
    </c:choose>

   </c:when>

   <c:otherwise>
    <%-->   Filter out Reference Documents and their sub types from the Part Details
        page 3rd level nav References Documents table document create action<--%>

    <c:choose>
        <c:when test='${not empty pickerParamFilterTypes}'>
            <jca:configureTypePicker>
                    <c:forEach var="item" items="${pickerParamFilterTypes}">
                    <p:pickerParam name="filterType" value="${item}"/>
                </c:forEach>
            </jca:configureTypePicker>
        </c:when>
        <c:otherwise>
          <jca:configureTypePicker>
		  <c:if test='${partRelDoc:getWcPDMMethodPref()=="false"}'>
			<p:pickerParam name="filterType"
                value="wt.doc.WTDocument|com.ptc.ReferenceDocument"/>
		  </c:if>
            <p:pickerParam name="filterType"
            value="wt.doc.WTDocument|com.ptc.InterferenceDetectionDefinition"/>
            <p:pickerParam name="filterType" value="wt.federation.ProxyDocument"/>
            <c:choose>
	          <c:when test='${validDesTypeRoleBObjectTypeList !=null && validDesTypeRoleBObjectTypeList.size() >0}'>
	            <c:forEach var="item" items="${validDesTypeRoleBObjectTypeList}">
	               <p:pickerParam name="seedType" value="${item}"/>
	            </c:forEach>
	          </c:when>
            </c:choose>
          </jca:configureTypePicker>

        </c:otherwise>
    </c:choose>

    </c:otherwise>
</c:choose>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>