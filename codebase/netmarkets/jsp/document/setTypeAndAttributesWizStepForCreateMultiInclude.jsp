<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/jcaMvc" prefix="mvc"%>
<%@ taglib prefix="wctags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.ptc.com/windchill/taglib/picker" prefix="p"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/partRelDoc" prefix="partRelDoc"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/configlinkui" prefix="clui"%>
<%@ taglib prefix="w" uri="http://www.ptc.com/windchill/taglib/wrappers"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>
<%@ include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<%@ include file="/netmarkets/jsp/components/defineItemReadOnlyPropertyPanel.jspf"%>

<c:if test='${isDTI}'>
<c:set var="pickerParamFilterTypes" value="<%=request.getAttribute(\"pickerParamFilterTypes\")%>" />
    <c:choose>
    <c:when test="${param.noRefDoc == null || partRelDoc:getWcPDMMethodPref()}">
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
            </jca:configureTypePicker>
        </c:when>
        <c:otherwise>
        <%-- configureTypePicker will be configured for the first time when create multiple document wizard opens in DTI and not when we are selecting cascading attributes. --%>
        <c:if test='${param.mda==null}'>
            <jca:configureTypePicker>       
                <p:pickerParam name="filterType"
                    value="wt.doc.WTDocument|com.ptc.InterferenceDetectionDefinition"/>
            </jca:configureTypePicker>
        </c:if>
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
            <p:pickerParam name="filterType"
                value="wt.doc.WTDocument|com.ptc.ReferenceDocument"/>
            <p:pickerParam name="filterType"
            value="wt.doc.WTDocument|com.ptc.InterferenceDetectionDefinition"/>
            </jca:configureTypePicker>
        </c:otherwise>
    </c:choose>
    </c:otherwise>
</c:choose>
</c:if>
<c:choose>
    <c:when test='${isDnD}'> 
        <%@ include file="/netmarkets/jsp/components/setAttributesReadOnlyPropertyPanel.jspf"%>
        <%@ include file="/netmarkets/jsp/components/getSetAttributesWizStepModels.jspf"%>
        <c:if test="${requestScope.attributesStepReadOnlyPanel != null}">
            <jca:renderPropertyPanel model="${attributesStepReadOnlyPanelModel}"/>
        </c:if>
    </c:when>
    <c:otherwise>
        <%@ include file="/netmarkets/jsp/components/defineItem.jspf"%>
        <br/>
        <br/>
    </c:otherwise>
</c:choose>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>