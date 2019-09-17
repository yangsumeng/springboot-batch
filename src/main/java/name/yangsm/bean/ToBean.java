package name.yangsm.bean;

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
@Table(name = "to_tab")
@Data
public class ToBean {
    @Column
    @Id
    private String id;
    @Column
    private String name;
}
