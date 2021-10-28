package jpabook.jpashop.domain;
import com.fasterxml.jackson.annotation.JsonIgnore; // @JsonIgnore 를 사용하기 위한 임포트
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy="member")
    private List<Order> orders = new ArrayList<>();

}
