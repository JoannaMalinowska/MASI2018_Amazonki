package com.pai.webservice.service;

import am.ik.aws.apa.AwsApaRequester;
import am.ik.aws.apa.AwsApaRequesterImpl;
import am.ik.aws.apa.jaxws.ItemSearchRequest;
import am.ik.aws.apa.jaxws.ItemSearchResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WatsonService implements IWatsonService {

    private AwsApaRequester amazonInstance;
    private ItemSearchRequest amazonRequest;

    public WatsonService(){
        this.amazonInstance = this.createAmazonInstance();
        this.amazonRequest = this.createAmazonRequest();
    }

    @Override
    public AwsApaRequester createAmazonInstance() {
        return new AwsApaRequesterImpl();
    }

    @Override
    public ItemSearchRequest createAmazonRequest() {
        return new ItemSearchRequest();
    }

    @Override
    public void setSearchCategory(String category) {
        this.amazonRequest.setSearchIndex(category);
    }

    @Override
    public void prepareKeywordsForRequest(List<String> keywords) {
        StringBuilder builder = new StringBuilder();
        for (String keyword: keywords) {
            builder.append(keyword);
            builder.append(",");
        }
        String result = builder.substring(0, builder.length() - 1);
        this.amazonRequest.setKeywords(result);
    }

    @Override
    public ItemSearchResponse getResultFromRequest() {
        return this.amazonInstance.itemSearch(this.amazonRequest);
    }


}
