package tech.ailef.jpromptmanager;

public class JPromptManagerOptions {
	private boolean printPrompts = false;
	
	public JPromptManagerOptions withPrintPrompts(boolean printPrompts) {
		this.printPrompts = printPrompts;
		return this;
	}
	
	public boolean isPrintPrompts() {
		return printPrompts;
	}
}
