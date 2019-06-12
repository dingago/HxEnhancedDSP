package hx.enhancedDsp;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import javassist.ClassPool;
import javassist.CtClass;

/**
 * 
 * @author Xiaowei Wang
 * @version 1.0
 * 
 * This class will be registered as a javaagent
 * 
 */
public class DspClassTransformer implements ClassFileTransformer {

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		/**
		 * Replace with customized class
		 */
		if ("com/wm/util/template/IfVarToken".equals(className)) {
			try {
				CtClass cc = ClassPool.getDefault().get(
						"com.wm.util.template.IfVarToken");
				return cc.toBytecode();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		if ("com/wm/util/template/TemplateTokenizer".equals(className)) {
			try {
				CtClass cc = ClassPool.getDefault().get(
						"com.wm.util.template.TemplateTokenizer");
				return cc.toBytecode();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void premain(String options, Instrumentation ins) {
		ins.addTransformer(new DspClassTransformer());
	}

	public static void agentmain(String options, Instrumentation ins) {
		ins.addTransformer(new DspClassTransformer());
	}
}
