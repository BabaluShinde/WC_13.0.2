package ext.splm.attachments.dataUtilities;

import java.util.*;
import java.lang.reflect.Method;
import java.util.Locale;

import wt.change2.ChangeHelper2;
import wt.change2.Changeable2;
import wt.change2.WTChangeActivity2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.part.WTPart;
import wt.type.Typed;
import wt.type.TypedUtilityService;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfState;

import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.windchill.enterprise.attachments.dataUtilities.ContentDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;
import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.TextBox;

public class ECNContentTypeDataUtility extends ContentDataUtility {

    @Override
    public Object getDataValue(String component_id, Object obj, ModelContext modelContext) throws WTException {

        List<String> internalValues = new ArrayList<>();
        List<String> displayValues = new ArrayList<>();
        List<String> defaultValues = new ArrayList<>();

        GUIComponentArray array = new GUIComponentArray();

        if (obj == null || component_id == null || modelContext == null) {
            return super.getDataValue(component_id, obj, modelContext);
        }

        if ("contentToolName".equalsIgnoreCase(component_id)
                && (ComponentMode.CREATE.equals(modelContext.getDescriptorMode())
                    || ComponentMode.EDIT.equals(modelContext.getDescriptorMode()))) {

            NmCommandBean nmCommandBean = modelContext.getNmCommandBean();
            Map<String, String[]> paramMap = nmCommandBean.getRequest().getParameterMap();

            // Fetch affected objects (same logic for both create/edit)
            Map<String, ArrayList<String>> affectedMap = getAffectedObjects(paramMap, modelContext);

            internalValues.addAll(affectedMap.get("internalValues"));
            displayValues.addAll(affectedMap.get("displayValues"));

            internalValues.add("OTHER");
            displayValues.add("OTHER");
            defaultValues.add("OTHER");

            // Create combo box
            ComboBox cBox = new ComboBox(new ArrayList<>(internalValues), new ArrayList<>(displayValues),
                    (defaultValues != null ? new ArrayList<>(defaultValues) : null));
            cBox.setName("toolName_component");
            cBox.setId(component_id);
            cBox.setMultiSelect(false);
            cBox.setRequired(true);
            cBox.setComponentHidden(false);
            cBox.addJsAction("onclick", "return copyValueToOOBTTextBox(this);");

            // Combine with original text box
            Object ootbComponent = super.getDataValue(component_id, obj, modelContext);
            if (ootbComponent instanceof TextBox) {
                TextBox ootbTxtBox = (TextBox) ootbComponent;
                if (ootbTxtBox.getValue() != null && !ootbTxtBox.getValue().isEmpty()) {
                    cBox.setSelected(ootbTxtBox.getValue());
                } else {
                    cBox.setSelected(defaultValues.get(0));
                    ootbTxtBox.setValue(defaultValues.get(0));
                }

                ootbTxtBox.setHidden(true);
                ootbTxtBox.setValueHidden(true);
                ootbTxtBox.setStyle("display: none;");
            }

            array.setNoWrap(true);
            array.addGUIComponent((TextBox) ootbComponent);
            array.addGUIComponent(cBox);
            return array;
        }
        
        else if ("contentToolName".equalsIgnoreCase(component_id)
                && ComponentMode.VIEW.equals(modelContext.getDescriptorMode())) {

            NmCommandBean nmCommandBean = modelContext.getNmCommandBean();
            Map<String, String[]> paramMap = nmCommandBean != null && nmCommandBean.getRequest() != null
                    ? nmCommandBean.getRequest().getParameterMap()
                    : new HashMap<>();

            Map<String, ArrayList<String>> affectedMap = getAffectedObjects(paramMap, modelContext);

            internalValues.addAll(affectedMap.get("internalValues"));
            displayValues.addAll(affectedMap.get("displayValues"));

            // Read-only ComboBox for view mode
            ComboBox viewCBox = new ComboBox(new ArrayList<>(internalValues), new ArrayList<>(displayValues),
                    null);
            viewCBox.setId(component_id + "_view");
            viewCBox.setName("toolName_component_view");
            viewCBox.setMultiSelect(false);
            viewCBox.setRequired(false);
            viewCBox.setReadOnly(true);
            viewCBox.setComponentHidden(false);

            Object ootbComponent = super.getDataValue(component_id, obj, modelContext);
            if (ootbComponent instanceof TextBox) {
                TextBox ootbTxtBox = (TextBox) ootbComponent;
                if (ootbTxtBox.getValue() != null && !ootbTxtBox.getValue().isEmpty()) {
                    viewCBox.setSelected(ootbTxtBox.getValue());
                } else if (!displayValues.isEmpty()) {
                    viewCBox.setSelected(displayValues.get(0));
                }
                ootbTxtBox.setHidden(true);
                ootbTxtBox.setValueHidden(true);
                ootbTxtBox.setStyle("display: none;");
            }

            array.setNoWrap(true);
            array.addGUIComponent((TextBox) ootbComponent);
            array.addGUIComponent(viewCBox);
            return array;
        }
        
        return super.getDataValue(component_id, obj, modelContext);
    }

    /**
     * Unified method to fetch affected objects for both create and edit wizards.
     * 
     */
    
    private Map<String, ArrayList<String>> getAffectedObjects(Map<String, String[]> paramMap,
                                                             ModelContext modelContext) throws WTException {

        ArrayList<String> internalValues = new ArrayList<>();
        ArrayList<String> displayValues = new ArrayList<>();
        ReferenceFactory rf = new ReferenceFactory();

        // -------------------------------
        // Handle request parameters (initialRows + addRows)
        // -------------------------------
        String[] initialRows = paramMap.get("initialRows_issues_affectedData_table");
        String[] addRows = paramMap.get("addRows_issues_affectedData_table");

        if (initialRows != null) {
            for (String row : initialRows) {
                for (String oidStr : row.split("#")) {
                    resolveAffectedObject(oidStr, rf, internalValues, displayValues);
                }
            }
        }

        if (addRows != null) {
            for (String row : addRows) {
                for (String oidStr : row.split("#")) {
                    resolveAffectedObject(oidStr, rf, internalValues, displayValues);
                }
            }
        }

        // -------------------------------
        // Fallback: If still empty, get from ECN context (PBO)
        // -------------------------------
        if (internalValues.isEmpty() && modelContext.getNmCommandBean() != null
                && modelContext.getNmCommandBean().getActionOid() != null) {

            Persistable primaryObj = (Persistable) modelContext.getNmCommandBean().getActionOid().getRefObject();

            if (primaryObj instanceof WTChangeOrder2) {
                WTChangeOrder2 co = (WTChangeOrder2) primaryObj;
                QueryResult affected = ChangeHelper2.service.getChangeablesBefore(co);

                while (affected.hasMoreElements()) {
                    Changeable2 changeable = (Changeable2) affected.nextElement();
                    String objNumber = getObjectNumber(changeable);
                    String objName = getObjectName(changeable);
                    String typeDisplay = getLocalizedTypeName(changeable);

                    displayValues.add(objName);
                    internalValues.add(objNumber);

                    System.out.println("Fallback Affected Obj: " + typeDisplay + " | Number = " + objNumber + " | Name = " + objName);
                }
            }
        }

        Map<String, ArrayList<String>> resultMap = new HashMap<>();
        resultMap.put("internalValues", internalValues);
        resultMap.put("displayValues", displayValues);

        return resultMap;
    }

    /**
     * Shared helper to resolve an OID into internal/display values
     */
    private void resolveAffectedObject(String oidStr, ReferenceFactory rf,
                                       ArrayList<String> internalValues, ArrayList<String> displayValues) {
        try {
            WTReference ref = rf.getReference(oidStr.trim());
            Persistable obj = (Persistable) ref.getObject();
            String objNumber = getObjectNumber(obj);
            String objName = getObjectName(obj);
            String typeDisplay = getLocalizedTypeName(obj);

            internalValues.add(objNumber);
            displayValues.add(objName);

            System.out.println("Resolved Obj: " + typeDisplay + " | Number = " + objNumber + " | Name = " + objName);
        } catch (Exception e) {
            System.out.println("Failed to resolve OID: " + oidStr + " | " + e.getMessage());
        }
    }

    private String getLocalizedTypeName(Object obj) {
        try {
            if (obj instanceof Typed) {
                TypeIdentifier typeId = TypeIdentifierUtilityHelper.service.getTypeIdentifier((Typed) obj);
                TypedUtilityService service = TypedUtilityServiceHelper.service;
                return service.getLocalizedTypeName(typeId, Locale.US);
            }
        } catch (Exception e) {
            // ignore
        }
        return "Unknown Type";
    }

    private String getObjectNumber(Object obj) {
        try {
            if (obj instanceof WTDocument) return ((WTDocument) obj).getNumber();
            if (obj instanceof WTPart) return ((WTPart) obj).getNumber();
            if (obj instanceof EPMDocument) return ((EPMDocument) obj).getNumber();
            if (obj instanceof WTChangeOrder2) return ((WTChangeOrder2) obj).getNumber();
            if (obj instanceof WTChangeActivity2) return ((WTChangeActivity2) obj).getNumber();
            Method m = obj.getClass().getMethod("getNumber");
            Object num = m.invoke(obj);
            return num != null ? num.toString() : "N/A";
        } catch (Exception e) {
            return "N/A";
        }
    }

    private String getObjectName(Object obj) {
        try {
            if (obj instanceof WTDocument) return ((WTDocument) obj).getName();
            if (obj instanceof WTPart) return ((WTPart) obj).getName();
            if (obj instanceof EPMDocument) return ((EPMDocument) obj).getName();
            if (obj instanceof WTChangeOrder2) return ((WTChangeOrder2) obj).getName();
            if (obj instanceof WTChangeActivity2) return ((WTChangeActivity2) obj).getName();
            Method m = obj.getClass().getMethod("getName");
            Object nameObj = m.invoke(obj);
            return nameObj != null ? nameObj.toString() : "N/A";
        } catch (Exception e) {
            return "N/A";
        }
    }
    

   

}
