package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Column {

    private String table_name;
    private String column_name;
    private String column_default;
    private String is_nullable;
    private String data_type;
    private Integer character_maximum_length;

}
