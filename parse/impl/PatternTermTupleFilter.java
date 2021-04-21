package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.util.Config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTermTupleFilter extends AbstractTermTupleFilter {

    /**
     * 构造函数
     *
     * @param input ：输入流
     */
    public PatternTermTupleFilter(AbstractTermTupleStream input) {
        super(input);
    }

    @Override
    public AbstractTermTuple next() {
        Pattern pattern = Pattern.compile(Config.TERM_FILTER_PATTERN);
        Matcher matcher = null;
        AbstractTermTuple tmp;
        while ((tmp = input.next()) != null) {
            matcher = pattern.matcher(tmp.term.getContent());
            if (matcher.matches())
                return tmp;
        }
        return null;
    }
}
