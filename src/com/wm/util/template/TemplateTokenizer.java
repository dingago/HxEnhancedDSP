package com.wm.util.template;

import hx.enhancedDsp.GlobalVariableToken;

public class TemplateTokenizer {
	int ptr;
	String buffer;
	long lastmod;
	boolean done;
	boolean loopEol;
	TemplateToken lastToken;
	TemplateToken rootToken;

	private TemplateTokenizer(String buffer, long lastmod) {
		this.ptr = 0;
		this.buffer = buffer;
		this.lastmod = lastmod;
		this.done = false;
		this.loopEol = false;
		this.lastToken = null;
		this.rootToken = null;
	}

	public static TemplateToken newRootToken(String buffer, long lastmod) {
		return new TemplateTokenizer(buffer, lastmod).getRootToken();
	}

	private Object collect(int ptr, int pos) {
		Object o = this.buffer.substring(ptr, pos);
		this.ptr = pos;
		return o;
	}

	private boolean createToken(String name, String args) {
		if ((name == null) || (name.length() > 15)) {
			return false;
		}
		if (name.startsWith("end")) {
			return tokenAdded(new EndToken(name, args));
		}
		if (name.equals("value")) {
			return tokenAdded(new ValueToken(args));
		}
		if (name.equals("include")) {
			return tokenAdded(new IncludeToken(args));
		}
		if (name.equals("loop")) {
			return tokenAdded(new LoopToken(args));
		}
		if (name.equals("loopsep")) {
			return tokenAdded(new LoopSepToken(args));
		}
		if (name.equals("ifvar")) {
			return tokenAdded(new IfVarToken(args));
		}
		if (name.equals("else")) {
			return tokenAdded(new IfVarElseToken(args));
		}
		if (name.equals("nl")) {
			return tokenAdded(new NewlineToken(args));
		}
		if (name.equals("scope")) {
			return tokenAdded(new ScopeToken(args));
		}
		if (name.equals("invoke")) {
			return tokenAdded(new InvokeToken(args));
		}
		if (name.equals("switch")) {
			return tokenAdded(new SwitchToken(args));
		}
		if (name.equals("case")) {
			return tokenAdded(new CaseToken(args));
		}
		if (name.equals("rename")) {
			return tokenAdded(new RenameToken(args));
		}
		if (name.equals("onerror")) {
			return tokenAdded(new OnErrorToken(args));
		}
		if (name.equals("sysvar")) {
			return tokenAdded(new SysvarToken(args));
		}
		if (name.equals("comment")) {
			return tokenAdded(new CommentToken(args));
		}
		if (name.equals("validConst")) {
			return tokenAdded(new ValidConstToken(args));
		}
		if (name.equals("gv")){
			/**
			 * Add a customized token
			 */
			return tokenAdded(new GlobalVariableToken(args));
		}
		return false;
	}

	private boolean tokenAdded(TemplateToken tt) {
		if (((tt instanceof LoopToken)) && (((LoopToken) tt).eol)) {
			this.loopEol = true;
		}
		this.lastToken = tt;
		return true;
	}

	public TemplateToken getRootToken() {
		this.rootToken = new TemplateToken(this, this.lastmod);
		return this.rootToken;
	}

	public boolean hasMoreTokens() {
		if ((!this.done) && (this.ptr < this.buffer.length())) {
			return true;
		}
		this.done = true;
		this.buffer = null;
		return false;
	}

	public Object nextToken() {
		while (this.ptr < this.buffer.length()) {
			int pos = this.buffer.indexOf('%', this.ptr);
			if ((this.lastToken != null) && (this.lastToken.skipNewline)) {
				this.lastToken = null;
				if (this.buffer.charAt(this.ptr) == '\n') {
					if (!this.loopEol) {
						this.ptr += 1;
						continue;
					}
					this.loopEol = false;
				}
				if ((this.buffer.charAt(this.ptr) == '\r')
						&& (this.buffer.charAt(this.ptr + 1) == '\n')) {
					if (!this.loopEol) {
						this.ptr += 2;
						continue;
					}
					this.loopEol = false;
				}
			}
			this.lastToken = null;
			if (pos < 0) {
				return collect(this.ptr, this.buffer.length());
			}
			if (pos > this.ptr) {
				return collect(this.ptr, pos);
			}
			int pos2 = this.buffer.indexOf(' ', pos + 1);

			int pos3 = this.buffer.indexOf('%', pos + 1);
			if (((pos2 < 0) || (pos2 > pos3)) && (pos3 > 0)) {
				pos2 = pos3;
			}
			String name = null;
			if (pos3 > 0) {
				name = this.buffer.substring(pos + 1, pos2);
			}
			String body = "";
			if (pos3 > pos2) {
				body = this.buffer.substring(pos2 + 1, pos3);
			}
			if (createToken(name, body)) {
				this.ptr = (pos3 + 1);
				return this.lastToken;
			}
			if (pos3 < 0) {
				return collect(this.ptr, this.buffer.length());
			}
			return collect(this.ptr, pos3);
		}
		this.done = true;
		this.buffer = null;
		return null;
	}
}
