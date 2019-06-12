package com.wm.util.template;

import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.util.JournalLogger;
import com.wm.util.StringMatcher;

import hx.enhancedDsp.RegexMatcher;

import java.util.StringTokenizer;

public class IfVarToken extends TemplateToken {
	String name;
	String equals;
	StringMatcher matcher;
	boolean ifNull;
	boolean notEmpty;
	boolean fromPipe;
	boolean exists;
	boolean gotElse;

	public IfVarToken(String args) {
		super(args);
	}

	public IfVarToken(String name, boolean isNull, boolean notEmpty,
			String equals, String vequals, String matches) {
		init();
		this.ifNull = isNull;
		this.notEmpty = notEmpty;
		this.name = name;
		if (equals != null) {
			this.equals = equals;
		} else if (matches != null) {
			this.equals = matches;
			this.matcher = new StringMatcher(this.equals);
		} else if (vequals != null) {
			this.fromPipe = true;
			this.equals = vequals;
		}
	}

	public void init() {
		this.ifNull = false;
		this.notEmpty = false;
		this.fromPipe = false;
		this.equals = null;
		this.matcher = null;
	}

	public boolean processArg(String value, int index) {
		boolean ret = true;
		if (super.processArg(value, index)) {
			ret = true;
		} else if (value.charAt(0) == '-') {
			if (value.equals("-isnull")) {
				this.ifNull = true;
				ret = true;
			} else if (value.equals("-notempty")) {
				this.notEmpty = true;
				ret = true;
			}
		} else if (value.startsWith("matches(")) {
			this.equals = getStringInsideParens(value);
			this.matcher = new StringMatcher(this.equals);
			if (this.equals == null) {
				ret = false;
			}
		} else if (value.startsWith("regexmatches(")) {
			/**
			 * Add a customized argument regexmatches
			 */
			this.equals = getStringInsideParens(value);
			this.matcher = new RegexMatcher(this.equals);
			if (this.equals == null) {
				ret = false;
			}
		}  else if (value.startsWith("equals(")) {
			this.equals = getStringInsideParens(value);
			if (this.equals == null) {
				ret = false;
			}
		} else if (value.startsWith("vequals(")) {
			this.fromPipe = true;
			this.equals = getStringInsideParens(value);
			if (this.equals == null) {
				ret = false;
			}
		} else {
			this.name = value;
		}
		if (JournalLogger.isLogEnabledDebugPlus(6, 17, 72)) {
			Object[] o = { value, new Integer(index).toString(),
					new Boolean(ret).toString() };
			JournalLogger.logDebugPlus(6, 17, 72, o);
		}
		return ret;
	}

	public boolean getResult() {
		return this.exists;
	}

	public void eval(Reporter r) {
		eval(r, null);
	}

	public void eval(Reporter r, String encoding) {
		this.gotElse = false;
		Object val = r.get(this.name);
		if (JournalLogger.isLogEnabledDebugPlus(6, 18, 72)) {
			JournalLogger.logDebugPlus(6, 18, 72, this.name,
					val != null ? val.toString() : "null");
		}
		this.exists = (val != null);
		if (this.equals != null) {
			if (this.exists) {
				String vstr = val.toString();
				if ((val instanceof String[])) {
					vstr = ((String[]) (String[]) val)[r.INDEX];
				}
				if ((this.notEmpty) && (vstr.length() == 0)) {
					this.exists = false;
				} else if (this.fromPipe) {
					Object rhs = r.get(this.equals);
					this.exists = ((rhs != null) && ((val.equals(rhs)) || (vstr
							.equals(rhs.toString()))));
				} else if (this.matcher != null) {
					this.exists = this.matcher.match(vstr);
				} else {
					this.exists = vstr.equals(this.equals);
				}
			}
		} else if (this.ifNull) {
			if (!this.exists) {
				IDataCursor id = r.current.getCursor();
				if ((this.name != null) && (this.name.length() > 0)) {
					StringTokenizer st = new StringTokenizer(this.name, "/");
					boolean varExist = false;
					while (st.hasMoreTokens()) {
						String t = st.nextToken();
						if ((id != null) && (id.first(t))) {
							varExist = true;
							Object obj = id.getValue();
							id.destroy();
							if ((obj instanceof IData)) {
								id = ((IData) obj).getCursor();
							} else {
								id = null;
							}
						} else {
							varExist = false;
						}
					}
					if (varExist) {
						this.exists = (!this.exists);
					}
				}
				if (id != null) {
					id.destroy();
				}
			} else {
				this.exists = (!this.exists);
			}
		} else if ((this.notEmpty) && (this.exists)) {
			if ((val instanceof Object[])) {
				if (((Object[]) val).length == 0) {
					this.exists = false;
				}
			} else if (((val instanceof String))
					&& (val.toString().length() == 0)) {
				this.exists = false;
			}
		}
		super.eval(r, encoding);
	}

	public void evalChild(Reporter r, Object o, String encoding) {
		if ((o instanceof IfVarElseToken)) {
			this.gotElse = true;
			return;
		}
		if (((this.exists) && (!this.gotElse))
				|| ((!this.exists) && (this.gotElse))) {
			super.evalChild(r, o, encoding);
		}
	}

	private static String getStringInsideParens(String value) {
		int i1 = value.indexOf('(') + 1;
		int i2 = value.indexOf(')', i1);
		if ((i1 <= 0) || (i2 < i1)) {
			return null;
		}
		if ((value.startsWith("'")) && (value.endsWith("'"))) {
			return value.substring(i1 + 1, i2 - 1);
		}
		return value.substring(i1, i2);
	}

	public String getName() {
		return this.name;
	}

	public boolean getIsNull() {
		return this.ifNull;
	}

	public boolean getNotEmpty() {
		return this.notEmpty;
	}

	public String getEquals() {
		return (this.matcher == null) && (!this.fromPipe) ? this.equals : null;
	}

	public String getVEquals() {
		return this.fromPipe ? this.equals : null;
	}

	public String getMatches() {
		return this.matcher != null ? this.equals : null;
	}
}
