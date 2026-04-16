package ext.amph.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.ptc.core.ui.resources.ComponentType;
import com.ptc.jca.mvc.components.JcaAttributeConfig;
import com.ptc.jca.mvc.components.JcaGroupConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.AttributePanelConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentId;
import com.ptc.mvc.components.ComponentParams;

import wt.project.Role;
import wt.team.Team;
import wt.team.TeamReference;
import wt.util.WTException;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

@ComponentBuilder("amphSetupParticipants")
public class AMPHSetupParticipantsTableBuilder extends AbstractComponentBuilder {

	private static final String DU_ID = "customSetupParticipantsDU";

	private List<String> getProcessRoles(ComponentParams params) {
		List<String> roleNames = new ArrayList<>();
		try {
			Object context = params.getContextObject();
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
					Vector teamRoles = team.getRoles();
					if (teamRoles != null) {
						for (Object obj : teamRoles) {
							if (obj instanceof Role) {
								Role role = (Role) obj;
								roleNames.add(role.toString());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println(">>> Error fetching process roles: " + e.getMessage());
			e.printStackTrace();
		}

		// Fallback if empty to prevent UI from breaking
		if (roleNames.isEmpty()) {
			roleNames.add("Reviewer");
			roleNames.add("Approver");
		}

		return roleNames;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {

		System.out.println(">>> BUILDER CONFIG HIT <<<");

		ComponentConfigFactory factory = getComponentConfigFactory();

		AttributePanelConfig panel = factory.newAttributePanelConfig(ComponentId.ATTRIBUTE_PANEL_ID);

		panel.setComponentType(ComponentType.INFO);

		JcaGroupConfig group = (JcaGroupConfig) factory.newGroupConfig();
		group.setId("ParticipantsGroup");
		group.setLabel("Participants");
		group.setIsGridLayout(false);

		List<String> dynamicRoles = getProcessRoles(params);

		for (String roleId : dynamicRoles) {

			JcaAttributeConfig attr = (JcaAttributeConfig) factory.newAttributeConfig();

			attr.setId(roleId);
			try {
				attr.setLabel(Role.toRole(roleId).getDisplay());
			} catch (Exception e) {
				attr.setLabel(roleId);
			}

			attr.setDataUtilityId(DU_ID);

			group.addComponent(attr);
		}

		panel.addComponent(group);
		panel.setView("/participants/customParticipantsTable.jsp");

		return panel;
	}

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {

		System.out.println(">>> BUILDER DATA HIT <<<");

		Map<String, Object> data = new HashMap<>();

		List<String> dynamicRoles = getProcessRoles(params);

		for (String roleId : dynamicRoles) {

			data.put(roleId, "");
		}

		return data;
	}
}