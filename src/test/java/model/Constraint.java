package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Constraint {

    private String constraint_name;
    private String table_name;
    private String constraint_type;
    private String enforced;

}
