package ext.amph.changenotice.forms;

import com.ptc.core.components.forms.ObjectFormProcessorDelegate;
import com.ptc.windchill.enterprise.change2.forms.delegates.AffectedAndResultingItemsFormDelegate;

//import ext.generic.logger.WCLogger;

public class AMPHAffectedAndResultingItemsFormDelegate extends AffectedAndResultingItemsFormDelegate {

//    private static final WCLogger LOGGER = new WCLogger(AMPHAffectedAndResultingItemsFormDelegate.class.getName());

    @Override
    public ObjectFormProcessorDelegate getResultingDataFormDelegate() {
        // TODO Auto-generated method stub
        return new AMPHChangeTaskResultingItemsFormDelegate();
    }

}
