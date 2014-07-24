import java.io.IOException;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

public class LuceneConfig {
	private LuceneConfig() {

	}

	private static Directory ramDir = null;
	private static Analyzer analyzer = null;
	private static IndexWriter writer = null;
	private static IndexReader reader = null;
	static {
		try {
			ramDir = FSDirectory.getDirectory("D:/lucene_index/");
			analyzer = new PaodingAnalyzer();
			writer = new IndexWriter(ramDir, analyzer);
			ramDir.makeLock(IndexWriter.WRITE_LOCK_NAME).release();
			reader=IndexReader.open(ramDir);
			
			//writer.optimize();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static IndexWriter getIndexWriter() throws CorruptIndexException,
			LockObtainFailedException, IOException {
		return writer;
	}

	public static IndexReader getReader() {
		return reader;
	}
	public static Analyzer getAnalyzer(){
		return analyzer;
	}
}
