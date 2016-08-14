package tutorial;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class Searcher
{
    public Searcher(String indexDirectoryPath) throws IOException
    {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDirectoryPath)));
        indexSearcher = new IndexSearcher(reader);
        Analyzer analyzer = new RussianAnalyzer();
        queryParser = new QueryParser(FIELD, analyzer);
        queryParser.setAllowLeadingWildcard(true);
    }

    public TopDocs search(String searchQuery) throws IOException, ParseException
    {
        Query query = queryParser.parse(/*"*" + */searchQuery/* + "*"*/);
        return indexSearcher.search(query, LuceneConstants.MAX_SEARCH);
    }

    public Document getDocument(ScoreDoc scoreDoc) throws IOException
    {
        return indexSearcher.doc(scoreDoc.doc);
    }

    public void close() throws IOException{
    }

    private IndexSearcher indexSearcher;
    private QueryParser queryParser;

    public static final String FIELD = "contents";
}
