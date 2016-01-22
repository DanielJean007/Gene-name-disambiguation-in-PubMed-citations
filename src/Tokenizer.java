import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer{

  static class Rule{
    final String name;
    final Pattern pattern;

    Rule(String name, String regex){
      this.name = name;
      pattern = Pattern.compile(regex);
    }
  }

  public ArrayList<String> tokenize(String source){
	List<Rule> rules = new ArrayList<Rule>();
	rules.add(new Rule("word", "[A-Za-z0-9._%+-]+"));
	rules.add(new Rule("delim", "[^ \n\t\r,.;:!?&()<>[]+\"']" ));
	rules.add(new Rule("nonEngWord", "[\\p{IsLatin}]+"));
	
    ArrayList<String> tokens = new ArrayList<String>();
    
    int pos = 0;
    final int end = source.length();
    Matcher m = Pattern.compile("dummy").matcher(source);
    m.useTransparentBounds(true).useAnchoringBounds(false);
    while (pos < end)
    {
      m.region(pos, end);
      for (Rule r : rules)
      {
        if (m.usePattern(r.pattern).lookingAt())
        {
          tokens.add((String) source.subSequence(m.start(), m.end()));
          pos = m.end();
          break;
        }
      }
      pos++;  // bump-along, in case no rule matched
    }
    return tokens;
  }
}