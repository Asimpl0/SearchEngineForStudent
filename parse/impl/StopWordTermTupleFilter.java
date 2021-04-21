package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.util.StopWords;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class StopWordTermTupleFilter extends AbstractTermTupleFilter {
    /**
     * 构造函数
     *
     * @param input 三元组流输入
     */
    public StopWordTermTupleFilter(AbstractTermTupleStream input) {
        super(input);
    }

    /**
     * 根据StopWords过滤获得下一个三元组
     *
     * @return 根据StopWords过滤获得的一个三元组
     */
    @Override
    public AbstractTermTuple next() {
        AbstractTermTuple tmp = null;
        List STOP_WORDS = Arrays.asList(StopWords.STOP_WORDS);
        while ((tmp = input.next()) != null) {
            if (!STOP_WORDS.contains(tmp.term.getContent()))
                return tmp;
        }
        return null;
    }
}
