package ext.splm.wizard;

import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.windchill.enterprise.attachments.mvc.builders.AttachmentsTableBuilder;

import wt.util.WTException;

@ComponentBuilder("customTestTable")
public class TestTable extends AttachmentsTableBuilder {

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
        System.out.println("### TestTable CONFIG HIT ###");
        return super.buildComponentConfig(params);
    }
}