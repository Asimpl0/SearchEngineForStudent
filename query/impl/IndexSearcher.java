package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.index.impl.Posting;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;
import hust.cs.javacourse.search.util.FileUtil;

import javax.print.attribute.standard.ReferenceUriSchemesSupported;
import java.io.File;
import java.util.*;

/**
 * <pre>
 *  IndexSearcher是检索具体实现的类
 * </pre>
 */
public class IndexSearcher extends AbstractIndexSearcher {

    /**
     * 从指定索引文件打开索引，加载到index对象里. 一定要先打开索引，才能执行search方法
     *
     * @param indexFile ：指定索引文件
     */
    @Override
    public void open(String indexFile) {
        this.index.load(new File(indexFile));
    }

    /**
     * 根据单个检索词进行搜索
     *
     * @param queryTerm ：检索词
     * @param sorter    ：排序器
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm, Sort sorter) {
        List<AbstractHit> hits = new ArrayList<>();
        /**
         * 在索引中搜索对应的term，返回对应的postinglist
         */
        AbstractPosting posting;
        AbstractPostingList postingList = index.search(queryTerm);
        Map<AbstractTerm, AbstractPosting> map = new TreeMap<>();
        if (postingList == null)
            return hits.toArray(new AbstractHit[hits.size()]);
        for (int i = 0; i < postingList.size(); i++) {
            posting = postingList.get(i);
            map.put(queryTerm, posting);
            hits.add(new Hit(posting.getDocId(), index.getDocName(posting.getDocId()), map));
        }
        sorter.sort(hits);
        return hits.toArray(new AbstractHit[hits.size()]);
    }


    /**
     * 根据二个检索词进行搜索
     *
     * @param queryTerm1 ：第1个检索词
     * @param queryTerm2 ：第2个检索词
     * @param sorter     ：    排序器
     * @param combine    ：   多个检索词的逻辑组合方式
     * @return ：命中结果数组
     */
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter, LogicalCombination combine) {

        //new一个保存命中结果的链表
        List<AbstractHit> hits = new ArrayList<>();
        //获得搜索词对应的postingList
        AbstractPostingList postingList = this.index.search(queryTerm1);
        AbstractPosting posting = new Posting();
        if (postingList == null)
            return hits.toArray(new AbstractHit[hits.size()]);
        //遍历postingList,将其中的每个posting处理生成Hit存放到链表中
        for (int i = 0; i < postingList.size(); i++) {
            posting = postingList.get(i);
            Map<AbstractTerm, AbstractPosting> map = new TreeMap<>();
            map.put(queryTerm1, posting);
            hits.add(new Hit(posting.getDocId(), index.getDocName(posting.getDocId()), map));
        }

        //根据谓词区分
        switch (combine) {
            case OR: {
                postingList = this.index.search(queryTerm2);
                //遍历queryTerm2的postingList,对其中的每个posting,查询当前的命中列表hitList,找到文档ID相同的hit修改其map，找不到则新建一个hit
                for (int i = 0; i < postingList.size(); i++) {
                    posting = postingList.get(i);
                    int j = 0;

                    //查询当前的命中列表hitList,找到文档ID相同的hit修改其map
                    for (j = 0; j < hits.size(); j++) {
                        AbstractHit hit = hits.get(j);
                        if (hit.getDocId() == posting.getDocId()) {
                            hit.getTermPostingMapping().put(queryTerm2, posting);
                            break;
                        }
                    }
                    //找不到则新建一个hit
                    if (j == hits.size()) {
                        Map<AbstractTerm, AbstractPosting> map = new TreeMap<>();
                        map.put(queryTerm2, posting);
                        hits.add(new Hit(posting.getDocId(), index.getDocName(posting.getDocId()), map));
                    }
                }
                break;
            }
            case AND: {
                postingList = this.index.search(queryTerm2);
                List<Integer> deleteList = new ArrayList<>();
                for (int i = 0; i < hits.size(); i++) {
                    AbstractHit hit = hits.get(i);
                    //从postingList中查找是否有和hit相同docId的posting
                    int index = 0;  //记录下标
                    //index不等于-1说明存在docId相同的posting，将其加入到map中
                    if ((index = postingList.indexOf(hit.getDocId())) != -1) {
                        hit.getTermPostingMapping().put(queryTerm2, postingList.get(index));
                    }
                    //当前hit不存在相同docId,记录其位置，之后删除
                    else
                        deleteList.add(i);
                }
                //删除
                for (int index : deleteList) {
                    hits.remove(index);
                }
                break;
            }
        }
        sorter.sort(hits);
        return hits.toArray(new AbstractHit[hits.size()]);
    }

}
