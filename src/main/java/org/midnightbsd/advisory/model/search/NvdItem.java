package org.midnightbsd.advisory.model.search;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lucas Holt
 */
@ToString
@EqualsAndHashCode
@Document(indexName = "nvd", type = "nvditem")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NvdItem implements Serializable, Comparable<NvdItem> {
    private static final long serialVersionUID = 3452319081969591585L;

    @Id
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    @Version
    private Long version;

    @Getter
    @Setter
    private String cveId;

    @Getter
    @Setter
    private String description;


    @Getter
    @Setter
    private List<Instance> instances;
    
    @Override
    public int compareTo(final NvdItem o) {
        return this.id.compareTo(o.getId());
    }
}