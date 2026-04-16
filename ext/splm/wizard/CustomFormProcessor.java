package ext.splm.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.WTObject;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.IBAValueUtility;
import wt.iba.value.service.IBAValueHelper;
import wt.inf.container.WTContainer;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTMessage;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.container.common.AttributeContainer;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.ddl.wtutil.IBAHelper;
//import com.ptc.ddl.wtutil.PrincipalHelper;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;



public class CustomFormProcessor extends DefaultObjectFormProcessor {
	//messages for the user
	private static final String goodTimes = "Congratulations,Selected Objects Has been Processed";
	private static final String missingData = "Some required data has not been supplied";

	@SuppressWarnings("unused")
	@Override
	public FormResult doOperation(final NmCommandBean cmdBean, final List<ObjectBean> objectBeans) throws WTException {
		final FormResult result = super.doOperation(cmdBean, objectBeans);

	
		@SuppressWarnings("unchecked")
		List<WTObject> list =   getSelectPart(cmdBean);;
		System.out.println("list size"+list.size());
		
		for(WTObject obj :list) {
			
			if(obj instanceof WTPart)
			{
				WTPart part = (WTPart) obj;
				System.out.println("Part Number "+ part.getNumber());
			}else if(obj instanceof WTDocument) {
				
				WTDocument doc = (WTDocument) obj;
				System.out.println("Document Number "+doc.getNumber());
			}
			
		
					
		}
		WTContainer container =  cmdBean.getContainer();
		if (cmdBean!=null) {
			setFeedbackMsg(result, goodTimes+" Number of Objects"+list.size(), FormProcessingStatus.SUCCESS);
		}
		else { // they didnt give enough info somehow
			setFeedbackMsg(result, missingData, FormProcessingStatus.FAILURE);
		}

		return result;
	}


private void setFeedbackMsg(final FormResult result, final String message, final FormProcessingStatus eVal) throws WTException {
		final FeedbackMessage messageObj = new FeedbackMessage(FeedbackType.SUCCESS, Locale.US, "Message", new ArrayList<String>(), message.split(","));
		result.addFeedbackMessage(messageObj);
		result.setStatus(eVal);
	}

	
	public static List<WTObject> getSelectPart(NmCommandBean nmcommandbean) throws WTException {
		int listSize = 0;
		ArrayList arraylist = nmcommandbean.getSelected();
		List<WTObject> selectedServicePartList = new ArrayList<WTObject>();
		
		listSize = arraylist.size();
		if ((arraylist != null )&& (listSize > 0)) {
			NmContext nmcontext = null;
			Persistable persistable = null;
			for (int i = 0; i < listSize; i++) {
				nmcontext = (NmContext) arraylist.get(i);
				persistable = (Persistable) nmcontext.getTargetOid().getWtRef().getObject();
				
				if(persistable instanceof WTPart){
					WTPart part=(WTPart)persistable;
					selectedServicePartList.add(part);
				}else if(persistable instanceof WTDocument)
				{
					WTDocument doc =(WTDocument) persistable;
					selectedServicePartList.add(doc);
				}
			}
		}
		return selectedServicePartList;
	}	
}


