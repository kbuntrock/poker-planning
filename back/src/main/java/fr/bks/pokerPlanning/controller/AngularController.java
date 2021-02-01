package fr.bks.pokerPlanning.controller;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * @author : Kévin Buntrock
 */
@RestController
public class AngularController {

    private static final Pattern REGEX = Pattern.compile("<base .*>");

    @Value("classpath:/static/app/index.html")
    private Resource indexFile;
    @Value("#{servletContext.contextPath}")
    private String servletContextPath;

    @GetMapping(path = {"/app", "/app/**/{?:[^\\.]*}"}, produces = MediaType.TEXT_HTML_VALUE)
    @Cacheable("AngularIndexHtml")
    public String index() throws IOException {
        final String appPath = servletContextPath + "/app/";
        final String indexContent = IOUtils.toString(indexFile.getInputStream(), StandardCharsets.UTF_8);
        return REGEX.matcher(indexContent).replaceFirst("<base href=\"" + appPath + "\">");
    }
}
