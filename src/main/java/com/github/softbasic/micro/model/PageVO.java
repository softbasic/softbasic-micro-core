package com.github.softbasic.micro.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public final class PageVO<T> implements Serializable {
    private static final long serialVersionUID = -4106030982324955419L;
    private int start;
    private int pageSize;
    private List<T> data;
    private int totalCount;
    private int totalPageCount;
    private int currentPageNo;
    private boolean hasNextPage;
    private boolean hasPreviousPage;
    private boolean isStartPage;
    private boolean isEndPage;

    public PageVO() {
        this(0, 0, 20, new ArrayList());
    }

    public PageVO(int start, int totalSize, int pageSize, List<T> data) {
        this.pageSize = 20;
        this.data = new ArrayList(0);
        this.pageSize = pageSize;
        this.start = start;
        this.totalCount = totalSize;
        this.data = data;
        this.init();
    }

    public static int getStartOfPage(int pageNo) {
        return getStartOfPage(pageNo, 20);
    }

    public static int getStartOfPage(int pageNo, int pageSize) {
        return (pageNo - 1) * pageSize;
    }



    protected void init() {
        if (this.totalCount % this.pageSize == 0) {
            this.totalPageCount = this.totalCount / this.pageSize;
        } else {
            this.totalPageCount = this.totalCount / this.pageSize + 1;
        }

        this.currentPageNo = this.start / this.pageSize + 1;
        this.hasNextPage = this.currentPageNo < this.totalPageCount;
        this.hasPreviousPage = this.currentPageNo > 1;
        this.isStartPage = this.currentPageNo == 1;
        this.isEndPage = this.currentPageNo == this.totalPageCount;
    }
}