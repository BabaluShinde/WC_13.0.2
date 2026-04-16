package ext.amph.workflow;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.team.Team;
import wt.team.TeamReference;
import wt.util.WTException;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfExecutionObject;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.ComboBox;

public class AMPHSetupParticipantsDataUtility extends AbstractDataUtility {

	private List<String> getProcessRoles(ModelContext mc) {
		List<String> roleNames = new ArrayList<>();
		try {
			Object context = mc.getNmCommandBean().getActionOid().getWtRef().getObject();
			WfProcess process = null;

			if (context instanceof WorkItem) {
				WorkItem workItem = (WorkItem) context;
				WfAssignedActivity wfaa = (WfAssignedActivity) workItem.getSource().getObject();
				process = wfaa.getParentProcess();
			} else if (context instanceof WfProcess) {
				process = (WfProcess) context;
			}

			if (process != null) {
				TeamReference teamRef = process.getTeamId();
				if (teamRef != null && teamRef.getObject() != null) {
					Team team = (Team) teamRef.getObject();
					Vector<?> teamRoles = team.getRoles();
					if (teamRoles != null) {
						for (Object obj : teamRoles) {
							if (obj instanceof Role) {
								roleNames.add(obj.toString());
							}
						}
					}
				}
			}
		} catch (Exception ignored) {
		}

		if (roleNames.isEmpty()) {
			roleNames.add("Reviewer");
			roleNames.add("Approver");
		}
		return roleNames;
	}

	private String getSavedComboValue(ModelContext mc, String roleId) {
		try {
			Object context = mc.getNmCommandBean().getActionOid().getWtRef().getObject();
			WfAssignedActivity activity = null;

			if (context instanceof WorkItem) {
				WorkItem workItem = (WorkItem) context;
				activity = (WfAssignedActivity) workItem.getSource().getObject();
			}

			if (activity == null) return "";

			activity = (WfAssignedActivity) PersistenceHelper.manager.refresh(activity);
			ProcessData actData = ((WfExecutionObject) activity).getContext();

			Field taskCommentsField = ProcessData.class.getDeclaredField("taskComments");
			taskCommentsField.setAccessible(true);
			String stored = (String) taskCommentsField.get(actData);

			if (stored == null || stored.isEmpty()) return "";

			for (String entry : stored.split("\\|")) {
				int eq = entry.indexOf('=');
				if (eq > 0) {
					String key = entry.substring(0, eq).trim();
					String val = entry.substring(eq + 1).trim();
					if (roleId.equalsIgnoreCase(key)) {
						return val;
					}
				}
			}
		} catch (Exception ignored) {
		}
		return "";
	}

	@Override
	public Object getDataValue(String componentId, Object datum, ModelContext mc) throws WTException {

		List<String> dynamicRoles = getProcessRoles(mc);

		if (dynamicRoles.contains(componentId)) {
			String savedUser = getSavedComboValue(mc, componentId);
			return buildComboBox(componentId, savedUser);
		}
		return componentId;
	}

	private ComboBox buildComboBox(String roleId, String selectedUser) throws WTException {

		List<String> internal = new ArrayList<>();
		List<String> display  = new ArrayList<>();

		try {
			QuerySpec qs = new QuerySpec(WTUser.class);
			QueryResult qr = PersistenceHelper.manager.find(qs);

			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();

				if (obj instanceof Object[]) {
					obj = ((Object[]) obj)[0];
				}

				if (obj instanceof WTUser) {
					WTUser user = (WTUser) obj;
					if (!user.isDisabled()) {
						internal.add(user.getName());
						display.add(user.getFullName() + " (" + user.getName() + ")");
					}
				}
			}
		} catch (Exception ignored) {
		}

		ComboBox combo = new ComboBox();

		internal.add(0, "");
		display.add(0, "-- Select --");

		combo.setInternalValues(new ArrayList<>(internal));
		combo.setValues(new ArrayList<>(display));

		combo.setId(roleId);
		combo.setName(roleId);
		combo.setSelected(selectedUser != null ? selectedUser : "");

		combo.setRequired(true);
		combo.addJsAction("onchange", "return onParticipantChange(this);");

		return combo;
	}
}