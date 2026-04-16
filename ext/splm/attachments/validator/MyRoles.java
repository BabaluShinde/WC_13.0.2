package ext.splm.attachments.validator;

import java.beans.PropertyVetoException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.team.RolePrincipalMap;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

public class MyRoles {

    // Example test method to call from WT Console or MethodServer
    public static void testRoles() {
        try {
            WTPrincipal user = SessionHelper.manager.getPrincipal();
            Set<Role> teamRoles = teamRoles(user);
            Set<Role> containerTeamRoles = containerTeamRoles(user);

            System.out.println("Team Roles size: " + teamRoles.size());
            System.out.println("Container Team Roles size: " + containerTeamRoles.size());
            for (Role r : teamRoles) System.out.println("TeamRole: " + r.getValue());
            for (Role r : containerTeamRoles) System.out.println("ContainerTeamRole: " + r.getValue());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Set<Role> teamRoles(WTPrincipal principal) throws WTException, PropertyVetoException {
        Set<Role> roles = new HashSet<>();
        QuerySpec qs = new QuerySpec();
        qs.setDistinct(true);
        int rpmIndex = qs.appendClassList(RolePrincipalMap.class, false);
        qs.appendSelectAttribute(RolePrincipalMap.ROLE, rpmIndex, false);
        qs.appendWhere(
                new SearchCondition(RolePrincipalMap.class, "principalParticipant.key",
                        SearchCondition.EQUAL, PersistenceHelper.getObjectIdentifier(principal)),
                new int[] { rpmIndex });

        QueryResult qr = PersistenceHelper.manager.find(qs);
        while (qr.hasMoreElements()) {
            Object[] nextArray = (Object[]) qr.nextElement();
            if (nextArray != null && nextArray.length == 1) {
                roles.add((Role) nextArray[0]);
            }
        }
        return roles;
    }

    public static Set<Role> containerTeamRoles(WTPrincipal principal) throws WTException {
        Set<Role> set = new HashSet<>();
        QueryResult containerTeams = findContainerTeams();
        while (containerTeams.hasMoreElements()) {
            ContainerTeam team = (ContainerTeam) containerTeams.nextElement();
            Iterator<Role> roles = team.getRoles().iterator();
            while (roles.hasNext()) {
                Role role = roles.next();
                WTGroup group = ContainerTeamHelper.service.findContainerTeamGroup(team,
                        ContainerTeamHelper.ROLE_GROUPS, role.toString());
                if (group != null && group.isMember(principal)) {
                    set.add(role);
                }
            }
        }
        return set;
    }

    private static QueryResult findContainerTeams() throws WTException {
        QuerySpec qs = new QuerySpec(ContainerTeam.class);
        return PersistenceHelper.manager.find(qs);
    }
}
