package com.example.assignment03.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

// Model for Keyword Fact Response
public class KeywordResponse {
    @SerializedName("total")
    @Expose
    private Long total;
    @SerializedName("result")
    @Expose
    private List<Result> result = null;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }
}
