package org.pardus.manager.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Console {
	public static ConsoleProcessResult Execute(String... params) throws IOException {
		ConsoleProcessResult result = new ConsoleProcessResult();
		ProcessBuilder pb = new ProcessBuilder(params);
//		List<String> ccc = pb.command();
//		if (ccc != null && ccc.size() > 0) {
//			for (String cccc : ccc) {
//				tConsole.appendText("C:" + cccc + "\r\n");
//			}
//		}
//		ProcessBuilder pb = new ProcessBuilder("myshellScript.sh", "myArg1", "myArg2");
//		Map<String, String> env = pb.environment();
//		env.put("VAR1", "myValue");
//		env.remove("OTHERVAR");
//		env.put("VAR2", env.get("VAR1") + "suffix");
//		pb.directory(new File("myDir"));

		pb.redirectErrorStream(true);
		Process p = pb.start();
		BufferedReader buffI = new BufferedReader(new InputStreamReader(p.getInputStream()));
		BufferedReader buffE = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		String line;
		while (p.isAlive()) {
			while ((line = buffI.readLine()) != null) {
				result.add(line);
			}

			while ((line = buffE.readLine()) != null) {
				result.addError(line);
			}
		}
		result.setExitValue(p.exitValue());
		return result;
	}
}
