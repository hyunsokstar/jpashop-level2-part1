package jpabook.jpashop.domain;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static javax.persistence.FetchType.*;
// 패치 타입 관련
import static javax.persistence.FetchType.*;


@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY) // 주문 정보가 멤버 정보를 다대일로 참조하므로 @ManyToOne 어노테이션을 적용
    @JoinColumn(name = "member_id") // fk는 멤버 엔티티의 memeber_id
    private Member member;

    // 주문 상품 정보: 상품, 주문 가격, 주문 수량 <=> 일정 주제로 상세 정보가 다양할 경우 엔티티를 따로 만든다고 보면 됨
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
    // 배송 정보 , 배송 상태 정보는 또 다른 엔티티
    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    private Delivery delivery;
    // 주문 날짜
    private LocalDateTime orderDate;
    // 주문 상태
    private OrderStatus status;

    // 주문 생성한 멤버 설정 + 해당 멤버의 주문 목록에 주문 엔티티 추가
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    // 오더와 오더 아이템(주문 상세 정보)간의 연관 관계 편의 함수
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    // 오더와 딜리버리간의 연관 관계 편의 함수
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);

        for(OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem);
        }

        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());

        return order;
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    public void cancel() {
        if(delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가 합니다");
        }
        this.setStatus(OrderStatus.CANCEL);
        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }


}
