package name.yangsm.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @description: 测试实体类
 * @author: yangsm
 * @create: 2019-09-17 13:58
 **/
@Entity
@Table(name = "from_tab")
@Data
@AllArgsConstructor
public class FromBean {
    @Column
    @Id
    private String id;
    @Column
    private String name;
    @Column
    private String type;

    @Column(name = "is_field")
    private String isField;
}
