package arenx.torrent.piratebay;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import arenx.torrent.piratebay.Query.QueryType;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Jsoup.class)
public class QueryTest {

	private static Logger logger = LoggerFactory.getLogger(QueryTest.class);

	@Test
	public void fff() throws InvalidQueryException, IOException, URISyntaxException {

		String searchDocString = IOUtils.toString(this.getClass().getResourceAsStream("pirate_bay_query"), "UTF-8");
		String torrentDocString = IOUtils.toString(this.getClass().getResourceAsStream("pirate_bay_torrent"), "UTF-8");

		Document searchDoc = Jsoup.parse(searchDocString, "UTF-8");
		Document torrentDoc = Jsoup.parse(torrentDocString, "UTF-8");

		PowerMockito.mockStatic(Jsoup.class);

		Connection searchConn = mock(Connection.class);
		when(searchConn.timeout(anyInt())).thenReturn(searchConn);
		when(searchConn.get()).thenReturn(searchDoc);

		Connection torrentConn = mock(Connection.class);
		when(torrentConn.timeout(anyInt())).thenReturn(torrentConn);
		when(torrentConn.get()).thenReturn(torrentDoc);

		Mockito.when(Jsoup.connect(Mockito.matches("(https://thepiratebay.org/search).*"))).thenReturn(searchConn);
		Mockito.when(Jsoup.connect(Mockito.matches("(https://thepiratebay.org/torrent).*"))).thenReturn(torrentConn);

		Query query = new Query.Builder(QueryType.SEARCH).SetTerm("bluray").SetCategory(Category.HDMovies).build();

		Collection<Torrent> ts = query.execute(0);
		Torrent t = ts.stream().findFirst().get();

		PowerMockito.verifyStatic();

		assertEquals(t.getName(), "Moana 2016 1080p BluRay x264 DTS-JYK");
		assertEquals(t.getUploadDate(), new Date(1487310467000l));
		assertEquals(t.getSize(), 2900401607l);
		assertEquals(t.getSeeders(), 8862);
		assertEquals(t.getLeechers(), 5331);
		assertEquals(t.getMagnetLink(),
				new URI("magnet:?xt=urn:btih:3979a828a7fa105af4a9e4af6f33c5b3402a1d94&dn=Moana+2016+1080p+BluRay+x264+DTS-JYK&"
						+ "tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&"
						+ "tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fpublic.popcorn-tracker.org%3A6969"));

	}
}
