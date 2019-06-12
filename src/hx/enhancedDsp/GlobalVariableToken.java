package hx.enhancedDsp;

import com.wm.app.b2b.server.globalvariables.GlobalVariablesException;
import com.wm.app.b2b.server.globalvariables.GlobalVariablesManager;
import com.wm.passman.PasswordManagerException;
import com.wm.util.GlobalVariables;
import com.wm.util.template.EmptyToken;
import com.wm.util.template.Reporter;

/**
 * 
 * @author Xiaowei Wang
 * @version 1.0
 *
 *          A customized token to extend the capability of DSP.
 */
public class GlobalVariableToken extends EmptyToken {

	/**
	 * The name of global variable to retrieve.
	 */
	String variable;

	public GlobalVariableToken(String args) {
		super(args);
	}

	public boolean processArg(String argValue, int index) {
		if (super.processArg(argValue, index)) {
			return true;
		}
		variable = argValue;
		return true;
	}

	public void eval(Reporter reporter) {
		eval(reporter, null);
	}

	public void eval(Reporter reporter, String encoding) {
		if (variable == null || variable.isEmpty()) {
			return;
		}

		try {
			GlobalVariablesManager manager = GlobalVariablesManager
					.getInstance();
			GlobalVariables.GlobalVariableValue gvValue = manager
					.getGlobalVariableValue(variable);
			reporter.appendBundleString(gvValue.getValue());
		} catch (GlobalVariablesException | PasswordManagerException e) {
			e.printStackTrace();
		}

	}
}
