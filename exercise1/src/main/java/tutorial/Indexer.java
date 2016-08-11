package tutorial;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Indexer
{

    public Indexer(String indexDirectoryPath) throws IOException
    {
        Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
        Analyzer analyzer = new RussianAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(OpenMode.CREATE);
        writer = new IndexWriter(indexDirectory, config);
    }

    public void close() throws IOException{
        writer.close();
    }

    public int createIndex(String dataDirPath) throws IOException
    {
        Path path = Paths.get(dataDirPath);
        DirectoryStream<Path> stream = Files.newDirectoryStream(
                path, (file) -> file.toString().toLowerCase().endsWith(".txt"));

        for (Path file : stream)
        {
            long lastModified = Files.getLastModifiedTime(file).toMillis();
            indexDoc(file, lastModified);
        }

        return writer.numDocs();
    }

    private void indexDoc(Path file, long lastModified) throws IOException
    {
        if (!Files.isDirectory(file)
                && !Files.isHidden(file)
                && Files.exists(file)
                && Files.isReadable(file))
        try (InputStream stream = Files.newInputStream(file)) {
            Document doc = new Document();

            Field pathField = new StringField(LuceneConstants.FILE_PATH, file.toString(), Field.Store.YES);
            doc.add(pathField);
            doc.add(new LongPoint(LuceneConstants.MODIFIED, lastModified));
            doc.add(new TextField(LuceneConstants.CONTENTS, new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

            System.out.println("adding " + file);
            writer.addDocument(doc);
        }
    }

    private IndexWriter writer;
}
