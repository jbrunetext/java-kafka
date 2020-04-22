package com.github.brunetj.elasticsearch;

import org.apache.http.HttpHost;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ElasticSearchConsumer {
    public static RestHighLevelClient createClient ()
    {
        RestClientBuilder builder = RestClient.builder(
                new HttpHost("localhost", 9200, "http"));


        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(ElasticSearchConsumer.class.getName());
        RestHighLevelClient client = createClient();
        String jsonString = "{\"foo\": \"bar\"}";
        IndexRequest indexRequest = new IndexRequest(
                "twitter",
                "_doc"
        ).source(jsonString, XContentType.JSON);
        IndexResponse indexResponse = null;
        try {
            indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String id = indexResponse.getId();
        logger.info(id);
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
