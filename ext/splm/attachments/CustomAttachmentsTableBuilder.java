package ext.splm.attachments;

import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.mvc.components.*;
import com.ptc.windchill.enterprise.attachments.mvc.builders.AttachmentsTableBuilder;

import wt.util.WTException;

@ComponentBuilder({ "customAttachmentsTable" })
@TypeBased({ "wt.content.ContentHolder" })
public class CustomAttachmentsTableBuilder extends AttachmentsTableBuilder {

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
	    MultiComponentConfig configs = (MultiComponentConfig) super.buildComponentConfig(params);

	    // Get the factory
	    ComponentConfigFactory factory = getComponentConfigFactory();

	    for (ComponentConfig child : configs.getComponents()) {
	    	
	        if (child instanceof TableConfig) {
	            TableConfig table = (TableConfig) child;

	            // Add hidden strike-through column
	            ColumnConfig strikeCol = factory.newColumnConfig("strikethrough", false);
	            strikeCol.setDataStoreOnly(true);  // not shown in UI
	            
	            ((JcaColumnConfig) strikeCol).setDataUtilityId("strikethrough");
	            table.addComponent(strikeCol);
	            table.setNonSelectableColumn (strikeCol);

	            // Mark this column as row strike-through driver
	            table.setStrikeThroughColumn(strikeCol);
	            
	        }
	    }

	    configs.setView("/attachments/customAttachmentTable.jsp");
	    return configs;
	}

}
