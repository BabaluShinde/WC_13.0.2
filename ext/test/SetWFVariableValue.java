package ext.test;

import wt.workflow.engine.WfProcess;
import wt.fc.PersistenceHelper;
import wt.workflow.engine.ProcessData;
import wt.method.RemoteAccess;

public class SetWFVariableValue implements RemoteAccess {

	public static void checkValues(Object testVariable, WfProcess process) throws Exception {

		ProcessData data = process.getContext();

		Object oldValue = data.getValue("testVariable");
		System.out.println("Old Value: " + oldValue);

		data.setValue("testVariable", "Updated via RMI!!");

		PersistenceHelper.manager.save(process);

		Object newValue = data.getValue("testVariable");
		System.out.println("New Value: " + newValue);
	}

	public static void main(String[] args) throws Exception {

		if (args.length == 0) {
			System.out.println("Please provide processId");
			return;
		}

		long processId = Long.parseLong(args[0]);

		String oidStr = "wt.workflow.engine.WfProcess:" + processId;

		wt.fc.ObjectIdentifier oid = wt.fc.ObjectIdentifier.newObjectIdentifier(oidStr);

		wt.workflow.engine.WfProcess process = (wt.workflow.engine.WfProcess) wt.fc.PersistenceHelper.manager
				.refresh(oid);

		wt.method.RemoteMethodServer rms = wt.method.RemoteMethodServer.getDefault();

		rms.invoke("checkValues", "ext.test.SetWFVariableValue", null,
				new Class[] { Object.class, wt.workflow.engine.WfProcess.class }, new Object[] { null, process });

		System.out.println("Executed successfully");
	}
}