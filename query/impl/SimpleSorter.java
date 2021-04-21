package hust.cs.javacourse.search.query.impl;


import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SimpleSorter implements Sort {
    public SimpleSorter() {

    }

    @Override
    /**
     * 对命中结果集合根据文档得分排序
     * @param hits ：命中结果集合
     */

    public void sort(List<AbstractHit> hits) {
        for (AbstractHit hit : hits) {
            hit.setScore(-score(hit));
        }
        Collections.sort(hits);
    }

    @Override
    public double score(AbstractHit hit) {
        Set<AbstractTerm> termSet = hit.getTermPostingMapping().keySet();
        double score = 0;
        for (AbstractTerm term : termSet) {
            score += hit.getTermPostingMapping().get(term).getFreq();
        }
        return score;
    }
}
