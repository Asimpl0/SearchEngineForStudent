package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.*;
import hust.cs.javacourse.search.util.Config;

import java.io.*;
import java.util.*;

/**
 * AbstractIndex的具体实现类
 */
public class Index extends AbstractIndex {

    /**
     * 构造函数
     */
    public Index() {

    }

    /**
     * 返回索引的字符串表示
     *
     * @return 索引的字符串表示
     */
    @Override
    public String toString() {
        return (this.docIdToDocPathMapping.toString() + termToPostingListMapping.toString());
    }

    /**
     * 添加文档到索引，更新索引内部的HashMap
     *
     * @param document ：文档的AbstractDocument子类型表示
     */
    @Override
    public void addDocument(AbstractDocument document) {

        /**
         将document的ID和PATH加入到map
         */
        docIdToDocPathMapping.put(document.getDocId(), document.getDocPath());
        /**
         * 对document中所有term转换为对应的posting
         */
        AbstractPostingList list;
        AbstractPosting posting;
        for (AbstractTermTuple tuple : document.getTuples()) {
            /**
             * 如果map中不存在该key或者value
             */
            list = termToPostingListMapping.get(tuple.term);
            if (list == null)
                termToPostingListMapping.put(tuple.term, new PostingList());
            /**
             * 重新获得当前term的postinglist
             */
            list = termToPostingListMapping.get(tuple.term);
            /**
             * 获得该postinglist中该docid对应的posting
             */
            int pos = list.indexOf(document.getDocId());

            /**
             * 不存在该doc对应的posting则新建一个
             */
            if (pos < 0) {
                posting = new Posting(document.getDocId(), 1, new ArrayList<>() {{
                    add(tuple.curPos);
                }});
                list.add(posting);
            } else {
                /**
                 * 存在该doc对应的posting
                 * 直接修改对应的值即可
                 */
                posting = list.get(pos);
                posting.setFreq(posting.getFreq() + 1);
                List<Integer> positions = posting.getPositions();
                positions.add(tuple.curPos);
                posting.setPositions(positions);
            }
        }

    }

    /**
     * <pre>
     * 从索引文件里加载已经构建好的索引.内部调用FileSerializable接口方法readObject即可
     * @param file ：索引文件
     * </pre>
     */
    @Override
    public void load(File file) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            this.readObject(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * <pre>
     * 将在内存里构建好的索引写入到文件. 内部调用FileSerializable接口方法writeObject即可
     * @param file ：写入的目标索引文件
     * </pre>
     */
    @Override
    public void save(File file) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            this.writeObject(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 返回指定单词的PostingList
     *
     * @param term : 指定的单词
     * @return ：指定单词的PostingList;如果索引字典没有该单词，则返回null
     */
    @Override
    public AbstractPostingList search(AbstractTerm term) {
        return termToPostingListMapping.get(term);
    }

    /**
     * 返回索引的字典.字典为索引里所有单词的并集
     *
     * @return ：索引中Term列表
     */
    @Override
    public Set<AbstractTerm> getDictionary() {
        return termToPostingListMapping.keySet();
    }

    /**
     * <pre>
     * 对索引进行优化，包括：
     *      对索引里每个单词的PostingList按docId从小到大排序
     *      同时对每个Posting里的positions从小到大排序
     * 在内存中把索引构建完后执行该方法
     * </pre>
     */
    @Override
    public void optimize() {
        /**
         * 对索引里每个单词的PostingList按docId从小到大排序
         */
        Set<AbstractTerm> key = this.getDictionary();
        AbstractPostingList postingList;
        for (AbstractTerm term : key) {
            postingList = termToPostingListMapping.get(term);
            postingList.sort();
            AbstractPosting posting;
            for (int i = 0; i < postingList.size(); i++) {
                posting = postingList.get(i);
                Collections.sort(posting.getPositions());
            }
        }
    }

    /**
     * 根据docId获得对应文档的完全路径名
     *
     * @param docId ：文档id
     * @return : 对应文档的完全路径名
     */
    @Override
    public String getDocName(int docId) {
        return docIdToDocPathMapping.get(docId);
    }

    /**
     * 写到二进制文件
     *
     * @param out :输出流对象
     */
    @Override
    public void writeObject(ObjectOutputStream out) {
        try {
//            out.writeObject(this.docIdToDocPathMapping);
//            out.writeObject(this.termToPostingListMapping);
            out.writeObject(docIdToDocPathMapping.size());
            Set<Integer> keyId = docIdToDocPathMapping.keySet();
            for(Integer key : keyId){
                out.writeObject(key);
                out.writeObject(docIdToDocPathMapping.get(key));
            }
            out.writeObject(termToPostingListMapping.size());
            Set<AbstractTerm> keyTerm = termToPostingListMapping.keySet();
            for(AbstractTerm  term : keyTerm){
                out.writeObject(term);
                out.writeObject(termToPostingListMapping.get(term));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从二进制文件读
     *
     * @param in ：输入流对象
     */
    @Override
    public void readObject(ObjectInputStream in) {
        try {
//            this.docIdToDocPathMapping = (Map<Integer, String>) (in.readObject());
//            this.termToPostingListMapping = (Map<AbstractTerm, AbstractPostingList>) (in.readObject());
            int size;
            this.docIdToDocPathMapping = new TreeMap<>();
            this.termToPostingListMapping = new TreeMap<>();

            size = (int) (in.readObject());
            for(int i = 0; i < size; i++){
                docIdToDocPathMapping.put((Integer) (in.readObject()), (String) (in.readObject()));
            }
            size = (int) (in.readObject());
            for(int i = 0; i<size; i++){
                termToPostingListMapping.put((AbstractTerm) (in.readObject()), (AbstractPostingList) (in.readObject()));
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 自我测试用
     */
    public void toFile() {
        File file = new File(Config.INDEX_DIR + "index.txt");
        BufferedWriter writer;
        if (file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer = new BufferedWriter(new FileWriter(file));
            Set<Integer> keyDoc = this.docIdToDocPathMapping.keySet();
            for (Integer key : keyDoc) {
                writer.write(key.toString() + "\t\t" + docIdToDocPathMapping.get(key) + "\n");
            }
            Set<AbstractTerm> keyTerm = this.termToPostingListMapping.keySet();
            for (AbstractTerm term : keyTerm) {
                writer.write(term.getContent() + "\t\t" + termToPostingListMapping.get(term).toString() + "\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
