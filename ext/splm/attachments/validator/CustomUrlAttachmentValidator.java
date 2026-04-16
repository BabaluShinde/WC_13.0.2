package ext.splm.attachments.validator;

import java.util.Locale;
import java.util.Map;
import com.ptc.core.ui.validation.DefaultUIComponentValidator;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationResult;
import com.ptc.core.ui.validation.UIValidationResultSet;
import com.ptc.core.ui.validation.UIValidationStatus;

import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.util.WTException;

public class CustomUrlAttachmentValidator extends DefaultUIComponentValidator {
	@Override
	public UIValidationResultSet performFullPreValidation(UIValidationKey validationKey,
			UIValidationCriteria validationCriteria, Locale locale) throws WTException {
		UIValidationResultSet result = super.performFullPreValidation(validationKey, validationCriteria, locale);
		
//		WTPrincipal principal = SessionHelper.manager.getPrincipal();
//		System.out.println("### [DEBUG] Logged-in User: " + principal.getName());

		

		if ("addUrlAttachment".equals(validationKey.getComponentID())
				|| "addEsaAttachment".equals(validationKey.getComponentID())) {


			// Access FormData map
			Map<String, Object> formData = validationCriteria.getFormData();
			if (formData != null) {
				for (String key : formData.keySet()) {
					if (key.contains("changeNotice$create") || key.contains("wt.change2.WTChangeOrder2")) {
						UIValidationResult result1 = UIValidationResult.newInstance(validationKey,
								UIValidationStatus.HIDDEN);
						UIValidationResultSet resultSet = UIValidationResultSet.newInstance();
						resultSet.addResult(result1);
						return resultSet;
					}
				}
			}
		}
		
		return super.performFullPreValidation(validationKey, validationCriteria, locale);
	}
}
