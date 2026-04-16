<script language="javascript">
var fieldPrefix = "";

function sendToParent() {
   var parent = window.opener;
   var fields = attachmentFields();
   for (var i = 0; i < fields.length; i++) {
       var parentField = parent.document.getElementById( fields[i].name );
       if ( parentField != null ) {
          parentField.value = fields[i].value;
       } else {
          addFieldToParent( fields[i].name, fields[i].value );
       }
   }
   window.close();
}

function attachmentFields()
{
    fields = new Array();
    for (var i = 0; i < document.forms[0].elements.length; i++) {
        if (document.forms[0].elements[i].name.indexOf(fieldPrefix) > 0 ) {
            fields[fields.length] = document.forms[0].elements[i];
        }
    }
    return fields;
}

function addFieldToParent( fieldname, value )
{
   var parent = window.opener;
   var lastRow = parent.document.getElementById('primary_attributes');
   if (lastRow) {
      var field = parent.document.createElement('INPUT');
      field.type = 'hidden';
      field.name = fieldname;
      field.id = fieldname;
      field.value = value;
      field.size = 25;
      field.className = 'fixed';
      lastRow.parentNode.insertBefore(field, lastRow.nextSibling);
   }
}

function retrieveParentValues( retrievalPrefix ) {
   fieldPrefix = retrievalPrefix;
   var parent = window.opener;
   var fields = attachmentFields();
   for (var i = 0; i < fields.length; i++) {
       var parentField = parent.document.getElementById( fields[i].name );
       if ( parentField != null ) {
          fields[i].value = parentField.value;
       } else {
          // alert("Must not have a parent field with ID of " + fields[i].name );
       }
   }
}

</script>

<%@ taglib uri="http://www.ptc.com/windchill/taglib/attachments" prefix="attachments" %>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<%@ include file="/netmarkets/jsp/components/createEditUIText.jspf"%>

<%-->Build a table descriptor and assign it to page variable td<--%>
<%--
< jca:describeAttributesTable var="attributesTableDescriptor" id="create.setAttributes" mode="CREATE"
              componentType="WIZARD_ATTRIBUTES_TABLE"
              type="wt.content.ContentItem" label="${attributesTableHeader}">
  < jca:describeProperty id="comments"/>
  < jca:describeProperty id="description"/>
  < jca:describeProperty id="distributable"/>
</jca:describeAttributesTable>

<%@ include file="/netmarkets/jsp/components/setAttributesWizStep.jspf"%>
--%>

<attachments:primaryAttributes />

<%@ include file="/netmarkets/jsp/util/end.jspf"%>
