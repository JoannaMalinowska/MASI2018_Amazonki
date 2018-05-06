package com.pai.webservice.service;

import am.ik.aws.apa.AwsApaRequester;
import am.ik.aws.apa.jaxws.ItemSearchRequest;
import am.ik.aws.apa.jaxws.ItemSearchResponse;

import java.util.List;

public interface IWatsonService {

    AwsApaRequester createAmazonInstance();
    ItemSearchRequest createAmazonRequest();
    void setSearchCategory(String category);
    void prepareKeywordsForRequest(List<String> keywords);
    ItemSearchResponse getResultFromRequest();
}
