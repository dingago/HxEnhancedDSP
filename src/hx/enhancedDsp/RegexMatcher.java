package hx.enhancedDsp;

import com.wm.util.StringMatcher;

import java.util.regex.Pattern;

/**
 * 
 * @author Xiaowei Wang
 * @version 1.0
 * 
 *          A string matcher to match regex pattern.
 */
public class RegexMatcher extends StringMatcher {
	private static final long serialVersionUID = 5195238414601607126L;
	private String pattern;

	public RegexMatcher(String pattern) {
		super(pattern);
		this.pattern = pattern;
	}

	@Override
	public boolean match(String value) {
		return Pattern.matches(pattern, value);
	}
}
