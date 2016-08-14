package tutorial;


import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;

public class LuceneTester
{

    public static void main(String[] args) throws Exception
    {
        LuceneTester tester = new LuceneTester();
        tester.createIndex();
        tester.search("яблоки");
    }

    private void createIndex() throws IOException{
        indexer = new Indexer(indexDir);
        int numIndexed;
        long startTime = System.currentTimeMillis();
        numIndexed = indexer.createIndex(dataDir);
        long endTime = System.currentTimeMillis();
        indexer.close();
        System.out.println(numIndexed+" File indexed, time taken: "
                                   +(endTime-startTime)+" ms");
    }
    private void search(String searchQuery) throws Exception {
        searcher = new Searcher(indexDir);
        long startTime = System.currentTimeMillis();
        TopDocs hits = searcher.search(searchQuery);
        long endTime = System.currentTimeMillis();

        System.out.println(hits.totalHits +
                                   " documents found. Time :" + (endTime - startTime));
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.getDocument(scoreDoc);
            System.out.println("File: "
                                       + doc.get(LuceneConstants.FILE_PATH));
        }
        searcher.close();
    }

    private String indexDir = "index";
    private String dataDir = "texts";
    private Indexer indexer;
    private Searcher searcher;
}
