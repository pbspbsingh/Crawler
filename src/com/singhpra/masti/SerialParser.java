package com.singhpra.masti;

import static com.singhpra.masti.common.Util.UTIL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.singhpra.masti.common.Loggeable;
import com.singhpra.masti.modal.Serial;

public class SerialParser implements Loggeable {

    private final String url;

    public SerialParser(String url) {
        this.url = url;
    }

    public List<Serial> parse() throws IOException {
        final Document document = Jsoup.parse(UTIL.getHTML(url), url);
        final Elements elements = document.select("ul.plus-list li a");

        final List<Serial> list = new ArrayList<>(elements.size());
        for (Element element : elements) {
            final String href = element.attr("href");
            int index = href.lastIndexOf('/');
            
            final Serial serial = new Serial();
            serial.setHref(href.substring(0, index) + "/episodes" + href.substring(index));
            serial.setTitle(element.attr("title"));
            serial.setKey(element.attr("title").replaceAll("[\\s.,-]+", "_").toLowerCase());
            
            list.add(serial);
        }
        return list;
    }

}
