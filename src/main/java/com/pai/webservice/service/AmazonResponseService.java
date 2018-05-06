package com.pai.webservice.service;

import am.ik.aws.apa.jaxws.Item;
import am.ik.aws.apa.jaxws.ItemSearchResponse;
import am.ik.aws.apa.jaxws.Items;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class AmazonResponseService implements IAmazonResponseService {

    private ItemSearchResponse amazonResponse;
    private Items item;

    public void setAmazonResponse(ItemSearchResponse amazonResponse) {
        this.amazonResponse = amazonResponse;
        this.item = amazonResponse.getItems().get(0);
    }

    @Override
    public String getLinkWithFinalResults() {
        return item.getMoreSearchResultsUrl();
    }

    @Override
    public BigInteger getQuantityResults() {
        return item.getTotalResults();
    }
}
