package com.es.phoneshop.model.product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PriceDate implements Serializable {
    private Date startDate;
    private BigDecimal price;

    public PriceDate() {

    }

    public PriceDate(Date startDate, BigDecimal price) {
        this.startDate = startDate;
        this.price = price;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
