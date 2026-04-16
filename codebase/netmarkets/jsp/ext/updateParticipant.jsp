<%@ page import="wt.workflow.work.WorkItem" %>
<%@ page import="wt.workflow.work.WfAssignedActivity" %>
<%@ page import="wt.workflow.engine.ProcessData" %>
<%@ page import="wt.workflow.engine.WfExecutionObject" %>
<%@ page import="wt.fc.PersistenceHelper" %>
<%@ page import="java.lang.reflect.Field" %>

<%
    String roleId = request.getParameter("roleId");
    String user   = request.getParameter("user");
    String oidStr = request.getParameter("oid");

    try {
        if (oidStr == null || oidStr.trim().isEmpty()) {
            throw new Exception("Missing OID parameter!");
        }

        wt.fc.ReferenceFactory rf = new wt.fc.ReferenceFactory();
        wt.fc.Persistable obj = rf.getReference(oidStr).getObject();

        if (!(obj instanceof WorkItem)) {
            out.print("ERROR: Invalid WorkItem reference");
            return;
        }

        WorkItem workItem           = (WorkItem) obj;
        WfAssignedActivity activity = (WfAssignedActivity) workItem.getSource().getObject();
        ProcessData actData         = ((WfExecutionObject) activity).getContext();

        // Access taskComments field
        Field taskCommentsField = ProcessData.class.getDeclaredField("taskComments");
        taskCommentsField.setAccessible(true);

        String existing = (String) taskCommentsField.get(actData);
        if (existing == null) existing = "";

        // Parse existing entries
        java.util.LinkedHashMap<String,String> roleMap = new java.util.LinkedHashMap<>();
        if (!existing.isEmpty()) {
            for (String entry : existing.split("\\|")) {
                int eq = entry.indexOf('=');
                if (eq > 0) {
                    roleMap.put(entry.substring(0, eq).trim(), entry.substring(eq + 1).trim());
                }
            }
        }

        // Update role mapping
        roleMap.put(roleId, user);

        // Serialize back
        StringBuilder sb = new StringBuilder();
        for (java.util.Map.Entry<String,String> e : roleMap.entrySet()) {
            if (sb.length() > 0) sb.append("|");
            sb.append(e.getKey()).append("=").append(e.getValue());
        }

        taskCommentsField.set(actData, sb.toString());
        PersistenceHelper.manager.save(activity);

        out.print("SUCCESS");

    } catch (Exception e) {
        out.print("ERROR: " + e.getMessage());
    }
%>