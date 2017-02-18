package arenx.torrent.piratebay;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

public class Query {

	private static Logger logger = LoggerFactory.getLogger(Query.class);
	
	enum QueryType {
		UNKNWON, SEARCH
	}

	enum OrderBy {
		ByName(1), 
		ByNameDescending(2), 
		ByUploaded(3), 
		ByUploadedDescending(4), 
		BySize(5), 
		BySizeDescending(6), 
		BySeeds(7), 
		BySeedsDescending(8), 
		ByLeechers(9), 
		ByLeechersDescending(10), 
		ByUledBy(11), 
		ByUledByDescending(12), 
		ByType(13), 
		ByTypeDescending(14), 
		ByDefault(99);

		private final int value;

		private OrderBy(int value) {

			this.value = value;
		}

		public int getValue() {

			return this.value;
		}
	}

	public static class Builder {

		private QueryType type;
		private String term = "";
		private Category category = Category.ALL;
		private OrderBy order = OrderBy.ByUploaded;

		public Builder(QueryType type) {
			this.type = type;
		}

		public Builder SetTerm(String term) {
			this.term = term;
			return this;
		}

		public Builder SetCategory(Category category) {
			this.category = category;
			return this;
		}
		
		public Builder SetOrderBy(OrderBy order) {
			this.order = order;
			return this;
		}

		public Query build() throws InvalidQueryException {
			switch (type) {
			case SEARCH:
				StrSubstitutor ss = new StrSubstitutor(ImmutableMap.<String, Object>builder()
						.put("term", term)
						.put("order", order.value)
						.put("category", category.number)
						.build());
				
				String template = "https://thepiratebay.org/search/${term}/${page}/${order}/${category}/";
				
				Query q = new Query();
				q.url = ss.replace(template);
				
				return q;
			default:
				throw new InvalidQueryException("invalid or not support query type [" + type + "]");
			}
		}

	}

	private String url;
	
	public Collection<Torrent> execute(int page) throws IOException{
		StrSubstitutor ss = new StrSubstitutor(ImmutableMap.<String, Object>builder()
				.put("page", page)
				.build());
		String s = ss.replace(this.url);
		
		Document doc = Jsoup.connect(s)
				.timeout(10000)
				.get();
		
		Element tbody = doc.getElementsByTag("tbody").get(0);
		Elements trs = tbody.getElementsByTag("tr");
		
		List<String> ids = trs.stream()
			.map(tr->{			
				Element td = tr.getElementsByTag("td").get(1);			
				Element a =td.getElementsByTag("a").get(0);			
				String href = a.attr("href");			
				String id = href.substring(href.indexOf("/") + 1);
				id = id.substring(id.indexOf("/") + 1);
				id = id.substring(0, id.indexOf("/"));	
				return id;
			})			
			.collect(Collectors.toList());
		
		List<Torrent> ts = new LinkedList<>();
		for (String id: ids) {
			Torrent t = Torrent.Get(id);
			ts.add(t);
		}
		
		return ts;
	}

}
