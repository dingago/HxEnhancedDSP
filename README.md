# HxEnhancedDSP
A sample project to enhance DSP with customized tag and customized argument.

## Dependencies
This project is depended on some exteranl library, including:
- wm-isclient.jar
- wm-isserver.jar
- tool.jar from JVM
- javassist.jar from jboss-javassist

## Deployment Instruction
1. Export the project as a jar file with build-in manifest, for example "hxEnhancedDSP.jar".
2. Copy hxEnhancedDSP.jar and javassist.jar to IS directory for example C:\SoftwareAG\IntegrationServer\instances\default\lib\jars\custom.
3. Add javassist.jar to classpath by updating wrapper configuration file, for example, adding a new line "wrapper.java.classpath.900=C:\SoftwareAG\IntegrationServer\instances\default\lib\jars\custom\javassist.jar" in file C:\SoftwareAG\profiles\IS_default\configuration\custom_wrapper.conf.
4. Register hxEnhancedDSP.jar as javaagent by updating wrapper configuration file, for example, adding a new line "wrapper.java.additional.901=-javaagent:C:\SoftwareAG\IntegrationServer\instances\default\lib\jars\custom\hxEnhancedDsp.jar" in file C:\SoftwareAG\profiles\IS_default\configuration\custom_wrapper.conf.
5. Start up the Integration Server. If you don't see any related error in wrapper.log, you should be good.
