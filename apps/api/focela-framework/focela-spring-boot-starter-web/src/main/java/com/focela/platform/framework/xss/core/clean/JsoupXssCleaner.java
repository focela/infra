package com.focela.platform.framework.xss.core.clean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

/**
 * XSS string filtering based on Jsoup
 */
public class JsoupXssCleaner implements XssCleaner {

    private final Safelist safelist;

    /**
     * Used to convert relative paths in src attributes to absolute paths. Not processed when empty;
     * the value should be the prefix of the absolute path (including the protocol part).
     */
    private final String baseUri;

    /**
     * No-arg constructor; by default uses the {@link JsoupXssCleaner#buildSafelist} method to build a safelist
     */
    public JsoupXssCleaner() {
        this.safelist = buildSafelist();
        this.baseUri = "";
    }

    /**
     * Build a Safelist rule for XSS cleaning.
     * Based on Safelist#relaxed():
     * 1. extended to support the style and class attributes
     * 2. the a tag additionally supports the target attribute
     * 3. the img tag additionally supports the data protocol to support base64
     *
     * @return Safelist
     */
    private Safelist buildSafelist() {
        // use the default provided by jsoup
        Safelist relaxedSafelist = Safelist.relaxed();
        // in rich-text editing some styles are implemented via style
        // for example red font style="color:red;", so the style attribute must be added to all tags
        // note: the style attribute carries injection risk <img STYLE="background-image:url(javascript:alert('XSS'))">
        relaxedSafelist.addAttributes(":all", "style", "class");
        // keep the target attribute of the a tag
        relaxedSafelist.addAttributes("a", "target");
        // support img as base64
        relaxedSafelist.addProtocols("img", "src", "data");

        // preserve relative paths; when preserving relative paths, the corresponding baseUri attribute must be provided, otherwise they will still be removed
        // WHITELIST.preserveRelativeLinks(false);

        // remove some protocol restrictions on the a and img tags; this would break XSS injection prevention, e.g. <img src=javascript:alert("xss")>
        // although WhiteList#isSafeAttribute could be overridden to handle it, there is hidden risk, so relative paths are not supported for now
        // WHITELIST.removeProtocols("a", "href", "ftp", "http", "https", "mailto");
        // WHITELIST.removeProtocols("img", "src", "http", "https");
        return relaxedSafelist;
    }

    @Override
    public String clean(String html) {
        return Jsoup.clean(html, baseUri, safelist, new Document.OutputSettings().prettyPrint(false));
    }

}

