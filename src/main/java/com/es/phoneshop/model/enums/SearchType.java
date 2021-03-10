package com.es.phoneshop.model.enums;

public enum SearchType {
    ALL_WORDS("all words"), ANY_WORD("any word");

    private final String label;

    SearchType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
