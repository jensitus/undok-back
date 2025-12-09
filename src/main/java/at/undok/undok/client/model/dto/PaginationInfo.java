package at.undok.undok.client.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaginationInfo {
    private int currentPage;
    private int pageSize;
    private long totalCounselings;
    private long totalClients;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;

    public PaginationInfo() {
    }

    public PaginationInfo(int currentPage, int pageSize,
                          long totalCounselings, long totalClients) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalCounselings = totalCounselings;
        this.totalClients = totalClients;
        this.totalElements = totalCounselings + totalClients;
        this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
        this.hasNext = currentPage < totalPages - 1;
        this.hasPrevious = currentPage > 0;
    }

}
