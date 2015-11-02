package com.hortonworks.iotas.notification.store.hbase;

import com.hortonworks.iotas.notification.store.hbase.mappers.IndexMapper;
import com.hortonworks.iotas.notification.store.hbase.mappers.Mapper;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PageFilter;

import java.nio.charset.StandardCharsets;

/**
 * A wrapper class used to hold the HBase table scan config params.
 */
public class HBaseScanConfig<T> {
    private static final int DEFAULT_NUM_ROWS = 10;

    private IndexMapper<T> mapper;
    private String indexedFieldValue;
    private final FilterList filterList = new FilterList();
    private long startTs = 0;
    private long endTs = Long.MAX_VALUE;

    public void setMapper(IndexMapper<T> mapper) {
        this.mapper = mapper;
    }

    public void setIndexedFieldValue(String value) {
        this.indexedFieldValue = value;
    }

    public void setNumRows(int n) {
        this.filterList.addFilter(new PageFilter(n == 0 ? DEFAULT_NUM_ROWS : n));
    }

    public void setStartTs(long startTsMillis) {
        this.startTs = startTsMillis;
    }

    public void setEndTs(long endTsMillis) {
        if (endTsMillis != 0) {
            this.endTs = endTsMillis;
        }
    }

    public void addFilter(Filter filter) {
        this.filterList.addFilter(filter);
    }

    // assumes that index table row key always has ts as suffix.
    public byte[] getStartRow() {
        StringBuilder sb = new StringBuilder();
        if (indexedFieldValue != null) {
            sb.append(indexedFieldValue).append(Mapper.ROWKEY_SEP);
        }
        sb.append(startTs);
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    // assumes that index table row key always has ts as suffix.
    // TODO: currently stop row excludes end Ts.
    public byte[] getStopRow() {
        StringBuilder sb = new StringBuilder();
        if (indexedFieldValue != null) {
            sb.append(indexedFieldValue).append(Mapper.ROWKEY_SEP);
        }
        sb.append(endTs);
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public FilterList filterList() {
        return filterList;
    }

    public IndexMapper<T> getMapper() {
        return mapper;
    }

    @Override
    public String toString() {
        return "HBaseScanConfig{" +
                "mapper=" + mapper +
                ", indexedFieldValue='" + indexedFieldValue + '\'' +
                ", filterList=" + filterList +
                ", startTsMillis=" + startTs +
                ", endTsMillis=" + endTs +
                '}';
    }
}
