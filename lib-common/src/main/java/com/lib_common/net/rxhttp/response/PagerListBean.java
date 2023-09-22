package com.lib_common.net.rxhttp.response;

import java.io.Serializable;
import java.util.List;

/**
 * author: zhaoke
 * blog  :
 * time  :2020/3/2 10:41
 * desc  : The constants of memory.
 */
public class PagerListBean<T> implements Serializable {

    private int pageNum;
    private int pageIndex;
    private int pageSize;
    private int totalSize;
    private int allNumber;
    private int current;
    private int pages;
    private int size;
    private int total;
    private boolean hitCount;


    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isHitCount() {
        return hitCount;
    }

    public void setHitCount(boolean hitCount) {
        this.hitCount = hitCount;
    }

    public boolean isSearchCount() {
        return searchCount;
    }

    public void setSearchCount(boolean searchCount) {
        this.searchCount = searchCount;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    private boolean searchCount;
    private int loadNumber;
    private int unloadNumber;
    private int completeNumber;
    private int unPaymentNumber;
    private int advancePaymentNumber;
    private int paymentingNumber;
    private int paymentNumber;
    private List<T> dispatchListDriverDtos;
    private List<T> content;
    private List<T> records;
    private List<T> data;
    private List<T> mpAppletsCountDto;
    private double loadNetWeightAllSum;
    private double payableTotalAmountAllSum;
    private double paymentTotalAmountAllSum;
    private double settlementDamageQtyAllSum;
    private double unloadNetWeightAllSum;
    private int shipmentAllNum;

    public List<T> getMpAppletsCountDto() {
        return mpAppletsCountDto;
    }

    public void setMpAppletsCountDto(List<T> mpAppletsCountDto) {
        this.mpAppletsCountDto = mpAppletsCountDto;
    }

    public double getLoadNetWeightAllSum() {
        return loadNetWeightAllSum;
    }

    public void setLoadNetWeightAllSum(double loadNetWeightAllSum) {
        this.loadNetWeightAllSum = loadNetWeightAllSum;
    }

    public double getPayableTotalAmountAllSum() {
        return payableTotalAmountAllSum;
    }

    public void setPayableTotalAmountAllSum(double payableTotalAmountAllSum) {
        this.payableTotalAmountAllSum = payableTotalAmountAllSum;
    }

    public double getPaymentTotalAmountAllSum() {
        return paymentTotalAmountAllSum;
    }

    public void setPaymentTotalAmountAllSum(double paymentTotalAmountAllSum) {
        this.paymentTotalAmountAllSum = paymentTotalAmountAllSum;
    }

    public double getSettlementDamageQtyAllSum() {
        return settlementDamageQtyAllSum;
    }

    public void setSettlementDamageQtyAllSum(double settlementDamageQtyAllSum) {
        this.settlementDamageQtyAllSum = settlementDamageQtyAllSum;
    }

    public double getUnloadNetWeightAllSum() {
        return unloadNetWeightAllSum;
    }

    public void setUnloadNetWeightAllSum(double unloadNetWeightAllSum) {
        this.unloadNetWeightAllSum = unloadNetWeightAllSum;
    }

    public int getShipmentAllNum() {
        return shipmentAllNum;
    }

    public void setShipmentAllNum(int shipmentAllNum) {
        this.shipmentAllNum = shipmentAllNum;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getAllNumber() {
        return allNumber;
    }

    public void setAllNumber(int allNumber) {
        this.allNumber = allNumber;
    }

    public int getLoadNumber() {
        return loadNumber;
    }

    public void setLoadNumber(int loadNumber) {
        this.loadNumber = loadNumber;
    }

    public int getUnloadNumber() {
        return unloadNumber;
    }

    public void setUnloadNumber(int unloadNumber) {
        this.unloadNumber = unloadNumber;
    }

    public int getCompleteNumber() {
        return completeNumber;
    }

    public void setCompleteNumber(int completeNumber) {
        this.completeNumber = completeNumber;
    }

    public int getUnPaymentNumber() {
        return unPaymentNumber;
    }

    public void setUnPaymentNumber(int unPaymentNumber) {
        this.unPaymentNumber = unPaymentNumber;
    }

    public int getAdvancePaymentNumber() {
        return advancePaymentNumber;
    }

    public void setAdvancePaymentNumber(int advancePaymentNumber) {
        this.advancePaymentNumber = advancePaymentNumber;
    }

    public int getPaymentingNumber() {
        return paymentingNumber;
    }

    public void setPaymentingNumber(int paymentingNumber) {
        this.paymentingNumber = paymentingNumber;
    }

    public int getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(int paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public List<T> getDispatchListDriverDtos() {
        return dispatchListDriverDtos;
    }

    public void setDispatchListDriverDtos(List<T> dispatchListDriverDtos) {
        this.dispatchListDriverDtos = dispatchListDriverDtos;
    }
    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
