package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.util.Config;

public class LengthTermTupleFilter extends AbstractTermTupleFilter {

    /**
     * 构造函数
     *
     * @param input ：输入流
     */
    public LengthTermTupleFilter(AbstractTermTupleStream input) {
        super(input);
    }

    /**
     * 根据Config.TERM_FILTER_MINLENGTH过滤三元组
     *
     * @return 过滤后的三元组
     */
    @Override
    public AbstractTermTuple next() {
        AbstractTermTuple tmp;
        while ((tmp = input.next()) != null) {
            if (tmp.term.getContent().length() >= Config.TERM_FILTER_MINLENGTH
                    && tmp.term.getContent().length() <= Config.TERM_FILTER_MAXLENGTH) {
                return tmp;
            }
        }
        return null;
    }
}
