package io.github.sullis.httpclient.example;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import com.google.common.collect.Streams;
import io.atlassian.fugue.Either;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class FileParser {
    private static final int EXPECTED_FIELDS = 6;

    public Stream<Either<LineStatus, URL>> parse(java.io.InputStream input, Charset charset) throws IOException {
        InputStreamReader reader = new InputStreamReader(input, charset);
        CSVParser parser = CSVFormat.DEFAULT.parse(reader);
        return Streams.stream(new CsvRecordIterator(parser.iterator()));
    }

    private class CsvRecordIterator
      implements Iterator<Either<LineStatus, URL>> {
        private Iterator<CSVRecord> _iter;
        private AtomicLong _lineCount = new AtomicLong(0);

        public CsvRecordIterator(Iterator<CSVRecord> iter) {
            _iter = iter;
        }

        @Override
        public boolean hasNext() {
            return _iter.hasNext();
        }

        @Override
        public Either<LineStatus, URL> next() {
            CSVRecord record = _iter.next();
            long lineNum = _lineCount.incrementAndGet();
            if (record.size() == 0) {
                return Either.left(LineStatus.create(
                        lineNum,
                        false,
                        "Blank line"));
            }
            if (record.size() != EXPECTED_FIELDS) {
                return Either.left(LineStatus.create(
                        lineNum,
                        false,
                        "Expected " + EXPECTED_FIELDS + " fields. Actual: " + record.size()));
            }
            if ("Rank".equals(record.get(0))) {
                return Either.left(LineStatus.create(
                        lineNum,
                        true,
                        "File header"));
            }
            String website = record.get(1).trim();
            try {
                return Either.right(new URL("https://" + website));
            } catch (MalformedURLException ex) {
                return Either.left(LineStatus.create(
                        lineNum,
                        false,
                        "Malformed url: " + website));
            }
        }
    }
}
