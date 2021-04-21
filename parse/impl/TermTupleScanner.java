package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.index.impl.TermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleScanner;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StringSplitter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * <pre>
 *     TermTupleScanner是AbstractTermTupleScanner的子类，即一个具体的TermTupleScanner对象就是
 *     一个AbstractTermTupleStream流对象，它利用java.io.BufferedReader去读取文本文件得到一个个三元组TermTuple.
 *
 *     其具体子类需要重新实现next方法获得文本文件里的三元组
 * </pre>
 */
public class TermTupleScanner extends AbstractTermTupleScanner {

    // 用来保存处理后的单词
    private List<String> words = new ArrayList<String>();
    // 用于记录当前word的位置
    private int curPos = 0;

    /**
     * 缺省构造函数
     */
    public TermTupleScanner() {
    }

    /**
     * 构造函数
     *
     * @param input
     */
    public TermTupleScanner(BufferedReader input) {
        this.input = input;
        this.settleInput();
    }

    private void settleInput() {
        String StrIinput = null;
        List<String> tmpWords = null;
        StringSplitter splitter = new StringSplitter();
        splitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);
        try {
            while ((StrIinput = this.input.readLine()) != null) {
                // 处理当前得到的句子
                tmpWords = splitter.splitByRegex(StrIinput);
                // 将当前处理得到的list加入
                words.addAll(tmpWords);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public AbstractTermTuple next() {
        if (curPos < words.size())
            return new TermTuple(new Term(words.get(curPos).toLowerCase(Locale.ROOT)), curPos++);
        else
            return null;
    }


}



