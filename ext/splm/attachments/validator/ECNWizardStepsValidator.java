package ext.splm.attachments.validator;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import com.ptc.core.ui.validation.DefaultUIComponentValidator;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationResult;
import com.ptc.core.ui.validation.UIValidationResultSet;
import com.ptc.core.ui.validation.UIValidationStatus;

import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.project.Role;
import wt.session.SessionHelper;
import wt.util.WTException;

public class ECNWizardStepsValidator extends DefaultUIComponentValidator {

	@Override
	public UIValidationResultSet performFullPreValidation(UIValidationKey validationKey,
			UIValidationCriteria validationCriteria, Locale locale) throws WTException {

		UIValidationResultSet resultSet = UIValidationResultSet.newInstance();

		WTPrincipal principal = SessionHelper.manager.getPrincipal();

		if (principal instanceof WTUser) {
			WTUser user = (WTUser) principal;

			// Get all roles of this user (team roles + container team roles)
			Set<Role> userRoles;
			try {
				userRoles = MyRoles.teamRoles(user);
				userRoles.addAll(MyRoles.containerTeamRoles(user));
			} catch (Exception e) {
				e.printStackTrace();
				// Default to enabled if roles cannot be determined
				resultSet.addResult(UIValidationResult.newInstance(validationKey, UIValidationStatus.ENABLED));
				return resultSet;
			}

			Map<String, Object> formData = validationCriteria.getFormData();
			if (formData != null) {
				for (Object value : formData.values()) {
					if (value != null && value.toString().contains("ChangeNotice")
							&& value.toString().contains("wt.change2.WTChangeOrder2")) {
						// Hide the wizard step if user has QUALITYENGINEER role
						if (userRoles.contains(Role.toRole("QUALITY ENGINEER"))) {
							resultSet.addResult(
									UIValidationResult.newInstance(validationKey, UIValidationStatus.HIDDEN));
							return resultSet;
						}

					}
				}
			}
		}

		// Default component enabled
		resultSet.addResult(UIValidationResult.newInstance(validationKey, UIValidationStatus.ENABLED));
		return resultSet;
	}
}
