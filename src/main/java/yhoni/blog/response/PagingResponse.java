package yhoni.blog.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagingResponse<T> {
    private T data;
    private Integer currentPage;
    private Integer currentPageSize;
    private Integer totalAllPage;
    private Long totalAllData;
    private Boolean isLast;
}
