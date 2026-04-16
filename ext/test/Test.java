package ext.test;

import wt.workflow.engine.WfProcess;

public class Test {

	public static void checkValues(Object testVariable, WfProcess process) {

		// Print values (for debugging)
		System.out.println("Variable: " + testVariable);
		System.out.println("Process: " + process);

		// Get process ID
		if (process != null) {
			System.out.println("Process ID: " + process.getPersistInfo().getObjectIdentifier().getId());
		}

      testVariable.toString(); // will throw NPE if null
	}
}