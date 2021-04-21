package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.parse.impl.TermTupleScanner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class TestScanner {
    public static void main(String[] args) {
        try {
            AbstractTermTuple tmp = null;
            BufferedReader input = new BufferedReader(new FileReader("D:\\1.txt"));
            AbstractTermTupleStream scanner = new PatternTermTupleFilter(new LengthTermTupleFilter(new StopWordTermTupleFilter(new TermTupleScanner(input))));
            while ((tmp = scanner.next()) != null) {
                System.out.println(tmp.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
