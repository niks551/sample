package com.convert.admin;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
public class SessionListener implements HttpSessionListener{

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionCreated(final HttpSessionEvent sessionEvent) {
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
     */
    public final void sessionDestroyed(final HttpSessionEvent sessionEvent) {

    }

}
