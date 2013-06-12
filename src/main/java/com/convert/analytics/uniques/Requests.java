/**
 * (C) 2011 Digi-Net Technologies, Inc.
 * 4420 Northwest 36th Avenue
 * Gainesville, FL 32606 USA
 * All rights reserved.
 */
package com.convert.analytics.uniques;

import java.io.Serializable;
import java.util.UUID;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.Gson;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;

/**
 * @author Ghais Issa <ghais.issa@convertglobal.com>
 * 
 */

public class Requests {

    private static final Gson gson = new Gson();

    private final AsyncHttpClient client;

    private final String url;

    public Requests(AsyncHttpClient client, String url) {
        this.client = client;
        this.url = url;
    }

    public ListenableFuture<Boolean> newInc(String key, long timestamp, byte[] payload) {
        final SettableFuture<Boolean> future = SettableFuture.create();
        Inc inc = new Inc(key, timestamp / 1000, new String(payload));
        Request r = new Request("HllQuery.Sacrifice", UUID.randomUUID().toString(), new Inc[] { inc });
        try {
            BoundRequestBuilder post = client.preparePost(url);
            post.setBody(gson.toJson(r).getBytes()).execute(new AsyncCompletionHandler<Boolean>() {

                @Override
                public Boolean onCompleted(com.ning.http.client.Response response) throws Exception {
                    Requests.IncResult r = gson.fromJson(response.getResponseBody(), Requests.IncResult.class);
                    future.set(r.getResult());
                    return r.getResult();

                }
            });
        } catch (Throwable t) {
            future.setException(t);
        }
        return future;
    }

    public ListenableFuture<Long> newEstimate(String key, long start, long end) {
        Estimate estimate = new Estimate(key, start / 1000, end / 1000);
        Request r = new Request("HllQuery.Estimate", UUID.randomUUID().toString(), new Estimate[] { estimate });
        final SettableFuture<Long> future = SettableFuture.create();
        try {
            BoundRequestBuilder post = client.preparePost(url);
            post.setBody(gson.toJson(r).getBytes()).execute(new AsyncCompletionHandler<Long>() {

                @Override
                public Long onCompleted(com.ning.http.client.Response response) throws Exception {
                    Requests.EstimateResult r = gson.fromJson(response.getResponseBody(), Requests.EstimateResult.class);
                    future.set(r.getResult());
                    return r.getResult();

                }
            });
        } catch (Throwable t) {
            future.setException(t);
        }
        return future;
    }

    private static class Request implements Serializable {

        private final String method;

        private final String id;

        private final Object[] params;

        private Request(String method, String id, Object[] params) {
            this.id = id;
            this.method = method;
            this.params = params;
        }
    }

    /**
     * 
     * @author Ghais Issa <ghais.issa@convertglobal.com>
     * 
     */
    private static class Inc implements Serializable {

        private final long Timestamp;

        private final String Payload;

        private final String Id;

        /**
         * 
         * @param id
         * @param timestamp
         * @param payload
         */
        public Inc(String id, long timestamp, String payload) {
            this.Timestamp = timestamp;
            this.Id = id;
            this.Payload = payload;
        }
    }

    /**
     * 
     * @author Ghais Issa <ghais.issa@convertglobal.com>
     * 
     */
    public static class Estimate implements Serializable {

        private long Start;

        private long End;

        private final String Id;

        /**
         * 
         * @param id
         * @param start
         * @param duration
         */
        public Estimate(String id, long start, long end) {
            this.Id = id;
            this.Start = start;
            this.End = end;
        }

    }

    public static class IncResult implements Serializable {

        private String id;

        private boolean result;

        private String error;

        //
        public IncResult() {
        }

        public String getId() {
            return id;
        }

        public boolean getResult() {
            return result;
        }

        public String getError() {
            return error;
        }
    }

    public static class EstimateResult implements Serializable {

        private String id;

        private long result;

        private String error;

        //
        public EstimateResult() {
        }

        public String getId() {
            return id;
        }

        public long getResult() {
            return result;
        }

        public String getError() {
            return error;
        }
    }
}
