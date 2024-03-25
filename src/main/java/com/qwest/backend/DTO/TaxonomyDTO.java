package com.qwest.backend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaxonomyDTO {
    private Long id;
    private String name;
    private String href;
    private Integer count;
    private String thumbnail;
    private String desc;
    private String color;
    private String taxonomy; // CATEGORY or TAG
}
