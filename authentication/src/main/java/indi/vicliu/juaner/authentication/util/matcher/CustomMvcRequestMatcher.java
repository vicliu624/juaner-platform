package indi.vicliu.juaner.authentication.util.matcher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.web.util.matcher.RequestVariablesExtractor;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.servlet.handler.MatchableHandlerMapping;
import org.springframework.web.servlet.handler.RequestMatchResult;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
public class CustomMvcRequestMatcher implements RequestMatcher, RequestVariablesExtractor {

    private final CustomMvcRequestMatcher.DefaultMatcher defaultMatcher = new CustomMvcRequestMatcher.DefaultMatcher();

    private final HandlerMappingIntrospector introspector;
    private final String pattern;
    private HttpMethod method;
    private String servletPath;

    public CustomMvcRequestMatcher(HandlerMappingIntrospector introspector, String pattern) {
        this.introspector = introspector;
        this.pattern = pattern;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (this.method != null && !this.method.name().equals(request.getMethod())) {
            return false;
        }
        if (this.servletPath != null
                && !this.servletPath.equals(request.getServletPath())) {
            return false;
        }

        /*
        MatchableHandlerMapping mapping = getMapping(request);
        if (mapping == null) {
            return this.defaultMatcher.matches(request);
        }
        RequestMatchResult matchResult = mapping.match(request, this.pattern);
        return matchResult != null;
        */
        return this.method.name().equalsIgnoreCase(request.getMethod()) && this.servletPath.equals(request.getServletPath());
    }

    private MatchableHandlerMapping getMapping(HttpServletRequest request) {
        try {
            return this.introspector.getMatchableHandlerMapping(request);
        } catch (Throwable t) {
            log.error("getMapping error",t);
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.security.web.util.matcher.RequestVariablesExtractor#
     * extractUriTemplateVariables(javax.servlet.http.HttpServletRequest)
     */
    @Override
    @Deprecated
    public Map<String, String> extractUriTemplateVariables(HttpServletRequest request) {
        return matcher(request).getVariables();
    }

    @Override
    public MatchResult matcher(HttpServletRequest request) {
        MatchableHandlerMapping mapping = getMapping(request);
        if (mapping == null) {
            return this.defaultMatcher.matcher(request);
        }
        RequestMatchResult result = mapping.match(request, this.pattern);
        return result == null ? MatchResult.notMatch()
                : MatchResult.match(result.extractUriTemplateVariables());
    }

    /**
     * @param method the method to set
     */
    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    /**
     * The servlet path to match on. The default is undefined which means any servlet
     * path.
     *
     * @param servletPath the servletPath to set
     */
    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    protected final String getServletPath() {
        return this.servletPath;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Mvc [pattern='").append(this.pattern).append("'");

        if (this.servletPath != null) {
            sb.append(", servletPath='").append(this.servletPath).append("'");
        }

        if (this.method != null) {
            sb.append(", ").append(this.method);
        }

        sb.append("]");

        return sb.toString();
    }

    private class DefaultMatcher implements RequestMatcher {

        private final UrlPathHelper pathHelper = new UrlPathHelper();

        private final PathMatcher pathMatcher = new AntPathMatcher();

        @Override
        public boolean matches(HttpServletRequest request) {
            String lookupPath = this.pathHelper.getLookupPathForRequest(request);
            return matches(lookupPath);
        }

        private boolean matches(String lookupPath) {
            return this.pathMatcher.match(CustomMvcRequestMatcher.this.pattern, lookupPath);
        }

        @Override
        public MatchResult matcher(HttpServletRequest request) {
            String lookupPath = this.pathHelper.getLookupPathForRequest(request);
            if (matches(lookupPath)) {
                Map<String, String> variables = this.pathMatcher.extractUriTemplateVariables(
                        CustomMvcRequestMatcher.this.pattern, lookupPath);
                return MatchResult.match(variables);
            }
            return MatchResult.notMatch();
        }
    }
}
