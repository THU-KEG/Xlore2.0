package BaiduExtractor;

import org.python.util.PythonInterpreter;

public class Dereplication {
	public static void main(String args[]) {
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.execfile("../other/Dereplication.py");
		interpreter.close();
	}
}
