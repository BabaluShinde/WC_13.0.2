<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/fmt" prefix="fmt"%>

<%@ page import="com.ptc.core.components.descriptor.DescriptorConstants" %>
<%@ page import="com.ptc.windchill.enterprise.doc.documentResource" %>

<fmt:setLocale value="${localeBean.locale}"/>
<fmt:setBundle basename="com.ptc.windchill.enterprise.doc.documentResource" />
<fmt:message var="attributeTableName" key="<%= documentResource.ATTRIBUTE_TABLE_NAME %>" />
<fmt:message var="locationLabel" key="<%= documentResource.LOCATION %>" />

<jca:describeAttributesTable var="attributesTableDescriptor" mode="VIEW" id="view.setAttribute"
    label="${attributeTableName}" scope="request" type="wt.doc.WTDocument">
    <jca:describeProperty id="name"/>
    <jca:describeProperty id="number"/>
    <jca:describeProperty id="version" />
    <jca:describeProperty id="containerName" />
    <jca:describeProperty id="orgid"/>
    <jca:describeProperty id="docTypeName"/>
    <jca:describeProperty id="lifeCycleState"/>
    <jca:describeProperty id="checkoutInfo" />
    <jca:describeProperty id="compositePath" label="${locationLabel}"/>
    <jca:describeProperty id="title" />
    <jca:describeProperty id="description"/>
    <jca:describeProperty id="lifeCycle.id"/>
    <jca:describeProperty id="teamTemplate.id"/>
    <jca:describeProperty id="ALL_CUSTOM_HARD_ATTRIBUTES_FOR_INPUT_TYPE"/>
    <jca:describeProperty id="ALL_SOFT_NON_CLASSIFICATION_SCHEMA_ATTRIBUTES"/>
    <jca:describeProperty id="creator"/>
    <jca:describeProperty id="thePersistInfo.createStamp"/>
    <jca:describeProperty id="modifier"/>
    <jca:describeProperty id="thePersistInfo.modifyStamp" />
</jca:describeAttributesTable>
