package com.aem.community.core.filters;

import com.day.cq.wcm.commons.AbstractImageServlet.ImageContext;
import com.day.cq.wcm.foundation.Image;
import com.day.image.Layer;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.engine.EngineConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component(service = Filter.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Demo to filter incoming requests",
                EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
                Constants.SERVICE_RANKING + "=0",
                "sling.filter.selectors=coreimg",
                "sling.filter.selectors=img",
//                "sling.filter.extensions=jpeg",
//                "sling.filter.extensions=jpg"
        })
@Designate(ocd = ChangeImageFilterConfig.class)
public class ChangeImageFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * Grayscale the images
     */
    private boolean model1;
    /**
     * Turn the images upside-down
     */
    private boolean model2;
    /**
     * Degrees that image need to turn
     */
    private int degreesToTurn;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) servletRequest;
        final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) servletResponse;

        String extension = slingRequest.getRequestPathInfo().getExtension();
        String imageType = getImageType(extension);

        servletResponse.setContentType(imageType);

        ImageContext context = new ImageContext(slingRequest, imageType);
        Image image = new Image(context.resource);

        Layer layer = null;

        try {
            layer = image.getLayer(false, false , true);
            layer = changeImageByConfig(layer);
            writeLayer(slingRequest, slingResponse, context, layer);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Activate
    @Modified
    public void activate(ChangeImageFilterConfig config) {
        this.model1 = config.model1();
        this.model2 = config.model2();
        this.degreesToTurn = config.degrees();
    }

    protected void writeLayer(SlingHttpServletRequest request, SlingHttpServletResponse response, ImageContext c, Layer layer)
            throws IOException, RepositoryException {

        int size = layer.getHeight() * layer.getWidth();
        if (size < 1048576)
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream(size);
            layer.write("image/jpg", 1.0, out);
            byte[] bytes = out.toByteArray();
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);
        }
        else {
            layer.write("image/jpg", 1.0, response.getOutputStream());
        }
    }

    private String getImageType(String ext)
    {
        if ("png".equals(ext))
            return "image/png";
        if ("gif".equals(ext))
            return "image/gif";
        if (("jpg".equals(ext)) || ("jpeg".equals(ext))) {
            return "image/jpg";
        }
        return null;
    }

    private Layer changeImageByConfig(Layer layer) {
        if (model1){
            layer.grayscale();
        }
        if (model2){
            layer.rotate(degreesToTurn);
        }
        return layer;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
