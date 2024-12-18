package com.devdeli.common.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageRequest{
    private int page = 1;
    private int size = 10;
    private String query = "";
    private String sortedBy = "id";
    private EnumSortDirection sortDirection = EnumSortDirection.DESC;
}
