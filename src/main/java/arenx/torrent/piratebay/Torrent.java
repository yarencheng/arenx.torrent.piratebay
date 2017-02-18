package arenx.torrent.piratebay;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableMap;

public class Torrent {
	
	private static Logger logger = LoggerFactory.getLogger(Torrent.class);
	private static Map<String, Torrent> cache = new TreeMap<>();
	private static SimpleDateFormat uploadDateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

	public static Torrent Get(String id) throws IOException{
		if (cache.containsKey(id)) {
			return cache.get(id);
		}
		
		String template = "https://thepiratebay.org/torrent/${id}/";
		StrSubstitutor ss = new StrSubstitutor(ImmutableMap.<String, Object>builder()
				.put("id", id)
				.build());
		
		String url = ss.replace(template);
		
		Document doc = Jsoup.connect(url)
				.timeout(10000)
				.get();
		
		Torrent t = new Torrent();
		t.name = doc.getElementsByAttributeValue("id", "title").text();
		
		Element details = doc.getElementsByAttributeValue("id", "details").get(0);
		
		Elements dts = details.getElementsByTag("dt"); // key of column
		Elements dds = details.getElementsByTag("dd"); // value of column
		
		Map<String, String> columns = new HashMap<>();
		for (int i=0;i<dds.size();i++){
			columns.put(dts.get(i).text(), dds.get(i).text());
		}
		
		logger.debug("columns:{}",columns);
		
		try {
			t.uploadDate = uploadDateformat.parse(columns.get("Uploaded:"));
		} catch (ParseException e) {
			throw new IOException("Failed to parse date", e);
		}
		
		String size = columns.get("Size:");
		t.size = Long.parseLong(CharMatcher.whitespace().trimFrom(size.substring(size.indexOf("(") + 1, size.indexOf("Byte"))));
		
		t.seeders = Integer.parseInt(columns.get("Seeders:"));
		t.leechers = Integer.parseInt(columns.get("Leechers:"));
		
		try {
			t.magnetLink = new URI(details.getElementsByAttributeValue("class", "download").get(0).getElementsByTag("a").attr("href"));
		} catch (URISyntaxException e) {
			throw new IOException("Failed to get magnet link", e);
		}		
		
		cache.put(id, t);
		
		return t;
	}
	
	private String id;
	private String name;
	private Date uploadDate;
	private long size;
	private int seeders;
	private int leechers;
	private URI magnetLink;
	
	private Torrent(){
		
	}

	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public Date getUploadDate() {
		return uploadDate;
	}
	
	public long getSize(){
		return size;
	}

	public int getSeeders() {
		return seeders;
	}

	public int getLeechers() {
		return leechers;
	}

	public URI getMagnetLink() {
		return magnetLink;
	}
	
	
}
